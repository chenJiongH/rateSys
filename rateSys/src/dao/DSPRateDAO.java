package dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import util.JDBCUtils;

public class DSPRateDAO {

	JdbcTemplate template = new JdbcTemplate(JDBCUtils.getDataSource());

	public List<Map<String, Object>> findPr(String spid, String nowTime) throws Exception {
		String sql = "SELECT  \r\n" + 
				"	DISTINCT pr.pid, pr.pname, pcm.commState \r\n" + 
				"FROM \r\n" + 
				"	pcm, \r\n" + 
				"	Projects pr \r\n" + 
				"WHERE \r\n" + 
				"	pcm.sid = ?\r\n" + 
				"	AND pcm.PID = pr.PID \r\n" + 
				"	AND ? BETWEEN pr.PSTIME AND pr.PETIME  \r\n" + 
				"	AND pr.PISSTART IN('TRUE','true')";
		return template.queryForList(sql, spid, nowTime);
	}

	public Boolean checkSchByPid(String pid) {
		try {
			String sql = "SELECT \r\n" + 
					"	sid\r\n" + 
					"FROM\r\n" + 
					"	pspgsch\r\n" + 
					"WHERE\r\n" + 
					"	PID = ?\r\n" + 
					"	AND FLAGE != 11";
			List<Map<String, Object>> notReadySch = template.queryForList(sql, pid);
			//该项目下找不到未完成自评的学校，则该项目可以被县级专家评分
			if(notReadySch != null && notReadySch.isEmpty())
				return true;
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

	public Map<String, Object> findSP(String spid) throws Exception{
		String sql = "SELECT\r\n" + 
				" dspecialists.spname,\r\n" + 
				" dspecialists.sporganization spo\r\n" + 
				"FROM\r\n" + 
				"	dspecialists\r\n" + 
				"WHERE\r\n" + 
				"	dspecialists.DSPID = ?";
		return template.queryForMap(sql, spid);
	}

	public List<Map<String, Object>> findSchByPid(String pid, String dmid, String spid) throws Exception{
		String sql = "SELECT  \r\n" + 
				"	sch.sid, \r\n" + 
				"	sch.sname, \r\n" + 
				"	selfreport.reportlocation,\r\n" + 
				"	selfreport.overallFileName\r\n" + 
				"FROM  \r\n" + 
				"	Schools sch, \r\n" + 
				"	selfreport, \r\n"
				+ " DSPGmember spm,\r\n"
				+ " DSPgroup spg,"
				+ " groupSchool gSchool " + 
				"WHERE \r\n" + 
				"	spm.DSPID = ?\r\n" + 
				"	AND spm.DSPGID = spg.DSPGID\r\n"
				+ " AND spg.pid = ?\r\n"
				+ " AND gSchool.pid = spg.pid\r\n"
				+ " AND gSchool.gid = spg.DSPGID\r\n"
				+ " AND gSchool.mid = ?\r\n" + 
				"	AND gSchool.sid = sch.sid\r\n" + 
				"	AND selfreport.pid = gSchool.pid\r\n" + 
				"	AND selfreport.sid = sch.sid";
		return template.queryForList(sql, spid, pid, dmid);
	}

	public Collection<? extends Map<String, Object>> findCByPidSpidSid(String spid, String pid, String sid) throws Exception{
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
				"	spscore.schoolscore,\r\n" + 
				"	spscore.description,\r\n" + 
				"	spscore.districtScore,\r\n" + 
				"	spscore.districtExplain,\r\n"
				+ " spscore.description\r\n" + 
				"FROM\r\n" + 
				"	dspgmember spm,\r\n" + 
				"	dspgroup spg,\r\n" + 
				"	dspginx spginx,\r\n" + 
				"	projects pr,\r\n" + 
				"	acriterion a,\r\n" + 
				"	bcriterion b,\r\n" + 
				"	ccriterion c,\r\n" + 
				"	spscore \r\n" + 
				"WHERE \r\n" + 
				"	spm.DSPID = ?\r\n" + 
				"	AND spm.DSPGID = spg.DSPGID\r\n" + 
				"	AND spg.PID = ?\r\n" + 
				"	AND spg.dspgid = spginx.DSPGID\r\n" + 
				"	AND spm.dspid = spginx.dspid\r\n" + 
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

	public int check(String spid, String pid) {
		String sql = "SELECT\r\n" + 
				"	spm.FLAGE\r\n" + 
				"FROM\r\n" + 
				"	DSPGmember spm,\r\n" + 
				"	DSPgroup spg\r\n" + 
				"WHERE\r\n" + 
				"	spg.PID = ?\r\n" + 
				"	AND spm.DSPID = ?\r\n" + 
				"	AND spg.DSPGID = spm.DSPGID"
				+ " LIMIT 1";
		return template.queryForObject(sql, Integer.class, pid, spid);
	}

	public void batchUpdate(Map<String, Object> cs, String spid) throws Exception{
		// 获取所有的分数id
		 Set<String> sidcidSet = new HashSet<>();
		for (String sidcid : cs.keySet()) {
			if("pid".equals(sidcid) || "draft".equals(sidcid))
				continue;
			if(sidcid.charAt(0) != '0')
				sidcidSet.add(sidcid);
			if(sidcid.charAt(0) == '0')
				sidcidSet.add(sidcid.substring(1));
		}
		Object[] sidcidList = sidcidSet.toArray();
		//开启事务
		String sql = "UPDATE\r\n" + 
				"	SPScore\r\n" + 
				"SET\r\n" + 
				"	spscore.DISTRICTSCORE = ?,\r\n" + 
				"	spscore.DISTRICTEXPLAIN = ?,\r\n" + 
				"	spscore.DSPID = ?\r\n" + 
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

	public void setFlage(String pid, String spid) {
//		根据项目号和专家ID改变专家评分标识
		String sql = "UPDATE\r\n" + 
				"	dspgmember \r\n" + 
				"SET\r\n" + 
				"	dspgmember.FLAGE = 1\r\n" + 
				"WHERE\r\n" + 
				"	dspgmember.DSPID = ?\r\n" + 
				"	AND dspgmember.DSPGID IN(SELECT dspgroup.DSPGID from DSPgroup WHERE dspgroup.pid = ?)";
		template.update(sql, spid, pid);
	}

	public void setloginedPrBySpid(String pid, String spid) throws Exception {
		String sql = "UPDATE pcm SET commState='已登录' WHERE pid=? AND sid=? AND commState='未提交'";
		template.update(sql, pid, spid);
	}

	public String setCommitPrBySpid(String pid, String spid, String nowRate) throws Exception {
		String sql = "UPDATE pcm SET nowRate=?, commState='已提交' WHERE pid=? AND sid=?";
		template.update(sql, nowRate, pid, spid);
		sql = "SELECT MID FROM DSpecialists WHERE DSPID=? LIMIT 1";
		return template.queryForObject(sql, String.class, spid);
	}

	public void tryToSetDmana(String distMid, String nowRate, String pid) throws Exception {
		String sql = "SELECT pid FROM pcm WHERE pid=? AND dmid=? AND commState='未提交' AND sid LIKE 'D%' LIMIT 1";
		try {
			template.queryForMap(sql, pid, distMid);
		} catch (Exception e) {
			// 修改自己的管理员记录
			sql = "UPDATE pcm SET nowRate=?, commState='已提交' WHERE pid=? AND sid=?";
			if(0 == template.update(sql, nowRate, pid, distMid))
				throw new Exception();
			// 如果该县所有管理员都已经提交，则该项目整体流程往上跑一步（包括上一级的管理员记录），否则只是该校跟着往上跑一步
			// findOneNotCommitDmana 查找一个未提交的管理员记录
			sql = "SELECT "
				+ "	pid "
				+ "FROM "
				+ "	pcm "
				+ "WHERE "
				+ " pid=? "
				+ "AND sid LIKE 'M%' "
				// 当前县id为空，下一级别id不为空，则该记录是县级管理员记录
				+ "AND dmid IS NULL ";
			switch(nowRate) {
			case "市":
				sql += "AND cmid IS NOT NULL ";
				break;
			case "结束":
			case "抽查":
			case "省":
				sql += "AND pmid IS NOT NULL ";
				break;
			}
			sql += "AND commState='未提交' LIMIT 1";
			try{
				// 查找不到未提交的县管理员记录，抛出异常
				template.queryForMap(sql, pid);
				// 并非所有县管理员都提交了，则只修改该县管理的所有学校
				sql = "UPDATE pcm SET nowRate=? WHERE pid=? AND dmid=?";
				template.update(sql, nowRate, pid, distMid);
			} catch (Exception e1) {
				// 所有县都提交了该项目，则无论是上次管理员 还是下级都直接设置级别为当前记录
				sql = "UPDATE"
					+ "	pcm "
					+ "SET "
					+ " nowRate=? "
					+ "WHERE "
					+ " pid=?";
				template.update(sql, nowRate, pid);
			}
		}
	}

	public String findDistIdBySpid(String spid)  throws Exception{
		String sql = "SELECT mid FROM DSpecialists dsp WHERE dsp.DSPID=? LIMIT 1";
		return template.queryForObject(sql, String.class, spid);
	}

	public String findManaIdBySpid(String spid) throws Exception{
		String sql = "SELECT mid FROM dspecialists WHERE dspid =? LIMIT 1";
		return template.queryForObject(sql, String.class, spid);
	}

}

