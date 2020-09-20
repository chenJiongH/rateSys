package dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import domain.PSPGmember;
import domain.PSPgroup;
import domain.PSpecialists;
import util.JDBCUtils;


public class AddMemberDao {

	JdbcTemplate template = JDBCUtils.getTemplate();

	public Collection<? extends PSPGmember> findMemberByPGid(String pspgid) {
		String sql = "SELECT * FROM PSPGmember WHERE PSPGID=?";
		return template.query(sql, new BeanPropertyRowMapper<PSPGmember>(PSPGmember.class), pspgid);
	}
	
	public Collection<? extends PSPGmember> findMemberByCGid(String pspgid) {
		String sql = "SELECT CSPID as PSPID,CSPGID as PSPGID,isleader FROM CSPGmember WHERE CSPgID=?";
		return template.query(sql, new BeanPropertyRowMapper<PSPGmember>(PSPGmember.class), pspgid);
	}
	
	public Collection<? extends PSPGmember> findMemberByDGid(String pspgid) {
		String sql = "SELECT DSPID as PSPID,DSPGID as PSPGID,isleader FROM DSPGmember WHERE DSPgID=?";
		return template.query(sql, new BeanPropertyRowMapper<PSPGmember>(PSPGmember.class), pspgid);
	}

	public List<PSpecialists> findPeoplePByMid(String mid) {
		String sql = "SELECT * FROM PSpecialists WHERE mid=?";
		return template.query(sql, new BeanPropertyRowMapper<PSpecialists>(PSpecialists.class), mid);
	}
	
	public List<PSpecialists> findPeopleCByMid(String mid) {
		String sql = "SELECT CSPID as PSPID,SPname,SPorganization FROM CSpecialists WHERE mid=?";
		return template.query(sql, new BeanPropertyRowMapper<PSpecialists>(PSpecialists.class), mid);
	}
	
	public List<PSpecialists> findPeopleDByMid(String mid) {
		String sql = "SELECT DSPID as PSPID,SPname,SPorganization FROM DSpecialists WHERE mid=?";
		return template.query(sql, new BeanPropertyRowMapper<PSpecialists>(PSpecialists.class), mid);
	}

	public void delOnePByPspid(String pspid, String pspgid) throws Exception{
		String sql = "DELETE FROM PSPGmember WHERE PSPID=? AND PSPGID=?";
		template.update(sql, pspid, pspgid);
	}

	public void delOneCByCspid(String pspid, String pspgid) throws Exception{
		String sql = "DELETE FROM CSPGmember WHERE CSPID=? AND CSPGID=?";
		template.update(sql, pspid, pspgid);
	}
	
	public void delOneDByDspid(String pspid, String pspgid) throws Exception{
		String sql = "DELETE FROM DSPGmember WHERE DSPID=? AND DSPgid=?";
		template.update(sql, pspid, pspgid);
	}
	//修改某个专家组的所有组长项为当前组长编号
	public void changeOnePLeader(String pspid, String pspgid) throws Exception{
		String sql = "UPDATE PSPGmember SET IsLeader=? WHERE PSPGID=?";
		template.update(sql, pspid, pspgid);
	}
	
	public void changeOneCLeader(String pspid, String pspgid) throws Exception{
		String sql = "UPDATE CSPGmember SET IsLeader=? WHERE CSPGID=?";
		template.update(sql, pspid, pspgid);
	}
	
	public void changeOneDLeader(String pspid, String pspgid) throws Exception{
		String sql = "UPDATE DSPGmember SET IsLeader=? WHERE DSPGID=?";
		template.update(sql, pspid, pspgid);
	}

	public void addDataByPid(String pspid, String pspgid, String isleader) throws Exception {
		String sql = "INSERT INTO PSPGmember values(?,?,?,?)";
		template.update(sql, pspid, pspgid, isleader, 0);
	}
	public void addDataByCid(String pspid, String pspgid, String isleader) throws Exception {
		String sql = "INSERT INTO CSPGmember values(?,?,?,?)";
		template.update(sql, pspid, pspgid, isleader, 0);
	}
	public void addDataByDid(String pspid, String pspgid, String isleader) throws Exception {
		String sql = "INSERT INTO DSPGmember values(?,?,?,?)";
		template.update(sql, pspid, pspgid, isleader, 0);
	}
	
