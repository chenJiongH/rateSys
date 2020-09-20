package dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import domain.Managers;
import util.JDBCUtils;

public class ManagersDao {
	
	JdbcTemplate template = JDBCUtils.getTemplate();
	//CCSService的createInitData方法中调用
	public void deleteTable() { 
		String sql = "delete from managers where mid!='M00001'";
		template.execute(sql);
	}
	//CCSService的createInitData方法中调用,插入生成的默认数据
	public void initDataInsert(String mid, String username, String password, String id) {
		String sql = "insert into managers(mid,Musername,Mpassword,TID) values(?,?,?,?)";
		template.update(sql, mid, username, password, id);
	}
	
	public boolean checkAll(List<Managers> managers) {
		
		try {
			for(Managers m : managers) {
				String sql = "select * from managers where mid=? and tid=?";
				//query方法没有找到值，则抛出异常
//				System.out.println(m.getMid()+m.getTid());
				template.queryForMap(sql, m.getMid(), m.getTid());
			}
			return true;			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	//AcMService的inDataByFile方法调用
	public boolean insertAll(List<Managers> managers) {
		try {
			String sql = "update managers set mname=?,musername=?,mpassword=?,mphono=?,tid=? where mid=?";
			//batchUpdate批量提交。减少sql语句的编译次数。一条语句更新错误则全部回滚
			int[] count =template.batchUpdate(sql, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					Managers m = managers.get(i);
				    ps.setString(1, m.getMname());
				    ps.setString(2, m.getMusername());
				    ps.setString(3, m.getMpassword());
				    ps.setString(4, m.getMphono());
				    ps.setString(5, m.getTid());
				    ps.setString(6, m.getMid());
				}
				@Override
				public int getBatchSize() {
					return managers.size();
				}
			});
			for(int i = 0; i < count.length; i++)
				if(count[i] == 0)
					return false;
		} catch (Exception e) {
			return false;
		} 
		return true;
	}
	
	public Managers findOndManByMid(String mid) throws Exception{
		String sql = "select * from managers where mid=? LIMIT 1";
		return template.queryForObject(sql, new BeanPropertyRowMapper<Managers>(Managers.class), mid);
	}
	
	public boolean updateOne(Managers m) {
		try {
			String sql = "update managers set mname=?,musername=?,mpassword=?,mphono=? where mid=?";
			template.update(sql, m.getMname(), m.getMusername(), m.getMpassword(), m.getMphono(), m.getMid());
			return true;
		} catch (Exception e) {
			return false;			
		} 
	}
	
	public List<Managers> findAllManas() throws Exception {
		String sql = "select * from Managers where mid!='M00001' order by tid";
		return template.query(sql, new BeanPropertyRowMapper<Managers>(Managers.class));	
	}
	
	public List<Managers> findManByPage(int curpage) throws Exception {
		int left = (curpage - 1) * 20 + 1;
		String sql = "select * from Managers limit ?,?";
		return template.query(sql, new BeanPropertyRowMapper<Managers>(Managers.class), left, 20);	
	}
	public Managers findOneByTid(String tid) throws Exception {
		String sql = "select * from Managers where tid=?";
		System.out.println(tid);
		return template.queryForObject(sql, new BeanPropertyRowMapper<Managers>(Managers.class), tid);
	}
	public List<Managers> findManasByMulCondi(String name, String username, String phono) {
		String sql = "select * from Managers where mname=? and musername=? and mphono=?";
		return template.query(sql, new BeanPropertyRowMapper<Managers>(Managers.class), name, username, phono);
	}
	public List<Managers> findManasByMulCondi(String name, String username) {
		String sql = "select * from Managers where mname=? and musername=?";
		return template.query(sql, new BeanPropertyRowMapper<Managers>(Managers.class), name, username);
	}
	public List<Managers> findManasByMulCondition(String name, String phono) {
		String sql = "select * from Managers where mname=? and mphono=?";
		return template.query(sql, new BeanPropertyRowMapper<Managers>(Managers.class), name, phono);
	}
	public List<Managers> findManasByMulCondi(String name) {
		String sql = "select * from Managers where mname=?";
		return template.query(sql, new BeanPropertyRowMapper<Managers>(Managers.class), name);
	}
	public List<Managers> findManasByMulConditi(String username, String phono) {
		String sql = "select * from Managers where musername=? and mphono=?";
		return template.query(sql, new BeanPropertyRowMapper<Managers>(Managers.class), username, phono);
	}
	public List<Managers> findManasByMulCondit(String username) {
		String sql = "select * from Managers where musername=?";
		return template.query(sql, new BeanPropertyRowMapper<Managers>(Managers.class), username);
	}
	public List<Managers> findManasByMulConditio(String phono) {
		String sql = "select * from Managers where mphono=?";
		return template.query(sql, new BeanPropertyRowMapper<Managers>(Managers.class), phono);
	}
	public Managers findOneByUP(String username, String password) {
		try {
			String sql = "select * from Managers where musername=? and mpassword=?";
			return template.queryForObject(sql, new BeanPropertyRowMapper<Managers>(Managers.class), username, password);
		} catch (Exception e) {
			return null;
		}
	}
	
