package dao;

import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import domain.SSPRatePageBean;
import domain.SSPRateScore;
import util.JDBCUtils;

public class SSPRateDAO {

	JdbcTemplate template = JDBCUtils.getTemplate();
	
	public void rate(List<SSPRateScore> spScores, String tid) throws Exception{
		String sql = "";
		String spsid = "";
		int number = 0;
		for(SSPRateScore spScore : spScores) {
			//找得到该记录则更新，找不到该记录，则插入记录
			try {
				//找到表中sid和cid所在记录主键，根据主键更新记录
				sql = "SELECT SPSID FROM SPScore WHERE sid=? AND cid=? limit 1";
				spsid = (String) template.queryForMap(sql, tid, spScore.getCid()).get("SPSID");
				if(spScore.getAnnexLocation() == null || spScore.getAnnexLocation().equals("")) {
					sql = "UPDATE SPScore SET SchoolScore=?, description=? WHERE spsid=? limit 1";
					template.update(sql, spScore.getSchoolScore(), spScore.getDescribe(), spsid);
				} else {
					sql = "UPDATE SPScore SET SchoolScore=?, AnnexLocation=?, description=? WHERE spsid=? limit 1";
					template.update(sql, spScore.getSchoolScore(), spScore.getAnnexLocation(), spScore.getDescribe(), spsid);
				}
			} catch (EmptyResultDataAccessException e) {
				//找不到记录，获取最后一条记录所在主键 + 1，插入记录,如果表中一条记录都没有，默认编号从1开始
				try {
					sql = "SELECT SPSID FROM SPScore ORDER BY SPSID DESC limit 1";
					spsid = (String) template.queryForMap(sql).get("SPSID");
					number = Integer.parseInt(spsid.substring(3));
				} catch(EmptyResultDataAccessException e2) {
				} finally {
					number++;
					if(number < 10) spsid = "SPS0000" + number;
					else if(number < 100) spsid = "SPS000" + number;
					else if(number < 1000) spsid = "SPS00" + number;
					else if(number < 10000) spsid = "SPS0" + number;
					else spsid = "SPS" + number; 
					
					sql = "INSERT INTO SPScore(SPSID, SID, CID, SchoolScore, AnnexLocation, description) VALUES(?,?,?,?,?,?)";
					template.update(sql, spsid, tid, spScore.getCid(), spScore.getSchoolScore(), spScore.getAnnexLocation(), spScore.getDescribe());
				}
			}
		}
	}

	public SSPRatePageBean findPageBean(String tid, String nowTime) throws Exception{
		SSPRatePageBean pageBean = new SSPRatePageBean();
		try {
			
		} catch (EmptyResultDataAccessException e) {
			
		}
		return pageBean;
	}

	public String findPPid(String tid, String nowTime) {
		try {
			String sql = "SELECT\r\n" + 
						"	pr.pid\r\n" + 
						"FROM\r\n" + 
						"	pspgsch spgsch,  Projects pr\r\n" + 
						"WHERE \r\n" + 
						"	spgsch.sid = ?\r\n" + 
						"	AND pr.pid = spgsch.pid\r\n" + 
						"	AND ? BETWEEN pr.PSTIME AND pr.PETIME\r\n" + 
						"	AND pr.PISSTART IN('true','TRUE')"
						//新增
						+ " LIMIT 1";
			Map<String, Object> p = template.queryForMap(sql, tid, nowTime);
			return (String) p.get("pid");
		} catch(Exception e) {
			return null;
		}
	}

	public String findPProcess(String pid) {
		try {
			String sql = "SELECT\r\n" + 
						"	pr.pprocess\r\n" + 
						"FROM\r\n" + 
						"	Projects pr\r\n" + 
						"WHERE \r\n" + 
						"	pr.pid = ?\r\n"
						+ " LIMIT 1";
			Map<String, Object> p = template.queryForMap(sql, pid);
			return (String) p.get("pprocess");
		} catch(Exception e) {
//			e.printStackTrace();
			return null;
		}
	}
	
	public String findCPid(String tid, String nowTime) {
		try {
			String sql = "SELECT\r\n" + 
						"	pr.pid\r\n" + 
						"FROM\r\n" + 
						"	cspgsch spgsch, cSPgroup spgroup, Projects pr\r\n" + 
						"WHERE \r\n" + 
						"	spgsch.sid = ?\r\n" + 
						"	AND spgsch.cspgid = spgroup.cspgid\r\n" + 
						"	AND spgroup.pid = pr.pid\r\n" + 
						"	AND ? BETWEEN pr.PSTIME AND pr.PETIME\r\n" + 
						"	AND pr.PISSTART IN('true','TRUE')"
						//新增
						+ " LIMIT 1";
			Map<String, Object> p = template.queryForMap(sql, tid, nowTime);
			return (String) p.get("pid");
		} catch(Exception e) {
			return null;
		}
	}
	
