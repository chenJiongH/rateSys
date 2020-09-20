package dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import domain.ProjectProcessFallbackPageBean;
import util.JDBCUtils;

public class ProjectProcessFallbackDao {

	JdbcTemplate template = JDBCUtils.getTemplate();

	public List<Map<String, Object>> findDPr(String tid, int startRecord) {
		String sql = "SELECT \r\n" + 
				"  DISTINCT pr.pid, pr.pname,pr.pprocess,pr.Petime,dist.DNAME, city.CNAME, dist.DID, city.CID\r\n" + 
				"FROM\r\n" + 
				"	DSpecialists sp,\r\n" + 
				"	Managers mana,\r\n"
				+ " CIties city,\r\n"
				+ " districts dist,\r\n" + 
				"	Schools sch,\r\n" + 
				"	pspgsch psch,\r\n" + 
				"	Projects pr\r\n" + 
				"WHERE\r\n" + 
				"	mana.TID = ?\r\n"
				+ " AND dist.DID = mana.TID\r\n"
				+ " AND city.CID = dist.CID\r\n" + 
				"	AND mana.TID = sch.DID\r\n" + 
				"	AND sch.SID = psch.SID\r\n" + 
				"	AND psch.PID = pr.PID\r\n" + 
				"	AND pr.PPROCESS LIKE '%县%'"
				+ " LIMIT ?,20";
		return template.queryForList(sql, tid, startRecord);
	}

	public List<Map<String, Object>> findCPr(String tid, int startRecord) {
		//查找该市管理员 的所有当前流程包含该市的项目
		String sql = "SELECT \r\n" + 
				"  DISTINCT pr.pid, pr.pname,pr.pprocess,pr.Petime, dist.DNAME, dist.DID, city.CNAME, city.CID\r\n" + 
				"FROM\r\n" + 
				"	Managers mana,\r\n"
				+ "	cities city,\r\n" + 
				"	Districts dist,\r\n" + 
				"	Schools sch,\r\n" + 
				"	pspgsch psch,\r\n" + 
				"	Projects pr\r\n" + 
				"WHERE\r\n" + 
				"	mana.TID = ?\r\n"
				+ " AND city.CID = dist.CID\r\n" + 
				"	AND dist.CID = mana.TID\r\n" + 
				"	AND dist.DID = sch.DID\r\n" + 
				"	AND sch.SID = psch.SID\r\n" + 
				"	AND psch.PID = pr.PID\r\n" + 
				"	AND pr.PPROCESS LIKE '%市%'"
				+ " LIMIT ?,20";
		return template.queryForList(sql, tid, startRecord);
	}
	
	/**
	 * 根据查找省专家 的所有包含省的项目
	 * @param spid
	 * @param nowTime
	 * @return
	 */
	public List<Map<String, Object>> findPPr(String tid, int startRecord) {
		String sql = "SELECT  \r\n" + 
					"	DISTINCT \r\n" + 
					"		pr.pid,  \r\n" + 
					"		pr.pname,  \r\n" + 
					"		pr.pprocess,\r\n" + 
					"		pr.Petime,\r\n" + 
					"		dist.DNAME,\r\n"
					+ "		city.cname,\r\n"
					+ "		dist.DID,\r\n"
					+ "		city.CID\r\n" + 
					"FROM  \r\n" + 
					"	Projects pr,\r\n"
					+ " cities city,\r\n" + 
					"	Districts dist,\r\n" + 
					"	Schools sch,\r\n" + 
					"	pspgsch \r\n" + 
					"WHERE \r\n" + 
					"	(pr.pprocess LIKE '%省%' \r\n" + 
					"	OR pr.pprocess LIKE '%抽查%')\r\n" + 
					"	AND pspgsch.sid = sch.sid \r\n" + 
					"	AND sch.did = dist.did\r\n" + 
					"	AND city.cid = dist.cid\r\n" + 
					"	AND pspgsch.pid = pr.pid"
					+ " LIMIT ?,20";
		return template.queryForList(sql,startRecord);
	}
	
	public Integer findDSchFlageByPid(String pid) {
		try {
			String sql = "SELECT\r\n" + 
					"	spgsch.flage\r\n" + 
					"FROM\r\n" + 
					"	pspgsch spgsch\r\n" +
					"WHERE\r\n" + 
					"	spgsch.pid = ?\r\n" +
					"	AND spgsch.flage != 11\r\n" + 
					"	LIMIT 1\r\n";
			return template.queryForObject(sql, Integer.class, pid);
		} catch(Exception e) {
			return null;
		}
	}

