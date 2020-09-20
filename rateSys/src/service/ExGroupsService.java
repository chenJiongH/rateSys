package service;

import java.sql.Connection;
import java.sql.SQLException;
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

import dao.AddMemberDao;
import dao.ExGroupsDao;
import dao.ProjectDao;
import domain.CSPgroup;
import domain.DSPgroup;
import domain.PSPgroup;
import domain.Project;
import domain.exGroupsPageBean;
import util.JDBCUtils;

public class ExGroupsService {

	public exGroupsPageBean queryData(PSPgroup group, String tid, String mid)  throws Exception{
		exGroupsPageBean pageBean = new exGroupsPageBean();
		ExGroupsDao groupDao = new ExGroupsDao();
		ProjectDao pDao = new ProjectDao();
		pageBean.setPs(pDao.findAllPro());
		if(tid != null && tid.charAt(0) == 'P')
			pageBean.setPgs(groupDao.queryPagePByPMN(group));
		if(tid != null && tid.charAt(0) == 'C')
			pageBean.setCgs(groupDao.queryPageCByPMN(group));
		if(tid != null && tid.charAt(0) == 'D')
			pageBean.setDgs(groupDao.queryPageDByPMN(group));
		return pageBean;
	}
	
	public exGroupsPageBean findPageBean(int curpage, String tid, String mid) throws Exception {
		exGroupsPageBean pageBean = new exGroupsPageBean();
		ExGroupsDao groupDao = new ExGroupsDao();
		ProjectDao pDao = new ProjectDao();

		pageBean.setPs(findListProject(tid, mid));
		System.out.println("prs" + pageBean.getPs());
		if(tid != null && tid.charAt(0) == 'P')
			pageBean.setPgs(groupDao.findPagePByMid(curpage, mid));
		if(tid != null && tid.charAt(0) == 'C')
			pageBean.setCgs(groupDao.findPageCByMid(curpage, mid));
		if(tid != null && tid.charAt(0) == 'D')
			pageBean.setDgs(groupDao.findPageDByMid(curpage, mid));
		return pageBean;
	}
	// 获取可分配的项目
	static List<Project> findListProject(String tid, String mid) {		
	ProjectDao pDao = new ProjectDao();
	List<Project> selectedProcess = null;
	Iterator<Project> iter = null;
	if(tid.charAt(0) == 'P') {
		selectedProcess = pDao.findAllPMPro(mid, tid);
		for (int i = 0; i < selectedProcess.size(); i++) {
			Project p = selectedProcess.get(i);
			//如果项目流程 只有 校 - 省，或者校  - 抽查，必须保证所有学校都拼好分数 校 - 省 - 抽查
			if(p.getPprocess().indexOf('省') == 2 || p.getPprocess().indexOf("抽查") == 2 ) {
				if(pDao.checkPschpcm(p.getPid()) == true) {
					selectedProcess.remove(i);
					i--;
				}
				continue;
			}
			else {
				// 必须得前一级专家（校）全部评完，才能给该项目分配高一级的专家组（专家组成员）。查找是否存在未提交的前一级管理员记录:true 存在未提交记录
				if(pDao.checkPpcm(p.getPid(), p.getPprocess()) == true) {
					selectedProcess.remove(i);
					i--;
					continue;
				}
			}
		}
	}
	else if(tid.charAt(0) == 'C') {
		selectedProcess = pDao.findAllCMPro(mid, tid);
		
		for (int i = 0; i < selectedProcess.size(); i++) {
			Project p = selectedProcess.get(i);
			// 该项目必须得存在该管理员记录（经过该市），才能给分配专家组（专家组成员）。查找是否存在的管理员记录:true 存在记录，表明下属校已经被添加到该项目下
			if(pDao.checkPschExist(p.getPid(), mid, tid) == true) {
				selectedProcess.remove(i);
				i--;
			}
			//如果项目流程 只有 校 - 市 - x，必须保证该市下面的所有学校都拼好分数
			if(p.getPprocess().indexOf('市') == 2) {
				if(pDao.checkCschpcm(p.getPid(), mid) == true) {
					selectedProcess.remove(i);
					i--;
				}	
				continue;
			}
			//必须得保证县级专家全部评完，才能给该项目分配市级的专家组（专家组成员）。查找是否存在未提交的县级管理员记录:true 存在未提交记录
			else {
				if(pDao.checkCpcm(p.getPid(), mid, p.getPprocess()) == true) {
					selectedProcess.remove(i);
					i--;
					continue;
				}
			}
		}
	}
	else if(tid.charAt(0) == 'D') {
		selectedProcess = pDao.findAllDMPro(mid, tid);
		for (int i = 0; i < selectedProcess.size(); i++) {
			Project p = selectedProcess.get(i);
			// 必须得存在该管理员记录，才能给分配专家组（专家组成员）。查找是否存在的管理员记录:true 存在记录，表明下属校已经被添加到该项目下
			if(pDao.checkPschExist(p.getPid(), mid, tid) == true) {
				selectedProcess.remove(i);
				i--;
				continue;
			}
			//必须得前一级学校全部评完，才能给该项目分配高一级的专家组（专家组成员）。查找是否存在未提交的管理员记录:true 存在未提交记录
			if(pDao.checkDschpcm(p.getPid(), mid) == true) {
				selectedProcess.remove(i);
				i--;
			}
		}
	}
		return selectedProcess;
	}

