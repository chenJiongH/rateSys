package dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import domain.CSpecialists;
import domain.DSpecialists;
import domain.PSpecialists;
import util.JDBCUtils;

public class PCDspecialistsDao {

	JdbcTemplate template = JDBCUtils.getTemplate();

	public CSpecialists findOneCByUP(String username, String password) {
		try {
			String sql = "select * from cspecialists where spusername=? and sppassword=?";
			return template.queryForObject(sql, new BeanPropertyRowMapper<CSpecialists>(CSpecialists.class), username, password);
		} catch (Exception e) {
//			e.printStackTrace();
			return null;
		}
	}

	public DSpecialists findOneDByUP(String username, String password) {

		try {
			String sql = "select * from Dspecialists where spusername=? and sppassword=?";
			return template.queryForObject(sql, new BeanPropertyRowMapper<DSpecialists>(DSpecialists.class), username, password);
		} catch (Exception e) {
//			e.printStackTrace();
			return null;
		}
	}

	public PSpecialists findOnePByUP(String username, String password) {
		try {
			String sql = "select * from Pspecialists where spusername=? and sppassword=?";
			return template.queryForObject(sql, new BeanPropertyRowMapper<PSpecialists>(PSpecialists.class), username, password);
		} catch (Exception e) {
//			e.printStackTrace();
			return null;
		}}