	public boolean checkDSchByPid(String pid) {
		try {
			String sql = "SELECT\r\n" + 
					"	spgsch.flage\r\n" + 
					"FROM\r\n" + 
					"	pspgsch spgsch\r\n" +
					"WHERE\r\n" + 
					"	spgsch.pid = ?\r\n" +
					"	LIMIT 1\r\n";
			template.queryForObject(sql, Integer.class, pid);
			return false;
		} catch(Exception e) {
			return true;
		}
	}
	
	public boolean checkAllDistByPid(String pid) {
		try {
			//根据项目id 并且市流程下为县 查找该项目下是否有未完成评分的县级专家，则该项目还不可被当前市专家所评分
			String sql = "SELECT\r\n" + 
					"	spm.DSPID\r\n" + 
					"FROM\r\n" + 
					"	DSPgroup spg,\r\n" + 
					"	DSPGmember spm\r\n" + 
					"WHERE\r\n" + 
					"	spg.pid = ?\r\n" + 
					"	AND spg.DSPGID = spm.DSPGID\r\n" + 
					"	AND spm.FLAGE = 0\r\n" + 
					"	Limit 1";
			template.queryForMap(sql, pid);
			return false;
		} catch (Exception e) {
			return true;
		}
	}
	
	/**
	 * 查看该项目是否已经被分配了县级专家
	 * @param pid
	 * @return
	 */
	public boolean checkAllDistByPid2(String pid) {
		try {
			//根据项目id 并且市流程下为县 查找该项目下是否有未完成评分的县级专家，则该项目还不可被当前市专家所评分
			String sql = "SELECT\r\n" + 
					"	spm.DSPID\r\n" + 
					"FROM\r\n" + 
					"	DSPgroup spg,\r\n" + 
					"	DSPGmember spm\r\n" + 
					"WHERE\r\n" + 
					"	spg.pid = ?\r\n" + 
					"	AND spg.DSPGID = spm.DSPGID\r\n" + 
					"	Limit 1";
			template.queryForMap(sql, pid);
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	public boolean checkAllProvice(String pid) {
		try {
			String sql = "SELECT\r\n" + 
					"	spm.PSPID\r\n" + 
					"FROM\r\n" + 
					"	PSPgroup spg,\r\n" + 
					"	PSPGmember spm\r\n" + 
					"WHERE\r\n" + 
					"	spg.pid = ?\r\n" + 
					"	AND spg.PSPGID = spm.PSPGID\r\n" + 
					"	AND spm.FLAGE = 0"
					+ " AND spg.IsOnSpot = 0\r\n" + 
					"	Limit 1";
			template.queryForMap(sql, pid);
			return false;
		} catch (Exception e) {
			return true;
		}
	}
	
	public boolean checkAllProvice2(String pid) {
		try {
			String sql = "SELECT\r\n" + 
					"	spm.PSPID\r\n" + 
					"FROM\r\n" + 
					"	PSPgroup spg,\r\n" + 
					"	PSPGmember spm\r\n" + 
					"WHERE\r\n" + 
					"	spg.pid = ?\r\n" + 
					"	AND spg.PSPGID = spm.PSPGID"
					+ " AND spg.IsOnSpot = 0\r\n" + 
					"	Limit 1";
			template.queryForMap(sql, pid);
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	public boolean checkAllSpot(String pid) {
		try {
			String sql = "SELECT\r\n" + 
					"	spm.PSPID\r\n" + 
					"FROM\r\n" + 
					"	PSPgroup spg,\r\n" + 
					"	PSPGmember spm\r\n" + 
					"WHERE\r\n" + 
					"	spg.pid = ?\r\n" + 
					"	AND spg.PSPGID = spm.PSPGID\r\n" + 
					"	AND spm.FLAGE = 0"
					+ " AND spg.IsOnSpot = 1\r\n" + 
					"	Limit 1";
			template.queryForMap(sql, pid);
			return false;
		} catch (Exception e) {
			return true;
		}
	}
	
	public boolean checkAllSpot2(String pid) {
		try {
			String sql = "SELECT\r\n" + 
					"	spm.PSPID\r\n" + 
					"FROM\r\n" + 
					"	PSPgroup spg,\r\n" + 
					"	PSPGmember spm\r\n" + 
					"WHERE\r\n" + 
					"	spg.pid = ?\r\n" + 
					"	AND spg.PSPGID = spm.PSPGID"
					+ " AND spg.IsOnSpot = 1\r\n" + 
					"	Limit 1";
			template.queryForMap(sql, pid);
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	public Object findOneDistByPid(String pid) throws Exception{
		String sql = "SELECT dname FROM pspgsch, Schools, Districts WHERE pid=? AND pspgsch.sid = schools.sid AND schools.did = Districts.did"
				+ " LIMIT 1";
		return template.queryForObject(sql, String.class, pid);
	}

	public Object findOneCityByPid(String pid) {
		String sql = "SELECT cname FROM pspgsch, Schools, Districts WHERE pid=? AND pspgsch.sid = schools.sid AND schools.did = Districts.did AND Districts.cid = Cities.cid"
				+ " LIMIT 1";
		return template.queryForObject(sql, String.class, pid);
	}

	public List<Map<String, Object>> findDPrByCondition(String tid, ProjectProcessFallbackPageBean queryForm) {
		String sql = "SELECT\r\n" + 
				"	pcm.*,\r\n" + 
				"	pr.pname,\r\n" + 
				"	pr.pstime,\r\n" + 
				"	pr.petime,\r\n" + 
				"	pr.pprocess,\r\n" + 
				"	mana.mname,\r\n" + 
				"	mana1.mname smname,\r\n" + 
				"	sp.spname\r\n" + 
				"FROM\r\n" + 
				"	(pcm,\r\n" + 
				"	projects pr)\r\n" + 
				"	LEFT JOIN managers mana ON mana.mid = pcm.sid\r\n" + 
				"	LEFT JOIN DSpecialists sp ON sp.dspid = pcm.sid\r\n" + 
				" LEFT JOIN managers mana1 ON mana1.tid = pcm.sid " +  
				"WHERE\r\n" + 
				"	pr.pid = pcm.pid\r\n" +
				"	AND pr.pname LIKE '%" + queryForm.getPname() + "%'\r\n" +
				"	AND pcm.dmid = ?\r\n"  
				+ " order by pcm.pid,pcm.sid\r\n";
		
//		if(!"".equals(queryForm.getPname())) 
//			sql += "\r\n AND pr.pname = '" + queryForm.getPname() + "'";
//		if(!"".equals(queryForm.getPetime())) 
//			sql += "\r\n AND pr.petime >= '" + queryForm.getPetime() + "'";
//		if(!"".equals(queryForm.getProcess())) 
//			sql += "\r\n AND pr.Pprocess = '" + queryForm.getProcess() + "'";
		
		return template.queryForList(sql, tid);
	}

	public List<Map<String, Object>> findCPrByCondition(String tid, ProjectProcessFallbackPageBean queryForm) {
		//查找该市管理员 的所有当前流程包含该市的项目
		String sql = "SELECT\r\n" + 
				"	pcm.*,\r\n" + 
				"	pr.pname,\r\n" + 
				"	pr.pstime,\r\n" + 
				"	pr.petime,\r\n" + 
				"	pr.pprocess,\r\n" + 
				"	mana.mname,\r\n"
				+ " mana1.mname smname, " + 
				"	sp.spname\r\n" + 
				"FROM\r\n" + 
				"	(pcm,\r\n" + 
				"	projects pr)\r\n" + 
				"	LEFT JOIN managers mana ON mana.mid = pcm.sid\r\n" + 
				"	LEFT JOIN cspecialists sp ON sp.cspid = pcm.sid\r\n"
				+ " LEFT JOIN managers mana1 ON mana1.tid = pcm.sid " + 
				"WHERE\r\n" + 
				"	pr.pid = pcm.pid\r\n" +
				"	AND pr.pname LIKE '%" + queryForm.getPname() + "%'\r\n" +
				"	AND pcm.cmid = ?\r\n" + 
				"	AND pcm.dmid IS NULL\r\n"  
				+ " order by pcm.pid,pcm.sid\r\n" ;

//		if(!"".equals(queryForm.getPname())) 
//			sql += "\r\n AND pr.pname = '" + queryForm.getPname() + "'";
//		if(!"".equals(queryForm.getPetime())) 
//			sql += "\r\n AND pr.petime >= '" + queryForm.getPetime() + "'";
//		if(!"".equals(queryForm.getProcess())) 
//			sql += "\r\n AND pr.Pprocess = '" + queryForm.getProcess() + "'";
		
		return template.queryForList(sql, tid);
	}
	
	/**
	 * 根据查找省专家 的所有包含省的项目
	 * @param spid
	 * @param nowTime
	 * @return
	 */
	public List<Map<String, Object>> findPPrByCondition(String mid, ProjectProcessFallbackPageBean queryForm) {
		String sql = "SELECT\r\n" + 
				"	pcm.*,\r\n" + 
				"	pr.pname,\r\n" + 
				"	pr.pstime,\r\n" + 
				"	pr.petime,\r\n" + 
				"	pr.pprocess,\r\n" + 
				"	mana.mname,\r\n" + 
				"	mana1.mname smname,\r\n" + 
				"	sp.spname\r\n" +  
				"FROM\r\n" + 
				"	(pcm,\r\n" + 
				"	projects pr)\r\n" + 
				"	LEFT JOIN managers mana ON mana.mid = pcm.sid\r\n" + 
				"	LEFT JOIN pspecialists sp ON sp.pspid = pcm.sid\r\n"
				+ "	LEFT JOIN managers mana1 ON mana1.tid = pcm.sid " + 
				"WHERE\r\n" + 
				"	pr.pid = pcm.pid\r\n" +
				"	AND pr.pname LIKE '%" + queryForm.getPname() + "%'\r\n" +
				"	AND pcm.pmid = ?\r\n" + 
				"	AND pcm.cmid IS NULL "  
				+ " order by pcm.pid,pcm.sid\r\n";
		
//		if(!"".equals(queryForm.getPname())) 
//			sql += "\r\n AND pr.pname = '" + queryForm.getPname() + "'";
//		if(!"".equals(queryForm.getPetime())) 
//			sql += "\r\n AND pr.petime >= '" + queryForm.getPetime() + "'";
//		if(!"".equals(queryForm.getProcess())) 
//			sql += "\r\n AND pr.Pprocess = '" + queryForm.getProcess() + "'";
		
		return template.queryForList(sql, mid);
	}
	
	public List<Map<String, Object>> findDPcmByCurpageMid(String mid, int startRecord) {
		String sql = "SELECT\r\n" + 
				"	pcm.*,\r\n" + 
				"	pr.pname,\r\n" + 
				"	pr.pstime,\r\n" + 
				"	pr.petime,\r\n" + 
				"	pr.pprocess,\r\n" + 
				"	mana.mname,\r\n" + 
				"	schools.sname smname,\r\n" + 
				"	sp.spname\r\n" + 
				"FROM\r\n" + 
				"	(pcm,\r\n" + 
				"	projects pr)\r\n" + 
				"	LEFT JOIN managers mana ON mana.mid = pcm.sid\r\n" + 
				"	LEFT JOIN DSpecialists sp ON sp.dspid = pcm.sid\r\n" + 
				" LEFT JOIN schools ON schools.sid = pcm.sid " + 
				"WHERE\r\n" + 
				"	pr.pid = pcm.pid\r\n" + 
				"	AND pcm.dmid = ?\r\n"  
				+ " order by pcm.pid,pcm.sid\r\n" +
				"	LIMIT ?, 20\r\n";
		return template.queryForList(sql, mid, startRecord);
	}
	/**
	 * 如果县级管理员项为空，才显示。表示县级管理员记录 或者 市级专家记录、校级管理员记录
	 * @param mid
	 * @param startRecord
	 * @return
	 */
	public List<Map<String, Object>> findCPcmByCurpageMid(String mid, int startRecord) {
		String sql = "SELECT\r\n" + 
				"	pcm.*,\r\n" + 
				"	pr.pname,\r\n" + 
				"	pr.pstime,\r\n" + 
				"	pr.petime,\r\n" + 
				"	pr.pprocess,\r\n" + 
				"	mana.mname,\r\n"
				+ " schools.sname smname, " + 
				"	sp.spname\r\n" + 
				"FROM\r\n" + 
				"	(pcm,\r\n" + 
				"	projects pr)\r\n" + 
				"	LEFT JOIN managers mana ON mana.mid = pcm.sid\r\n" + 
				"	LEFT JOIN cspecialists sp ON sp.cspid = pcm.sid\r\n"
				+ " LEFT JOIN schools ON schools.sid = pcm.sid " + 
				"WHERE\r\n" + 
				"	pr.pid = pcm.pid\r\n" + 
				"	AND pcm.cmid = ?\r\n" + 
				"	AND pcm.dmid IS NULL\r\n"  
				+ " order by pcm.pid,pcm.sid\r\n" +
				"	LIMIT ?, 20\r\n";
		return template.queryForList(sql, mid, startRecord);
	}
	public List<Map<String, Object>> findPPcmByCurpageMid(String mid, int startRecord) {
		String sql = "SELECT\r\n" + 
				"	pcm.*,\r\n" + 
				"	pr.pname,\r\n" + 
				"	pr.pstime,\r\n" + 
				"	pr.petime,\r\n" + 
				"	pr.pprocess,\r\n" + 
				"	mana.mname,\r\n" + 
				"	schools.sname smname,\r\n" + 
				"	sp.spname\r\n" +  
				"FROM\r\n" + 
				"	(pcm,\r\n" + 
				"	projects pr)\r\n" + 
				"	LEFT JOIN managers mana ON mana.mid = pcm.sid\r\n" + 
				"	LEFT JOIN pspecialists sp ON sp.PSPID = pcm.sid\r\n"
				+ "	LEFT JOIN schools ON schools.sid = pcm.sid " + 
				"WHERE\r\n" + 
				"	pr.pid = pcm.pid\r\n" + 
				"	AND pcm.pmid = ?\r\n" + 
				"	AND pcm.cmid IS NULL\r\n"  
				+ " order by pcm.pid,pcm.sid\r\n" +
				"	LIMIT ?, 20\r\n";
		return template.queryForList(sql, mid, startRecord);
	}

	public void backPCMByPidMid(String pid, String sid, String nowRate) throws Exception {
		String sql = "UPDATE pcm SET nowRate=?, commState=? WHERE pid=? AND sid=? LIMIT 1";
		if(1 != template.update(sql, nowRate, "未提交", pid, sid))
			throw new Exception();
	}
	/**
	 * 查找是否有该省级分配到pid项目下的专家已经登录或者提交了
	 * @param pid
	 * @param mid
	 * @return
	 */
	public boolean findOnePspLoginedOrCommit(String pid, String mid) {
		try {
			String sql = "SELECT\r\n" + 
					"	pid,sid\r\n" + 
					"FROM\r\n" + 
					"	pcm\r\n" + 
					"WHERE\r\n" + 
					"	pcm.sid LIKE 'P%'\r\n" + 
					"	AND pcm.pmid = 'M00001'\r\n" + 
					"	AND pcm.pid = ?\r\n" + 
					"	AND pcm.commState != '未提交'\r\n" + 
					"	LIMIT 1";
			template.queryForMap(sql, pid);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean findOneCspLoginedOrCommit(String pid, String mid) {
		try {
			String sql = "SELECT\r\n" + 
					"	pid,sid\r\n" + 
					"FROM\r\n" + 
					"	pcm\r\n" + 
					"WHERE\r\n" + 
					"	pcm.sid LIKE 'C%'\r\n" + 
					"	AND pcm.cmid = ?\r\n" + 
					"	AND pcm.pid = ?\r\n" + 
					"	AND pcm.commState != '未提交'\r\n" + 
					"	LIMIT 1";
			System.out.println(pid + ':' + mid);
			template.queryForMap(sql, mid, pid);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean findOneDspLoginedOrCommit(String pid, String mid) {
		try {
			String sql = "SELECT\r\n" + 
					"	pid,sid\r\n" + 
					"FROM\r\n" + 
					"	pcm\r\n" + 
					"WHERE\r\n" + 
					"	pcm.sid LIKE 'D%'\r\n" + 
					"	AND pcm.dmid = ?\r\n" + 
					"	AND pcm.pid = ?\r\n" + 
					"	AND pcm.commState != '未提交'\r\n" + 
					"	LIMIT 1";
			template.queryForMap(sql, mid, pid);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean findOneCmanaIsCommit(String pid, String mid) {
		try {
			String sql = "SELECT\r\n" + 
					"	pid,sid\r\n" + 
					"FROM\r\n" + 
					"	pcm\r\n" + 
					"WHERE\r\n" + 
					"	pcm.sid LIKE 'M%'\r\n" + 
					"	AND pcm.cmid = ?\r\n" + 
					"	AND pcm.pid = ?\r\n" + 
					"	AND pcm.commState = '已提交'\r\n" + 
					"	LIMIT 1";
			template.queryForMap(sql, mid, pid);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean findOneDmanaIsCommit(String pid, String mid) {
		try {
			String sql = "SELECT\r\n" + 
					"	pid,sid\r\n" + 
					"FROM\r\n" + 
					"	pcm\r\n" + 
					"WHERE\r\n" + 
					"	pcm.sid LIKE 'M%'\r\n" + 
					"	AND pcm.dmid = ?\r\n" + 
					"	AND pcm.pid = ?\r\n" + 
					"	AND pcm.commState = '已提交'\r\n" + 
					"	LIMIT 1";
			template.queryForMap(sql, mid, pid);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void processFallbackByPidSid(String pid, String sid, String nowRate) throws Exception {
		String sql = "UPDATE pcm SET nowRate=?, commState='已登录' WHERE pid=? AND sid=? LIMIT 1";
		if(1 != template.update(sql, nowRate, pid, sid))
			throw new Exception();
	}

	public void setSchFallbackFlagByPidSidMid(String pid, String sid, String mid) throws Exception {
		String sql = "UPDATE pspgsch SET flage=0 WHERE pid=? AND sid=? LIMIT 1";
		if(1 != template.update(sql, pid, sid))
			throw new Exception();
	}

	public void setDspFallbackFlagByPidSidMid(String pid, String sid, String mid) throws Exception {
		String sql = "UPDATE\r\n" + 
				"	DSPGmember sp\r\n" + 
				"SET \r\n" + 
				"	flage = 0\r\n" + 
				"WHERE \r\n" + 
				"	sp.dspid = ?\r\n" + 
				"	AND sp.dspgid = \r\n" + 
				"						(SELECT\r\n" + 
				"							spg.dspgid\r\n" + 
				"						FROM\r\n" + 
				"							DSPgroup spg\r\n" + 
				"						WHERE \r\n" + 
				"							spg.dspgid = sp.dspgid\r\n" + 
				"							AND spg.pid=?"
				+ "						LIMIT 1)\r\n" + 
				"	LIMIT 1\r\n";
		if(1 != template.update(sql, sid, pid))
			throw new Exception();
	}

	public void setCspFallbackFlagByPidSidMid(String pid, String sid, String mid) throws Exception {
		String sql = "UPDATE\r\n" + 
				"	CSPGmember sp\r\n" + 
				"SET \r\n" + 
				"	flage = 0\r\n" + 
				"WHERE \r\n" + 
				"	sp.cspid = ?\r\n" + 
				"	AND sp.cspgid = \r\n" + 
				"							(SELECT\r\n" + 
				"								spg.cspgid\r\n" + 
				"							FROM\r\n" + 
				"								CSPgroup spg\r\n" + 
				"							WHERE \r\n" + 
				"								spg.cspgid = sp.cspgid\r\n" + 
				"								AND spg.pid=?"
				+ "							LIMIT 1)\r\n" + 
				"	LIMIT 1\r\n";
		if(1 != template.update(sql, sid, pid))
			throw new Exception();
	}

	public void setPspFallbackFlagByPidSidMid(String pid, String sid, String mid) throws Exception {
		String sql = "UPDATE\r\n" + 
				"	PSPGmember sp\r\n" + 
				"SET \r\n" + 
				"	flage = 0\r\n" + 
				"WHERE \r\n" + 
				"	sp.pspid = ?\r\n" + 
				"	AND sp.pspgid = \r\n" + 
				"							(SELECT\r\n" + 
				"								spg.pspgid\r\n" + 
				"							FROM\r\n" + 
				"								PSPgroup spg\r\n" + 
				"							WHERE \r\n" + 
				"								spg.pspgid = sp.pspgid\r\n" + 
				"								AND spg.pid=?"
				+ "							LIMIT 1)\r\n" + 
				"	LIMIT 1\r\n";
		if(1 != template.update(sql, sid, pid))
			throw new Exception();
	}
	/**
	 * 传入管理员id和项目id，判断该管理员下的专家是否已经登录，如果有登录的专家，则不可回退前一级管理员
	 * @param mid
	 * @param pid
	 * @return
	 */
	public boolean findOneCommitOrLoginSP(String mid, String pid) {
		try {
			String sql = "SELECT pid,sid "
					  + "FROM pcm "
					  + "WHERE "
					  + " (dmid =? AND sid like 'D%') "
					  + " OR (cmid=? AND sid like 'C%') "
					  + " OR (pmid=? AND sid like 'P%')"
					  + " AND pid=? "
					  + " AND commState != '未提交' "
					  + "LIMIT 1";
			template.queryForMap(sql, mid, mid, mid, pid);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
