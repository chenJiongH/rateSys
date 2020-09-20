package dao;

import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import domain.PSpecialists;
import util.JDBCUtils;

public class PCDSPChangePinDao {

	JdbcTemplate template = JDBCUtils.getTemplate();
	public  Map<String, Object> findSPMessage(String spid) throws Exception{
		String sql = "SELECT * FROM ";
		if(spid.charAt(0) == 'P')
			sql += "pspecialists WHERE pspid=?";
		else if(spid.charAt(0) == 'C')
			sql += "cspecialists WHERE cspid=?";
		else if(spid.charAt(0) == 'D')
			sql += "dspecialists WHERE dspid=?";
		return template.queryForMap(sql, spid);
	}
	public void changeMessage(PSpecialists sp, String newPass) throws Exception{
		String sql = "UPDATE ";
		if(sp.getPspid().charAt(0) == 'P') 
			sql += "PSpecialists ";
		else if(sp.getPspid().charAt(0) == 'C') 
			sql += "CSpecialists ";
		else if(sp.getPspid().charAt(0) == 'D') 
			sql += "DSpecialists ";
		
		sql += "SET SPname=?, SPpassword=?, SPphone=?, SPorganization=?, SPspecialty=?, SPage=?, SPtitle=?, SPrank=?, SPfields=?, SPgrade=? WHERE ";
		
		if(sp.getPspid().charAt(0) == 'P') 
			sql += "PSPID=? ";
		else if(sp.getPspid().charAt(0) == 'C') 
			sql += "CSPID=? ";
		else if(sp.getPspid().charAt(0) == 'D') 
			sql += "DSPID=? ";
		sql += "AND SPpassword=?";
		int flag = template.update(sql, sp.getSpname(), newPass, sp.getSpphone(), sp.getSporganization(), sp.getSpspecialty(), sp.getSpage(), sp.getSptitle(), sp.getSprank(), sp.getSpfields(), sp.getSpgrade(), sp.getPspid(), sp.getSppassword());
		if(flag == 0)
			throw new Exception();
	}

}
