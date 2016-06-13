package com.oracle2couchbase;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;

public class Connection {
	
	private Bucket couchbaseBucket;
	private java.sql.Connection oracleConnection;
	
	private static final Logger log = Logger.getLogger( Connection.class.getName() );
	
	private static Connection instance = new Connection();

	private Connection(){
		
		try {
			// Init Couchbase bucket 
			if(Config.getCbBucketPassword() != null)
				couchbaseBucket = CouchbaseCluster.create(Arrays.asList(Config.getCbClusterAddress().split(",")))
					.openBucket(Config.getCbBucketName(), Config.getCbBucketPassword());
			else
				couchbaseBucket = CouchbaseCluster.create(Arrays.asList(Config.getCbClusterAddress().split(",")))
				.openBucket(Config.getCbBucketName());
			
			// Init Oracle Connection
			StringBuffer sbUrl = new StringBuffer("jdbc:oracle:thin:@");
			sbUrl.append(Config.getOraAddress() + ":");
			sbUrl.append(Config.getOraPort());
			if(Config.getOraSid() != null){
				sbUrl.append(":" + Config.getOraSid());
			}
			else{
				sbUrl.append("/" + Config.getOraService());
			}
			log.log(Level.INFO, "Oracle connection url: "+ sbUrl.toString());
			oracleConnection = DriverManager.getConnection(sbUrl.toString(), Config.getOraUser(), Config.getOraPassword());
		} catch (Exception e) {
			e.printStackTrace();
			log.severe("Could not establish connection. Exiting. Error message: " + e.getMessage());
			System.exit(0);
		}
	}
	
	public static Bucket getCouchbaseBucket(){
		return instance.couchbaseBucket;
	}
	
	public static java.sql.Connection  getOracleConnection(){
		return instance.oracleConnection;
	}

}
