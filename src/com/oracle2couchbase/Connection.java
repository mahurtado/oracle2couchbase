package com.oracle2couchbase;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.CouchbaseCluster;

public class Connection {
	
	public static Bucket getCouchbaseBucket(){
		return CouchbaseCluster.create(Arrays.asList(Config.getInstance().getCbClusterAddress().split(",")))
				.openBucket(Config.getInstance().getCbBucketName(), Config.getInstance().getCbBucketPassword());
	}
	
	public static java.sql.Connection  getOracleConnection(){
		try {
			StringBuffer sbUrl = new StringBuffer("jdbc:oracle:thin:@");
			sbUrl.append(Config.getInstance().getOraAddress() + ":");
			sbUrl.append(Config.getInstance().getOraPort());
			if(Config.getInstance().getOraSid() != null){
				sbUrl.append(":" + Config.getInstance().getOraSid());
			}
			else{
				sbUrl.append("/" + Config.getInstance().getOraService());
			}
			return DriverManager.getConnection(sbUrl.toString(), Config.getInstance().getOraUser(), Config.getInstance().getOraPassword());
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

}
