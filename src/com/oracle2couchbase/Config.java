package com.oracle2couchbase;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Config {
	
	private static final Logger log = Logger.getLogger( Config.class.getName() );
	
	// clusterAddress format: ip1,ip2,ip3,...
	public static final String DEFAULT_CB_CLUSTER_ADDRESS = "127.0.0.1";
	public static final String DEFAULT_BUCKET_NAME = "default";
	public static final String DEFAULT_ORA_ADDRESS = "127.0.0.1";
	public static final String DEFAULT_ORA_PORT = "1521";
	
	private String cbClusterAddress;
	private String cbBucketName;
	private String cbBucketPassword;
	private String oraAddress;
	private String oraPort;
	private String oraUser;
	private String oraPassword;
	private String oraSid;
	private String oraService;
	private String oraTables;	
	
	private static Config instance = new Config();
	
	private Config(){
		
		cbClusterAddress = System.getProperty("cbClusterAddress", DEFAULT_CB_CLUSTER_ADDRESS);
		cbBucketName = System.getProperty("cbBucketName", DEFAULT_BUCKET_NAME);
		cbBucketPassword = System.getProperty("cbBucketPassword");
		oraAddress = System.getProperty("oraAddress", DEFAULT_ORA_ADDRESS);
		oraPort = System.getProperty("oraPort", DEFAULT_ORA_PORT);
		oraUser = System.getProperty("oraUser");
		oraPassword = System.getProperty("oraPassword");
		oraSid = System.getProperty("oraSid");
		oraService = System.getProperty("oraService");
		oraTables = System.getProperty("oraTables");
		
		log.log(Level.INFO, "Configuration Loaded: "+ this.toString());
	}

	public static String getCbClusterAddress() {
		return instance.cbClusterAddress;
	}

	public static String getCbBucketName() {
		return instance.cbBucketName;
	}

	public static String getCbBucketPassword() {
		return instance.cbBucketPassword;
	}

	public static String getOraAddress() {
		return instance.oraAddress;
	}

	public static String getOraPort() {
		return instance.oraPort;
	}

	public static String getOraUser() {
		return instance.oraUser;
	}

	public static String getOraPassword() {
		return instance.oraPassword;
	}

	public static String getOraSid() {
		return instance.oraSid;
	}

	public static String getOraService() {
		return instance.oraService;
	}

	public static String getOraTables() {
		return instance.oraTables;
	}

	@Override
	public String toString() {
		return "Config [cbClusterAddress=" + cbClusterAddress + ", cbBucketName=" + cbBucketName + ", cbBucketPassword="
				+ cbBucketPassword + ", oraAddress=" + oraAddress + ", oraPort=" + oraPort + ", oraUser=" + oraUser
				+ ", oraPassword=" + oraPassword + ", oraSid=" + oraSid + ", oraService=" + oraService + ", oraTables="
				+ oraTables + "]";
	}
	
}
