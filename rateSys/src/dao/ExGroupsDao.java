package dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import domain.CSPgroup;
import domain.DSPgroup;
import domain.PSPgroup;
import util.JDBCUtils;

public class ExGroupsDao {

	JdbcTemplate template = JDBCUtils.getTemplate();
	
	public List<PSPgroup> findPagePByMid(int curpage, String mid) {
		int start = (curpage - 1) * 20;
		int end = 20;
		String sql  = "SELECT * FROM PSPgroup where mid=? limit ?,?";
		return template.query(sql, new BeanPropertyRowMapper<PSPgroup>(PSPgroup.class), mid, start, end);
	}
	public List<CSPgroup> findPageCByMid(int curpage, String mid) {
		int start = (curpage - 1) * 20;
		int end = 20;
		String sql  = "SELECT * FROM CSPgroup where mid=? limit ?,?";
		return template.query(sql, new BeanPropertyRowMapper<CSPgroup>(CSPgroup.class), mid, start, end);
	}
	public List<DSPgroup> findPageDByMid(int curpage, String mid) {
		int start = (curpage - 1) * 20;
		int end = 20;
		String sql  = "SELECT * FROM DSPgroup where mid=? limit ?,?";
		return template.query(sql, new BeanPropertyRowMapper<DSPgroup>(DSPgroup.class), mid, start, end);
	}
	public PSPgroup findLastOnePGroup() {
		try {
			String sql = "SELECT * FROM PSPgroup ORDER BY pspgid DESC limit 1";
			return template.queryForObject(sql, new BeanPropertyRowMapper<PSPgroup>(PSPgroup.class));
			//没有找到值返回空
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public CSPgroup findLastOneCGroup() {
		try {
			String sql = "SELECT * FROM CSPgroup ORDER BY cspgid DESC limit 1";
			return template.queryForObject(sql, new BeanPropertyRowMapper<CSPgroup>(CSPgroup.class));
			//没有找到值返回空
		} catch (Exception e) {
			return null;
		}
	}
	public DSPgroup findLastOneDGroup() {
		try {
			String sql = "SELECT * FROM DSPgroup ORDER BY dspgid DESC limit 1";
			return template.queryForObject(sql, new BeanPropertyRowMapper<DSPgroup>(DSPgroup.class));
			//没有找到值返回空
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void insertOnePGroup(PSPgroup group, int isOnSpot) throws Exception{
		String sql = "INSERT INTO PSPgroup VALUES(?,?,?,?,?)";
		template.update(sql, group.getPspgid(), group.getSpgname(), group.getPid(), group.getMid(), isOnSpot);
	}
	
	public void insertOneCGroup(PSPgroup group) throws Exception{
		String sql = "INSERT INTO CSPgroup VALUES(?,?,?,?)";
		template.update(sql, group.getPspgid(), group.getSpgname(), group.getPid(), group.getMid());
	}
	
	public void insertOneDGroup(PSPgroup group, int isOnSpot) throws Exception{
		String sql = "INSERT INTO DSPgroup VALUES(?,?,?,?)";
		template.update(sql, group.getPspgid(), group.getSpgname(), group.getPid(), group.getMid());
	}
	public List<PSPgroup> queryPagePByPMN(PSPgroup group) throws Exception{
		String sql = "SELECT * FROM PSPgroup WHERE mid='" + group.getMid() + "'";
		if(!"".equals(group.getPid()))
			sql += " AND pid='" + group.getPid() + "'";
		if(!"".equals(group.getSpgname())) 
			sql += " AND spgname like '%" + group.getSpgname() + "%'";
		return template.query(sql, new BeanPropertyRowMapper<PSPgroup>(PSPgroup.class));
	}
	public List<CSPgroup> queryPageCByPMN(PSPgroup group) throws Exception{
		String sql = "SELECT * FROM CSPgroup WHERE mid='" + group.getMid() + "'";
		if(!"".equals(group.getPid()))
			sql += " AND pid='" + group.getPid() + "'";
		if(!"".equals(group.getSpgname())) 
			sql += " AND spgname like '%" + group.getSpgname() + "%'";
		return template.query(sql, new BeanPropertyRowMapper<CSPgroup>(CSPgroup.class));
	}
	public List<DSPgroup> queryPageDByPMN(PSPgroup group) throws Exception{
		String sql = "SELECT * FROM DSPgroup WHERE mid='" + group.getMid() + "'";
		if(!"".equals(group.getPid()))
			sql += " AND pid='" + group.getPid() + "'";
		if(!"".equals(group.getSpgname()))
			sql += " AND spgname like '%" + group.getSpgname() + "%'";
		return template.query(sql, new BeanPropertyRowMapper<DSPgroup>(DSPgroup.class));
	}
	public void delOnePByGid(String gid) throws Exception{
		String sql = "DELETE FROM PSPgroup WHERE PSPGID=?";
		template.update(sql, gid);
	}
	public void delOneCByGid(String gid) throws Exception{
		String sql = "DELETE FROM CSPgroup WHERE CSPGID=?";
		template.update(sql, gid);
	}
	public void delOneDByGid(String gid) throws Exception{
		String sql = "DELETE FROM DSPgroup WHERE DSPGID=?";
		template.update(sql, gid);
	}
	public boolean checkGroupName(String spgname, String mid, String tid, String pid) {
		String sql = "";
		if(tid.charAt(0) == 'P') {
			sql = "SELECT\r\n" + 
					"	PSPGID\r\n" + 
					"FROM\r\n" + 
					"	PSPgroup\r\n" + 
					"WHERE\r\n" + 
					"	pspgroup.MID = '" + mid + "'\r\n" + 
					"	AND SPGNAME = '" + spgname + "'\r\n"
					+ " AND PID = '" + pid + "'" + 
							"	LIMIT 1";
		} else if(tid.charAt(0) == 'C') {
			sql = "SELECT\r\n" + 
					"	CSPGID\r\n" + 
					"FROM\r\n" + 
					"	CSPgroup\r\n" + 
					"WHERE\r\n" + 
					"	cspgroup.MID = '" + mid + "'\r\n" + 
					"	AND SPGNAME = '" + spgname + "'\r\n" +
					" AND PID = '" + pid + "'" +  
							"	LIMIT 1";
		} else if(tid.charAt(0) == 'D') {
			sql = "SELECT\r\n" + 
					"	DSPGID\r\n" + 
					"FROM\r\n" + 
					"	DSPgroup\r\n" + 
					"WHERE\r\n" + 
					"	Dspgroup.MID = '" + mid + "'\r\n" + 
					"	AND SPGNAME = '" + spgname + "'\r\n" + 
					" AND PID = '" + pid + "'" + 
							"	LIMIT 1";
		}
		try {
			template.queryForMap(sql);
			return false;
		} catch (Exception e) {
			return true;
		}
	}
	
	public List<Map<String, Object>> fuzzySelectP(String mid, String gName) {
		String sql = "SELECT * from PSPgroup WHERE MID = '" + mid + "' AND SPGNAME like '%" + gName + "%'";
		return template.queryForList(sql);
	}
	
	public List<Map<String, Object>> fuzzySelectC(String mid, String gName) {
		String sql = "SELECT * from CSPgroup WHERE MID = '" + mid + "' AND SPGNAME like '%" + gName + "%'";
		return template.queryForList(sql);
	}
	
	public List<Map<String, Object>> fuzzySelectD(String mid, String gName) {
		String sql = "SELECT * from DSPgroup WHERE MID = '" + mid + "' AND SPGNAME like '%" + gName + "%'";
		return template.queryForList(sql);
	}
	/**
	 * 判断修改之后有没有重名专家组
	 * @param groupName
	 * @param gid
	 * @param mid
	 */
	public void checkPRepeatName(String groupName, String gid, String mid) throws Exception {
		String sql = "Select * from PSPgroup WHERE MID=? AND PSPGID!=? AND SPGname=?";
		try {
			template.queryForMap(sql, mid, gid, groupName);
			throw new Exception("名称重复");
		} catch (Exception e) {
			return; 
		}
	}
	public void checkCRepeatName(String groupName, String gid, String mid) throws Exception {
		String sql = "Select * from CSPgroup WHERE MID=? AND CSPGID!=? AND SPGname=?";
		try {
			template.queryForMap(sql, mid, gid, groupName);
			throw new Exception("名称重复");
		} catch (Exception e) {
			return; 
		}
	}
	public void checkDRepeatName(String groupName, String gid, String mid) throws Exception {
		String sql = "Select * from DSPgroup WHERE MID=? AND DSPGID!=? AND SPGname=?";
		try {
			template.queryForMap(sql, mid, gid, groupName);
			throw new Exception("名称重复");
		} catch (Exception e) {
			return; 
		}
	}
	/**
	 * 改变专家组的名称
	 * @param tid
	 * @param mid
	 * @param groupName
	 * @param gid
	 * @param projectName
	 * @param isonspot
	 */
	public void changePData(String tid, String mid, String groupName, String gid, String projectName, int isonspot)  throws Exception{
		String sql = "UPDATE pspgroup SET spgname = ?,IsOnSpot=? WHERE pspgid=?";
		template.update(sql, groupName, isonspot, gid);
	}
	public void changeCData(String tid, String mid, String groupName, String gid, String projectName, int isonspot)  throws Exception{
		String sql = "UPDATE pspgroup SET spgname = ? WHERE pspgid=?";
		template.update(sql, groupName, gid);
	}
	public void changeDData(String tid, String mid, String groupName, String gid, String projectName, int isonspot) throws Exception{
		String sql = "UPDATE pspgroup SET spgname = ? WHERE pspgid=?";
		template.update(sql, groupName, gid);
	}
	
	public void delMultiPcmByPGidPid(String gid, String pid) throws Exception{
		String sql = "DELETE pcm FROM PSPgroup spg,PSPGmember sp,pcm WHERE spg.PSPGID=? AND spg.PSPGID = sp.PSPGID AND pcm.sid = sp.pspid AND pcm.pid=?";
		template.update(sql, gid, pid);
	}
	public void delMultiPcmByCGidPid(String gid, String pid) throws Exception{
		String sql = "DELETE pcm FROM CSPgroup spg,CSPGmember sp,pcm WHERE spg.CSPGID=? AND spg.CSPGID = sp.CSPGID AND pcm.sid = sp.Cspid AND pcm.pid=?";
		template.update(sql, gid, pid);
	}
	public void delMultiPcmByDGidPid(String gid, String pid) throws Exception{
		String sql = "DELETE pcm FROM DSPgroup spg,DSPGmember sp,pcm WHERE spg.DSPGID=? AND spg.DSPGID = sp.DSPGID AND pcm.sid = sp.Dspid AND pcm.pid=?";
		template.update(sql, gid, pid);
	}
	public void delMultiPMemberByPGidPid(String gid) throws Exception{
		String sql = "DELETE FROM PSPGmember WHERE PSPGID=?";
		template.update(sql, gid);
	}
	public void delMultiCMemberByPGidPid(String gid) throws Exception{
		String sql = "DELETE FROM CSPGmember WHERE CSPGID=?";
		template.update(sql, gid);
	}
	public void delMultiDMemberByPGidPid(String gid) throws Exception{
		String sql = "DELETE FROM DSPGmember WHERE DSPGID=?";
		template.update(sql, gid);
	}
	public void delMultiPspCriterion(String gid) throws Exception{
		String sql = "DELETE FROM pspginx WHERE pspgid = ?";
		template.update(sql, gid);
	}
	public void delMultiCspCriterion(String gid) throws Exception{
		String sql = "DELETE FROM cspginx WHERE cspgid = ?";
		template.update(sql, gid);
	}
	public void delMultiDspCriterion(String gid) throws Exception{
		String sql = "DELETE FROM dspginx WHERE dspgid = ?";
		template.update(sql, gid);
	}
}