	//分管理员类别获取三张管理员表中对应一张表的最后一个编号+1.插入专家组
	public String addData(PSPgroup group, String tid, int isOnSpot) throws Exception{
		String message = "组名称重复";
		ExGroupsDao groupDao = new ExGroupsDao();
		
		if(groupDao.checkGroupName(group.getSpgname(), group.getMid(), tid, group.getPid()) == false) {//check重名
			return message;
		}
		
		String lastNum = "";
		if(tid.charAt(0) == 'P') {
			PSPgroup pg = groupDao.findLastOnePGroup();
			if(pg == null) lastNum = "PSPG00001";
			else {
				int num = Integer.parseInt(pg.getPspgid().substring(4)) + 1;
				if(num < 10) lastNum = "PSPG0000" + num;
				else if(num < 100) lastNum = "PSPG000" + num;
				else if(num < 1000) lastNum = "PSPG00" + num;
				else if(num < 10000) lastNum = "PSPG0" + num;
				else lastNum = "PSPG" + num;
			}

			group.setPspgid(lastNum);
			groupDao.insertOnePGroup(group, isOnSpot);
		} else if(tid.charAt(0) == 'C') {
			CSPgroup pg = groupDao.findLastOneCGroup();
			if(pg == null) lastNum = "CSPG00001";
			else {
				int num = Integer.parseInt(pg.getCspgid().substring(4)) + 1;
				if(num < 10) lastNum = "CSPG0000" + num;
				else if(num < 100) lastNum = "CSPG000" + num;
				else if(num < 1000) lastNum = "CSPG00" + num;
				else if(num < 10000) lastNum = "CSPG0" + num;
				else lastNum = "CSPG" + num;
			}
			
			group.setPspgid(lastNum);
			groupDao.insertOneCGroup(group);
		} else if(tid.charAt(0) == 'D') {
			DSPgroup pg = groupDao.findLastOneDGroup();
			if(pg == null) lastNum = "DSPG00001";
			else {
				int num = Integer.parseInt(pg.getDspgid().substring(4)) + 1;
				if(num < 10) lastNum = "DSPG0000" + num;
				else if(num < 100) lastNum = "DSPG000" + num;
				else if(num < 1000) lastNum = "DSPG00" + num;
				else if(num < 10000) lastNum = "DSPG0" + num;
				else lastNum = "DSPG" + num;
			}
			
			group.setPspgid(lastNum);
			groupDao.insertOneDGroup(group, isOnSpot);
		}
		
		message = "添加成功";
		return message;
	}