	public String findDPid(String tid, String nowTime) {
		try {
			String sql = "SELECT\r\n" + 
						"	pr.pid\r\n" + 
						"FROM\r\n" + 
						"	dspgsch spgsch, dSPgroup spgroup, Projects pr\r\n" + 
						"WHERE \r\n" + 
						"	spgsch.sid = ?\r\n" + 
						"	AND spgsch.dspgid = spgroup.dspgid\r\n" + 
						"	AND spgroup.pid = pr.pid\r\n" + 
						"	AND ? BETWEEN pr.PSTIME AND pr.PETIME\r\n" + 
						"	AND pr.PISSTART IN('true','TRUE')"
						+ " LIMIT 1";
			Map<String, Object> p = template.queryForMap(sql, tid, nowTime);
			return (String) p.get("pid");
		} catch(Exception e) {
			return null;
		}
	}

	public Map<String, Object> findMana(String tid) throws Exception{
		String sql = "SELECT\r\n" + 
					"	mana.MNAME mname,\r\n" + 
					"	sch.SNAME sname \r\n" + 
					"FROM\r\n" + 
					"	managers mana,\r\n" + 
					"	SCHOOLS sch \r\n" + 
					"WHERE\r\n" + 
					"	mana.TID = ? \r\n" + 
					"	AND sch.SID = ? \r\n" + 
					"	LIMIT 1";
		return template.queryForMap(sql, tid, tid);
	}

	public List<Map<String, Object>> findTableData(String pid, String tid) {
		System.out.println(pid + " : " + tid);
		String sql = "SELECT\r\n" + 
					"	A.ANAME aname,\r\n" + 
					"	A.AID aid,\r\n" + 
					"	B.BNAME bname,\r\n" + 
					"	B.BID bid,\r\n" + 
					"	C.CID cid,\r\n" + 
					"	C.CNAME cname,\r\n" + 
					"	C.ISANNEX isannex,\r\n" + 
					"	C.ASSESSMETHOD method,\r\n" + 
					"	C.ISEXPLAIN isexplain,\r\n" + 
					"	C.SCORE score,\r\n" + 
					"	C.SEGSCORE segscore,\r\n" + 
					"	C.THRESHHOLD threshhold,\r\n" + 
					"	spscore.SCHOOLSCORE,\r\n" + 
					"	spscore.description,\r\n" + 
					"	spscore.ANNEXLOCATION\r\n" + 
					"FROM\r\n" + 
					"	Projects pr,\r\n" + 
					"	Acriterion A,\r\n" + 
					"	Bcriterion B,\r\n" + 
					"	Ccriterion C\r\n" + 
					"	LEFT JOIN spscore ON spscore.CID = C.CID AND spscore.sid = ?\r\n" + 
					"WHERE\r\n" + 
					"	pr.pid = ?\r\n" + 
					"	AND pr.PID = A.PID \r\n" + 
					"	AND A.AID = B.AID \r\n" + 
					"	AND B.BID = C.BID\r\n" + 
					"	ORDER BY C.CID";
		return template.queryForList(sql, tid, pid);
	}

	public void updateSelfreport(String tid, String filePath, String pid, String overallFilePath) {
		String sql = "";
		String overName = overallFilePath.substring(overallFilePath.lastIndexOf("\\") + 1);
		try {
			sql = "SELECT * FROM SELFREPORT WHERE pid=? AND sid=?";
			template.queryForMap(sql, pid, tid);
			sql = "UPDATE SELFREPORT SET REPORTLOCATION=?,overallFileName=?  WHERE pid=? AND sid=?";
			template.update(sql, filePath, overName, pid, tid);
		} catch (EmptyResultDataAccessException e) {
			sql = "INSERT INTO SELFREPORT VALUES(?,?,?,?)";	
			System.out.println(overallFilePath + " here");
			template.update(sql, pid, tid, filePath, overName);
		}
	}

	public Map<String, Object> findOverall(String tid, String pid) {
		try {
			String sql = "SELECT REPORTLOCATION,overallFileName  FROM SELFREPORT WHERE pid=? AND sid=? LIMIT 1";
			return template.queryForMap(sql, pid, tid);
		} catch (Exception e) {
			return null;
		}
	}

