package dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import util.JDBCUtils;

public class TaskMDAO {

	JdbcTemplate template = JDBCUtils.getTemplate();
	
	public List<Map<String, Object>> findPSpgByMid(String mid) {
		 String sql = "SELECT PSPGID as spgid,SPGname,Projects.PID,Projects.Pname,MID FROM PSPgroup,Projects WHERE mid=? AND pSPgroup.pid=projects.pid";
		 return template.queryForList(sql, mid);
	}

	public List<Map<String, Object>> findCSpgByMid(String mid) {
		 String sql = "SELECT CSPGID as spgid,SPGname,Projects.PID,Projects.Pname,MID FROM CSPgroup,Projects WHERE mid=? AND CSPgroup.pid=projects.pid";
		 return template.queryForList(sql, mid);
	}

	public List<Map<String, Object>> findDSpgByMid(String mid) {
		 String sql = "SELECT DSPGID as spgid,SPGname,Projects.PID,Projects.Pname,MID FROM DSPgroup,Projects WHERE mid=? AND dSPgroup.pid=projects.pid";
		 return template.queryForList(sql, mid);
	}

	public List<Map<String, Object>> findPSpgsch(String tid, String pid) {
		System.out.println(pid);
		String sql = "SELECT  \r\n" + 
				"		pj.PID, pj.PNAME, sch.SID, sch.SNAME, sch.DID \r\n" + 
				"FROM  \r\n" + 
				"		pspgsch spgsch, Schools sch, Projects pj \r\n" + 
				"WHERE  \r\n" + 
				"		spgsch.SID = sch.SID AND  spgsch.PID = ? AND pj.pid = spgsch.pid";
		return template.queryForList(sql, pid);
	}
			
	public List<Map<String, Object>> findCSpgsch(String tid, String gid) {
		String sql = "SELECT \r\n" + 
				"		pj.PID, pj.PNAME, spg.cSPGID spgid, spg.SPGNAME, sch.SID, sch.SNAME, sch.DID\r\n" + 
				"FROM \r\n" + 
				"		cspgsch spgsch, cSPgroup spg, Schools sch, Projects pj\r\n" + 
				"WHERE \r\n" + 
				"		spgsch.cSPGID = spg.cSPGID AND spgsch.SID = sch.SID AND spg.PID = pj.PID"
					+ " AND spgsch.CSPGID=?";
		return template.queryForList(sql, gid);
	}
	
	public List<Map<String, Object>> findDSpgsch(String tid, String gid) {
		String sql = "SELECT \r\n" + 
				"		pj.PID, pj.PNAME, spg.dSPGID spgid, spg.SPGNAME, sch.SID, sch.SNAME, sch.DID\r\n" + 
				"FROM \r\n" + 
				"		dspgsch spgsch, dSPgroup spg, Schools sch, Projects pj\r\n" + 
				"WHERE \r\n" + 
				"		spgsch.dSPGID = spg.dSPGID AND spgsch.SID = sch.SID AND spg.PID = pj.PID"
					+ " AND spgsch.DSPGID=?";
		return template.queryForList(sql, gid);
	}

	public void addPSpgsch(String pid, String sid, String mid) throws Exception{
		System.out.println(pid + " : " + sid + " : " + mid);
		String sql = "INSERT INTO pspgsch VALUES(?,?,?,0)";
		template.update(sql, pid, sid, mid);
	}
	
	public void addCSpgsch(String gid, String sid, String mid) throws Exception{
		String sql = "INSERT INTO cspgsch VALUES(?,?,?,0)";
		template.update(sql, gid, sid, mid);
	}
	
	public void addDSpgsch(String gid, String sid, String mid) throws Exception{
		String sql = "INSERT INTO dspgsch VALUES(?,?,?,0)";
		template.update(sql, gid, sid, mid);
	}

	public void delPSpgsch(String gid, String sid, String mid) throws Exception{
		String sql = "DELETE FROM pspgsch WHERE pid=? AND SID=? AND MID=? LIMIT 1";
		template.update(sql, gid, sid, mid);
	}
	
	public void delCSpgsch(String gid, String sid, String mid) throws Exception{
		String sql = "DELETE FROM cspgsch WHERE cspgid=? AND SID=? AND MID=?";
		template.update(sql, gid, sid, mid);
	}
	
	public void delDSpgsch(String gid, String sid, String mid) throws Exception{
		String sql = "DELETE FROM dspgsch WHERE dspgid=? AND SID=? AND MID=?";
		template.update(sql, gid, sid, mid);
	}