	public List<PSpecialists> findPByMid(String mid) {
		try {
			String sql = "select * from pspecialists where mid=?";
			return template.query(sql, new BeanPropertyRowMapper<PSpecialists>(PSpecialists.class), mid);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<CSpecialists> findCByMid(String mid) {
		try {
			String sql = "select * from cspecialists where mid=?";
			return template.query(sql, new BeanPropertyRowMapper<CSpecialists>(CSpecialists.class), mid);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<DSpecialists> findDByMid(String mid) {
		try {
			String sql = "select * from dspecialists where mid=?";
			return template.query(sql, new BeanPropertyRowMapper<DSpecialists>(DSpecialists.class), mid);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void delOnePById(String spid) throws Exception {
		String sql = "delete from pspecialists where pspid=?";
		template.update(sql, spid);
	}

	public void delOneCById(String spid) throws Exception {
		String sql = "delete from cspecialists where cspid=?";
		template.update(sql, spid);
	}
	
	public void delOneDById(String spid) throws Exception {
		String sql = "delete from dspecialists where dspid=?";
		template.update(sql, spid);
	}

	public List<PSpecialists> findAllP() throws Exception {
		String sql = "select * from PSpecialists";
		return template.query(sql, new BeanPropertyRowMapper<PSpecialists>(PSpecialists.class));
	}

	public List<CSpecialists> findAllC() throws Exception {
		String sql = "select * from CSpecialists";
		return template.query(sql, new BeanPropertyRowMapper<CSpecialists>(CSpecialists.class));
	}

	public List<DSpecialists> findAllD() throws Exception{
		String sql = "select * from DSpecialists";
		return template.query(sql, new BeanPropertyRowMapper<DSpecialists>(DSpecialists.class));
	}

	public void insertOneP(String sql, PSpecialists sp, String spPinyin) throws Exception {
		if(null == sp.getSpname() || "".equals(sp.getSpname())) {
			sp.setSpname("' '");
		}
		sql += ",'" + sp.getSpname() + "'";
		if(null == sp.getSpusername() || "".equals(sp.getSpusername())) {
			sp.setSpusername("' '");
		}
		sql += ",'" + sp.getSpusername() + "'";
		if(null == sp.getSppassword() || "".equals(sp.getSppassword())) {
			sp.setSppassword("' '");
		}
		sql += ",'" + sp.getSppassword() + "'";
		if(null == sp.getSpphone() || "".equals(sp.getSpphone())) {
			sp.setSpphone("' '");
		}
		sql += ",'" + sp.getSpphone() + "'";
		if(null == sp.getSporganization() || "".equals(sp.getSporganization())) {
			sp.setSporganization("' '");
		}
		sql += ",'" + sp.getSporganization() + "'";
		if(null == sp.getSpspecialty() || "".equals(sp.getSpspecialty())) {
			sp.setSpspecialty("' '");
		}
		sql += ",'" + sp.getSpspecialty() + "'";
		sql += "," + sp.getSpage();
		if(null == sp.getSptitle() || "".equals(sp.getSptitle())) {
			sp.setSptitle("' '");
		}
		sql += ",'" + sp.getSptitle() + "'";
		if(null == sp.getSprank() || "".equals(sp.getSprank())) {
			sp.setSprank("' '");
		}
		sql += ",'" + sp.getSprank() + "'";
		if(null == sp.getSpfields() || "".equals(sp.getSpfields())) {
			sp.setSpfields("' '");
		}
		sql += ",'" + sp.getSpfields() + "'";
		if(null == sp.getSpgrade() || "".equals(sp.getSpgrade())) {
			sp.setSpgrade("' '");
		}
		sql += ",'" + sp.getSpgrade() + "'";			
		if(null == sp.getMid() || "".equals(sp.getMid())) {
		}
		sql += ",'" + sp.getMid() + "'";
		sql += ",'" + spPinyin + "')";
		template.update(sql);
	}

	public void insertOneC(PSpecialists sp) {
		// TODO Auto-generated method stub
		
	}

	public List<PSpecialists> findOnePByUsername(String spusername) {
		try {
			String sql = "select * from PSpecialists where spusername=?";
			return template.query(sql, new BeanPropertyRowMapper<PSpecialists>(PSpecialists.class),spusername);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<DSpecialists> findOneDByUsername(String spusername) {
		try {
			String sql = "select * from DSpecialists where spusername=?";
			return template.query(sql, new BeanPropertyRowMapper<DSpecialists>(DSpecialists.class),spusername);
			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<CSpecialists> findOneCByUsername(String spusername) {
		try {
			String sql = "select * from CSpecialists where spusername=?";
			return template.query(sql, new BeanPropertyRowMapper<CSpecialists>(CSpecialists.class),spusername);
			
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void delOnePByUsername(String spusername) throws Exception {
		String sql = "delete from PSpecialists where spusername=?";
		template.update(sql, spusername);
	}

	public void delOneDByUsername(String spusername) throws Exception{
		String sql = "delete from DSpecialists where spusername=?";
		template.update(sql, spusername);
	}

	public void delOneCByUsername(String spusername) throws Exception {
		String sql = "delete from CSpecialists where spusername=?";
		template.update(sql, spusername);
	}

	public List<PSpecialists> findPBySql(String sql) {
		return template.query(sql, new BeanPropertyRowMapper<PSpecialists>(PSpecialists.class));
	}

	public List<DSpecialists> findDBySql(String sql) {
		return template.query(sql, new BeanPropertyRowMapper<DSpecialists>(DSpecialists.class));
	}

	public List<CSpecialists> findCBySql(String sql) {
		return template.query(sql, new BeanPropertyRowMapper<CSpecialists>(CSpecialists.class));
	}

	public PSpecialists findLastOneP() {
		try {
			String sql = "select * from PSpecialists order by pspid DESC limit 1";
			return template.queryForObject(sql, new BeanPropertyRowMapper<PSpecialists>(PSpecialists.class));			
		} catch (Exception e) {
			return null;
		}
	}

	public CSpecialists findLastOneC() {
		try {
			String sql = "select * from CSpecialists order by cspid DESC limit 1";
			return template.queryForObject(sql, new BeanPropertyRowMapper<CSpecialists>(CSpecialists.class));			
		} catch (Exception e) {
			return null;
		}
	}

	public DSpecialists findLastOneD() {
		try {
			String sql = "select * from DSpecialists order by dspid DESC limit 1";
			return template.queryForObject(sql, new BeanPropertyRowMapper<DSpecialists>(DSpecialists.class));			
		} catch (Exception e) {
			return null;
		}
	}

	public String checkUserBySpid(String spid, String username, String oldPassword, String spTableName, String spIDItemName) {
		try {
			String sql = "select spname from " + spTableName + " where " + spIDItemName + "=? AND SPusername=? AND SPpassword=?";
			return template.queryForObject(sql, java.lang.String.class, spid, username, oldPassword);			
		} catch (Exception e) {
			return null;
		}
	}

	public void updatePinBySpid(String spid, String newPassword, String spTableName, String spIDItemName, String name) {
		String sql = "UPDATE " + spTableName + " set SPpassword=?,spname=? WHERE " + spIDItemName + "=?";
		template.update(sql, newPassword, name, spid);
	}
	
	/**
	 * 判断当前县级管理员是否已经有开启的项目
	 * @param dspid
	 * @param nowTime
	 * @return
	 */
	public boolean checkExistPrByDspid(String dspid, String nowTime) {
		try {
			String sql = "SELECT\r\n" + 
					"	sp.DSPID\r\n" + 
					"FROM\r\n" + 
					"	DSpecialists sp,\r\n" + 
					"	DSPgroup spg,\r\n" + 
					"	DSPGmember spm,\r\n" + 
					"	Projects pr\r\n" + 
					"WHERE\r\n" + 
					"	sp.dspid = ?\r\n" + 
					"	AND sp.dspid = spm.dspid\r\n" + 
					"	AND spg.dspgid = spm.dspgid\r\n" + 
					"	AND spg.pid = pr.pid\r\n" + 
					"	AND pr.pisstart IN('TRUE','true')\r\n" + 
					"	AND ? BETWEEN pr.PSTIME AND pr.PETIME\r\n" + 
					"	LIMIT 1";
			template.queryForMap(sql, dspid, nowTime);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean checkExistPrByCspid(String cspid, String nowTime) {
		try {
			String sql = "SELECT\r\n" + 
					"	sp.CSPID\r\n" + 
					"FROM\r\n" + 
					"	CSpecialists sp,\r\n" + 
					"	CSPgroup spg,\r\n" + 
					"	CSPGmember spm,\r\n" + 
					"	Projects pr\r\n" + 
					"WHERE\r\n" + 
					"	sp.cspid = ?\r\n" + 
					"	AND sp.cspid = spm.cspid\r\n" + 
					"	AND spg.cspgid = spm.cspgid\r\n" + 
					"	AND spg.pid = pr.pid\r\n" + 
					"	AND pr.pisstart IN('TRUE','true')\r\n" + 
					"	AND ? BETWEEN pr.PSTIME AND pr.PETIME\r\n" + 
					"	LIMIT 1";
			template.queryForMap(sql, cspid, nowTime);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean checkExistPrByPspid(String pspid, String nowTime) {
		try {
			String sql = "SELECT\r\n" + 
					"	pr.PID\r\n" + 
					"FROM\r\n" + 
					"	PSpecialists sp,\r\n" + 
					"	PSPgroup spg,\r\n" + 
					"	PSPGmember spm,\r\n" + 
					"	Projects pr\r\n" + 
					"WHERE\r\n" + 
					"	sp.pspid = ?\r\n" + 
					"	AND sp.pspid = spm.pspid\r\n" + 
					"	AND spg.pspgid = spm.pspgid\r\n" + 
					"	AND spg.pid = pr.pid\r\n" + 
					"	AND pr.pisstart IN('TRUE','true')\r\n" + 
					"	AND ? BETWEEN pr.PSTIME AND pr.PETIME\r\n" + 
					"	LIMIT 1";
			System.out.println(template.queryForMap(sql, pspid, nowTime).get("PID"));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public List<Map<String, Object>> findSpByPinyinMid(String sql) {
		return template.queryForList(sql);
	}

	public PSpecialists findOneBySpid(String spid) {
		String sql = "";
		if(spid.charAt(0) == 'P') {
			sql = "SELECT pspecialists.spusername FROM pspecialists WHERE PSPID=? LIMIT 1";
		} else if(spid.charAt(0) == 'C') {
			sql = "SELECT cspecialists.spusername FROM cspecialists WHERE CSPID=? LIMIT 1";
		} else if(spid.charAt(0) == 'D') {
			sql = "SELECT dspecialists.spusername FROM dspecialists WHERE DSPID=? LIMIT 1";
		}
		return template.queryForObject(sql, new BeanPropertyRowMapper<PSpecialists>(PSpecialists.class), spid);
	}

	public void testInsertAndDel(String spid, String spusername, boolean b, String tid) throws Exception{ //产生重复姓名则抛出异常，没有则插入后删除
	    String sqlIn = "";
	    String sqlDel = "";
	    // SPname 添加了重复名称约束
	    if(tid.charAt(0) == 'P' && !b) ; //属于当前表并且未改动用户名，则不试探插入该表
	    else {
	    	sqlIn = "insert into PSpecialists(PSPID,SPusername) values(?,?)";
	    	sqlDel = "delete from PSpecialists where pspid=?";
		    template.update(sqlIn, spid, spusername);
		    template.update(sqlDel, spid);
	    }
	    if(tid.charAt(0) == 'C' && !b) ;
	    else {
	    	sqlIn = "insert into CSpecialists(CSPID,SPusername) values(?,?)";
	    	sqlDel = "delete from CSpecialists where cspid=?";
		    template.update(sqlIn, spid, spusername);
		    template.update(sqlDel, spid);
	    }
	    if(tid.charAt(0) == 'D' && !b) ;
	    else {
	    	sqlIn = "insert into DSpecialists(DSPID,SPusername) values(?,?)";
	    	sqlDel = "delete from DSpecialists where dspid=?";
		    template.update(sqlIn, spid, spusername);
		    template.update(sqlDel, spid);
	    }
    	sqlIn = "insert into Managers(MID,Musername) values(?,?)";
    	sqlDel = "delete from Managers where MID=?";
	    template.update(sqlIn, spid, spusername);
	    template.update(sqlDel, spid);
	}

	public void delOneBySpid(String spid) {
	    String sqlDel = "";
	    if(spid.charAt(0) == 'P') {
	    	sqlDel = "delete from PSpecialists where pspid=?";
		    template.update(sqlDel, spid);
	    } else if(spid.charAt(0) == 'C') {
	    	sqlDel = "delete from CSpecialists where cspid=?";
		    template.update(sqlDel, spid);
	    } else if(spid.charAt(0) == 'D') {
	    	sqlDel = "delete from DSpecialists where dspid=?";
		    template.update(sqlDel, spid);
	    }
	}

	public Map<String, Object> getUser(String cookieUser, String spid) throws Exception{
		if(spid.charAt(0) == 'P') {
			return template.queryForMap("SELECT spname,spusername FROM PSpecialists WHERE spusername=? LIMIT 1", cookieUser);
		} else if(spid.charAt(0) == 'C') {
			return template.queryForMap("SELECT spname,spusername FROM CSpecialists WHERE spusername=? LIMIT 1", cookieUser);
		} else {
			return template.queryForMap("SELECT spname,spusername FROM DSpecialists WHERE spusername=? LIMIT 1", cookieUser);
		}
	}

	public void findOneSpnameByspNameMidSpid(String spname, String mid, String pspid, String tid) throws Throwable {
		try {
			// 查找同名专家
			String sql = "";
			if(tid.charAt(0) == 'P') {
				sql = "SELECT * FROM PSpecialists WHERE mid=? AND spname=?";
				if(pspid != null)
					sql += " AND pspid!=?";
			} else if(tid.charAt(0) == 'C') {
				sql = "SELECT * FROM CSpecialists WHERE mid=? AND spname=?";
				if(pspid != null)
					sql += " AND cspid!=?";
			} else {
				sql = "SELECT * FROM DSpecialists WHERE mid=? AND spname=?";
				if(pspid != null)
					sql += " AND dspid!=?";
			}
			sql += " LIMIT 1";
			System.out.println(spname + ": " + pspid + " : " + mid);
			if(pspid != null)
				template.queryForMap(sql, mid, spname, pspid);
			else 
				template.queryForMap(sql, mid, spname);
			throw new Throwable();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
