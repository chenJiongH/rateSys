package dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import domain.Schools;
import util.JDBCUtils;

public class GroupSchoolTaskM {
	JdbcTemplate template = JDBCUtils.getTemplate();

	public List<Map<String, Object>> findProjects(String mid, String tid) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowTime = dateFormat.format(new Date());
		String sql = "SELECT "
					+ " DISTINCT pro.pid, "
					+ " pro.Pname "
					+ "FROM "
					+ " Projects pro, "
					+ " pcm "
					+ "WHERE "
					+ " pro.petime>? "
					+ " AND pro.PisStart IN ('true', 'TRUE') "
					+ " AND pro.pid = pcm.pid ";
		if(tid.charAt(0) != 'P')
					sql += " AND pcm.sid = ?";
		else 
			sql += " AND pcm.pmid = ? AND (pro.Pprocess LIKE '%省%' OR pro.pprocess LIKE '%抽查')";
		return template.queryForList(sql, nowTime, mid);
	}

	public List<Map<String, Object>> findPSpgByMid(String mid, String pid) {
		String sql = "SELECT "
					+ " PSPGID gid, "
					+ " SPGname gname "
					+ "FROM "
					+ " PSPgroup "
					+ "WHERE "
					+ " pid=? "
					+ " AND mid=?";
		return template.queryForList(sql, pid, mid);
	}
	
	public List<Map<String, Object>> findCSpgByMid(String mid, String pid) {
		String sql = "SELECT "
					+ " CSPGID gid, "
					+ " SPGname gname "
					+ "FROM "
					+ " CSPgroup "
					+ "WHERE "
					+ " pid=? "
					+ " AND mid=?";
		return template.queryForList(sql, pid, mid);
	}	
	
	public List<Map<String, Object>> findDSpgByMid(String mid, String pid) {
		String sql = "SELECT "
					+ " DSPGID gid, "
					+ " SPGname gname "
					+ "FROM "
					+ " DSPgroup "
					+ "WHERE "
					+ " pid=? "
					+ " AND mid=?";
		return template.queryForList(sql, pid, mid);
	}

	public List<Schools> findRemainSchFromPidMid(String mid, String pid, String tid, String gid) {
		String midType = "pmid";
		if(tid.charAt(0) == 'C')
			midType = "cmid";
		else if(tid.charAt(0) == 'D')
			midType = "dmid";
		String sql = "SELECT "
					+ " pcm.sid, "
					+ " schools.sname "
					+ "FROM "
					+ " pcm "
					+ " LEFT JOIN groupschool gs ON gs.pid = pcm.pid AND gs.sid=pcm.sid AND gs.mid=?";
					// 如果是省级，还得区分专家组是省级专家组还是抽查级别专家组,嵌套子查询 查找该gid是哪个级别的专家组。第二个等于号是指查找系统级别专家组的。如果学校被分配的专家组级别不同，则该专家组不需要再次分配该学校
				if(tid.charAt(0) == 'P')
					sql += " LEFT JOIN pspgroup ON pspgroup.pspgid=gs.gid AND pspgroup.isOnSpot = (SELECT isOnSpot FROM pspgroup WHERE pspgid='"+gid+"' LIMIT 1)";
				sql += " LEFT JOIN schools ON schools.sid = pcm.sid "
					+ "WHERE "
					+ " pcm.pid ='"+pid+"' "
					+ " AND pcm."+midType+"='"+mid+"'"
					+ " AND pcm.sid LIKE 'S%' "
					// 该项目下的该学校还没有被当前管理员所分配
					+ " AND (gs.sid IS NULL ";
				if(tid.charAt(0) == 'P')
					sql += "  OR (gs.sid IS NOT NULL AND pspgroup.spgname IS NULL))";
				else 
					sql += ")";
				return template.query(sql, new BeanPropertyRowMapper<Schools>(Schools.class), mid);
	}
	
	/**
	 * 返回已经分配记录的专家组名称，项目名称，校名称。
	 * @param mid
	 * @param pid
	 * @param gid
	 * @return
	 */
	public List<Map<String, Object>> findPGroupSchoolByGidSid(String mid, String pid, String gid) {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowTime = dateFormat.format(date);
		String sql = "SELECT "
					+ " sch.sid, "
					+ " sch.sname, "
					+ " spgroup.spgname, "
					+ " pr.pname "
					+ "FROM "
					+ " pspgroup spgroup, "
					+ " groupSchool gschool, "
					+ " projects pr, "
					+ " Schools sch "
					+ "WHERE "
					+ " gschool.pid='" + pid + "'"
					+ " AND gschool.gid='" + gid + "'"
					+ " AND gschool.mid='" + mid + "'"
					+ " AND spgroup.pspgid=gschool.gid "
					+ " AND pr.pid=gschool.pid "
					+ " AND sch.sid=gschool.sid "
					+ " AND pr.petime >= ?";													
		return template.queryForList(sql, nowTime);
	}

	public List<Map<String, Object>> findCGroupSchoolByGidSid(String mid, String pid, String gid) {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowTime = dateFormat.format(date);
		String sql = "SELECT "
					+ " sch.sid, "
					+ " sch.sname, "
					+ " spgroup.spgname, "
					+ " pr.pname "
					+ "FROM "
					+ " cspgroup spgroup, "
					+ " groupSchool gschool, "
					+ " projects pr, "
					+ " Schools sch "
					+ "WHERE "
					+ " gschool.pid=? "
					+ " AND gschool.gid=? "
					+ " AND gschool.mid=? "
					+ " AND spgroup.cspgid=gschool.gid "
					+ " AND pr.pid=gschool.pid "
					+ " AND sch.sid=gschool.sid"
					+ " AND pr.petime >= ?";
		return template.queryForList(sql, pid, gid, mid, nowTime);
	}

	public List<Map<String, Object>> findDGroupSchoolByGidSid(String mid, String pid, String gid) {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowTime = dateFormat.format(date);
		String sql = "SELECT "
					+ " sch.sid, "
					+ " sch.sname, "
					+ " spgroup.spgname, "
					+ " pr.pname "
					+ "FROM "
					+ " dspgroup spgroup, "
					+ " groupSchool gschool, "
					+ " projects pr, "
					+ " Schools sch "
					+ "WHERE "
					+ " gschool.pid=? "
					+ " AND gschool.gid=? "
					+ " AND gschool.mid=? "
					+ " AND spgroup.dspgid=gschool.gid "
					+ " AND pr.pid=gschool.pid "
					+ " AND sch.sid=gschool.sid"
					+ " AND pr.petime >= ?";
		return template.queryForList(sql, pid, gid, mid, nowTime);
	}

	public void batchInsert(List<String> sids, String pid, String mid, String gid) throws Exception {
		String sql = "INSERT INTO\r\n" + 
					"	groupschool\r\n" + 
					"   VALUES(?,?,?,?)";
		template.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, pid);
				ps.setString(2, gid);
				ps.setString(3, sids.get(i));
				ps.setString(4, mid);
			}
			
			@Override
			public int getBatchSize() {
				return sids.size();
			}
		});
	}

	public void batchDel(List<String> sids, String pid, String mid, String gid) throws Exception {
		String sql = "DELETE FROM " 
					+ "	groupschool " 
					+ "WHERE "
					+ " pid=? "
					+ " AND gid=? "
					+ " AND sid=? "
					+ " AND mid=? ";
		template.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, pid);
				ps.setString(2, gid);
				ps.setString(3, sids.get(i));
				ps.setString(4, mid);
			}
			
			@Override
			public int getBatchSize() {
				return sids.size();
			}
		});
	}

	public boolean checkPrByExistGroup(String pid, String tid, String mid) {
		try {
			String sql = "";
			// 找得到该项目的专家组，则该专家组可以分配项目下的学校
			if(tid.charAt(0) == 'P') {
				sql = "SELECT * FROM pspgroup WHERE pid=? AND mid=? LIMIT 1";
			} else if(tid.charAt(0) == 'C') {
				sql = "SELECT * FROM cspgroup WHERE pid=? AND mid=? LIMIT 1";
			} else if(tid.charAt(0) == 'D') {
				sql = "SELECT * FROM dspgroup WHERE pid=? AND mid=? LIMIT 1";
			}
			template.queryForMap(sql, pid, mid);
			// 找得到该项目的专家组，则该专家组可以分配项目下的学校
			return true;
		} catch (Exception e) {
			return false;
		}
			
	}
	/**
	 * 传入pid和管理id。查看该项目是否可以评分pcm表中该项目下的，以当前管理员id为mid项，并且sid为m开头（当前管理员的下属管理员）都处于已经提交状态，该项目才可以看见。
	 * @param pid
	 * @param mid
	 * @return
	 */
	public boolean checkPrByExistOneSubManaNotCommit(String pid, String mid) {
		try {
			String sql = "SELECT pid, sid "
						+ "FROM pcm "
						+ "WHERE pid=? "
						+ " AND (cmid=? OR dmid=? OR pmid=?) "
						// 当前管理员的下属管理员和学校都已经提交
						+ " AND (sid like 'S%' OR sid like 'M%') "
						+ " AND commState='未提交' "
						+ " LIMIT 1";
			template.queryForMap(sql, pid, mid, mid, mid);
			return true;
		} catch (Exception e) {	
//			e.printStackTrace();
			return false;
		}
	}
}