	public Map<String, Object> checkP(String tid, String nowTime) {
		try {
			String sql = "SELECT\r\n" + 
					"	spgsch.flage, spgsch.mid\r\n" + 
					"FROM\r\n" + 
					"	pspgsch spgsch, Projects pr\r\n" + 
					"WHERE \r\n" + 
					"	spgsch.sid = ?\r\n" + 
					"	AND spgsch.pid = pr.pid\r\n" + 
					"	AND ? BETWEEN pr.PSTIME AND pr.PETIME\r\n" + 
					"	AND pr.PISSTART IN('true','TRUE') "
					+ "LIMIT 1";
			return template.queryForMap(sql, tid, nowTime);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Map<String, Object> checkC(String tid, String nowTime) {
		try {
			String sql = "SELECT\r\n" + 
					"	spgsch.flage, spgsch.cspgid spgid, spgsch.mid\r\n" + 
					"FROM\r\n" + 
					"	cspgsch spgsch, cSPgroup spgroup, Projects pr\r\n" + 
					"WHERE \r\n" + 
					"	spgsch.sid = ?\r\n" + 
					"	AND spgsch.cspgid = spgroup.cspgid\r\n" + 
					"	AND spgroup.pid = pr.pid\r\n" + 
					"	AND ? BETWEEN pr.PSTIME AND pr.PETIME\r\n" + 
					"	AND pr.PISSTART IN('true','TRUE') "
					+ "LIMIT 1";
			return template.queryForMap(sql, tid, nowTime);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Map<String, Object> checkD(String tid, String nowTime) {
		try {
			String sql = "SELECT\r\n" + 
					"	spgsch.flage, spgsch.dspgid spgid, spgsch.mid\r\n" + 
					"FROM\r\n" + 
					"	dspgsch spgsch, dSPgroup spgroup, Projects pr\r\n" + 
					"WHERE \r\n" + 
					"	spgsch.sid = ?\r\n" + 
					"	AND spgsch.dspgid = spgroup.dspgid\r\n" + 
					"	AND spgroup.pid = pr.pid\r\n" + 
					"	AND ? BETWEEN pr.PSTIME AND pr.PETIME\r\n" + 
					"	AND pr.PISSTART IN('true','TRUE') "
					+ "LIMIT 1";
			return template.queryForMap(sql, tid, nowTime);
		} catch (Exception e) {
			return null;
		}
	}

	public Boolean SelfSetFlageP(String tid, String pid, String mid) {
		String sql = "UPDATE pspgsch SET FLAGE = FLAGE + 1 WHERE pid=? AND sid=? AND mid=?";
		int count = template.update(sql, pid, tid, mid);
		return count == 0? false: true;
	}
	
	public Boolean SelfSetFlageC(String tid, String spgid, String mid) {
		String sql = "UPDATE cspgsch SET FLAGE = FLAGE + 1 WHERE cspgid=? AND sid=? AND mid=?";
		int count = template.update(sql, spgid, tid, mid);
		return count == 0? false: true;
	}
	
	public Boolean SelfSetFlageD(String tid, String spgid, String mid) {
		String sql = "UPDATE dspgsch SET FLAGE = FLAGE + 1 WHERE dspgid=? AND sid=? AND mid=?";
		int count = template.update(sql, spgid, tid, mid);
		return count == 0? false: true;
	}

	public Map<String, Object> checkOverallP(String pid, String tid) {
		return null;
	}

	public Boolean OverallSetFlageP(String tid, String pid, String mid) {
		String sql = "UPDATE pspgsch SET FLAGE = FLAGE + 10 WHERE pid=? AND sid=? AND mid=?";
		int count = template.update(sql, pid, tid, mid);
		return count == 0? false: true;
	}

	public Boolean OverallSetFlageC(String tid, String spgid, String mid) {
		String sql = "UPDATE cspgsch SET FLAGE = FLAGE + 10 WHERE cspgid=? AND sid=? AND mid=?";
		int count = template.update(sql, spgid, tid, mid);
		return count == 0? false: true;
	}
	
	public Boolean OverallSetFlageD(String tid, String spgid, String mid) {
		String sql = "UPDATE dspgsch SET FLAGE = FLAGE + 10 WHERE dspgid=? AND sid=? AND mid=?";
		int count = template.update(sql, spgid, tid, mid);
		return count == 0? false: true;
	}

	public Integer findPSchFlageByPidSid(String pid, String tid) {
		try {
			String sql = "SELECT\r\n" + 
					"	spgsch.flage\r\n" + 
					"FROM\r\n" + 
					"	pspgsch spgsch,\r\n" + 
					"	pspgroup spgroup,\r\n" + 
					"	projects\r\n" + 
					"WHERE\r\n" + 
					"	projects.pid = ?\r\n" + 
					"	AND spgroup.pid = projects.pid\r\n" + 
					"	AND spgsch.pspgid = spgroup.pspgid\r\n" + 
					"	AND spgsch.sid = ?\r\n" + 
					"	LIMIT 1\r\n";
			return template.queryForObject(sql, Integer.class, pid, tid);
		} catch(Exception e) {
			return null;
		}
	}

	public Integer findCSchFlageByPidSid(String pid, String tid) {
		try {
			String sql = "SELECT\r\n" + 
					"	spgsch.flage\r\n" + 
					"FROM\r\n" + 
					"	cspgsch spgsch,\r\n" + 
					"	cspgroup spgroup,\r\n" + 
					"	projects\r\n" + 
					"WHERE\r\n" + 
					"	projects.pid = ?\r\n" + 
					"	AND spgroup.pid = projects.pid\r\n" + 
					"	AND spgsch.cspgid = spgroup.cspgid\r\n" + 
					"	AND spgsch.sid = ?\r\n" + 
					"	LIMIT 1\r\n";
			return template.queryForObject(sql, Integer.class, pid, tid);
		} catch(Exception e) {
			return null;
		}
	}
	
	public Integer findDSchFlageByPidSid(String pid, String tid) {
		try {
			String sql = "SELECT\r\n" + 
					"	spgsch.flage\r\n" + 
					"FROM\r\n" + 
					"	pspgsch spgsch\r\n" +
					"WHERE\r\n" + 
					"	spgsch.pid = ?\r\n" +
					"	AND spgsch.sid = ?\r\n" + 
					"	LIMIT 1\r\n";
			return template.queryForObject(sql, Integer.class, pid, tid);
		} catch(Exception e) {
			return null;
		}
	}
	
	public boolean checkAllDistByPid(String pid, String tid) {
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
					"	AND spm.FLAGE = 0\r\n" + 
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
					"	AND spg.PSPGID = spm.PSPGID\r\n" + 
					"	Limit 1";
			template.queryForMap(sql, pid);
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	public void setSpcmByPidSid(String pid, String tid, String nowRate) throws Exception {
		String sql = "UPDATE pcm SET nowRate = ?, commState='已提交' WHERE pid=? AND sid=?";
		if(0 == template.update(sql, nowRate, pid, tid))
			throw new Exception();
	}

	public boolean findOneSchNotCommitByPid(String pid) {
		String sql = "SELECT pid FROM pcm WHERE pid=? AND sid LIKE 'S%' AND commState='未提交' LIMIT 1";
		try {
			template.queryForMap(sql, pid);
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	public void setPrUpLevelMidRateByPid(String pid, String nowRate, String upLevelMid, String upAndUp) throws Exception {
		// 改动自己的管理员流程
		String sql = "UPDATE pcm SET nowRate=? WHERE pid=? AND sid=? LIMIT 1";
		template.update(sql, nowRate, pid, upLevelMid);
		// 如果所有的上级流程管理员都已经提交。则整个项目向前跑一步(上上一级管理员从校、进阶到县，上上级为省、抽查除外)
		if(!"".equals(upAndUp)) {
			if("市".equals(upAndUp)) {
				// 查找该条管理员记录的市级管理员账号，如果该市级管理员下的所有县级都已经提交，则该市级账号，流程从校到县
				sql = "SELECT cmid FROM pcm WHERE pid=? AND sid=? LIMIT 1";
				String upAndUpMid = template.queryForObject(sql, String.class, pid, upLevelMid);
				try {
					System.out.println(upAndUpMid);
					sql = "SELECT * FROM pcm WHERE pid=? AND cmid=? AND sid LIKE 'M%' AND nowRate='校' LIMIT 1";
					// 该市下无未提交的县管理员记录
					template.queryForMap(sql, pid, upAndUpMid);
				} catch (Exception e) {
					e.printStackTrace();
					sql = "UPDATE pcm SET nowRate=? WHERE pid=? AND sid=? LIMIT 1";
					template.update(sql, nowRate, pid, upAndUpMid);
				}
			}
		}
	}
	
	public String findOneDMidBySid(String spid) throws Exception {
		String sql = "SELECT mid FROM Managers mana,Schools sch WHERE sch.sid=? AND mana.tid=sch.did LIMIT 1";
		return template.queryForObject(sql, String.class, spid);
	}

	public boolean findOneSchNotCommitByPidUpLevelMid(String pid, String upLevelMid) {
		String sql = "SELECT pid FROM pcm WHERE pid=? AND sid LIKE 'S%' AND commState='未提交' AND dmid=? LIMIT 1";
		try {
			template.queryForMap(sql, pid, upLevelMid);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	public String findOneCMidBySid(String spid) throws Exception {
		String sql = "SELECT mid FROM Managers mana,Schools sch,Districts dist WHERE sch.sid=? AND dist.did=sch.did AND mana.tid=dist.cid LIMIT 1";
		return template.queryForObject(sql, String.class, spid);
	}

	public boolean findOneSchNotCommitByPidCUpLevelMid(String pid, String upLevelMid) {
		String sql = "SELECT pid FROM pcm WHERE pid=? AND sid LIKE 'S%' AND commState='未提交' AND cmid=? LIMIT 1";
		try {
			template.queryForMap(sql, pid, upLevelMid);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
}
