package com.oss.app;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.oss.app.config.Config;
import com.oss.app.service.TableDataService;
import com.oss.app.utils.ExcelUtils;
import com.oss.app.utils.JavaEmail;

public class Test {


	private static ClassPathXmlApplicationContext ctx = null;
	
	public Test(){
		initContainer();
	}
	
	public void initContainer(){
		TableDataService tdService = ctx.getBean("tableDataService", TableDataService.class);
		try {
			String startDate = Config.BEGINDATE;
			String endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
			List<Map<String,Object>> list = tdService.getTableList(startDate,endDate);
			if(CollectionUtils.isNotEmpty(list)){
				HSSFWorkbook wb = ExcelUtils.exportExcel("注册客户列表", list,
						new String[]{"主键","公司名称"},
						new String[]{"id","companyName"});
				String fileName = "注册客户列表";
				File file = new File(Config.TEMPPATH);
				if(!file.exists()){
					file.mkdir();
				}
				String pathName = Config.TEMPPATH+System.currentTimeMillis()+".xls";
				FileOutputStream fileOut = new FileOutputStream(pathName);
				ExcelUtils.export(wb, fileName, fileOut);
				JavaEmail email = new JavaEmail(false);
				String subject = Config.BEGINDATE+"~"+endDate+"注册客户";
			    String sendHtml = Config.BEGINDATE+"~"+endDate+"注册客户客户列表内容";
				email.doSendHtmlEmailWithAttachment(subject, sendHtml,new File(pathName));
				//修改 当前 时间为 下次的 开始时间
				Config.modifyBeginDate(endDate);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		/*for (Map<String, Object> map : list) {
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				System.out.print(entry.getKey()+":"+entry.getValue()+"|");
			}
			System.out.println("");
		}*/
	}
	
	
	public static void main(String[] args) {
		ctx = new ClassPathXmlApplicationContext(new String[] { "conf/spring/spring-*.xml" });
		new Test();
	}
	
}
