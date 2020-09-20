package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

import dao.PSPRateDAO;
import dao.ProjectProcessFallbackDao;
import domain.ProjectProcessFallbackPageBean;
import util.JDBCUtils;

public class ProjectProcessFallbackService {

	public List<Map<String, Object>> findPageBean(String mid, String tid, int curpage) throws Exception{
		ProjectProcessFallbackDao dao = new ProjectProcessFallbackDao();
		List<Map<String, Object>> pcm = null;
		int startRecord = (curpage - 1) * 20;
		if(tid.charAt(0) == 'P')
			pcm = dao.findPPcmByCurpageMid(mid, startRecord);
		else if(tid.charAt(0) == 'C')
			pcm = dao.findCPcmByCurpageMid(mid, startRecord);
		else if(tid.charAt(0) == 'D')
			pcm = dao.findDPcmByCurpageMid(mid, startRecord);
		return pcm;
	}
	
	public List<Map<String, Object>> queryPageBean(String mid, String tid, ProjectProcessFallbackPageBean queryForm) throws Exception {
		ProjectProcessFallbackDao dao = new ProjectProcessFallbackDao();
		List<Map<String, Object>> selectedData = null;
		if(tid.charAt(0) == 'D') {
			//查找所有经过该县的符合条件的项目
			selectedData = dao.findDPrByCondition(mid, queryForm);
		} else if(tid.charAt(0) == 'C') {
			//查找所有经过该市的符合条件的项目
			selectedData = dao.findCPrByCondition(mid, queryForm);
		} else if(tid.charAt(0) == 'P') {
			//查找所有经过该省的符合条件的项目
			selectedData = dao.findPPrByCondition(mid, queryForm);
		} 
		return selectedData;
	}


	public String processFallback(String tid, String pid, String sid, String mid, String process, String nowRate) throws Exception{
		String message = "项目回退失败，请重新回退或刷新页面";

		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(JDBCUtils.getDataSource());
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();  
	    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);  
	    TransactionStatus status = transactionManager.getTransaction(def);
		try {
			ProjectProcessFallbackDao dao = new ProjectProcessFallbackDao();
			// 回退专家，如果当前自己的管理员记录未提交，则可以设置专家记录为已登录，返回流程，回退前一级管理员，查找当前级别已登录的专家记录
			// 流程回退, 获取上一级流程。
			nowRate = backNowRate(nowRate, process);
			// 回退前一级管理员，查找当前级别是否有已登录的专家记录，回退自己的专家，得保证当前管理员的当前状态为未提交，回退学校：得保证没有专家进行登录
			if(sid.charAt(0) == 'M') {
				if(dao.findOneCommitOrLoginSP(mid, pid) == true) {
					message = "已经有本地专家登录，不可回退项目";
					throw new Exception();
				}
				// 回退管理员
				dao.backPCMByPidMid(pid, sid, nowRate);
			}
			else {
				// 标记回退到的当前流程
				boolean canFallBack = false;
				// 判断当前管理员级别，根据级别判断大概流程
				if(tid.charAt(0) == 'P') {
					// 既然该管理员可以看见校级，则该项目流程一定是 校 - 该管理员级别 - xxx类型的项目
					if(sid.charAt(0) == 'S') {
						// 查找该管理员下的已经提交或者登录的专家，没找到 则可回退校级评分
						if(dao.findOnePspLoginedOrCommit(pid, mid) == false) {
							canFallBack = true;
						}
					} else {
						// 此时回退Pxxxxx专家。判断当前管理员在该项目下的流程记录状态是否为已提交
						// 省级管理员可以随意回退自己的专家
						canFallBack = true;
					}
					// 回退市级专家
				} else if(tid.charAt(0) == 'C') {
					// 既然该管理员可以看见校级，则该项目流程一定是 校 - 该管理员级别 - xxx类型的项目
					if(sid.charAt(0) == 'S') {
						// 查找该管理员下的已经提交或者登录的专家，没找到 则可回退校级评分
						if(dao.findOneCspLoginedOrCommit(pid, mid) == false) 
							canFallBack = true;
					} else {
						// 此时回退Pxxxxx专家。判断当前管理员在该项目下的流程记录状态是否为已提交
						if(dao.findOneCmanaIsCommit(pid, mid) == false)
							canFallBack = true;
					}
					// 回退县级专家
				} else if(tid.charAt(0) == 'D') {
					// 既然该管理员可以看见校级，则该项目流程一定是 校 - 该管理员级别 - xxx类型的项目
					if(sid.charAt(0) == 'S') {
						// 查找该管理员下的已经提交或者登录的专家，没找到 则可回退校级评分
						if(dao.findOneDspLoginedOrCommit(pid, mid) == false) 
							canFallBack = true;
					} else {
						// 此时回退Pxxxxx专家。判断当前管理员在该项目下的流程记录状态是否为已提交
						if(dao.findOneDmanaIsCommit(pid, mid) == false)
							canFallBack = true;
					}
				}
				// 可以回退，则开始回退
				if(canFallBack) {
					dao.processFallbackByPidSid(pid, sid, nowRate);
					if(sid.charAt(0) == 'S') {
//						回退学校，消除项目学校标志
						dao.setSchFallbackFlagByPidSidMid(pid, sid, mid);
					} else if(sid.charAt(0) == 'D') {
//						回退县级专家，消除县级专家组成员标志
						dao.setDspFallbackFlagByPidSidMid(pid, sid, mid);
					} else if(sid.charAt(0) == 'C') {
//						回退市级专家，消除市级专家组成员标志
						dao.setCspFallbackFlagByPidSidMid(pid, sid, mid);
					} else if(sid.charAt(0) == 'P') {
//						回退省、抽查级专家，消除省、抽查级专家组成员标志
						dao.setPspFallbackFlagByPidSidMid(pid, sid, mid);
					}
				}
				else {
					message = "无权限回退当前流程，请联系上级单位";
					throw new Exception("流程权限问题");
				}
			}
			message = "项目回退成功";
		    transactionManager.commit(status);
		} catch (Exception e) {
			e.printStackTrace();
			transactionManager.rollback(status);
		}  
		return message;
		
	}


	private String backNowRate(String nowRate, String process) throws Exception {
		if("结束".equals(nowRate)) {
			if("查".equals((nowRate = process.substring(process.length() - 1)))) 
				nowRate = "抽查";
		} else 
			nowRate = process.substring(process.indexOf(nowRate) - 2, process.indexOf(nowRate) - 1);
		return nowRate;
		
	}

}