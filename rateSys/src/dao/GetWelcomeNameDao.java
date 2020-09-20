package dao;

import org.springframework.jdbc.core.JdbcTemplate;

import util.JDBCUtils;

public class GetWelcomeNameDao {
	
	JdbcTemplate template = JDBCUtils.getTemplate();
	
	public String getManaName(String mid, String tid) {
		String sql = "SELECT mname name FROM managers WHERE mid=? LIMIT 1";
		return template.queryForObject(sql, String.class, mid);
	}

	public String getSpName(String spid) {
		String sql = "SELECT spname name FROM ";
		if(spid.charAt(0) == 'D') 
			sql += "DSpecialists WHERE DSPID=?";
		if(spid.charAt(0) == 'C') 
			sql += "CSpecialists WHERE CSPID=?";
		if(spid.charAt(0) == 'P') 
			sql += "PSpecialists WHERE PSPID=?";
		return template.queryForObject(sql, String.class, spid);
	}

}