	public List<PSPgroup> findGroupPByMid(String mid, String pname) {
		String sql = "SELECT PSPgroup.*,projects.pname FROM PSPgroup,projects WHERE mid=? AND projects.pid = pspgroup.pid";
		if(pname != null && !"".equals(pname))
			sql += " AND projects.pname LIKE '%" + pname + "%'";
		return template.query(sql, new BeanPropertyRowMapper<PSPgroup>(PSPgroup.class), mid);
	}
	
	public List<PSPgroup> findGroupCByMid(String mid, String pname) {
		String sql = "SELECT CSPGID as PSPGID,SPGname,CSPgroup.PID,MID,projects.pname  FROM CSPgroup,projects WHERE mid=? AND projects.pid = Cspgroup.pid";
		if(pname != null && !"".equals(pname))
			sql += " AND projects.pname LIKE '%" + pname + "%'";
		return template.query(sql, new BeanPropertyRowMapper<PSPgroup>(PSPgroup.class), mid);
	}
	
	public List<PSPgroup> findGroupDByMid(String mid, String pname) {
		String sql = "SELECT DSPGID as PSPGID,SPGname,DSPgroup.PID,MID,projects.pname  FROM DSPgroup,projects WHERE mid=? AND projects.pid = Dspgroup.pid";
		if(pname != null && !"".equals(pname))
			sql += " AND projects.pname LIKE '%" + pname + "%'";
		return template.query(sql, new BeanPropertyRowMapper<PSPgroup>(PSPgroup.class), mid);
	}

	
	public List<PSPgroup> findGroupPByMidGid(String mid, String pspgid, String pname) {
		String sql = "SELECT PSPgroup.*,projects.pname  FROM PSPgroup,projects WHERE mid=? AND projects.pid = pspgroup.pid AND SPGname like '%" + pspgid + "%'";
		if(pname != null && !"".equals(pname))
			sql += " AND projects.pname LIKE '%" + pname + "%'";
		return template.query(sql, new BeanPropertyRowMapper<PSPgroup>(PSPgroup.class), mid);
	}
	
	public List<PSPgroup> findGroupCByMidGid(String mid, String pspgid, String pname) {
		String sql = "SELECT CSPGID as PSPGID,SPGname,CSPgroup.PID,MID,projects.pname  FROM CSPgroup,projects WHERE mid=? AND projects.pid = Cspgroup.pid AND SPGname like '%" + pspgid + "%'";
		if(pname != null && !"".equals(pname))
			sql += " AND projects.pname LIKE '%" + pname + "%'";
		return template.query(sql, new BeanPropertyRowMapper<PSPgroup>(PSPgroup.class), mid);
	}
	
	public List<PSPgroup> findGroupDByMidGid(String mid, String pspgid, String pname) {
		String sql = "SELECT DSPGID as PSPGID,SPGname,DSPgroup.PID,MID,projects.pname  FROM DSPgroup,projects WHERE mid=? AND projects.pid = Dspgroup.pid AND SPGname like '%" + pspgid + "%'";
		if(pname != null && !"".equals(pname))
			sql += " AND projects.pname LIKE '%" + pname + "%'";
		return template.query(sql, new BeanPropertyRowMapper<PSPgroup>(PSPgroup.class), mid);
	}

	public Collection<? extends PSPGmember> findMemberByPGidPid(String pspgid, String pspid) {
		String sql = "SELECT * FROM PSPGmember WHERE PSPGID=? AND pspid=?";
		return template.query(sql, new BeanPropertyRowMapper<PSPGmember>(PSPGmember.class), pspgid, pspid);
	}

	public Collection<? extends PSPGmember> findMemberByCGidPid(String pspgid, String pspid) {
		String sql = "SELECT CSPID as PSPID,CSPGID as PSPGID,isleader FROM CSPGmember WHERE CSPGID=? AND CSPID=?";
		return template.query(sql, new BeanPropertyRowMapper<PSPGmember>(PSPGmember.class), pspgid, pspid);
	}

