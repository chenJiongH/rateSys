package dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import util.JDBCUtils;

public class PSPRateDAO {
	JdbcTemplate template = JDBCUtils.getTemplate();
	
	/**
	 * 根据省专家id在pcm表中查找该省专家 的所有当前时间不过时 的项目
	 * @param spid
	 * @param nowTime
	 * @return
	 */
	public List<Map<String, Object>> findPr(String spid, String nowTime) {
		String sql = "SELECT  \r\n" + 
				"	DISTINCT pr.pid,  \r\n" + 
				"	pr.pname,  \r\n" + 
				"	pr.pprocess,  \r\n"
				+ " pcm.commState, \r\n" + 
				"	spg.IsOnSpot   \r\n" + 
				"FROM  \r\n" + 
				"	pcm,  \r\n" + 
				"	PSPGmember spm,  \r\n" + 
				"	PSPgroup spg,  \r\n" + 
				"	Projects pr  \r\n" + 
				"WHERE  \r\n" + 
				"	pcm.sid = ?\r\n" + 
				"	AND pcm.sid = spm.PSPID  \r\n" + 
				"	AND spm.PSPGID = spg.PSPGID     \r\n" + 
				"	AND pcm.pid = pr.pid\r\n" + 
				"	AND spg.PID = pr.PID  \r\n" + 
				"	AND ? BETWEEN pr.PSTIME AND pr.PETIME  \r\n" + 
				"	AND pr.PISSTART IN('TRUE','true')";
		return template.queryForList(sql, spid, nowTime);
	}

	/**
	 * 根据项目id 并且省下一直接流程为校（即流程中没有市） 查找该项目下是否有未完成评分的学校
	 * @param pid
	 * @return
	 */
	public boolean checkAllSchByPid(String pid) {
		try {
			String sql = "SELECT \r\n" + 
					"	sid\r\n" + 
					"FROM\r\n" + 
					"	pspgsch\r\n" + 
					"WHERE\r\n" + 
					"	PID = ?\r\n" + 
					"	AND FLAGE != 11\r\n" + 
					"	LIMIT 1";
			template.queryForMap(sql, pid);
			return false;
		} catch (Exception e) {
			return true;
		}
		
	}
	
	/**
	 * 根据项目id 并且省流程下为市 查找该项目下是否有未完成评分的市级专家，则该项目还不可被当前省专家所评分
	 * @param pid
	 * @return
	 */
	public boolean checkAllCityByPid(String pid) {
		try {
			String sql = "SELECT\r\n" + 
					"	spm.CSPID\r\n" + 
					"FROM\r\n" + 
					"	CSPgroup spg,\r\n" + 
					"	CSPGmember spm\r\n" + 
					"WHERE\r\n" + 
					"	spg.pid = ?\r\n" + 
					"	AND spg.CSPGID = spm.CSPGID\r\n" + 
					"	AND spm.FLAGE = 0\r\n" + 
					"	Limit 1";
			template.queryForMap(sql, pid);
			return false;
		} catch (Exception e) {
			return true;
		}
	}
	
	/**
	 * 根据项目id 并且省流程下为市 查找该项目下是否有未完成评分的市级专家，则该项目还不可被当前省专家所评分
	 * @param pid
	 * @return
	 */
	public boolean checkAllCityByPid2(String pid) {
		try {
			String sql = "SELECT\r\n" + 
					"	spm.CSPID\r\n" + 
					"FROM\r\n" + 
					"	CSPgroup spg,\r\n" + 
					"	CSPGmember spm\r\n" + 
					"WHERE\r\n" + 
					"	spg.pid = ?\r\n" + 
					"	AND spg.CSPGID = spm.CSPGID\r\n" + 
					"	Limit 1";
			template.queryForMap(sql, pid);
			return false;
		} catch (Exception e) {
			return true;
		}
	}
	/**
	 * 抽查流程，判断上一省级流程 的所有省级专家组是否已经都评分
	 * @param pid
	 * @return
	 */
	public boolean checkAllProByPid(String pid) {
		try {
			String sql = "SELECT\r\n" + 
					"	spm.PSPID\r\n" + 
					"FROM\r\n" + 
					"	PSPgroup spg,\r\n" + 
					"	PSPGmember spm\r\n" + 
					"WHERE\r\n" + 
					"	spg.pid = ?\r\n" + 
					"	AND spg.PSPGID = spm.PSPGID\r\n" + 
					"	AND spg.IsOnSpot = 0\r\n" + 
					"	AND spm.FLAGE = 0\r\n" + 
					"	Limit 1";
			template.queryForMap(sql, pid);
			return false;
		} catch (Exception e) {
			return true;
		}
	}
	