	public void delData(String gid, boolean isDelGroup) throws Exception {
		Connection connection = null;
		DataSourceTransactionManager transactionManager = null;
		TransactionStatus status = null;
		//开启事务
		if(isDelGroup) {
			 transactionManager = new DataSourceTransactionManager(JDBCUtils.getDataSource());
			DefaultTransactionDefinition def = new DefaultTransactionDefinition();  
		    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);  
		     status = transactionManager.getTransaction(def);
		}
		try {
			// 获取项目的项目流程			
			ExGroupsDao groupDao = new ExGroupsDao();
			// 删除该专家组所属成员的所有项目流程记录
			// 获取项目的项目流程			
			Map<String, Object> project = new HashMap<String, Object>();
			AddMemberDao memberDao = new AddMemberDao();
			if(gid.charAt(0) == 'P')
				project = memberDao.findOneProjectByPSPGid(gid);
			else if(gid.charAt(0) == 'C')
				project = memberDao.findOneProjectByCSPGid(gid);
			else if(gid.charAt(0) == 'D')
				project = memberDao.findOneProjectByDSPGid(gid);
			String pid = project.get("pid").toString();
			// 删除该专家组下成员的分配指标、所属成员的项目流程记录 、 所属成员、 该专家组 
			if(gid.charAt(0) == 'P') {
				groupDao.delMultiPspCriterion(gid);
				groupDao.delMultiPcmByPGidPid(gid, pid);
				groupDao.delMultiPMemberByPGidPid(gid);
				groupDao.delOnePByGid(gid);
			}
			else if(gid.charAt(0) == 'C') {
				groupDao.delMultiCspCriterion(gid);
				groupDao.delMultiPcmByCGidPid(gid, pid);
				groupDao.delMultiCMemberByPGidPid(gid);
				groupDao.delOneCByGid(gid);
			}
			else if(gid.charAt(0) == 'D') {
				groupDao.delMultiDspCriterion(gid);
				groupDao.delMultiPcmByDGidPid(gid, pid);
				groupDao.delMultiDMemberByPGidPid(gid);
				groupDao.delOneDByGid(gid);	
			}
			if(isDelGroup) {
			    transactionManager.commit(status);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(isDelGroup)
				transactionManager.rollback(status);
			throw new Exception();
		} 
	}

	public List<Map<String, Object>> fuzzySelect(String tid, String mid, String gName) {
		ExGroupsDao groupDao = new ExGroupsDao();
		List<Map<String, Object>> fuzzyData = null;
		if(tid != null && tid.charAt(0) == 'P')
			fuzzyData = groupDao.fuzzySelectP(mid, gName);
		if(tid != null && tid.charAt(0) == 'C')
			fuzzyData = groupDao.fuzzySelectC(mid, gName);
		if(tid != null && tid.charAt(0) == 'D')
			fuzzyData = groupDao.fuzzySelectD(mid, gName);
		return fuzzyData;
	}

	public String changeData(String tid, String mid, String groupName, String gid, String projectName, int isonspot) throws Exception {
		String message = "用户名重复";
		try {
			ExGroupsDao groupDao = new ExGroupsDao();
			if(gid.charAt(0) == 'P') {
				groupDao.checkPRepeatName(groupName, gid, mid);
				groupDao.changePData(tid, mid, groupName, gid, projectName, isonspot);
			}
			else if(gid.charAt(0) == 'C') {
				groupDao.checkCRepeatName(groupName, gid, mid);
				groupDao.changeCData(tid, mid, groupName, gid, projectName, isonspot);
			}
			else if(gid.charAt(0) == 'D') {
				groupDao.checkDRepeatName(groupName, gid, mid);
				groupDao.changeDData(tid, mid, groupName, gid, projectName, isonspot);
			}
			message = "修改成功";
		} catch (Exception e) {
		} 
		return message;
	}
	
}	