	public Collection<? extends PSPGmember> findMemberByDGidPid(String pspgid, String pspid) {
		String sql = "SELECT DSPID as PSPID,DSPGID as PSPGID,isleader FROM DSPGmember WHERE DSPGID=? AND DSPID=?";
		return template.query(sql, new BeanPropertyRowMapper<PSPGmember>(PSPGmember.class), pspgid, pspid);
	}
	
	public List<Map<String, Object>> findAllMemberPByMid(String mid) {
		String sql = "SELECT \r\n" + 
				"	spname,\r\n" + 
				"	sporganization,\r\n" + 
				"	pinyin\r\n" + 
				"FROM\r\n" + 
				"	PSpecialists\r\n" + 
				"WHERE\r\n" + 
				"	mid = ?";
		return template.queryForList(sql, mid);
	}
	

	public List<Map<String, Object>> findAllMemberCByMid(String mid) {
		String sql = "SELECT \r\n" + 
				"	spname,\r\n" + 
				"	sporganization,\r\n" + 
				"	pinyin\r\n" + 
				"FROM\r\n" + 
				"	CSpecialists\r\n" + 
				"WHERE\r\n" + 
				"	mid = ?";
		return template.queryForList(sql, mid);
	}
	

	public List<Map<String, Object>> findAllMemberDByMid(String mid) {
		String sql = "SELECT \r\n" + 
				"	spname,\r\n" + 
				"	sporganization,\r\n" + 
				"	pinyin\r\n" + 
				"FROM\r\n" + 
				"	DSpecialists\r\n" + 
				"WHERE\r\n" + 
				"	mid = ?";
		return template.queryForList(sql, mid);
	}

	public void delCByPSpid(String pspid, String pspgid) throws Exception{
		String sql = "DELETE FROM ";
		if(pspid.charAt(0) == 'P') {
			sql += "pspginx WHERE PSPID=? AND PSPGID=?"; 
		}
		else if(pspid.charAt(0) == 'C') {
			sql += "cspginx  WHERE CSPID=? AND CSPGID=?"; 
		}
		else if(pspid.charAt(0) == 'D') {
			sql += "dspginx WHERE DSPID=? AND DSPGID=?"; 
		}
		template.update(sql, pspid, pspgid);
	}

	public Map<String, Object> findOneProjectByPSPGid(String pspgid) {
		String sql = "SELECT pprocess, Projects.pid FROM PSPgroup, Projects WHERE pspgid=? AND pspgroup.PID = projects.pid LIMIT 1";
		return template.queryForMap(sql, pspgid);
	} 

	public Map<String, Object> findOneProjectByCSPGid(String pspgid) {
		String sql = "SELECT pprocess, Projects.pid FROM CSPgroup, Projects WHERE cspgid=? AND cspgroup.PID = projects.pid LIMIT 1";
		return template.queryForMap(sql, pspgid);
	}

	public Map<String, Object> findOneProjectByDSPGid(String pspgid) {
		String sql = "SELECT pprocess, Projects.pid FROM DSPgroup, Projects WHERE dspgid=? AND dspgroup.PID = projects.pid LIMIT 1";
		return template.queryForMap(sql,  pspgid);
	}
	
	public int findSpotOrProvinByPSpgid(String pspgid) {
		String sql = "SELECT isOnSpot FROM PSPgroup WHERE PSPGID=? LIMIT 1";
		return template.queryForObject(sql, Integer.class, pspgid);
	}
	
	public Map<? extends String, ? extends Object> findMultiMidByDSpid(String pspid) throws Exception {
		String sql = "SELECT \r\n" + 
				"  mana.mid dmid,\r\n" + 
				"	mana1.mid cmid\r\n" + 
				"FROM \r\n" + 
				"	(DSpecialists dsp,\r\n" + 
				"	managers mana,\r\n" + 
				"	districts dist)\r\n" + 
				"	LEFT JOIN managers mana1 ON mana1.TID = dist.CID\r\n" + 
				"WHERE\r\n" + 
				"	dsp.DSPID = ?\r\n" + 
				"	AND mana.mid = dsp.MID\r\n" + 
				"	AND mana.tid = dist.DID\r\n" + 
				"	LIMIT 1";
		return template.queryForMap(sql, pspid);
	}

