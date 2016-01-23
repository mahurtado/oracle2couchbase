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
		
		log.log(Level.FINE, "Configuration loaded: "+ this.toString());
	}

	public static Config getInstance(){
		return instance;
	}

	public String getCbClusterAddress() {
		return cbClusterAddress;
	}

	public String getCbBucketName() {
		return cbBucketName;
	}

	public String getCbBucketPassword() {
		return cbBucketPassword;
	}

	public String getOraAddress() {
		return oraAddress;
	}

	public String getOraPort() {
		return oraPort;
	}

	public String getOraUser() {
		return oraUser;
	}

	public String getOraPassword() {
		return oraPassword;
	}

	public String getOraSid() {
		return oraSid;
	}

	public String getOraService() {
		return oraService;
	}

	public String getOraTables() {
		return oraTables;
	}

	@Override
	public String toString() {
		return "Config [cbClusterAddress=" + cbClusterAddress + ", cbBucketName=" + cbBucketName + ", cbBucketPassword="
				+ cbBucketPassword + ", oraAddress=" + oraAddress + ", oraPort=" + oraPort + ", oraUser=" + oraUser
				+ ", oraPassword=" + oraPassword + ", oraSid=" + oraSid + ", oraService=" + oraService + ", oraTables="
				+ oraTables + "]";
	}
	
}
