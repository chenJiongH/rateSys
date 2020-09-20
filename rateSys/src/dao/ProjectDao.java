package dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import domain.Archives;
import domain.Project;
import util.JDBCUtils;

public class ProjectDao {

	JdbcTemplate template = JDBCUtils.getTemplate();
	
	public  Project findOneByPname(String pname) throws Exception {
		System.out.println(pname);
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowTime = dateFormat.format(date);
		String sql = "select * from projects where pname=? AND ? BETWEEN PSTIME AND PETIME LIMIT 1";
		return template.queryForObject(sql, new BeanPropertyRowMapper<Project>(Project.class), pname, nowTime);
	}

	public Project findOneByPid(String pid) throws Exception {
		String sql = "select * from projects where pid=? LIMIT 1";
		return template.queryForObject(sql, new BeanPropertyRowMapper<Project>(Project.class), pid);
	}

	public Project findFirstOne() throws Exception {
		String sql = "select * from projects limit 1";
		return template.queryForObject(sql, new BeanPropertyRowMapper<Project>(Project.class));
	}

	public List<Project> findAllPro() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowTime = dateFormat.format(date);
		String sql = "select * from projects pr WHERE ? BETWEEN pr.PSTIME AND pr.PETIME";
		return template.query(sql, new BeanPropertyRowMapper<Project>(Project.class), nowTime);
	}

	public List<Project> findOnePagePj(int curpage) {
		int start = (curpage - 1) * 20;
		int end = 20;
		String sql = "select * from projects limit " + start + "," + end;
		return template.query(sql, new BeanPropertyRowMapper<Project>(Project.class));
	}

	public List<Project> queryPageBean(Project p, String stime, String etime) {
		String sql = "select * from projects where 1 = 1";
		if(p.getPdisplayExplain() != null) 
			sql += " AND PdisplayExplain IN('true','TRUE')";
		else sql += " AND PdisplayExplain IN('false','FALSE')";
		if(stime != null) 
			sql += " AND pstime>='" + stime + "'";
		if(etime != null) 
			sql += " AND petime<='" + etime + "'";
		if(p.getPisannex() != null) sql += " AND Pisannex IN('true','TRUE')";
		else  sql += " AND Pisannex IN('false','FALSE')";
		if(p.getPisstart() != null)	sql += " AND PisStart IN('true','TRUE')";
		else sql += " AND PisStart IN('flase','FALSE')";
		if(!"".equals(p.getPname()))
			sql += " AND Pname LIKE '%" + p.getPname() + "%'";
		if(!"".equals(p.getPprocess()))
			sql += " AND Pprocess='" + p.getPprocess() + "'";
		return template.query(sql, new BeanPropertyRowMapper<Project>(Project.class));
	}

	public Project findLastOneP() {
		try {
			String sql = "select * from projects order by pid desc limit 1";
			return template.queryForObject(sql, new BeanPropertyRowMapper<Project>(Project.class));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void insert(Project p) throws Exception{
		String sql = "insert into projects values(?,?,?,?,?,?,?,?)";
		template.update(sql, p.getPid(), p.getPname(), p.getPstime(), p.getPetime(), p.getPisannex(), p.getPisstart(), p.getPdisplayExplain(), p.getPprocess());
	}

	public void changePByPid(Project p) throws Exception {
		String sql = "update projects set pname=?,pisannex=?,pisstart=?,PdisplayExplain=?,PPROCESS=?,Pstime=?,Petime=? where pid=?";
		template.update(sql, p.getPname(), p.getPisannex(), p.getPisstart(), p.getPdisplayExplain(), p.getPprocess(), p.getPstime(), p.getPetime(), p.getPid());
	}

	public void delPByPid(String pid) throws Exception{
		String sql = "delete from projects where pid=?";
		template.update(sql, pid);
	}

	public List<Project> findPnameByPname(String pname) {
		String sql = "select pid from projects where pname=? LIMIT 2";
		return template.query(sql, new BeanPropertyRowMapper<Project>(Project.class), pname);
	}
	/**
	 * UNION ALL 连接县、市、省三张专家组表
	 * @param pid
	 * @return 所有的专家组id
	 */
	public List<Map<String, Object>> findAllGroupByPid(String pid) {
		String sql = "SELECT\r\n" + 
				"	PSPGID spgid\r\n" + 
				"FROM\r\n" + 
				"	pspgroup\r\n" + 
				"WHERE\r\n" + 
				"	PID = ?\r\n" + 
				"UNION ALL\r\n" + 
				"SELECT\r\n" + 
				"	CSPGID spgid\r\n" + 
				"FROM\r\n" + 
				"	cspgroup\r\n" + 
				"WHERE\r\n" + 
				"	PID = ?\r\n" + 
				"UNION ALL\r\n" + 
				"SELECT\r\n" + 
				"	DSPGID spgid\r\n" + 
				"FROM\r\n" + 
				"	dspgroup\r\n" + 
				"WHERE\r\n" + 
				"	PID = ?";
		return template.queryForList(sql, pid, pid, pid);
	}
	
	// 查找当前该县下面的所有流程包括县的项目
	public List<Project> findAllDMPro(String mid,String tid) {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowTime = dateFormat.format(date);
		String sql = "SELECT\r\n" + 
				"	DISTINCT pr.*\r\n" + 
				"FROM\r\n" + 
				"	Projects pr,\r\n" + 
				"	pcm,\r\n" + 
				"	schools sch\r\n" + 
				"WHERE\r\n" + 
				"	Pprocess LIKE '%县%'\r\n" + 
				"	AND pcm.sid = sch.sid \r\n" + 
				"	AND sch.did = ?\r\n" + 
				"   AND petime >= ?";
		return template.query(sql, new BeanPropertyRowMapper<Project>(Project.class), tid, nowTime);
	}
	
	// 查找当前该省下面的所有流程包括省、抽查的项目
	public List<Project> findAllPMPro(String mid,String tid) {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowTime = dateFormat.format(date);
		String sql = "SELECT \r\n" + 
				"	DISTINCT pr.* \r\n" + 
				"FROM \r\n" + 
				"	Projects pr,\r\n" + 
				"	pcm  \r\n" + 
				"WHERE \r\n" + 
				"	(Pprocess LIKE '%省%' \r\n" + 
				"	OR Pprocess LIKE '%抽查%')\r\n" + 
				"	AND pcm.pid = pr.pid"
				+ " AND petime >= ?";
		return template.query(sql, new BeanPropertyRowMapper<Project>(Project.class), nowTime);
	}
	
	// 查找当前该市下面的所有流程包括市的项目
	public List<Project> findAllCMPro(String mid,String tid) {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowTime = dateFormat.format(date);
		String sql = "SELECT\r\n" + 
				"	DISTINCT pr.*\r\n" + 
				"FROM\r\n" + 
				"	Projects pr,\r\n" + 
				"	pcm,\r\n" + 
				"	schools sch,\r\n" + 
				"	districts dist\r\n" + 
				"WHERE\r\n" + 
				"	Pprocess LIKE '%市%'\r\n" + 
				"	AND pcm.sid = sch.sid \r\n" + 
				"	AND sch.did = dist.did\r\n" + 
				"	AND dist.CID = ?\r\n" + 
				"   AND petime >= ?";
		return template.query(sql, new BeanPropertyRowMapper<Project>(Project.class), tid, nowTime);
	}

	public boolean checkPpcm(String pid, String process) {
		try {
			// 查看项目流程中 省级的前一级流程是什么，则该前一级流程的管理员必须得 全部完成评分
			int provinceIndex = process.indexOf("省");
			if(provinceIndex == -1)
				provinceIndex = process.indexOf("抽查");
			String nowRate = "";
			nowRate = process.substring(provinceIndex - 2, provinceIndex - 1);
			
			String sql = "SELECT\r\n" + 
					"	*\r\n" + 
					"FROM\r\n" + 
					"	pcm\r\n" + 
					"WHERE\r\n" + 
					"	commState = '未提交'\r\n" + 
					"	AND sid LIKE 'M%'\r\n" + 
					"	AND pid = ?\r\n" + 
//					"	AND pmid = 'M00001'\r\n" + 
//					"	AND nowRate = '" + nowRate + "'\r\n" + 
					"	LIMIT 1";
			template.queryForMap(sql, pid);
			// 存在前一级未提交记录，不可分配高级专家
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	public boolean checkCpcm(String pid,String mid, String process) {
		try {
			// 查看项目流程中 省级的前一级流程是什么，则该前一级流程的管理员必须得 全部完成评分
			
			String sql = "SELECT\r\n" + 
					"	*\r\n" + 
					"FROM\r\n" + 
					"	pcm\r\n" + 
					"WHERE\r\n" + 
					"	commState = '未提交'\r\n" + 
					"	AND sid LIKE 'M%'\r\n" + 
					"	AND pid = ?\r\n" + 
					"	AND cmid = ?\r\n" + 
					"	LIMIT 1";
			template.queryForMap(sql, pid, mid);
			// 存在前一级未提交记录，不可分配高级专家
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	public boolean checkDschpcm(String pid, String mid) {
		try {
			String sql = "SELECT\r\n" + 
					"	*\r\n" + 
					"FROM\r\n" + 
					"	pcm\r\n" + 
					"WHERE\r\n" + 
					"	commState = '未提交'\r\n" + 
					"	AND sid LIKE 'S%'\r\n" + 
					"	AND pid = ?\r\n" + 
					"	AND dmid = ?\r\n" + 
					"	LIMIT 1";
			template.queryForMap(sql, pid, mid);
			// 存在前一级未提交记录，不可分配高级专家
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	public boolean checkCschpcm(String pid, String mid) {
		try {
			String sql = "SELECT\r\n" + 
					"	*\r\n" + 
					"FROM\r\n" + 
					"	pcm\r\n" + 
					"WHERE\r\n" + 
					"	commState = '未提交'\r\n" + 
					"	AND sid LIKE 'S%'\r\n" + 
					"	AND pid = ?\r\n"
					+ " AND cmid=?\r\n" + 
					"	LIMIT 1";
			template.queryForMap(sql, mid, pid);
			// 存在前一级未提交记录，不可分配高级专家
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	public boolean checkPschpcm(String pid) {
		try {
			String sql = "SELECT\r\n" + 
					"	*\r\n" + 
					"FROM\r\n" + 
					"	pcm\r\n" + 
					"WHERE\r\n" + 
					"	commState = '未提交'\r\n" + 
					"	AND sid LIKE 'S%'\r\n" + 
					"	AND pid = ?\r\n" + 
					"	LIMIT 1";
			template.queryForMap(sql, pid);
			// 存在前一级未提交记录，不可分配高级专家
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	public boolean checkPschExist(String pid, String mid, String tid) {
		try {
			String sql = "SELECT\r\n" + 
					"	*\r\n" + 
					"FROM\r\n" + 
					"	pcm\r\n" + 
					"WHERE\r\n";
					if(tid.charAt(0) == 'P')
						sql += "   sid = ?\r\n";
					else if(tid.charAt(0) == 'C')
						sql += "   sid = ?\r\n";
					else if(tid.charAt(0) == 'D')
						sql += "   sid = ?\r\n";
					
					sql += "	AND pid = ?\r\n LIMIT 1";
			template.queryForMap(sql, mid, pid);
			// 存在管理员记录，证明有学校，可考虑分配高级专家
			return false;
		} catch(Exception e) {
			return true;
		}
	}

	public String findNowRateByPcmTidPid(String tid, String pid) {
		String sql = "SELECT nowRate FROM pcm WHERE sid=? AND pid=? LIMIT 1";
		return template.queryForObject(sql, String.class, tid, pid);
	}

	public void delCcriterionByPid(String pid) throws Exception {
		String sql = "DELETE \r\n" + 
				"	c \r\n" + 
				"FROM\r\n" + 
				"	ccriterion c,\r\n" + 
				"	bcriterion b,\r\n" + 
				"	acriterion a \r\n" + 
				"WHERE\r\n" + 
				"	a.pid = ? \r\n" + 
				"	AND a.aid = b.aid \r\n" + 
				"	AND c.bid = b.bid ";
		template.update(sql, pid);
	}

	public void delBcriterionByPid(String pid) throws Exception {
		String sql = "DELETE \r\n" + 
				"	b \r\n" + 
				"FROM\r\n" + 
				"	bcriterion b,\r\n" + 
				"	acriterion a \r\n" + 
				"WHERE\r\n" + 
				"	a.pid = ? \r\n" + 
				"	AND a.aid = b.aid ";
		template.update(sql, pid);
	}


	public void delAcriterionByPid(String pid) throws Exception {
		String sql = "DELETE \r\n" + 
				"	a \r\n" + 
				"FROM\r\n" + 
				"	acriterion a \r\n" + 
				"WHERE\r\n" + 
				"	a.pid = ? \r\n";
		template.update(sql, pid);
	}
	/**
	 * 根据项目id 查找该归档的字段。主要使用左连接
	 * @param pid
	 * @return
	 */
	public void InsertAllArchiveByPid(String pid) throws Throwable {
		String sql = "SELECT\r\n" + 
				"	a.aname,\r\n" + 
				"	b.bname,\r\n" + 
				"	c.cname,\r\n" + 
				"	c.score,\r\n" + 
				"	spscore.schoolScore,\r\n" + 
				"	spscore.description,\r\n" + 
				"	spscore.annexLocation,\r\n" + 
				"	cities.cname cityName,\r\n" + 
				"	dist.dname distName,\r\n" + 
				"	s.sname schoolName,\r\n" + 
				"	selfreport.reportLocation,\r\n"	+ 
				"   selfreport.overallFileName,\r\n" + 
				"	spscore.districtscore distScore,\r\n" + 
				"	spscore.districtexplain distExplain,\r\n" + 
				"	dsp.sporganization DspOrganization,\r\n" + 
				"	dsp.spname dspName,\r\n" + 
				"	dsp.spphone dspPhone,\r\n" + 
				"	spscore.CITYSCORE cityScore,\r\n" + 
				"	spscore.CITYEXPLAIN cityExplain,\r\n" + 
				"	csp.sporganization CspOrganization,\r\n" + 
				"	csp.spname cspName,\r\n" + 
				"	csp.spphone cspPhone,\r\n" + 
				"	spscore.PROSCORE pspScore,\r\n" + 
				"	spscore.PROEXPLAIN pspExplain,\r\n" + 
				"	psp1.sporganization PspOrganization,\r\n" + 
				"	psp1.spname pspName,\r\n" + 
				"	psp1.spphone pspPhone,\r\n" + 
				"	spscore.onSpotScore checkScore,\r\n" + 
				"	spscore.onSpotExplain checkExplain,\r\n" + 
				"	psp2.sporganization checkspOrganization,\r\n" + 
				"	psp2.spname checkspName,\r\n" + 
				"	psp2.spphone checkPhone\r\n" + 
				"FROM\r\n" + 
				"	spscore\r\n" + 
				"	LEFT JOIN pspecialists psp1 ON psp1.PSPID = spscore.pspid \r\n" +
				"	LEFT JOIN pspecialists psp2 ON psp2.PSPID = spscore.onSpotID\r\n" +
				"	LEFT JOIN cspecialists csp ON csp.CSPID = spscore.CSPID\r\n" +
				"	LEFT JOIN dspecialists dsp ON dsp.DSPID = spscore.DSPID\r\n" +
				"	LEFT JOIN ccriterion c ON c.CID = spscore.CID\r\n" +
				"	LEFT JOIN bcriterion b ON b.BID = c.BID\r\n" +
				"	LEFT JOIN acriterion a ON (a.AID = b.AID AND a.pid = ?)\r\n" + 
				"	LEFT JOIN selfreport ON (selfreport.sid = spscore.sid AND selfreport.pid = ?)\r\n" + 
				"	LEFT JOIN schools s ON s.SID = spscore.SID\r\n" + 
				"	LEFT JOIN districts dist ON dist.did = s.did\r\n" + 
				"	LEFT JOIN cities ON cities.CID = dist.cid\r\n" +
				"WHERE \r\n" + 
				"	a.aname IS NOT NULL";
		List<Archives> archives = template.query(sql, new BeanPropertyRowMapper<Archives>(Archives.class), pid, pid);
		sql = "SELECT pname FROM projects WHERE pid =? LIMIT 1";
		String pname = template.queryForObject(sql, String.class, pid);
		sql = "INSERT INTO archives(pname, aname, bname, cname, score, schoolScore, description, annexLocation, "
								+ "cityName, distName, schoolName, reportLocation, distScore, distExplain, DspOrganization, "
								+ "dspName, dspPhone, cityScore, cityExplain, CspOrganization, cspName, cspPhone, pspScore, "
								+ "pspExplain, PspOrganization, pspName, pspPhone, checkScore, checkExplain, checkspOrganization, "
								+ "checkspName, checkPhone, overallFileName) "
								+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		for (Archives archive : archives) {
			archive.setPname(pname);
			template.update(sql, archive.getPname(), archive.getAname(), archive.getBname(), archive.getCname(), archive.getScore(), archive.getSchoolScore(), archive.getDescription(), archive.getAnnexLocation(),
								 archive.getCityName(), archive.getDistName(), archive.getSchoolName(), archive.getReportLocation(), archive.getDistScore(), archive.getDistExplain(), archive.getDspOrganization(),
								 archive.getDspName(), archive.getDspPhone(), archive.getCityScore(), archive.getCityExplain(), archive.getCspOrganization(), archive.getCspName(), archive.getCspPhone(), archive.getPspScore(),
								 archive.getPspExplain(), archive.getPspOrganization(), archive.getPspName(), archive.getPspPhone(), archive.getCheckScore(), archive.getCheckExplain(), archive.getCheckspOrganization(),
								 archive.getCheckspName(), archive.getCheckPhone(), archive.getOverallFileName());
		}
	}

	public void delPspgSchByPid(String pid) throws Throwable {
		String sql = "DELETE FROM pspgsch WHERE pid = ?";
		template.update(sql, pid);
	}
	
	public void delSpScoreByPid(String pid) throws Throwable {
		String sql = "DELETE\r\n" + 
				"	spscore\r\n" + 
				"FROM\r\n" + 
				"	spscore\r\n" + 
				"LEFT JOIN acriterion a ON a.PID = ?\r\n" + 
				"LEFT JOIN bcriterion b ON b.aid = a.aid\r\n" + 
				"LEFT JOIN ccriterion c ON (c.cid = spscore.cid AND c.bid = b.bid)\r\n" + 
				"WHERE \r\n" + 
				"	c.cid IS NOT NULL\r\n";
		template.update(sql, pid);
	}

	public void delSelfReportByPid(String pid) throws Throwable {
		String sql = "DELETE FROM selfreport WHERE pid = ?";
		template.update(sql, pid);
	}

	public void delPcmByPid(String pid) {
		String sql = "DELETE FROM pcm WHERE pid = ? AND sid like 'S%'";
		template.update(sql, pid);
	}
	public void delPcmAllByPid(String pid) {
		String sql = "DELETE FROM pcm WHERE pid = ?";
		template.update(sql, pid);
	}
	
	public void delGroupSchByPid(String pid) {
		String sql = "DELETE FROM groupschool WHERE pid = ? ";
		template.update(sql, pid);
	}


}