	public Map<? extends String, ? extends Object> findMultiMidByCSpid(String pspid) throws Exception {
		String sql = "SELECT \r\n" + 
				"  csp.mid cmid\r\n" + 
				"FROM \r\n" + 
				"	Cspecialists csp\r\n" + 
				"WHERE\r\n" + 
				"	csp.CSPID = ?\r\n" + 
				"	LIMIT 1";
		return template.queryForMap(sql, pspid);
	}

	public void addPspidPCM(String pid, String pspid, int spotFlag) throws Exception {
		String sql = "INSERT INTO pcm(pid, sid, nowRate, commState,pmid) VALUES(?,?";
		if(spotFlag == 1) 
			sql += ",'抽查'";
		else sql += ",'省'";
		sql += ",'未提交','M00001')";
		template.update(sql, pid, pspid);
	}

	public void addCspidPCM(String pid, String pspid, Map<String, Object> pcmMap, List<String> rateNum,
			Map<String, String> strRateMap) throws Exception {
		StringBuilder sql = new StringBuilder("INSERT INTO pcm(pid, sid, nowRate, commState");
		// 校 -> sspid
		for (String nowRate : rateNum) {
			//市级专家，校、县管理员id为空
			if("校".equals(nowRate) || "县".equals(nowRate)) {
				continue ;
			}
			sql.append(", " + strRateMap.get(nowRate));
		}
		sql.append(") VALUES('" + pid + "', '" + pspid + "', '市', '未提交'");
		for (String nowRate : rateNum) {
			if("校".equals(nowRate) || "县".equals(nowRate)) {
				continue ;
			}
			sql.append(", '" + pcmMap.get(strRateMap.get(nowRate)) + "'");
		}
		sql.append(")");
		template.update(sql.toString());
	}
	
	public void addDspidPCM(String pid, String pspid, Map<String, Object> pcmMap, List<String> rateNum,
			Map<String, String> strRateMap) throws Exception {
		StringBuilder sql = new StringBuilder("INSERT INTO pcm(pid, sid, nowRate, commState");
		// 校 -> sspid
		System.out.println(rateNum);
		for (String nowRate : rateNum) {
			//市级专家，校管理员id为空
			if("校".equals(nowRate) ) {
				continue ;
			}
			sql.append(", " + strRateMap.get(nowRate));
		}
		sql.append(") VALUES('" + pid + "', '" + pspid + "', '县', '未提交'");
		for (String nowRate : rateNum) {
			if("校".equals(nowRate)) {
				continue ;
			}
			sql.append(", '" + pcmMap.get(strRateMap.get(nowRate)) + "'");
		}
		sql.append(")");
		System.out.println(sql.toString());
		template.update(sql.toString());
	}

	public void delOnePcmByPidSpid(String pspid, String pid) {
		String sql = "DELETE FROM pcm WHERE pid=? AND sid=? LIMIT 1";
		template.update(sql, pid, pspid);
	}

	public List<PSPgroup> findGroupPByMidPid(String mid, String pid) throws Throwable {
		String sql = "SELECT PSPgroup.*,projects.pname FROM PSPgroup,projects WHERE mid=? AND projects.pid = pspgroup.pid AND projects.pid=?";
		return template.query(sql, new BeanPropertyRowMapper<PSPgroup>(PSPgroup.class), mid, pid);
	}

	public List<PSPgroup> findGroupCByMidPid(String mid, String pid) throws Throwable  {
		String sql = "SELECT CSPGID as PSPGID,SPGname,CSPgroup.PID,MID,projects.pname  FROM CSPgroup,projects WHERE mid=? AND projects.pid = Cspgroup.pid AND projects.pid=?";
		return template.query(sql, new BeanPropertyRowMapper<PSPgroup>(PSPgroup.class), mid, pid);
	}

	public List<PSPgroup> findGroupDByMidPid(String mid, String pid) throws Throwable  {
		String sql = "SELECT DSPGID as PSPGID,SPGname,DSPgroup.PID,MID,projects.pname  FROM DSPgroup,projects WHERE mid=? AND projects.pid = Dspgroup.pid AND projects.pid=?";
		return template.query(sql, new BeanPropertyRowMapper<PSPgroup>(PSPgroup.class), mid, pid);
	}
}