	public void changeOneByUTN(String username, String tid, String name, String newPass) throws Exception {
		String sql = "update Managers set mpassword=? where musername=? and tid=? and mname=?";
		template.update(sql, newPass, username, tid, name);
	}
	
	public void updatePin(String newPassword, String name, String mid, String phono) throws Exception{
		String sql = "";
		if(phono == null) {
			sql = "update Managers set mpassword=?,mname=? where mid=?";
			template.update(sql, newPassword, name, mid);
		}
		else {
			sql = "update Managers set mpassword=?,mname=?,mphono=? where mid=?";
			template.update(sql, newPassword, name, phono, mid);
		}
	}
	
	public String checkUserBymid(String mid, String username, String oldPassword) {
		try {
			String sql = "select Mname from Managers where musername=? and mpassword=?";
			return template.queryForObject(sql, java.lang.String.class, username, oldPassword);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public void updatePinByMid(String mid, String newPassword, String name) {
		String sql = "UPDATE Managers set Mpassword=?,name=? WHERE mid=?";
		template.update(sql, newPassword, name, mid);
	}
	
	/**
	 * 查找当前学校在省级专家组中的已开启项目
	 * @param string
	 * @param nowTime
	 * @return
	 */
	public List<Map<String, Object>> checkExistPrBySidPSch(String tid, String nowTime) {
		String sql = "SELECT\r\n" + 
				"	pr.pid\r\n" + 
				"FROM\r\n" + 
				"	pspgsch spgsch,\r\n" + 
				"	PSPgroup spg,\r\n" + 
				"	Projects pr\r\n" + 
				"WHERE\r\n" + 
				"	spgsch.sid = ?\r\n" + 
				"	AND spgsch.pspgid = spg.pspgid\r\n" + 
				"	AND spg.pid = pr.pid\r\n" + 
				"	AND pr.pisstart IN('TRUE','true')\r\n" + 
				"	AND ? BETWEEN pr.PSTIME AND pr.PETIME";
		return template.queryForList(sql, tid, nowTime);
	}
	
	/**
	 * 查找当前学校在市级专家组中的已开启项目
	 * @param string
	 * @param nowTime
	 * @return
	 */
	public List<Map<String, Object>> checkExistPrBySidCSch(String tid, String nowTime) {
		String sql = "SELECT\r\n" + 
				"	pr.pid\r\n" + 
				"FROM\r\n" + 
				"	cspgsch spgsch,\r\n" + 
				"	cSPgroup spg,\r\n" + 
				"	Projects pr\r\n" + 
				"WHERE\r\n" + 
				"	spgsch.sid = ?\r\n" + 
				"	AND spgsch.cspgid = spg.cspgid\r\n" + 
				"	AND spg.pid = pr.pid\r\n" + 
				"	AND pr.pisstart IN('TRUE','true')\r\n" + 
				"	AND ? BETWEEN pr.PSTIME AND pr.PETIME";
		return template.queryForList(sql, tid, nowTime);
	}
	
	/**
	 * 查找当前学校在县级专家组中的已开启项目
	 * @param string
	 * @param nowTime
	 * @return
	 */
	public List<Map<String, Object>> checkExistPrBySidDSch(String tid, String nowTime) {
		String sql = "SELECT\r\n" + 
				"	pr.pid\r\n" + 
				"FROM\r\n" + 
				"	dspgsch spgsch,\r\n" + 
				"	dSPgroup spg,\r\n" + 
				"	Projects pr\r\n" + 
				"WHERE\r\n" + 
				"	spgsch.sid = ?\r\n" + 
				"	AND spgsch.dspgid = spg.dspgid\r\n" + 
				"	AND spg.pid = pr.pid\r\n" + 
				"	AND pr.pisstart IN('TRUE','true')\r\n" + 
				"	AND ? BETWEEN pr.PSTIME AND pr.PETIME";
		return template.queryForList(sql, tid, nowTime);
	}
	
	public Map<String, Object> findOneByTidToMap(String cid) {
		String sql = "select * from managers where tid=?";
		return template.queryForMap(sql, cid);
	}
	
	public Map<String, Object> findOneAndDistByTidToMap(String cid) {
		try {
			String sql = "select managers.*,districts.dname from managers,districts where tid=? AND districts.did=managers.tid";
			return template.queryForMap(sql, cid);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Map<String, Object> findOneAndCityByTidToMap(String cid) {
			String sql = "select managers.*,cities.cname from managers,cities where tid=? AND cities.cid=managers.tid";
			return template.queryForMap(sql, cid);
	}
	
	/**
	 * 根据市县校名，查找某个确定管理员
	 * @param cname
	 * @param distname
	 * @param schname
	 * @return
	 */
	public Map<String, Object> findoneByCDSname(String cname, String distname, String schname) {
		String sql = "SELECT\r\n" + 
				"	managers.*,\r\n" + 
				"	cdsview.*\r\n" + 
				"FROM\r\n" + 
				"	managers,\r\n" + 
				"	cdsview \r\n" + 
				"WHERE\r\n" + 
				"	tid = cdsview.sid \r\n" + 
				"	AND cdsview.cname = ? \r\n" + 
				"	AND cdsview.dname = ?\r\n" + 
				"	AND cdsview.sname = ?";
		return template.queryForMap(sql, cname, distname, schname);
	}
	
	/**
	 * 根据管理员项条件，查询管理员
	 * @param musername
	 * @param mphono
	 * @param mname
	 * @return
	 */
	public Collection<? extends Map<String, Object>> findManaByUsernamePhonoMname(String musername, String mphono,
			String mname) {
		String sql = "SELECT\r\n" + 
				"	managers.*\r\n" + 
				"FROM\r\n" + 
				"	managers\r\n" + 
				"WHERE 1 = 1\r\n";
				if(!"".equals(mname)) 
					sql += "AND managers.MNAME LIKE '%" + mname + "%'\r\n";
				if(!"".equals(musername))
					sql += "AND managers.musername LIKE '%" + musername + "%'\r\n"; 
				if(!"".equals(mphono))
					sql += "AND managers.mphno = '" + mphono + "'\r\n";
			System.out.println(sql);
			return template.queryForList(sql);
	}
	public String findLastOne() {
		String sql = "SELECT\r\n" + 
				"	MID\r\n" + 
				"FROM\r\n" + 
				"	managers\r\n" + 
				"	ORDER BY MID DESC\r\n" + 
				"	LIMIT 1";
		return template.queryForObject(sql, String.class);
	}
	
	public List<Map<String, Object>> findCityLackMana() {
		String sql = "SELECT\r\n" + 
				"	*\r\n" + 
				"FROM\r\n" + 
				"	V_cityljmana\r\n" + 
				"WHERE\r\n" + 
				"	V_cityljmana.tid is NULL";
		return template.queryForList(sql);
	}
	
	public List<Map<String, Object>> findDistLackMana() {
		String sql = "SELECT\r\n" + 
				"	*\r\n" + 
				"FROM\r\n" + 
				"	V_distljmana\r\n" + 
				"WHERE\r\n" + 
				"	V_distljmana.tid is NULL";
		return template.queryForList(sql);
	}
	
	public List<Map<String, Object>> findSchLackMana() {
		String sql = "SELECT\r\n" + 
				"	*\r\n" + 
				"FROM\r\n" + 
				"	V_schljmana\r\n" + 
				"WHERE\r\n" + 
				"	V_schljmana.tid is NULL";
		return template.queryForList(sql);
	}
	
	public boolean checkUnique(String musername, String mid) {
		String sql = "SELECT mid FROM managers WHERE musername=? AND mid != ?";
		try {
			template.queryForObject(sql, String.class, musername, mid);
			return false;
		} catch (Exception e) {
			return true;
		}
	}
	public Map<String, Object> getUser(String cookieUser, String tid) throws Exception{
		return template.queryForMap("SELECT Mname spname,Musername spusername,Mphono phono FROM Managers WHERE Musername=? LIMIT 1", cookieUser);
	}
}