	/**
	 * 根据项目id查找该专家的姓名、单位
	 * @param spid
	 * @return
	 */
	public Map<String, Object> findSP(String spid) {
		String sql = "SELECT\r\n" + 
				"	sp.spname,\r\n" + 
				"	sp.sporganization\r\n" + 
				"FROM\r\n" + 
				"	Pspecialists sp\r\n" + 
				"WHERE\r\n" + 
				"	sp.pspid = ?\r\n" + 
				"	LIMIT 1";
		return template.queryForMap(sql, spid);
	}
	
	/**
	 * 根据已有pid和IsOnSpot标识，查找某个pid下的专家组，获得该专家组下的所有学校及其各学校的总评附件
	 * @param pid
	 * @param isOnSpot
	 * @return
	 */
	public List<Map<String, Object>> findSchByPidCmid(String pid, String spid, int isOnSpot) {
		String sql = "SELECT  \r\n" + 
				"	sch.sid, \r\n" + 
				"	sch.sname, \r\n" + 
				"	selfreport.reportlocation,\r\n" + 
				"	selfreport.overallFileName\r\n" + 
				"FROM  \r\n" + 
				"	Schools sch, \r\n" + 
				"	selfreport, \r\n"
				+ " PSPGmember spm,\r\n"
				+ " PSPgroup spg,"
				+ " groupSchool gSchool " + 
				"WHERE \r\n" + 
				"	spm.PSPID = ?\r\n" + 
				"	AND spm.PSPGID = spg.PSPGID\r\n"
				+ " AND spg.pid = ?\r\n"
				+ " AND spg.isOnSpot = ?"
				+ " AND gSchool.pid = spg.pid\r\n"
				+ " AND gSchool.gid = spg.PSPGID\r\n"
				+ " AND gSchool.mid = 'M00001'\r\n" + 
				"	AND gSchool.sid = sch.sid\r\n" + 
				"	AND selfreport.pid = gSchool.pid\r\n" + 
				"	AND selfreport.sid = sch.sid";
		return template.queryForList(sql, spid, pid, isOnSpot);
	}
	
	/**
	 * 根据已有专家id和pid,判断是否是抽查级别 把该专家组下面的学校一个一个进行添加专家所有C指标。有多个指标
	 * @param spid
	 * @param pid
	 * @param sid
	 * @param isOnSpot
	 * @return
	 */
	public Collection<? extends Map<String, Object>> findCByPidSpidSid(String spid, String pid, String sid, int isOnSpot) {
		String flag = ""; //sql语句注释标志
		if(isOnSpot == 0)
			flag = "--";
		String sql = "SELECT\r\n" + 
				"	c.cid,\r\n" + 
				"	c.cname,\r\n" + 
				"	a.aname,\r\n" + 
				"	b.bname,\r\n" + 
				"	c.isannex,\r\n" + 
				"	c.score,\r\n" + 
				"	c.threshhold,\r\n" + 
				"	c.segscore,\r\n" + 
				"	c.isexplain,\r\n" + 
				"	spscore.sid,\r\n" + 
				"	spscore.annexlocation,\r\n" + 
				"	spscore.schoolscore,\r\n"
				+ " spscore.description,\r\n" + 
				"	spscore.districtScore,\r\n" + 
				"	spscore.districtExplain,\r\n" + 
				"	spscore.cityScore,\r\n" + 
				"	spscore.cityExplain,\r\n" + 
				"	spscore.proscore,\r\n" + 
				"	spscore.proexplain,\r\n" + 
				"	spscore.onSpotScore,\r\n" + 
				"	spscore.onSpotExplain\r\n" + 
				"FROM\r\n" + 
				"	pspgmember spm,\r\n" + 
				"	pspgroup spg,\r\n" + 
				"	pspginx spginx,\r\n" + 
				"	projects pr,\r\n" + 
				"	acriterion a,\r\n" + 
				"	bcriterion b,\r\n" + 
				"	ccriterion c,\r\n" + 
				"	spscore\r\n" + 
				"WHERE \r\n" + 
				"	spm.pSPID = ?\r\n" + 
				"	AND spm.pSPGID = spg.pSPGID\r\n" + 
				"	AND spg.PID = ?\r\n" + 
				"	AND spg.pspgid = spginx.pSPGID\r\n" + 
				"	AND spm.pspid = spginx.pspid\r\n" + 
				"	AND pr.pid = spg.Pid\r\n" + 
				"	AND pr.pid = A.pid\r\n" + 
				"	AND A.Aid = B.Aid\r\n" + 
				"	AND C.Bid = B.Bid\r\n" + 
				"	AND C.cid = spginx.cid\r\n" + 
				"	AND spscore.cid = c.cid\r\n" + 
				"	AND spscore.sid = ?\r\n" + 
				"	ORDER BY spscore.sid,C.CID";
		return template.queryForList(sql, spid, pid, sid);
	}

