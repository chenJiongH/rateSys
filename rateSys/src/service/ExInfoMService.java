package service;

import java.util.List;
import java.util.Map;

import dao.CCSDao;
import dao.PCDspecialistsDao;
import domain.CSpecialists;
import domain.DSpecialists;
import domain.ExInfoPageBean;
import domain.PSpecialists;
import util.ChineseToPinyinUtils;

public class ExInfoMService {

	public ExInfoPageBean getPageBean(String tid, String mid) throws Exception {
		ExInfoPageBean pageBean = new ExInfoPageBean();
		PCDspecialistsDao pcdDao = new PCDspecialistsDao();
		if(tid != null && tid.charAt(0) == 'P') {
			pageBean.setPs(pcdDao.findPByMid(mid));
		} else if(tid != null && tid.charAt(0) == 'C') {
			pageBean.setCs(pcdDao.findCByMid(mid));
		} else if(tid != null && tid.charAt(0) == 'D') {
			pageBean.setDs(pcdDao.findDByMid(mid));
		}
		return pageBean;
	}

	public void del(String spid) throws Exception {
		PCDspecialistsDao pcdDao = new PCDspecialistsDao();
		if(spid != null && spid.charAt(0) == 'P')
			pcdDao.delOnePById(spid);
		else if(spid != null && spid.charAt(0) == 'C')
			pcdDao.delOneCById(spid);
		else if(spid != null && spid.charAt(0) == 'D')
			pcdDao.delOneDById(spid);
	}

	public String add(PSpecialists sp, String tid, String mid) {
		String message = "";
		try {
			PCDspecialistsDao pcdDao = new PCDspecialistsDao();
			try {
				String testId = "K8";
				pcdDao.testInsertAndDel("K8", sp.getSpusername(), true, tid);  //使用唯一约束来返回重复（unique）异常 ，没有重复则删除插入该新修改记录
			} catch (Exception e) {
				return "账号重复";
			}
			try {
				pcdDao.findOneSpnameByspNameMidSpid(sp.getSpname(), mid, sp.getPspid(), tid);
			} catch (Throwable e) {
				e.printStackTrace();
				return "姓名重复";
			}
			
			CCSDao ccsDao = new CCSDao();
//		查找tid所在单位名称
//			String rankName = "";
//			if(tid.charAt(0) == 'P') rankName = "福建省";
//			if(tid.charAt(0) == 'C') {
//				rankName = ccsDao.findOneCByCid(tid).getCname();
//			} else if(tid.charAt(0) == 'D') {
//				rankName = ccsDao.findOneDByCid(tid).getDname();
//			}
//			sp.setSporganization(rankName);
			
			PSpecialists ps = null;
			DSpecialists ds = null;
			CSpecialists cs = null;
			int number = 1;
			ChineseToPinyinUtils toPinyin = new ChineseToPinyinUtils();
			String spPinyin = toPinyin.getStringPinYin(sp.getSpname());
			if(tid.charAt(0) == 'P') {
				//添加时候的插入，则创建编号。
				if(sp.getPspid() ==null) {
					ps = pcdDao.findLastOneP();
					if(ps != null) number = Integer.parseInt(ps.getPspid().substring(3)) + 1;
					if(number < 10) sp.setPspid("PSP0000" + number);
					else if(number < 100) sp.setPspid("PSP000" + number);
					else if(number < 1000) sp.setPspid("PSP00" + number);
					else if(number < 10000) sp.setPspid("PSP0" + number);
					else sp.setPspid("PSP" + number);					
				}
				String sql = "insert into PSpecialists values('" + sp.getPspid() + "'";
				pcdDao.insertOneP(sql, sp, spPinyin);
			} else if(tid.charAt(0) == 'C') {
				if(sp.getPspid() == null) {
					cs = pcdDao.findLastOneC();
					if(cs != null) number = Integer.parseInt(cs.getCspid().substring(3)) + 1;
					if(number < 10) sp.setPspid("CSP0000" + number);
					else if(number < 100) sp.setPspid("CSP000" + number);
					else if(number < 1000) sp.setPspid("CSP00" + number);
					else if(number < 10000) sp.setPspid("CSP0" + number);
					else sp.setPspid("CSP" + number);
				}
				String sql = "insert into CSpecialists values('" + sp.getPspid() + "'";
				pcdDao.insertOneP(sql, sp, spPinyin);
				
			} else if(tid.charAt(0) == 'D') {
				if(sp.getPspid() == null) {
					ds = pcdDao.findLastOneD();
					if(ds != null) number = Integer.parseInt(ds.getDspid().substring(3)) + 1;
					if(number < 10) sp.setPspid("DSP0000" + number);
					else if(number < 100) sp.setPspid("DSP000" + number);
					else if(number < 1000) sp.setPspid("DSP00" + number);
					else if(number < 10000) sp.setPspid("DSP0" + number);
					else sp.setPspid("DSP" + number);					
				}
				String sql = "insert into DSpecialists values('" + sp.getPspid() + "'";
				pcdDao.insertOneP(sql, sp, spPinyin);
			}
			return "添加成功";
		} catch (Exception e) {
			e.printStackTrace();
			return "添加账号失败、请检查数据输入";
		}
	}

