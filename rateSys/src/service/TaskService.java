package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.alibaba.druid.pool.DruidDataSource;

import dao.CCSDao;
import dao.ManagersDao;
import dao.TaskMDAO;
import domain.Cities;
import domain.Districts;
import domain.TaskAllCDG;
import domain.TaskPageBean;
import util.JDBCUtils;

public class TaskService {

	public TaskPageBean findPageBean(String did, String pid, String tid, String mid, String flag) throws Exception{
		TaskPageBean pageBean = new TaskPageBean();
		CCSDao cdsDao = new CCSDao();
		//查找当前县下的校（改动） -》 如果学校已经被挑选了，则不再加入显示队列
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowTime = dateFormat.format(new Date());
		pageBean.setSs(cdsDao.findSchBydid(did, nowTime));
		
		ManagersDao manaDao = new ManagersDao();
		pageBean.setMana(manaDao.findOndManByMid(mid));
		
		if(flag != null) {
			TaskMDAO taskDao = new TaskMDAO(); 
//			根据当前校和专家组查找 专家组表（中间表）学校表+项目名称（已废弃）
//			根据当前项目id查当前学校的项目-学校
			if(tid.charAt(0) == 'P') 
				pageBean.setSpgsch(taskDao.findPSpgsch(did, pid));
		}
		return pageBean;
	}

	public TaskAllCDG findCDG(String mid, String tid) throws Exception {
		CCSDao cdsDao = new CCSDao();
		TaskAllCDG taskCDG = new TaskAllCDG();
		//根据管理员级别查找其所管理的市、县
		if(tid == null) 
			throw new Exception();
		if(tid.charAt(0) == 'P') {
			taskCDG.setCs(cdsDao.findCities());
			taskCDG.setDs(cdsDao.findAllDist());
		} else if(tid.charAt(0) == 'C') {
			List<Cities> cs = new ArrayList<Cities>();
			cs.add(cdsDao.findOneCByCid(tid));
			taskCDG.setCs(cs);
			taskCDG.setDs(cdsDao.findDistByCid(cs.get(0).getCid()));
		} else if(tid.charAt(0) == 'D') {
			List<Districts> ds = new ArrayList<Districts>();
			ds.add(cdsDao.findOneDByCid(tid));
			taskCDG.setDs(ds);
			List<Cities> cs = new ArrayList<Cities>();
			cs.add(cdsDao.findOneCByCid(ds.get(0).getCid()));
			taskCDG.setCs(cs);
		}
		//查找该管理员
		ManagersDao manaDao = new ManagersDao();
		taskCDG.setMana(manaDao.findOndManByMid(mid));
		//查找项目表
		TaskMDAO taskDao = new TaskMDAO(); 
		taskCDG.setPros(taskDao.findProjects());
		//查找专家组表
		if(tid.charAt(0) == 'P') 
			taskCDG.setSpg(taskDao.findPSpgByMid(mid));
		else if(tid.charAt(0) == 'C') 
			taskCDG.setSpg(taskDao.findCSpgByMid(mid));
		else if(tid.charAt(0) == 'D') 
			taskCDG.setSpg(taskDao.findDSpgByMid(mid));
		return taskCDG;
	}

	public void addSpgsch(String pid, List<String> sids, String mid, String tid) throws Exception  {
		//开启事务
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(JDBCUtils.getDataSource());
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();  
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);  
		TransactionStatus status = transactionManager.getTransaction(def);
		try {
			TaskMDAO taskDao = new TaskMDAO();
			Map<String, Object> pcmMap = new HashMap();
			// 获取项目的项目流程
			String process = taskDao.findOneProjectByPid(pid);
			List<String> rateNum = new ArrayList<String>();
			rateNum.add("校");
			char nowRate = 0; 
			// 截取所有的流程, 放到list数组中
			while( process.length() >= 3) {
				process = process.substring(2);
				System.out.println(nowRate);
				if(process.charAt(0) == '省') {
					rateNum.add("省");
					nowRate = '省';
					continue;
				}
				if(process.charAt(0) == '抽' && nowRate != '省') {
					rateNum.add("省");
				}
				else if(process.charAt(0) != '抽')
					rateNum.add("" + process.charAt(0));
				nowRate = process.charAt(0);
			}
			// 每个流程对应的管理员级别
			Map<String, String> strRateMap = new HashMap<String, String>();
			strRateMap.put("校", "smid");
			strRateMap.put("县", "dmid");
			strRateMap.put("市", "cmid");
			strRateMap.put("省", "pmid");
//				pid + " : " + sid + " : " + mid
			pcmMap.put("pmid", "M00001");
			for(String sid : sids) {
				// 添加项目学校任务表记录
				taskDao.addPSpgsch(pid, sid, mid);
				// 添加项目流程管理记录 -- 学校记录
				// 根据学校获取所有的校、县、市管理员信息
				pcmMap.putAll(taskDao.findMultiMidBySid(sid));
				// 根据该项目的所有流程，添加项目流程管理记录
				taskDao.addPCM(pid, pcmMap, sid, rateNum, strRateMap);
				
				// 添加项目流程管理记录 -- 管理员记录记录
				for (String nowRate1 : rateNum) {
					switch(nowRate1) {
					case "县":
						taskDao.addDPCM(pid, rateNum, pcmMap);
						break;
					case "市":
						taskDao.addCPCM(pid, rateNum, pcmMap);
						break;
					// 省级专家不加入记录
//					case "省":
//						taskDao.addPPCM(pid, rateNum, pcmMap);
					}
				}
					
			}
		    transactionManager.commit(status);
		} catch (Exception e) {
			e.printStackTrace();
			transactionManager.rollback(status);
			throw new Exception();
		} 
	}

	public void delSpgsch(String pid, List<String> sids, String mid, String tid) throws Exception  {
		//开启事务
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(JDBCUtils.getDataSource());
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();  
	    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);  
	    TransactionStatus status = transactionManager.getTransaction(def);
		try {
			TaskMDAO taskDao = new TaskMDAO(); 
			if(tid.charAt(0) == 'P') 
				for(String sid : sids) {
					taskDao.delPSpgsch(pid, sid, mid);
					taskDao.delPCM(pid, sid);
					taskDao.delGroupSchool(pid, sid);
				}
		    transactionManager.commit(status);
		} catch (Exception e) {
			e.printStackTrace();
			transactionManager.rollback(status);
			throw new Exception();
		} 
	}
	
}
