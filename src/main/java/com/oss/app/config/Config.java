package com.oss.app.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Config {
	
	public static final String BEGINDATE;
	public static final String TEMPPATH;
	
	static Properties props = new Properties();
	static{
		try {
			InputStream in = new FileInputStream("conf/config.properties");
			props.load(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		TEMPPATH = props.getProperty("tempPath");
		BEGINDATE = props.getProperty("beginDate");
		
	}
	
	public static void modifyBeginDate(String newDate){
		try {
			FileOutputStream of = new FileOutputStream("conf/config.properties",false);
			props.setProperty("beginDate", newDate);
			props.store(of, "modify beginDate");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