	public String change(PSpecialists sp, String tid, String mid) {
		try {
			PCDspecialistsDao pcdDao = new PCDspecialistsDao();
			/**
			 *  插入试探删除法
			 */
			String testId = "K8";
			if(!sp.getSpusername().equals(pcdDao.findOneBySpid(sp.getPspid()).getSpusername())) {			//用户名修改了
				pcdDao.testInsertAndDel("K8", sp.getSpusername(), true, tid);  //使用唯一约束来返回重复（unique）异常 ，没有重复则删除插入该新修改记录
			}
			else 
				pcdDao.testInsertAndDel("K8", sp.getSpusername(), false, tid); //第三个参数来判断是否要试探插入当前表
			
			pcdDao.delOneBySpid(sp.getPspid());//删除但该记录再插入该记录
			
			add(sp, tid, mid);
			return "修改成功";
			} catch(Exception e) {
				return "修改失败,用户名已存在";
			} 
	}

	public ExInfoPageBean selectPageBean(PSpecialists sp, String tid) {
		ExInfoPageBean pageBean = new ExInfoPageBean();
		PCDspecialistsDao pcdDao = new PCDspecialistsDao();
		int flag = 0;
		String sql = "";
		if(tid.charAt(0) == 'P') {
			 sql = "select * from PSpecialists where mid = '" + sp.getMid() + "'";
			flag = 1;
		} else if(tid.charAt(0) == 'C') {
			 sql = "select * from CSpecialists where mid = '" + sp.getMid() + "'";
			flag = 2;
		} else if(tid.charAt(0) == 'D') {
			 sql = "select * from DSpecialists where mid = '" + sp.getMid() + "'";
			flag = 3;
		}
		if(sp.getSpfields() != null && !sp.getSpfields().equals("")) {
			sql += " and spfields like '%" + sp.getSpfields() + "%'";
		} 
		if(sp.getSpage() != 0)
			sql += " and spage=" + sp.getSpage();
		if(sp.getSpgrade() != null && !sp.getSpgrade().equals("")) {
			sql += " and spgrade like '%" + sp.getSpgrade() + "%'";
		}
		if(sp.getSporganization() != null && !sp.getSporganization().equals("")) {
			sql += " and Sporganization like '%" + sp.getSporganization() + "%'";
		}
//		if(sp.getSppassword() != null && !sp.getSppassword().equals("")) {
//			sql += " and Sppassword='" + sp.getSppassword() + "'";
//		}	
		if(sp.getSpphone() != null && !sp.getSpphone().equals("")) {
			sql += " and Spphone like '%" + sp.getSpphone() + "%'";
		}
		if(sp.getSprank() != null && !sp.getSprank().equals("")) {
			sql += " and Sprank like '%" + sp.getSprank() + "%'";
		}
		if(sp.getSpspecialty() != null && !sp.getSpspecialty().equals("")) {
			sql += " and Spspecialty like '%" + sp.getSpspecialty() + "%'";
		}
		if(sp.getSptitle() != null && !sp.getSptitle().equals("")) {
			sql += " and Sptitle like '%" + sp.getSptitle() + "%'";
		}
		if(sp.getSpusername() != null && !sp.getSpusername().equals("")) {
			sql += " and Spusername like '%" + sp.getSpusername() + "%'";
		}
		ChineseToPinyinUtils toPinyin = new ChineseToPinyinUtils();
		String spPinyin = toPinyin.getStringPinYin(sp.getSpname());
		for(int i = 0; i < spPinyin.length(); i++) {
			if(i == 0) sql += " and pinyin like '%" + spPinyin.charAt(i);
//			if(i != 0) sql += "+";
			else 
			sql += "%" + spPinyin.charAt(i);
		}
		sql += "%'";
//		boolean flag1 = true; //为true时有中文名存在
//		for(int i = 0; i < sp.getSpname().length(); i++) {
//			if(toPinyin.getCharPinYin(sp.getSpname().charAt(i)) != null) {
//				flag1 = false;
//				break;
//			}
//		}
//		if(!flag1) { //存在中文汉字则加名称条件
//			sql += " and Spname like '%" + sp.getSpname() + "%'";
//		}
		if(flag == 1) {
			pageBean.setPs(pcdDao.findPBySql(sql));
		} else if(flag == 2) {
			pageBean.setCs(pcdDao.findCBySql(sql));
		} else if(flag ==3) {
			pageBean.setDs(pcdDao.findDBySql(sql));
		}
		
		return pageBean;
		
	}

