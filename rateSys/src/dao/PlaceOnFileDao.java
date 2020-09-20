package dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import domain.Archives;
import domain.PSPRatePageBean;
import util.JDBCUtils;

public class PlaceOnFileDao {

	JdbcTemplate template = JDBCUtils.getTemplate();
	// 获取省管理员的存活项目
	public List<Map<String, Object>> findExistPr()  {
		String sql = "SELECT\r\n" + 
				"	DISTINCT pcm.pid,projects.pname\r\n" + 
				"FROM\r\n" + 
				"	pcm\r\n" + 
				"LEFT JOIN projects ON projects.pid = pcm.pid";
		return template.queryForList(sql);
	}
	// 获取市、县管理员的存活项目
	public List<Map<String, Object>> findCityDistExistPr(String mid) {
		String sql = "SELECT\r\n" + 
				"	DISTINCT pcm.pid,projects.pname\r\n" + 
				"FROM\r\n" + 
				"	pcm\r\n" + 
				"LEFT JOIN projects ON projects.pid = pcm.pid "
				+ "WHERE pcm.sid = ?";
		return template.queryForList(sql, mid);
	}
	
	public List<Map<String, Object>> findArchivePr() {
		String sql = "SELECT\r\n" + 
				"	DISTINCT archives.pname\r\n" + 
				"FROM\r\n" + 
				"	archives";
		return template.queryForList(sql);
	}

	public List<Map<String, Object>> findExistSchool(String pid) {
		String sql = "SELECT\r\n" + 
				"	DISTINCT schools.sid,schools.sname\r\n" + 
				"FROM\r\n" + 
				"	pcm\r\n" + 
				"LEFT JOIN schools ON schools.sid = pcm.sid\r\n" + 
				"WHERE\r\n" + 
				"	pcm.pid = ?\r\n" + 
				"	AND pcm.sid LIKE 'S%'";
		return template.queryForList(sql, pid);
	}

	public List<Map<String, Object>> findArchiveSchool(String pid) {
		String sql = "SELECT\r\n" + 
				"	DISTINCT schoolName sname\r\n" + 
				"FROM\r\n" + 
				"	archives\r\n" + 
				"WHERE\r\n" + 
				"	archives.pname = ?";
		return template.queryForList(sql, pid);
	}

	public List<Archives> findExistCByPidSid(String pid, String sid) {
		String sql = "SELECT \r\n" + 
				"	a.aname,\r\n" + 
				"	b.bname,\r\n" + 
				"	c.cname,\r\n" + 
				"	c.score,\r\n" + 
				"	spscore.schoolScore,\r\n" + 
				"	spscore.description,\r\n" + 
				"	spscore.annexLocation,\r\n" + 
				"	selfreport.overallFileName overallFileName,\r\n" + 
				"	selfreport.reportLocation,\r\n" + 
				"	spscore.DISTRICTSCORE distScore,\r\n" + 
				"	spscore.DISTRICTEXPLAIN distExplain,\r\n" + 
				"	dspecialists.SPORganization DspOrganization,\r\n" + 
				"	dspecialists.spname dspName,\r\n" + 
				"	dspecialists.SPPHONE dspPhone,\r\n" + 
				"	spscore.CITYSCORE cityScore,\r\n" + 
				"	spscore.CITYEXPLAIN cityExplain,\r\n" + 
				"	cspecialists.SPORganization CspOrganization,\r\n" + 
				"	cspecialists.spname cspName,\r\n" + 
				"	cspecialists.SPPHONE cspPhone,\r\n" + 
				"	spscore.PROSCORE pspScore,\r\n" + 
				"	spscore.PROEXPLAIN pspExplain,\r\n" + 
				"	psp1.SPORganization PspOrganization,\r\n" + 
				"	psp1.spname pspName,\r\n" + 
				"	psp1.SPPHONE pspPhone,\r\n" + 
				"	spscore.onSpotScore checkScore,\r\n" + 
				"	spscore.onSpotEXPLAIN checkExplain,\r\n" + 
				"	psp2.SPORganization checkspOrganization,\r\n" + 
				"	psp2.spname checkspName,\r\n" + 
				"	psp2.SPPHONE checkPhone\r\n" + 
				"FROM\r\n" + 
				"	(Acriterion a,\r\n" + 
				"	Bcriterion b,\r\n" + 
				"	Ccriterion c,\r\n" + 
				"	spscore)\r\n" + 
				"LEFT JOIN selfreport ON selfreport.PID = ? AND selfreport.sid = ?\r\n" + 
				"LEFT JOIN dspecialists ON dspecialists.DSPID = spscore.DSPID \r\n" + 
				"LEFT JOIN cspecialists ON cspecialists.CSPID = spscore.CSPID \r\n" + 
				"LEFT JOIN pspecialists psp1 ON psp1.PSPID = spscore.PSPID \r\n" + 
				"LEFT JOIN pspecialists psp2 ON psp2.PSPID = spscore.onSpotID\r\n" + 
				"WHERE\r\n" + 
				"	a.pid = ?\r\n" + 
				"	AND a.aid = b.aid\r\n" + 
				"	AND b.bid = c.bid\r\n" + 
				"	AND c.cid = spscore.cid\r\n" + 
				"	AND spscore.sid = ?"
				+ " ORDER BY c.cid ASC";
		return template.query(sql, new BeanPropertyRowMapper<Archives>(Archives.class), pid, sid, pid, sid);
	}

	public List<Archives> findArchiveCByPidSid(String pid, String sid) {
		String sql = "SELECT * FROM archives WHERE pname = ? AND schoolName=?";
		return template.query(sql, new BeanPropertyRowMapper<Archives>(Archives.class), pid, sid);
	}

	public Map<String, Object> findAllPathSchoolBySid(String sid) {
		String sql = "SELECT "
				+ "		schools.sname schoolName,  "
				+ "		districts.dname distName,  "
				+ "		cities.cname cityName"
				+ "	  FROM "
				+ "		schools, districts, cities "
				+ "	  WHERE "
				+ "	 	schools.sid = ? "
				+ "		AND schools.did = districts.did "
				+ "		AND districts.cid = cities.cid "
				+ "	  LIMIT 1";
		return template.queryForMap(sql, sid);
	}

	public Map<String, Object> findAllPathSchoolByPnameSname(String pname, String sid) {
		String sql = "SELECT "
				+ "		cityName,"
				+ "		distName,"
				+ "		schoolName "
				+ "	  FROM "
				+ "		archives"
				+ "	  WHERE"
				+ "		pname =? "
				+ "		AND schoolName = ? "
				+ "	  LIMIT 1";
		return template.queryForMap(sql, pname, sid);
	}
	
}