	public List<Map<String, Object>> findProjects() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowTime = dateFormat.format(new Date());
		String sql = "SELECT pid, Pname FROM Projects WHERE petime>?";
		return template.queryForList(sql, nowTime);
	}
	/**
	 * 查找一个项目的流程
	 * @param pid
	 * @return
	 */
	public String findOneProjectByPid(String pid) {
		String sql = "SELECT Pprocess FROM Projects WHERE pid = ? LIMIT 1";
		return template.queryForObject(sql, String.class, pid);
	}

	public Map<String, Object> findMultiMidBySid(String sid) throws Exception{
		String sql = "SELECT \r\n" + 
				"	mana.mid smid,\r\n" + 
				"	mana1.mid dmid,\r\n" + 
				"	mana2.mid cmid\r\n" + 
				"FROM \r\n" + 
				"	(schools sch,\r\n" + 
				"	managers mana,\r\n" + 
				"	districts dist,\r\n" + 
				"	cities city)\r\n" + 
				"	LEFT JOIN managers mana1 ON mana1.TID = dist.DID\r\n" + 
				"	LEFT JOIN managers mana2 ON mana2.TID = city.CID\r\n" + 
				"WHERE\r\n" + 
				"	mana.tid = ?\r\n" + 
				"	AND mana.tid = sch.sid\r\n" + 
				"	AND sch.did = dist.did\r\n" + 
				"	AND dist.cid = city.cid\r\n" + 
				"	LIMIT 1";
		return template.queryForMap(sql, sid);
	}

	public void addPCM(String pid, Map<String, Object> pcmMap, String sid, List<String> rateNum, Map<String, String> strRateMap) throws Exception {
		StringBuilder sql = new StringBuilder("INSERT INTO pcm(pid, sid, nowRate, commState");
		// 校 -> sspid
		for (String nowRate : rateNum) {
			System.out.println(nowRate);
			sql.append(", " + strRateMap.get(nowRate));
		}
		sql.append(") VALUES('" + pid + "', '" + sid + "', '校', '未提交'");
		for (String nowRate : rateNum) 
			sql.append(", '" + pcmMap.get(strRateMap.get(nowRate)) + "'");
		sql.append(")");
		template.update(sql.toString());
	}

	public void delPCM(String pid, String sid) {
		String sql = "DELETE FROM pcm WHERE pid=? AND sid=? LIMIT 1";
		template.update(sql, pid, sid);
	}

	public void addDPCM(String pid, List<String> rateNum, Map<String, Object> pcmMap) throws Exception {
		try {
			String sql = "INSERT INTO pcm(pid, sid, nowrate, commState";
			//标记当前管理员是否是最后一个流程的管理员。如果是最后一个流程的管理员，则需要都关联省级管理员。
			int flag = 0;
			if(rateNum.contains("市")) {
				flag = 1;
				sql += ", cmid";
			}
			if(flag == 0 || rateNum.contains("省"))
				sql += ", pmid";
			sql += ") VALUES('" + pid + "', '" + pcmMap.get("dmid") + "', '校', '未提交'";
			if(rateNum.contains("市")) 
				sql += ", '" + pcmMap.get("cmid") + "'";
			if(flag == 0 || rateNum.contains("省"))
				sql += ", '" + pcmMap.get("pmid") + "'";
			sql += ")";
			template.update(sql);
		} catch(org.springframework.dao.DuplicateKeyException e) {
			
		}
	}

	public void addCPCM(String pid, List<String> rateNum, Map<String, Object> pcmMap) throws Exception {
		try {
			String sql = "INSERT INTO pcm(pid, sid, nowrate, commState";
			//如果最后一个流程是市，则该市级管理员记录仍然得关联到省级管理员
			sql += ", pmid";
			sql += ") VALUES('" + pid + "', '" + pcmMap.get("cmid") + "', '校', '未提交'";
			sql += ", '" + pcmMap.get("pmid") + "'";
			sql += ")";
			template.update(sql);
		} catch(org.springframework.dao.DuplicateKeyException e) {
			
		}
	}

	public void addPPCM(String pid, List<String> rateNum, Map<String, Object> pcmMap) throws Exception{
		try {
			String sql = "INSERT INTO pcm(pid, sid, nowrate, commState, pmid";
			sql += ") VALUES('" + pid + "', '" + pcmMap.get("pmid") + "', '省', '未提交', '" + pcmMap.get("pmid") + "'";
			sql += ")";
			template.update(sql);
		} catch(org.springframework.dao.DuplicateKeyException e) {
			
		}
	}

	public void delGroupSchool(String pid, String sid) {
		String sql = "DELETE FROM groupSchool WHERE pid=? AND SID=? LIMIT 1";
		template.update(sql, pid, sid);
	}
}