	public boolean selectCheckTid(String tid, String sporganization) {
		try {
			CCSDao ccsDao = new CCSDao();
			if(tid.charAt(0) == 'C') {
				if(!sporganization.equals(ccsDao.findOneCByCid(tid).getCname())) {
					return false;
				}
			} else if(tid.charAt(0) == 'D') {
				if(!sporganization.equals(ccsDao.findOneDByCid(tid).getDname())) {
					return false;
				}
			} else if(tid.charAt(0) == 'S') {
				if(!sporganization.equals(ccsDao.findOneSByCid(tid).getSname())) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<Map<String, Object>> fuzzySelect(String tid, String mid, String name) {
		ExInfoPageBean pageBean = new ExInfoPageBean();
		PCDspecialistsDao pcdDao = new PCDspecialistsDao();
		int flag = 0;
		String sql = "";
		if(tid.charAt(0) == 'P') {
			 sql = "select * from PSpecialists where mid = '" + mid + "'";
			flag = 1;
		} else if(tid.charAt(0) == 'C') {
			 sql = "select * from CSpecialists where mid = '" + mid + "'";
			flag = 2;
		} else if(tid.charAt(0) == 'D') {
			 sql = "select * from DSpecialists where mid = '" + mid + "'";
			flag = 3;
		}		ChineseToPinyinUtils toPinyin = new ChineseToPinyinUtils();
		String spPinyin = toPinyin.getStringPinYin( name );
		for(int i = 0; i < spPinyin.length(); i++) {
			if(i == 0) 
				sql += " and pinyin like '%" + spPinyin.charAt(i);
			else 
				sql += "%" + spPinyin.charAt(i);
		}
		sql += "%'";
//		boolean flag1 = true; //为true时有中文名存在
//		for(int i = 0; i < sp.getSpname().length(); i++) {
//			if(toPinyin.getCharPinYin(sp.getSpname().charAt(i)) != null) {
//				flag1 = false;
//				break;
//			}
//		}
//		if(!flag1) { //存在中文汉字则加名称条件
//			sql += " and Spname like '%" + sp.getSpname() + "%'";
//		}
		return pcdDao.findSpByPinyinMid(sql);
	}

}
