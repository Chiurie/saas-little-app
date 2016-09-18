package com.oss.app.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service("tableDataService")
public class TableDataServiceImpl implements TableDataService{

	@Autowired
	private JdbcTemplate jdbcTemplate;

	
	@Override
	public List<Map<String,Object>> getTableList(String starDate,String endDate) {
		String sql = "select id,companyName from t_saas_company where createdDate < now() and status=0";
		List<Map<String,Object>> list = jdbcTemplate.queryForList(sql);
		return list;
	}

}