	public int check(String spid, String pid, int isOnSpot) {
		String sql = "SELECT\r\n" + 
				"	spm.FLAGE\r\n" + 
				"FROM\r\n" + 
				"	PSPGmember spm,\r\n" + 
				"	PSPgroup spg,\r\n" + 
				"	Projects pr\r\n" + 
				"WHERE\r\n" + 
				"	pr.PID = ?\r\n" + 
				"	AND spg.PID = pr.PID\r\n" + 
				"	AND spg.isonspot = ?\r\n" + 
				"	AND spm.PSPID = ?\r\n" + 
				"	AND spg.PSPGID = spm.PSPGID\r\n" + 
				"	LIMIT 1";
		return template.queryForObject(sql, Integer.class, pid, isOnSpot, spid);
	}
	
	public void batchUpdate(Map<String, Object> cs, String spid, int isOnSpot) throws Exception{
		// 获取所有的分数id
		 Set<String> sidcidSet = new HashSet<>();
		for (String sidcid : cs.keySet()) {
			if("pid".equals(sidcid) || "draft".equals(sidcid) || "isOnSpot".equals(sidcid))
				continue;
			if(sidcid.charAt(0) != '0')
				sidcidSet.add(sidcid);
			if(sidcid.charAt(0) == '0')
				sidcidSet.add(sidcid.substring(1));
		}
		Object[] sidcidList = sidcidSet.toArray();
		
		String scoreType = "proscore";
		String explain = "proexplain";
		String id = "PSPID";
		if(isOnSpot == 1) {
			id = "onSpotID";
			explain = "onSpotExplain";
			scoreType = "onSpotScore";
		}
		String sql = "UPDATE\r\n" + 
				"	SPScore\r\n" + 
				"SET\r\n" + 
				"	spscore." + scoreType + " = ?,\r\n" + 
				"	spscore." + explain + " = ?,\r\n" + 
				"	spscore." + id + " = ?\r\n" + 
				"WHERE\r\n" + 
				"	spscore.CID = ?\r\n" + 
				"	AND spscore.SID = ?";
		int count[] = template.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				String sidcid = sidcidList[i].toString();
				String sid = sidcid.substring(0, sidcid.indexOf('C'));
				String cid = sidcid.substring(sidcid.indexOf('C'));
				// 分数
				String score = "0";
				if(cs.get(sidcid) != null)
					score = cs.get(sidcid).toString();
				ps.setFloat(1, Float.parseFloat(score));
				// 说明
				String explain = "";
				if(cs.get("0" + sidcid) != null)
					explain = cs.get("0" + sidcid).toString();
				ps.setString(2, explain);
				ps.setString(3, spid);
				ps.setString(4, cid);
				ps.setString(5, sid);
			}
			
			@Override
			public int getBatchSize() {
				return sidcidSet.size();
			}
		});
			for(int i = 0; i < count.length; i++)
			if(count[i] == 0)
				throw new Exception();
	}
	

	public void setFlage(String pid, String spid, int isOnSpot) {
//		根据项目号和专家ID改变专家评分标识
		String sql = "UPDATE\r\n" + 
				"	pspgmember \r\n" + 
				"SET\r\n" + 
				"	pspgmember.FLAGE = 1\r\n" + 
				"WHERE\r\n" + 
				"	pspgmember.PSPID = ?\r\n" + 
				"	AND pspgmember.PSPGID IN(SELECT pspgroup.PSPGID from PSPgroup WHERE pspgroup.pid = ? AND pspgroup.IsOnSpot = ?)";
		template.update(sql, spid, pid, isOnSpot);
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

	public String setCommitPrBySpid(String pid, String spid, String nowRate) throws Exception {
		String sql = "UPDATE pcm SET nowRate=?, commState='已提交' WHERE pid=? AND sid=?";
		System.out.println(template.update(sql, nowRate, pid, spid) + " : " + pid + " : " + spid);
		sql = "SELECT MID FROM PSpecialists WHERE PSPID=? LIMIT 1";
		return template.queryForObject(sql, String.class, spid);
	}

	public void tryToSetDmana(String ProvinceMid, String nowRate, String pid) throws Exception {
		String sql = "SELECT pid FROM pcm WHERE pid=? AND pmid=? AND commState='未提交' AND sid LIKE 'P%' LIMIT 1";
		try {
			template.queryForMap(sql, pid, ProvinceMid);
		} catch (Exception e) {
//			sql = "UPDATE pcm SET nowRate=?, commState='已提交' WHERE pid=? AND sid=?";
//			if(0 == template.update(sql, nowRate, pid, ProvinceMid))
//				throw new Exception();
			
			sql = "UPDATE pcm SET nowRate=? WHERE pid=?";
			template.update(sql, nowRate, pid);
		}
	}
	
	public String findManaIdBySpid(String spid) throws Exception{
		String sql = "SELECT mid FROM pspecialists WHERE pspid =? LIMIT 1";
		return template.queryForObject(sql, String.class, spid);
	}


}
