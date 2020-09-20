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

public class CSPRateDAO {
	JdbcTemplate template = JDBCUtils.getTemplate();

	public List<Map<String, Object>> findPr(String spid, String nowTime) {
		//根据市专家id查找该市专家 的所有当前已开启 并且 流程包含市的项目
		String sql = "SELECT  \r\n" + 
				"	DISTINCT pr.pid, pr.pname,pr.pprocess, pcm.commState \r\n" + 
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

	public boolean checkAllSchByPid(String pid) {
		try {
			//根据项目id 并且市流程下为校（即流程中没有县） 查找该项目下是否有未完成评分的学校
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

	public Map<String, Object> findSP(String spid) {
		//根据项目id查找该专家的姓名、单位
		String sql = "SELECT\r\n" + 
				"	sp.spname,\r\n" + 
				"	sp.sporganization\r\n" + 
				"FROM\r\n" + 
				"	Cspecialists sp\r\n" + 
				"WHERE\r\n" + 
				"	sp.cspid = ?\r\n" + 
				"	LIMIT 1";
		return template.queryForMap(sql, spid);
	}

	public List<Map<String, Object>> findSchByPidCmid(String pid, String mid, String spid) {
		//根据已有pid，查找某个pid下的专家组，获得该专家组下的所有学校及其各学校的总评附件
		String sql = "SELECT  \r\n" + 
				"	sch.sid, \r\n" + 
				"	sch.sname, \r\n" + 
				"	selfreport.reportlocation,\r\n" + 
				"	selfreport.overallFileName\r\n" + 
				"FROM  \r\n" + 
				"	Schools sch, \r\n" + 
				"	selfreport, \r\n"
				+ " CSPGmember spm,\r\n"
				+ " CSPgroup spg,"
				+ " groupSchool gSchool " + 
				"WHERE \r\n" + 
				"	spm.CSPID = ?\r\n" + 
				"	AND spm.CSPGID = spg.CSPGID\r\n"
				+ " AND spg.pid = ?\r\n"
				+ " AND gSchool.pid = spg.pid\r\n"
				+ " AND gSchool.gid = spg.CSPGID\r\n"
				+ " AND gSchool.mid = ?\r\n" + 
				"	AND gSchool.sid = sch.sid\r\n" + 
				"	AND selfreport.pid = gSchool.pid\r\n" + 
				"	AND selfreport.sid = sch.sid";
		return template.queryForList(sql, spid, pid, mid);
	}

	public Collection<? extends Map<String, Object>> findCByPidSpidSid(String spid, String pid, String sid) {
		//根据已有专家id和pid,把该专家组下面的学校一个一个进行添加专家所有C指标。有多个指标
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
				"	spscore.schoolscore,\r\n" + 
				"	spscore.description,\r\n" + 
				"	spscore.districtScore,\r\n" + 
				"	spscore.districtExplain,\r\n" + 
				"	spscore.cityScore,\r\n" + 
				"	spscore.cityExplain\r\n" +
				"FROM\r\n" + 
				"	cspgmember spm,\r\n" + 
				"	cspgroup spg,\r\n" + 
				"	cspginx spginx,\r\n" + 
				"	projects pr,\r\n" + 
				"	acriterion a,\r\n" + 
				"	bcriterion b,\r\n" + 
				"	ccriterion c,\r\n" + 
				"	spscore\r\n" + 
				"WHERE \r\n" + 
				"	spm.cSPID = ?\r\n" + 
				"	AND spm.cSPGID = spg.cSPGID\r\n" + 
				"	AND spg.PID = ?\r\n" + 
				"	AND spg.cspgid = spginx.cSPGID\r\n" + 
				"	AND spm.cspid = spginx.cspid\r\n" + 
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
				"	CSPGmember spm,\r\n" + 
				"	CSPgroup spg,\r\n" + 
				"	Projects pr\r\n" + 
				"WHERE\r\n" + 
				"	pr.PID = ?\r\n" + 
				"	AND spg.PID = pr.PID\r\n" + 
				"	AND spm.CSPID = ?\r\n" + 
				"	AND spg.CSPGID = spm.CSPGID";
		System.out.println(sql + " : " + pid + " : " + spid);
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
				"	spscore.cityScore = ?,\r\n" + 
				"	spscore.cityExplain = ?,\r\n" + 
				"	spscore.CSPID = ?\r\n" + 
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
				"	cspgmember \r\n" + 
				"SET\r\n" + 
				"	cspgmember.FLAGE = 1\r\n" + 
				"WHERE\r\n" + 
				"	cspgmember.CSPID = ?\r\n" + 
				"	AND cspgmember.CSPGID IN(SELECT cspgroup.CSPGID from CSPgroup WHERE cspgroup.pid = ?)";
		template.update(sql, spid, pid);
	}
	
	public String setCommitPrBySpid(String pid, String spid, String nowRate) throws Exception {
		String sql = "UPDATE pcm SET nowRate=?, commState='已提交' WHERE pid=? AND sid=?";
		template.update(sql, nowRate, pid, spid);
		sql = "SELECT MID FROM CSpecialists WHERE CSPID=? LIMIT 1";
		return template.queryForObject(sql, String.class, spid);
	}

	public void tryToSetDmana(String cityMid, String nowRate, String pid) throws Exception {
		String sql = "SELECT pid FROM pcm WHERE pid=? AND cmid=? AND commState='未提交' AND sid LIKE 'C%' LIMIT 1";
		try {
			template.queryForMap(sql, pid, cityMid);
		} catch (Exception e) {
			// 修改自己的管理员记录
			sql = "UPDATE pcm SET nowRate=?, commState='已提交' WHERE pid=? AND sid=?";
			if(0 == template.update(sql, nowRate, pid, cityMid))
				throw new Exception();
			// 如果该市所有管理员都已经提交，则该项目整体流程往上跑一步（包括上一级的管理员记录），否则只是该市下的县和校跟着往上跑一步
			// findOneNotCommitDmana 查找一个未提交的管理员记录
			sql = "SELECT "
				+ "	pid "
				+ "FROM "
				+ "	pcm "
				+ "WHERE "
				+ " pid=? "
				+ "AND sid LIKE 'M%' "
				// 当前管理员id为空，下一级id不为空，则是当前级别类型的管理员记录
				+ "AND cmid IS NULL ";
			sql += "AND commState='未提交' LIMIT 1";
			try{
				// 查找不到未提交的市管理员记录，抛出异常
				template.queryForMap(sql, pid);
				// 并非所有市管理员都提交了，则只修改该市管理的所有县、学校
				// 该管理员下面的所有县和校都前进一步
				sql = "UPDATE pcm SET nowRate=? WHERE pid=? AND cmid=?";
				template.update(sql, nowRate, pid, cityMid);
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

	public String findcityIdBySpid(String spid) {
		String sql = "SELECT mid FROM Cspecialists csp WHERE csp.CSPID=? LIMIT 1";
		return template.queryForObject(sql, String.class, spid);
	}

	public String findManaIdBySpid(String spid) {
		String sql = "SELECT mid FROM cspecialists WHERE cspid =? LIMIT 1";
		return template.queryForObject(sql, String.class, spid);
	}

}
