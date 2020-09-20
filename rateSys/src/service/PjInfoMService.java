package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.eclipse.jdt.internal.compiler.apt.util.Archive;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import dao.IndexSetDao;
import dao.ProjectDao;
import domain.Acriterion;
import domain.Archives;
import domain.PjInfoMPageBean;
import domain.Project;
import util.JDBCUtils;

public class PjInfoMService {

	public PjInfoMPageBean findPageBean(int curpage) throws Exception{
		ProjectDao pDao = new ProjectDao();
		PjInfoMPageBean pageBean = new PjInfoMPageBean();
		pageBean.setPj(pDao.findOnePagePj(curpage));
		pageBean.setCurpage(curpage);
		return pageBean;
	}

	public PjInfoMPageBean queryPageBean(Project p, String stime, String etime) throws Exception {
		ProjectDao pDao = new ProjectDao();
		PjInfoMPageBean pageBean = new PjInfoMPageBean();
		pageBean.setPj(pDao.queryPageBean(p, stime, etime));
		return pageBean;
	}

	public void addData(Project p) throws Exception{
		ProjectDao pDao = new ProjectDao();
		Project lastP = pDao.findLastOneP();
		String pid = "";
		int pnum = 1;
		//如果项目表中有项目，则获取最后一个项目编号 + 1。如果没有项目，编号从1开始
		if(lastP != null) pnum = Integer.parseInt(lastP.getPid().substring(1)) + 1;
		if(pnum < 10) pid = "P0000" + pnum;
		else if(pnum < 100) pid = "P000" + pnum;
		else if(pnum < 1000) pid = "P00" + pnum;
		else if(pnum < 10000) pid = "P0" + pnum;
		else pid = "P" + pnum;
		p.setPid(pid);
		pDao.insert(p);
	}

	public void changeData(Project p) throws Exception{
		ProjectDao pDao = new ProjectDao();
		pDao.changePByPid(p);
	}

	/*
	 * 1、只有评审完的项目才归档了（查找pcm表，找不到未提交数据，则归档）
	 * 2、没评审完，提示管理员关闭项目再删除项目（没评审完，判断是否已经关闭，已经关闭，可以删除。未关闭，提示删除）
	 */
	public String checkPByIndex(String pid, String message) throws Exception {
		IndexSetDao indexDao = new IndexSetDao();
		// 找到未提交的记录
		if(indexDao.findOneNoCommitByPid(pid) == true) {
			// 未评审完，已经关闭，进行删除
			if(indexDao.checkShutDownByPid(pid) == true)
				return "false";
			// 未评审完，未关闭
			else 
				return "middle";
		}
		return "true";
	}

	public void delData(String pid, String flag) throws Exception{
		//开启事务
		System.out.println(pid);
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(JDBCUtils.getDataSource());
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();  
	    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);  
	    TransactionStatus status = transactionManager.getTransaction(def);
		try {
			ProjectDao pDao = new ProjectDao();
			// 先对项目进行归档。归档字段详见归档表。将学校项目评分表为基表，连接其他各表字段。再插入查找出来的归档记录
			// 里面包含查找和插入操作，删除或者归档项目
			if("true".equals(flag))
				pDao.InsertAllArchiveByPid(pid);
			// 删除项目下的学校的项目流程记录：(只删除学校的)
			pDao.delPcmByPid(pid);
			// 删除项目下的项目学校分配记录：
			pDao.delPspgSchByPid(pid);
			// 删除项目下的专家组学校分配记录：
			pDao.delGroupSchByPid(pid);
			// 删除项目下的学校自评：
			pDao.delSpScoreByPid(pid);
			// 删除项目下的总评：
			pDao.delSelfReportByPid(pid);
			// 删除项目下的abc指标：
			pDao.delCcriterionByPid(pid);
			pDao.delBcriterionByPid(pid);
			pDao.delAcriterionByPid(pid);
//			获取项目下的所有专家组
			List<Map<String, Object>> groupsByPid = pDao.findAllGroupByPid(pid);
			// 删除项目下的专家组 、 专家组成员 、 专家成员、组成员被分配的id,根据专家组ID
			ExGroupsService groupServer = new ExGroupsService();
			for (Map<String, Object> map : groupsByPid) {
				groupServer.delData(map.get("spgid").toString(), false);
			}
			// 删除项目
			pDao.delPByPid(pid);
			// 删除项目下所有项目流程记录：(不只删除学校的)
			pDao.delPcmAllByPid(pid);
		    transactionManager.commit(status);
		} catch (Throwable e) {
			e.printStackTrace();
			transactionManager.rollback(status);
			throw new Exception();
		}  
	}

	public String findPnameByPname(String pname) {
		ProjectDao pDao = new ProjectDao();
		if(pDao.findPnameByPname(pname).size() >= 1) { //项目名重复
			return "该项目名称已存在";
		}
		System.out.println(pDao.findPnameByPname(pname));
		return "success";
	}

	public String changeCheckPnameByPid(String pid) throws Exception {
		ProjectDao pDao = new ProjectDao();
		return pDao.findOneByPid(pid).getPname();
	}
	
}
