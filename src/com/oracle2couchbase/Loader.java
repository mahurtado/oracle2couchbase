package com.oracle2couchbase;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.Types.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;

public class Loader {

	private static final Logger log = Logger.getLogger( Loader.class.getName() );
	
	public static final String KEY_SEPARATOR = "::";
	public static final int MAX_VALUE_SIZE = 20*1024*1024;
	public static final int MAX_ERRORS = 50;
	public static final int POS_COL_NAME = 4;
	public static final int POS_COL_TYPE = 5;
	public static final int POS_COL_DECIMALDIGITS = 9;
	public static final int POS_COL_INDEX = 17;	
	public static final int POS_KEY_COL_NAME = 4;
	public static final int POS_KEY_COL_INDEX = 5;
	
	private static HashMap<String,List<Column>> tableColumns = new HashMap<String,List<Column>>();
	private static HashMap<String,List<PK_Column>> tablePKColumns = new HashMap<String,List<PK_Column>>();
	
	public static void main(String[] args) {
		java.sql.Connection oraConn = null;
		try {
			oraConn = Connection.getOracleConnection();
			String tables[] = Config.getOraTables().split(",");
 
			for(String table : tables){
				getTableInfo(oraConn, table);
				int insertedRows = processTable(oraConn, table);
				log.log(Level.INFO, "TABLE " + table + " processed. " + insertedRows + " documents loaded");
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(oraConn != null){
				try {
					oraConn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public static void getTableInfo(java.sql.Connection oraConn, String tableName){
		try {
			
			ResultSet result_c = oraConn.getMetaData().getColumns(null, null, tableName, null);
			List<Column> columns = new ArrayList<Column>();
			HashMap<String,Integer> hColumnIndex = new HashMap<String,Integer>();
			HashMap<String,Integer> hColumnType = new HashMap<String,Integer>();
			while(result_c.next()){
				String columnName = result_c.getString(POS_COL_NAME);
				int    columnType = result_c.getInt(POS_COL_TYPE);
				int    decimalDigits = result_c.getInt(POS_COL_DECIMALDIGITS);
				int   columnIndex = result_c.getInt(POS_COL_INDEX);
				
				if(decimalDigits > 0 && columnType == Types.DECIMAL){
					columnType = Types.FLOAT;
				}
				
				hColumnIndex.put(columnName, columnIndex);
				hColumnType.put(columnName, columnType);
				columns.add(new Column(columnIndex, columnName, columnType));
			}
			Collections.sort(columns);
			tableColumns.put(tableName, columns);
			
			ResultSet result_k = oraConn.getMetaData().getPrimaryKeys(null, null, tableName);
			List<PK_Column> keyColumns = new ArrayList<PK_Column>();
			while(result_k.next()){
				String name = result_k.getString(POS_KEY_COL_NAME);
				int keyIndex = result_k.getInt(POS_KEY_COL_INDEX);
				int columnIndex = hColumnIndex.get(name);
				int type = hColumnType.get(name);
				keyColumns.add(new PK_Column(columnIndex, keyIndex, name, type));
			}
			Collections.sort(keyColumns);
			tablePKColumns.put(tableName, keyColumns);
			
			log.log(Level.INFO, "TABLE " + tableName);
			log.log(Level.INFO, "Columns");
			log.log(Level.INFO, Arrays.toString(columns.toArray(new Column[0])));
			log.log(Level.INFO, "PK_Columns");
			log.log(Level.INFO, Arrays.toString(keyColumns.toArray(new PK_Column[0])));
			
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error processing table info: " + tableName);
			e.printStackTrace();
		}
			
	}
	
	public static int processTable(java.sql.Connection oraConn, String tableName){
		int insertedRows = 0;
		try {
			Bucket bucket = Connection.getCouchbaseBucket();
			String query = "SELECT * FROM " + tableName;
			Statement stmt = oraConn.createStatement();
	        ResultSet row = stmt.executeQuery(query);
	        int numErrors = 0;
	        String key = null;
	        while (row.next()) {
	        	try {
					key = getKey(tableName, row);
					JsonObject json = getJson(tableName, row);
					log.log(Level.FINE, json.toString());
					JsonDocument doc = JsonDocument.create(key, json);
					bucket.insert(doc);
					insertedRows++;
				} catch (Exception e) {
					numErrors++;
					e.printStackTrace();
					log.log(Level.SEVERE, "Error processing row. key: " + key);
					if(numErrors >= MAX_ERRORS){
						log.log(Level.SEVERE, "Maximum number of errors reached. Exiting.");
						break;
					}
				}
	        	
	        }	
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error processing table: " + tableName);
			e.printStackTrace();
		}
		return insertedRows;
		
		
	}

	private static JsonObject getJson(String tableName, ResultSet row) {
		JsonObject value = JsonObject.create();
		List<Column> columns = tableColumns.get(tableName);
		for(Column column : columns){
			value.put(column.getName().toLowerCase(), getJsonTypeObject(row, column.getIndex(), column.getType()));
		}
		value.put("type", tableName.toLowerCase());
		return value;
	}

	private static String getKey(String tableName, ResultSet row) {
		List<PK_Column>pk_columns = tablePKColumns.get(tableName);
		StringBuffer sbKey = new StringBuffer(tableName.toLowerCase());
		for(PK_Column pk_column : pk_columns){
			sbKey.append(KEY_SEPARATOR + getJsonTypeObject(row, pk_column.getColumnIndex(), pk_column.getType()));
		}
		return sbKey.toString();
	}
	
	private static String getISO8601String(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat.format(date);
	}
	
	private static Object getJsonTypeObject(ResultSet row, int index, int type){
		Object res = null;
		try {
			if(row.getObject(index) == null)
				return null;
			
			switch(type) {
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.LONGVARCHAR:
			case Types.NCHAR:
			case Types.NVARCHAR:
				res = row.getString(index);
				break;
			// ISO 8601
			case Types.DATE:
			case Types.TIME:
			case Types.TIME_WITH_TIMEZONE:
			case Types.TIMESTAMP:	
			case Types.TIMESTAMP_WITH_TIMEZONE:
				res = getISO8601String(row.getDate(index));
				break;
			/*case Types.TIMESTAMP:	
			case Types.TIMESTAMP_WITH_TIMEZONE:	
				res = row.getTimestamp(index).getTime();
				break;*/
			case Types.BOOLEAN:
				res = row.getBoolean(index);
				break;
			case Types.NUMERIC:
			case Types.DECIMAL:	
			case Types.BIGINT:
				res = row.getLong(index);
				break;
			case Types.SMALLINT:
			case Types.INTEGER:
				res = row.getInt(index);
				break;
			case Types.FLOAT:
			case Types.REAL:
			case Types.DOUBLE:
				res = row.getDouble(index);
				break;
			// Binary types stored as Base64 strings
			// RAW/LONGRAW
			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
				res = Base64.getEncoder().encodeToString(row.getBytes(index));
				break;
			// BLOB
			case Types.BLOB:
				Blob blob = row.getBlob(index);
				long size = blob.length();
				if(size > MAX_VALUE_SIZE)
					throw new Exception("BLOB value exceed 20 Mb");
				res = Base64.getEncoder().encodeToString(blob.getBytes(1l, (int)size));
				blob.free();
				break;
			// CLOB
			case Types.CLOB:
				Clob clob = row.getClob(index);
				long c_size = clob.length();
				if(c_size > MAX_VALUE_SIZE)
					throw new Exception("CLOB value exceed 20 Mb");
				res = clob.getSubString(1, (int)c_size);
				clob.free();
				break;
			default:	
				//res = row.getString(index);
				//break;
				throw new Exception("UNSUPPORTED TYPE: " + type);
			}
		} 
		catch (Exception e) {			
			log.log(Level.SEVERE, "Error processing object. index: " + index + "; type: " + type);
			e.printStackTrace();
		}
		return res;
	}

}