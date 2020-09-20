package service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import dao.CCSDao;
import dao.ManagersDao;
import domain.AcmPageBean;
import domain.Cities;
import domain.Districts;
import domain.Managers;
import domain.Schools;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import util.JDBCUtils;

public class AcMService {

	public boolean inDataByFile(String filePath) throws Exception {
		File file = new File(filePath);
		//读取Excel用workbook，书写Excel用writableworkbook
		Workbook workbook = Workbook.getWorkbook(file);
		Sheet sheet = workbook.getSheet(0);
		String content = "";
		List<Managers> managers = new ArrayList<Managers>();
		for(int i = 1; i < sheet.getRows(); i++) {
			Managers manager = new Managers();
			Cell cell = sheet.getCell(0, i);
			manager.setMid(cell.getContents());
			cell = sheet.getCell(1, i);
			manager.setMname(cell.getContents());
			cell = sheet.getCell(2, i);
			manager.setMusername(cell.getContents());
			cell = sheet.getCell(3, i);
			manager.setMpassword(cell.getContents());
			cell = sheet.getCell(4, i);
			manager.setMphono(cell.getContents());
			
			cell = sheet.getCell(8, i);
			content = cell.getContents();
			if("市级".equals(content)) {
				cell = sheet.getCell(9, i);
				manager.setTid(cell.getContents());
			} else if("县级".equals(content)) {
				cell = sheet.getCell(10, i);
				manager.setTid(cell.getContents());
			} else if("校级".equals(content)) {
				cell = sheet.getCell(11, i);
				manager.setTid(cell.getContents());
			}
			managers.add(manager);
		}
		//开启事务
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(JDBCUtils.getDataSource());
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();  
	    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED); 
	    TransactionStatus status = transactionManager.getTransaction(def);
		try {
			ManagersDao mdao = new ManagersDao();
			//判断数据没有问题，则在里面进行插入操作
			if(mdao.insertAll(managers)) {
				transactionManager.commit(status);
				return true;
			} else 
				throw new Exception();
		} catch (Exception e) {
			e.printStackTrace();
			transactionManager.rollback(status);
			return false;
		} 
	}

	public String changeData(Managers m, String rank, String cname, String distname, String schname) throws SQLException {
		CCSDao ccsDao = new CCSDao();
		ManagersDao manaDao = new ManagersDao();
		String message = "";
		//开启事务
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(JDBCUtils.getDataSource());
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();  
	    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED); 
	    TransactionStatus status = transactionManager.getTransaction(def);
		try {
			m.setTid(manaDao.findOndManByMid(m.getMid()).getTid());
			if("市级".equals(rank)) {
				//判断待修改的该MID账号是不是这个市的唯一账号。
				String cid = ccsDao.findOndCityByCname(cname).getCid();
				if(!m.getTid().equals(cid)) {
					message = "修改不成功，该账号不属于当前市的账号";
					return message;
				}
			} else if("县级".equals(rank)) {
				//判断待修改的该MID账号是不是这个县的唯一账号。
				//传入县名和市的cid获取唯一的一条县记录，判断该县的did是不是账号的tid
				String did = ccsDao.findOndDistByDname(distname,ccsDao.findOndCityByCname(cname).getCid()).getDid();
				if(!m.getTid().equals(did)) {
					message = "修改不成功，该账号不属于当前县的账号";
					return message;					
				}
			} else if("校级".equals(rank)) {
				//判断待修改的该MID账号是不是这个校的唯一账号。
				//传入校名和县的did获取唯一的一条校记录，判断该校的sid是不是账号的tid
				String sid = ccsDao.findOneSchBySname(schname,ccsDao.findOndDistByDname(distname,ccsDao.findOndCityByCname(cname).getCid()).getDid()).getSid();
				if(!m.getTid().equals(sid)) {
					message = "修改不成功，该账号不属于当前校的账号";
					return message;			
				}
			}
			if(manaDao.checkUnique(m.getMusername(), m.getMid()) == false) {
				message = "账号重复";
				return message;
			}
			if(manaDao.updateOne(m) == false) {
				message = "修改不成功，更新出错";
				return message;			
			}
			message = "修改成功";
			transactionManager.commit(status);
  		} catch (Exception e) {
			e.printStackTrace();
			transactionManager.rollback(status);
  			message = "修改不成功";
		}
		return message;
	}

	public void export(String filePath) throws Exception {
		File file = new File(filePath);
		file.createNewFile();
		OutputStream os = new FileOutputStream(file);
		
		WritableWorkbook workbook = Workbook.createWorkbook(os);
		WritableSheet sheet = workbook.createSheet("sheet1", 0);
		sheet.setColumnView(4, 15);
		WritableFont font = new WritableFont(WritableFont.TIMES, 14, WritableFont.NO_BOLD);
		WritableCellFormat format = new WritableCellFormat(font);
		Label label = null;
		String[] title = {"MID", "姓名", "账号", "密码", "电话", "市", "县区", "学校", "级别", "市编号", "县区编号", "学校编号"};
		for(int i = 0; i < 12; i++) {
			label = new Label(i, 0, title[i], format);
			sheet.addCell(label);
		}
		AcmPageBean pageBean = new AcmPageBean();
		ManagersDao manaDao = new ManagersDao();
		CCSDao ccsDao = new CCSDao();
		pageBean.setCities(ccsDao.findCities());
		pageBean.setDist(ccsDao.findAllDist());
		pageBean.setSchools(ccsDao.findAllSch());
		pageBean.setManagers(manaDao.findAllManas());
		int sheetRow = 0;
		String cname = "";
		String dname = "";
		String cid = "";
		String did = "";
		String sid = "";
		String sname = "";
		for(Managers m : pageBean.getManagers()) {
			sheetRow++;
			if(m.getTid().charAt(0) == 'C') {
				label = new Label(0, sheetRow, m.getMid(), format);
				sheet.addCell(label);
				label = new Label(1, sheetRow, m.getMname(), format);
				sheet.addCell(label);
				label = new Label(2, sheetRow, m.getMusername(), format);
				sheet.addCell(label);
				label = new Label(3, sheetRow, m.getMpassword(), format);
				sheet.addCell(label);
				label = new Label(4, sheetRow, m.getMphono(), format);
				sheet.addCell(label);
				for(Cities city : pageBean.getCities())
					if(m.getTid().equals(city.getCid())) {
						cid = m.getTid();
						cname = city.getCname();
						break;
					}
				label = new Label(5, sheetRow, cname, format);
				sheet.addCell(label);
				label = new Label(8, sheetRow, "市级", format);
				sheet.addCell(label);
				label = new Label(9, sheetRow, m.getTid(), format);
				sheet.addCell(label);
			} else if(m.getTid().charAt(0) == 'D') {
				for(Districts d : pageBean.getDist()) 
					if(d.getDid().equals(m.getTid())) {
						did = d.getDid();
						cid = d.getCid();
						dname = d.getDname();
						break;
					}
				for(Cities city : pageBean.getCities())
					if(cid.equals(city.getCid())) {
						cname = city.getCname();
						break;
					}
				label = new Label(0, sheetRow, m.getMid(), format);
				sheet.addCell(label);
				label = new Label(1, sheetRow, m.getMname(), format);
				sheet.addCell(label);
				label = new Label(2, sheetRow, m.getMusername(), format);
				sheet.addCell(label);
				label = new Label(3, sheetRow, m.getMpassword(), format);
				sheet.addCell(label);
				label = new Label(4, sheetRow, m.getMphono(), format);
				sheet.addCell(label);
				label = new Label(5, sheetRow, cname, format);
				sheet.addCell(label);
				label = new Label(6, sheetRow, dname, format);
				sheet.addCell(label);
				label = new Label(8, sheetRow, "县级", format);
				sheet.addCell(label);
				label = new Label(9, sheetRow, cid, format);
				sheet.addCell(label);
				label = new Label(10, sheetRow, did, format);
				sheet.addCell(label);
			} else if(m.getTid().charAt(0) == 'S') {
				for(Schools s : pageBean.getSchools()) 
					if(s.getSid().equals(m.getTid())) {
						sid = s.getSid();
						did = s.getDid();
						sname = s.getSname();
						break;
					}
				for(Districts d : pageBean.getDist()) 
					if(d.getDid().equals(did)) {
						cid = d.getCid();
						dname = d.getDname();
						break;
					}
				for(Cities city : pageBean.getCities())
					if(cid.equals(city.getCid())) {
						cname = city.getCname();
						break;
					}
				label = new Label(0, sheetRow, m.getMid(), format);
				sheet.addCell(label);
				label = new Label(1, sheetRow, m.getMname(), format);
				sheet.addCell(label);
				label = new Label(2, sheetRow, m.getMusername(), format);
				sheet.addCell(label);
				label = new Label(3, sheetRow, m.getMpassword(), format);
				sheet.addCell(label);
				label = new Label(4, sheetRow, m.getMphono(), format);
				sheet.addCell(label);
				label = new Label(5, sheetRow, cname, format);
				sheet.addCell(label);
				label = new Label(6, sheetRow, dname, format);
				sheet.addCell(label);
				label = new Label(7, sheetRow, sname, format);
				sheet.addCell(label);
				label = new Label(8, sheetRow, "校级", format);
				sheet.addCell(label);
				label = new Label(9, sheetRow, cid, format);
				sheet.addCell(label);
				label = new Label(10, sheetRow, did, format);
				sheet.addCell(label);
				label = new Label(11, sheetRow, sid, format);
				sheet.addCell(label);
			}
		}
		//写入数据
		workbook.write();
		//关流
		workbook.close();
	}

	public AcmPageBean findPageBean(int curpage) throws Exception {
		AcmPageBean pageBean = new AcmPageBean();
		ManagersDao manaDao = new ManagersDao();
		CCSDao ccsDao = new CCSDao();
		pageBean.setCities(ccsDao.findCities());
		pageBean.setDist(ccsDao.findAllDist());
		pageBean.setSchools(ccsDao.findAllSch());
		pageBean.setManagers(manaDao.findManByPage(curpage));
//		pageBean.setTotalpage(manaDao.findAllManas().size());
		pageBean.setCurpage(curpage);
		return pageBean;
	}

	public String findTid(String rank, String cname, String distname, String schname) throws Exception {
		CCSDao ccsDao = new CCSDao();
		if("市级".equals(rank) && !"".equals(cname)) {
			return ccsDao.findOndCityByCname(cname).getCid();
		} else if("县级".equals(rank) && !"".equals(cname) && !"".equals(distname)) {
			String cid = ccsDao.findOndCityByCname(cname).getCid();
			//通过城市的cid和县名，查找县的id
			return ccsDao.findOndDistByDname(distname, cid).getDid();
		} else if("校级".equals(rank) && !"".equals(cname) && !"".equals(distname) && !"".equals(schname) ) {
			String cid = ccsDao.findOndCityByCname(cname).getCid();
			String did = ccsDao.findOndDistByDname(distname, cid).getDid();
			return ccsDao.findOneSchBySname(schname, did).getSid();
		}
		return "级别对应的数值为空";
	}

	public AcmPageBean findPageBeanByMana(Managers m) throws Exception {
		AcmPageBean pageBean = new AcmPageBean();
		ManagersDao manaDao = new ManagersDao();
		CCSDao ccsDao = new CCSDao();
		pageBean.setCities(ccsDao.findCities());
		pageBean.setDist(ccsDao.findAllDist());
		pageBean.setSchools(ccsDao.findAllSch());

		List<Managers> managers = new ArrayList<Managers>();
		//如果存在Tid，则只会查询到唯一账号，则此时把这个唯一账号和其他三个条件逐一判断
		if(m.getTid() != null) {
			Managers mana = manaDao.findOneByTid(m.getTid());
			boolean flag = true; 
			if(!"".equals(m.getMname())) 
				flag = (m.getMname().equals(mana.getMname()));
			else if(!"".equals(m.getMusername()))
				flag = (m.getMusername().equals(mana.getMusername()));
			else if(!"".equals(m.getMphono()))
				flag = (m.getMphono().equals(mana.getMphono()));
			if(flag) {
				managers.add(mana);
				pageBean.setManagers(managers);				
			}
			//没有按照Tid查询，则会有多条记录，根据当前已有的用户名、电话、姓名分情况查询
		} else {
			String name = m.getMname();
			String username = m.getMusername();
			String phono = m.getMphono();
			if(!"".equals(name)) {
				if(!"".equals(username)) {
					if(!"".equals(phono)) {
						//存在name、usename、phono
						pageBean.setManagers(manaDao.findManasByMulCondi(name,username,phono));
					} else 
						//存在name、usename
						pageBean.setManagers(manaDao.findManasByMulCondi(name,username));
				} else {
					if(!"".equals(phono)) {
						//存在name、phono
						pageBean.setManagers(manaDao.findManasByMulCondition(name,phono));
					} else 
						//存在name
						pageBean.setManagers(manaDao.findManasByMulCondi(name));
				}
			} else if(!"".equals(username)) {
				if(!"".equals(phono)) {
					//存在username、phono
					pageBean.setManagers(manaDao.findManasByMulConditi(username,phono));
				} else 
					//存在username
					pageBean.setManagers(manaDao.findManasByMulCondit(username));
			} else if(!"".equals(phono)) {
//				存在phono
				pageBean.setManagers(manaDao.findManasByMulConditio(phono));
			} 
		}
		return pageBean;
	}

	public List<Map<String, Object>> findCityManaByCid(String cid) throws Exception {
		ManagersDao manaDao = new ManagersDao();
		CCSDao cdsdao = new CCSDao();
		List<Map<String, Object>> manaByCid = new ArrayList<Map<String,Object>>();
		//查找该市的市级管理员
		String cname = "";
		Map<String, Object> temporary = null;
		try {
			System.out.println(cid);
			manaByCid.add(manaDao.findOneAndCityByTidToMap(cid));
			manaByCid.get(0).put("sname", "");
			manaByCid.get(0).put("dname", "");
		} catch(Exception e) {//当前市无管理员
			e.printStackTrace();
		} finally {
			cname = cdsdao.findOneCByCid(cid).getCname();
			List<Map<String, Object>> dists = cdsdao.findDistByCidNow2(cid);
			for(Map<String, Object> dist : dists) {
				//查找该县管理员
					temporary = manaDao.findOneAndDistByTidToMap((String) dist.get("did"));
					if(temporary != null) {
						manaByCid.add(temporary);
						manaByCid.get(manaByCid.size() - 1).put("cname", cname);
						manaByCid.get(manaByCid.size() - 1).put("sname", "");
						manaByCid.addAll(cdsdao.findSchByDid((String) dist.get("did")));
						//查找该县下的校管理员
					}
			}
		}
		return manaByCid;
	}

	public List<Map<String, Object>> selectData(Managers m, String rank, String cname, String distname,	String schname, String schoolName) throws Exception {
		List<Map<String, Object>> selectMana = new ArrayList<Map<String, Object>>(); 
		CCSDao cdsdao = new CCSDao();
		ManagersDao manaDao = new ManagersDao();
		String cid = "";
		String did = "";
		// 新增加的判断分支，如果校名称查询有值，则市县校都不起作用。按照校名称模糊查询。之前只要去掉该分支即可
		if (!"".equals(schoolName)) {
			//借助视图 cdsview，里面有市县校的名称 和 对应的id
			selectMana.addAll(cdsdao.findSchByFuzzySchoolName(schoolName));
			
		}
		else if("市级".equals(rank)) {
			distname = ""; 
			schname = "";
			cid = cdsdao.findOndCityByCname(cname).getCid();
			selectMana = this.findCityManaByCid(cid); //查找出该市的所有管理员
		}
		else if("县级".equals(rank)) {
			schname = "";
			cid = cdsdao.findOndCityByCname(cname).getCid();
			did = cdsdao.findOndDistByDname(distname, cid).getDid(); 
			selectMana.add(manaDao.findOneAndDistByTidToMap(did)); //查找该县管理员
			selectMana.get(selectMana.size() - 1).put("cname", cname);
			selectMana.get(selectMana.size() - 1).put("sname", "");
			
			//查找该县下的校管理员，借助视图 cdsview，里面有市县校的名称
			selectMana.addAll(cdsdao.findSchByDid(did));
		}
		else if("校级".equals(rank)) {
			cid = cdsdao.findOndCityByCname(cname).getCid();
			did = cdsdao.findOndDistByDname(distname, cid).getDid();
			selectMana.add(manaDao.findoneByCDSname(cname, distname, schname));//根据市县校查找唯一一个管理员。管理员tid = sid
		}
		else { //非市县校。靠姓名、账号、电话查询
			selectMana.addAll(manaDao.findManaByUsernamePhonoMname(m.getMusername(), m.getMphono(), m.getMname()));
			//增加每个管理员的市县校名
			for(int i = 0; i <selectMana.size(); i++) {
				String tid = (String) selectMana.get(i).get("TID");
				if(tid.charAt(0) == 'S') {
					Map oneCDSMap = cdsdao.findCDSNameBySid(tid);
					selectMana.get(i).put("cname", oneCDSMap.get("cname"));
					selectMana.get(i).put("dname", oneCDSMap.get("dname"));
					selectMana.get(i).put("sname", oneCDSMap.get("sname"));
				}
				if(tid.charAt(0) == 'D') {
					Map oneCDSMap = cdsdao.findCDSNameByDid(tid);
					selectMana.get(i).put("cname", oneCDSMap.get("cname"));
					selectMana.get(i).put("dname", oneCDSMap.get("dname"));
					selectMana.get(i).put("sname", "");
				}
				if(tid.charAt(0) == 'C') {
					Map oneCDSMap = cdsdao.findCDSNameByCid(tid);
					selectMana.get(i).put("cname", oneCDSMap.get("cname"));
					selectMana.get(i).put("dname", "");
					selectMana.get(i).put("sname", "");
				}
			}
		}
		
		List<Map<String, Object>> secondSelectMana = new ArrayList<Map<String,Object>>();
		for(int i = 0; i < selectMana.size(); i++) {//判断所有管理员是否符合名称、用户名、手机条件
			if(!"".equals(m.getMusername())) {//姓名条件不空，判断姓名是否正确
//				if(!m.getMusername().contains((CharSequence)  selectMana.get(i).get("MUSERNAME") )) {
					//false
				if(!selectMana.get(i).get("MUSERNAME").toString().contains(m.getMusername())) {
					continue;
				}
			}
			if(!"".equals(m.getMname())) {
//				if(!m.getMname().contains((CharSequence) selectMana.get(i).get("MNAME") )) {
				if(!selectMana.get(i).get("MNAME").toString().contains(m.getMname())) {
					continue;
				}
			}
			if(!"".equals(m.getMphono())) {
				if(!m.getMphono().equals( selectMana.get(i).get("MPHONO") )) {
					continue;
				}
			}
			secondSelectMana.add(selectMana.get(i)); //经过删选后，加入集合
		}
		return secondSelectMana;
	}

}
