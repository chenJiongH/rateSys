package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import domain.CCS;
import domain.Cities;
import domain.CityCountySchool;
import domain.Districts;
import domain.Schools;
import util.JDBCUtils;

public class CCSDao {

	JdbcTemplate template = JDBCUtils.getTemplate();

//	@Test
	public CCS findCCS() throws SQLException {
//		System.out.println("CCSDao运行了");
//		JdbcTemplate template = new JdbcTemplate(JDBCUtils.getDataSource());
		// 开启事务管理，当事务管理开启时，template每次获取的都是同一个连接，此时设置连接手动提交则有效
//		TransactionSynchronizationManager.initSynchronization();
//		Connection conn = null;
		CCS ccs = null;
		try {
//			conn = template.getDataSource().getConnection();
//			conn.setAutoCommit(false);
			ccs = new CCS();
			String sql = "select * from cities";
			List<Map<String, Object>> cities = template.queryForList(sql);
			ccs.setCity(cities);
			sql = "select * from districts";
			List<Map<String, Object>> dist = template.queryForList(sql);
			ccs.setDist(dist);
			sql = "select * from schools";
			List<Map<String, Object>> sch = template.queryForList(sql);
			ccs.setSch(sch);

//			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			conn.setAutoCommit(true);
//			TransactionSynchronizationManager.clearSynchronization();
		}
		return ccs;
	}

	public List<Cities> findCities() {
		List<Cities> cs = new ArrayList<Cities>();
		String sql = "select * from cities";
		return template.query(sql, new BeanPropertyRowMapper<Cities>(Cities.class));
	}

	public List<Districts> findDistByCid(String cid) {
		List<Districts> districts = new ArrayList<Districts>();
		String sql = "select * from districts where cid=?";
		return template.query(sql, new BeanPropertyRowMapper<Districts>(Districts.class), cid);
	}

	public List<Schools> findSchBydid(String did, String nowTime) {
		List<Schools> districts = new ArrayList<Schools>();
		String sql = "SELECT\r\n" + "	sch.*\r\n" + "FROM\r\n" + "	schools sch\r\n"
				+ "	LEFT JOIN pspgsch psch ON psch.sid = sch.sid\r\n"
				+ "	LEFT JOIN projects pr ON pr.pid = psch.pid\r\n" + "WHERE\r\n" + "	did = ?\r\n"
				+ "	AND (pr.petime < ? OR pr.petime IS NULL )";
		return template.query(sql, new BeanPropertyRowMapper<Schools>(Schools.class), did, nowTime);
	}

	public Cities findOndCityByCname(String cname) throws Exception {
		String sql = "select * from cities where cname=?";
		return template.queryForObject(sql, new BeanPropertyRowMapper<Cities>(Cities.class), cname);
	}

	public Districts findOndDistByDname(String distname, String cid) throws Exception {
		String sql = "select * from districts where dname=? and cid=?";
		return template.queryForObject(sql, new BeanPropertyRowMapper<Districts>(Districts.class), distname, cid);
	}

	public Schools findOneSchBySname(String schname, String did) throws Exception {
		String sql = "select * from schools where sname=? and did=?";
		return template.queryForObject(sql, new BeanPropertyRowMapper<Schools>(Schools.class), schname, did);
	}

	public List<Districts> findAllDist() throws Exception {
		String sql = "select * from Districts";
		return template.query(sql, new BeanPropertyRowMapper<Districts>(Districts.class));
	}

	public List<Schools> findAllSch() throws Exception {
		String sql = "select * from schools";
		return template.query(sql, new BeanPropertyRowMapper<Schools>(Schools.class));
	}

	public Cities findOneCByCid(String tid) throws Exception {
		String sql = "select * from cities where cid=? LIMIT 1";
		return template.queryForObject(sql, new BeanPropertyRowMapper<Cities>(Cities.class), tid);
	}

	public Districts findOneDByCid(String tid) throws Exception {
		String sql = "select * from Districts where did=? LIMIT 1";
		return template.queryForObject(sql, new BeanPropertyRowMapper<Districts>(Districts.class), tid);
	}

	public Schools findOneSByCid(String tid) throws Exception {
		String sql = "select * from Schools where sid=? LIMIT 1";
		return template.queryForObject(sql, new BeanPropertyRowMapper<Schools>(Schools.class), tid);
	}

	public List<Map<String, Object>> getDSByCname(String cname) {
		String sql = "SELECT * from cdsview WHERE cname = ?";
		return template.queryForList(sql, cname);
	}

	public List<Map<String, Object>> findAllCity() {
		String sql = "select * from cities";
		return template.queryForList(sql);
	}

	public List<Map<String, Object>> findDistByCidNow(String cid) {
		String sql = "select Districts.* from Cities,Districts where Cities.cid=Districts.cid AND Cities.cname=?";
		return template.queryForList(sql, cid);
	}

	public List<Map<String, Object>> findDistByCidNow2(String cid) {
		String sql = "select Districts.* from Cities,Districts where Cities.cid=Districts.cid AND Cities.cid=?";
		return template.queryForList(sql, cid);
	}

	public List<Map<String, Object>> findSchByDid(String dname, String cname) {
		String sql = "select cdsview.SNAME from cdsview where dname=? AND cname=?";
		return template.queryForList(sql, dname, cname);
	}

	public List<Map<String, Object>> findSchByDid(String did) {
		String sql = "select managers.*,cdsview.sid,cdsview.cname,cdsview.dname,cdsview.sname from cdsview,managers where cdsview.did=? AND cdsview.sid=managers.tid";
		return template.queryForList(sql, did);
	}

	public Map findCDSNameBySid(String tid) throws Exception {
		String sql = "SELECT\r\n" + "	cdsview.*\r\n" + "FROM\r\n" + "	cdsview\r\n" + "WHERE\r\n"
				+ "	cdsview.sid = ?\r\n" + "	LIMIT 1";
		return template.queryForMap(sql, tid);
	}

	public Map findCDSNameByDid(String tid) {
		String sql = "SELECT\r\n" + "	cdsview.*\r\n" + "FROM\r\n" + "	cdsview\r\n" + "WHERE\r\n"
				+ "	cdsview.did = ?\r\n" + "	LIMIT 1";
		return template.queryForMap(sql, tid);
	}

	public Map findCDSNameByCid(String tid) {
		String sql = "SELECT\r\n" + "	cdsview.*\r\n" + "FROM\r\n" + "	cdsview\r\n" + "WHERE\r\n"
				+ "	cdsview.cid = ?\r\n" + "	LIMIT 1";
		return template.queryForMap(sql, tid);
	}

	public Collection<? extends Map<String, Object>> findSchByFuzzySchoolName(String schoolName) {
		String sql = "select managers.*,cdsview.sid,cdsview.cname,cdsview.dname,cdsview.sname from cdsview,managers where cdsview.sname like '%" + schoolName + "%' AND cdsview.sid=managers.tid";
		return template.queryForList(sql);
	}
}
