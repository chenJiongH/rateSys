package service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import dao.CCSDao;
import dao.GroupSchoolTaskM;
import dao.ManagersDao;
import dao.TaskMDAO;
import domain.TaskPageBean;
import util.JDBCUtils;

public class GroupSchoolMService {

	public TaskPageBean findPageBean(String gid, String pid, String tid, String mid, String flag) throws Exception{
		TaskPageBean pageBean = new TaskPageBean();
		GroupSchoolTaskM taskDao = new GroupSchoolTaskM(); 
		//查找当前县下的校（改动） -》 则不再加入显示队列
		// 查找该项目下，未分配给专家组的所有存留学校，如果学校已经被挑选了，则不加入显示
		pageBean.setSs(taskDao.findRemainSchFromPidMid(mid, pid, tid, gid));
		
		ManagersDao manaDao = new ManagersDao();
		pageBean.setMana(manaDao.findOndManByMid(mid));
		
		// 返回已经分配记录的专家组名称，项目名称，校名称。
		if(tid.charAt(0) == 'P') 
			pageBean.setSpgsch(taskDao.findPGroupSchoolByGidSid(mid, pid, gid));
		else if(tid.charAt(0) == 'C')
			pageBean.setSpgsch(taskDao.findCGroupSchoolByGidSid(mid, pid, gid));
		else if(tid.charAt(0) == 'D') 
			pageBean.setSpgsch(taskDao.findDGroupSchoolByGidSid(mid, pid, gid));
		return pageBean;
	}

	public List<Map<String, Object>> findAllProORGroupByPid(String mid, String tid, String pid) throws Exception {
		
		List<Map<String, Object>> projectORGroup = new ArrayList<Map<String,Object>>();
		//查找该管理员
		GroupSchoolTaskM taskDao = new GroupSchoolTaskM(); 
		if(pid == null) {
			//查找项目表，要求项目未过期、项目已开启、pcm表中有该管理员的mid作为sid项的记录
			projectORGroup = taskDao.findProjects(mid, tid);
			// 遍历项目，项目必须存在专家组。才能分配学校 (此时会导致，被回退并且已经分配了专家组的项目还会出现。需求：是被回退之后，不再出现该项目）
			// 改成：遍历项目，pcm表中该项目下的，以当前管理员id为mid项，并且sid为m开头（当前管理员的下属管理员）都处于已经提交状态，该项目才可以看见。
			Iterator<Map<String, Object>> it = projectORGroup.iterator();
			while(it.hasNext()) {
				Map<String, Object> project = it.next();
//				if(taskDao.checkPrByExistGroup(project.get("pid").toString(), tid, mid) == false) {
//					it.remove();
//				}
				if(taskDao.checkPrByExistOneSubManaNotCommit(project.get("pid").toString(), mid)) {
					it.remove();
				}
			}
				
		} else {
			if(tid.charAt(0) == 'P') 
				projectORGroup = taskDao.findPSpgByMid(mid, pid);
			else if(tid.charAt(0) == 'C')
				projectORGroup = taskDao.findCSpgByMid(mid, pid);
			else if(tid.charAt(0) == 'D') 
				projectORGroup = taskDao.findDSpgByMid(mid, pid);
		}
		//查找专家组表
		return projectORGroup;
	}

	public void addSpgsch(String pid, List<String> sids, String mid, String tid, String gid) throws Exception  {
		//开启事务
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(JDBCUtils.getDataSource());
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();  
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);  
		TransactionStatus status = transactionManager.getTransaction(def);
		try {
			GroupSchoolTaskM dao = new GroupSchoolTaskM();
			dao.batchInsert(sids, pid, mid, gid);
		    transactionManager.commit(status);
		} catch (Exception e) {
			e.printStackTrace();
			transactionManager.rollback(status);
			throw new Exception();
		} 
	}

	public void delSpgsch(String pid, List<String> sids, String mid, String tid, String gid) throws Exception  {
		//开启事务
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(JDBCUtils.getDataSource());
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();  
	    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);  
	    TransactionStatus status = transactionManager.getTransaction(def);
		try {
			GroupSchoolTaskM dao = new GroupSchoolTaskM();
			dao.batchDel(sids, pid, mid, gid);
		    transactionManager.commit(status);
		} catch (Exception e) {
			e.printStackTrace();
			transactionManager.rollback(status);
			throw new Exception();
		} 
	}
	
}
