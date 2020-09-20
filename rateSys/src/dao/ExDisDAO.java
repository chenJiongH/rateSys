package dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import util.JDBCUtils;

public class ExDisDAO {

	JdbcTemplate template = JDBCUtils.getTemplate();
	
	public List<Map<String, Object>> findPGroupByMid(String mid, String pid) throws Exception{
		String sql = "SELECT PSPGID gid,SPGname gname FROM PSPgroup,projects WHERE MID=? AND projects.PID = pspgroup.PID AND projects.pid=?";
		return template.queryForList(sql, mid, pid);
	}

	public List<Map<String, Object>> findCGroupByMid(String mid, String pid) throws Exception{
		String sql = "SELECT CSPGID gid,SPGname gname FROM CSPgroup,projects WHERE MID=? AND projects.PID = cspgroup.PID AND projects.pid=?";
		return template.queryForList(sql, mid, pid);
	}
	
	public List<Map<String, Object>> findDGroupByMid(String mid, String pid) throws Exception{
		String sql = "SELECT DSPGID gid,SPGname gname FROM DSPgroup,projects WHERE MID=? AND projects.PID = dspgroup.PID AND projects.pid=?";
		return template.queryForList(sql, mid, pid);
	}

	public List<Map<String, Object>> findPMemberByGid(String gid) throws Exception{
		String sql = "SELECT Pspecialists.PSPID spid,SPname spname FROM Pspecialists,PSPGmember WHERE Pspecialists.PSPID = PSPGmember.PSPID AND PSPGmember.PSPGID=?";
		return template.queryForList(sql, gid);
	}

	public List<Map<String, Object>> findCMemberByGid(String gid) throws Exception{
		String sql = "SELECT Cspecialists.CSPID spid,SPname spname FROM Cspecialists,CSPGmember WHERE Cspecialists.cSPID = CSPGmember.CSPID AND CSPGmember.CSPGID=?";
		return template.queryForList(sql, gid);
	}
	
	public List<Map<String, Object>> findDMemberByGid(String gid) throws Exception{
		String sql = "SELECT Dspecialists.DSPID spid,SPname spname FROM Dspecialists,DSPGmember WHERE Dspecialists.DSPID = DSPGmember.DSPID AND DSPGmember.DSPGID=?";
		return template.queryForList(sql, gid);
	}

	public List<Map<String, Object>> findPIndex(String gid) throws Exception{
		String sql = "SELECT\r\n" + 
				"	C.CID cid,\r\n" + 
				"	C.CNAME cname,\r\n" + 
				"	B.BNAME bname,\r\n" + 
				"	A.ANAME aname,\r\n" + 
				"	A.AID aid,\r\n" + 
				"	B.BID bid,\r\n" + 
				"	inx.pspid spid\r\n" + 
				"FROM\r\n" + 
				"	( PSPgroup spg, Projects, Acriterion A, Bcriterion B, Ccriterion C )\r\n" + 
				"	LEFT JOIN pspginx inx ON spg.PSPGID = inx.pspgid \r\n" + 
				"	AND inx.CID = C.cid \r\n" + 
				"WHERE\r\n" + 
				"	spg.PSPGID = ? \r\n" + 
				"	AND spg.PID = projects.PID \r\n" + 
				"	AND A.PID = projects.PID \r\n" + 
				"	AND B.AID = A.AID \r\n" + 
				"	AND C.BID = B.BID";
		return template.queryForList(sql, gid);
	}
	
