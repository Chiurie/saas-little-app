package com.oss.app;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.oss.app.config.Config;
import com.oss.app.service.TableDataService;

public class Test {


	private static ClassPathXmlApplicationContext ctx = null;
	
	public Test(){
		initContainer();
	}
	
	public void initContainer(){
		TableDataService tdService = ctx.getBean("tableDataService", TableDataService.class);
		List<Map<String,Object>> list = tdService.getTableList();
		for (Map<String, Object> map : list) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				System.out.print(entry.getKey()+":"+entry.getValue()+"|");
			}
			System.out.println("");
		}
	}
	
	
	public static void main(String[] args) {
		ctx = new ClassPathXmlApplicationContext(new String[] { "conf/spring/spring-*.xml" });
		new Test();
		String a = "测试Jar";
		System.out.println(StringUtils.isNotBlank(a));
		System.out.println(a+":Hello World!");
		try {
			for (int i =0;i < 100;i++) {
				System.out.println(Config.ONE+":"+Config.SECOND+":"+Config.TIMEOUT);
				Thread.sleep(1000);
				System.out.println(i+":Hello World!");	
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}
	
}
