package com.oss.app.config;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Config {
	
	public static final String ONE;
	public static final String SECOND;
	public static final String TIMEOUT;
	
	static Properties props = new Properties();
	static{
		try {
			InputStream in = new FileInputStream("conf/config.properties");
			props.load(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ONE = props.getProperty("one");
		SECOND = props.getProperty("second");
		TIMEOUT = props.getProperty("timeout");
		
	}

}