	public List<Map<String, Object>> findCIndex(String gid) throws Exception{
		String sql = "SELECT\r\n" + 
				"	C.CID cid,\r\n" + 
				"	C.CNAME cname,\r\n" + 
				"	B.BNAME bname,\r\n" + 
				"	A.ANAME aname,\r\n" + 
				"	A.AID aid,\r\n" + 
				"	B.BID bid,\r\n" + 
				"	inx.cspid spid \r\n" + 
				"FROM\r\n" + 
				"	( CSPgroup spg, Projects, Acriterion A, Bcriterion B, Ccriterion C )\r\n" + 
				"	LEFT JOIN cspginx inx ON spg.CSPGID = inx.cspgid \r\n" + 
				"	AND inx.CID = C.cid \r\n" + 
				"WHERE\r\n" + 
				"	spg.CSPGID = ? \r\n" + 
				"	AND spg.PID = projects.PID \r\n" + 
				"	AND A.PID = projects.PID \r\n" + 
				"	AND B.AID = A.AID \r\n" + 
				"	AND C.BID = B.BID";
		return template.queryForList(sql, gid);
	}
	
	public List<Map<String, Object>> findDIndex(String gid) throws Exception{
		String sql = "SELECT\r\n" + 
				"	C.CID cid,\r\n" + 
				"	C.CNAME cname,\r\n" + 
				"	B.BNAME bname,\r\n" + 
				"	A.ANAME aname,\r\n" + 
				"	A.AID aid,\r\n" + 
				"	B.BID bid,\r\n" + 
				"	inx.dspid spid\r\n" + 
				"FROM\r\n" + 
				"	( DSPgroup spg, Projects, Acriterion A, Bcriterion B, Ccriterion C )\r\n" + 
				"	LEFT JOIN dspginx inx ON spg.DSPGID = inx.dspgid \r\n" + 
				"	AND inx.CID = C.cid \r\n" + 
				"WHERE\r\n" + 
				"	spg.DSPGID = ? \r\n" + 
				"	AND spg.PID = projects.PID \r\n" + 
				"	AND A.PID = projects.PID \r\n" + 
				"	AND B.AID = A.AID \r\n" + 
				"	AND C.BID = B.BID";
		return template.queryForList(sql, gid);
	}

	public List<Map<String, Object>> findPMI(String spid, String gid) throws Exception{
		String sql = "SELECT CID cid FROM pspginx WHERE PSPID=? AND PSPGid=?";
		return template.queryForList(sql, spid, gid);
	}

	public List<Map<String, Object>> findCMI(String spid, String gid) throws Exception{
		String sql = "SELECT CID cid FROM cspginx WHERE cSPID=? AND CSPGID=?";
		return template.queryForList(sql, spid, gid);
	}

	public List<Map<String, Object>> findDMI(String spid, String gid) throws Exception{
		String sql = "SELECT CID cid FROM dspginx WHERE dSPID=? AND DSPGID=?";
		return template.queryForList(sql, spid, gid);
	}

	public void addPMemIndex(String gid, String spid, String mid, String cid) throws Exception{
		String sql = "INSERT INTO pspginx VALUES(?,?,?,?)";
		template.update(sql, gid, spid, cid, mid);
	}

	public void addCMemIndex(String gid, String spid, String mid, String cid) throws Exception{
		String sql = "INSERT INTO cspginx VALUES(?,?,?,?)";
		template.update(sql, gid, spid, cid, mid);
	}

	public void addDMemIndex(String gid, String spid, String mid, String cid) throws Exception{
		String sql = "INSERT INTO dspginx VALUES(?,?,?,?)";
		template.update(sql, gid, spid, cid, mid);
	}

	public void delPMemIndex(String gid, String spid, String mid, String cid) throws Exception{
		String sql = "DELETE FROM pspginx WHERE PSPGID=? AND PSPID=? AND CID=? AND mid=?";
		template.update(sql, gid, spid, cid, mid);
	}

	public void delCMemIndex(String gid, String spid, String mid, String cid) throws Exception{
		String sql = "DELETE FROM cspginx WHERE cSPGID=? AND cSPID=? AND CID=? AND mid=?";
		template.update(sql, gid, spid, cid, mid);
	}

	public void delDMemIndex(String gid, String spid, String mid, String cid) throws Exception{
		String sql = "DELETE FROM dspginx WHERE dSPGID=? AND dSPID=? AND CID=? AND mid=?";
		template.update(sql, gid, spid, cid, mid);
	}

	
}
