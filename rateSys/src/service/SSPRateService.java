package service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import dao.CSPRateDAO;
import dao.PSPRateDAO;
import dao.ProjectDao;
import dao.SSPRateDAO;
import domain.Project;
import domain.SSPRatePageBean;
import domain.SSPRateScore;
import util.JDBCUtils;

public class SSPRateService {

	public void rate(boolean setFlag, Map<String, String> allCri, Map<String, String> fileMap, Map<String, String> scoreMap, Map<String, String> describeMap, String mid, String tid, String spgid, String mid2, String overallImgPath, String overallFilePath) throws Exception{
		//开启事务
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(JDBCUtils.getDataSource());
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();  
	    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);  
	    TransactionStatus status = transactionManager.getTransaction(def);
		try {
			List<SSPRateScore> spScores = new ArrayList<SSPRateScore>();
			for(String cid : allCri.keySet()) {
				SSPRateScore spScore = new SSPRateScore();
				spScore.setCid(cid);
				if(scoreMap.get(cid) != null && !"".equals(scoreMap.get(cid))) {
					spScore.setSchoolScore(Float.parseFloat(scoreMap.get(cid)));
				}
				else 
					spScore.setSchoolScore(-1);
				if(describeMap.get(cid) != null) 
					spScore.setDescribe(describeMap.get(cid));
				spScore.setAnnexLocation(fileMap.get(cid));
				spScores.add(spScore);
			}
			SSPRateDAO dao = new SSPRateDAO();
			dao.rate(spScores, tid);
			//此时不是保存草稿，而是提交
			if(setFlag == true) {
				toSetSelfFlage(tid, spgid, mid2);
			}
			//插入总评
			overall(mid, tid, overallImgPath, overallFilePath);
			String pid = "";
			if(setFlag == true) {
				pid = toSetOverallFlage(tid, spgid, mid2);
				// 如果找不到该项目未提交的学校记录，改变上级管理员记录 从校到当前记录
				// 因为是直接上级，所以和管理员的MID有关
				String process = dao.findPProcess(pid);
				// 县、市、省、抽查皆有可能，但是只改动县、市的管理员
				String nowRate = process.substring(2, 3);
				String upLevelMid = "";
				String upAndUp = "";// 上上级别,用于控制整个项目的流程
				if(process.length() >= 4)
					upAndUp = process.substring(4, 5);
				if("县".equals(nowRate)) {
					upLevelMid = dao.findOneDMidBySid(tid);
					if(false == dao.findOneSchNotCommitByPidUpLevelMid(pid, upLevelMid)) {
						dao.setPrUpLevelMidRateByPid(pid, nowRate, upLevelMid, upAndUp);
					}
				} else if("市".equals(nowRate)) {
					upLevelMid = dao.findOneCMidBySid(tid);
					if(false == dao.findOneSchNotCommitByPidCUpLevelMid(pid, upLevelMid)) {
						dao.setPrUpLevelMidRateByPid(pid, nowRate, upLevelMid, upAndUp);
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

	public SSPRatePageBean findPageBean(String tid) throws Exception {

//		System.out.println(JDBCUtils.getDataSource());
		
		SSPRatePageBean pageBean = new SSPRatePageBean();
		SSPRateDAO dao = new SSPRateDAO();
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowTime = dateFormat.format(date);
		//在各级专家表和项目表中查询唯一的一个该校已经开启的项目id
		String pid = dao.findPPid(tid, nowTime);
		if(pid == null)
			throw new Exception();
		pageBean.setMana(dao.findMana(tid));
		pageBean.setOverallReport(dao.findOverall(tid, pid));
		
		pageBean.setTableData(dao.findTableData(pid, tid));
		return pageBean;
	}

	public void overall(String mid, String tid, String filePath, String overallFilePath) throws Exception{
		SSPRateDAO dao = new SSPRateDAO();
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowTime = dateFormat.format(date);
		//在各级专家表和项目表中查询唯一的一个该校已经开启的项目id
		String pid = dao.findPPid(tid, nowTime);
//		if(pid == null) 
//			pid = dao.findCPid(tid, nowTime);
//		if(pid == null) 
//			pid = dao.findDPid(tid, nowTime);
		if(pid == null)
			throw new Exception();
		dao.updateSelfreport(tid, filePath, pid, overallFilePath);
	}

	public Map<String, Object> check(String tid) {
		String message = "提交自评未成功，请重新登录或再次提交";
		SSPRateDAO dao = new SSPRateDAO();
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowTime = dateFormat.format(date);
		Map<String, Object> map = null;
		
		map = dao.checkP(tid, nowTime);
		if(map != null && ("1".equals(map.get("flage").toString()) || "11".equals(map.get("flage").toString()))) {
			message = "您已提交过考核，不可中途修改考核";
		}
		if(map == null) {
			map = new HashMap<String, Object>();
		}
		map.put("message", message);
		return map;
	}

	public void toSetSelfFlage(String tid, String spgid, String mid) throws Exception {
		SSPRateDAO dao = new SSPRateDAO();
		// 设置评分标识
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowTime = dateFormat.format(date);
		String pid = dao.findPPid(tid, nowTime);
		String process = dao.findPProcess(pid);
		
		boolean flag = dao.SelfSetFlageP(tid, pid, mid);
		if(!flag) 
			throw new Exception();
		// 设置项目流程标志
		String nowRate = process.substring(2, 3);
		if("抽".equals(nowRate))
			nowRate = "抽查";
		dao.setSpcmByPidSid(pid, tid, nowRate);
	}

	public Map<String, Object> checkOverall(String tid) {
		String message = "总评文件提交未成功，请重新登录或再次提交";
		SSPRateDAO dao = new SSPRateDAO();
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowTime = dateFormat.format(date);
		Map<String, Object> map = null;
		
		map = dao.checkP(tid, nowTime);
		if(map == null) {
			map = new HashMap<String, Object>();
		}
		if(map != null && ("10".equals(map.get("flage").toString()) || "11".equals(map.get("flage").toString()))) {
			message = "您已经提交过考核，不可中途修改考核内容";
		}
		map.put("message", message);
		return map;
	}

	public String toSetOverallFlage(String tid, String spgid, String mid) {
		SSPRateDAO dao = new SSPRateDAO();
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowTime = dateFormat.format(date);
		String pid = dao.findPPid(tid, nowTime);
//		String process = dao.findPProcess(pid);
		
		dao.OverallSetFlageP(tid, pid, mid);
		// pid 用于管理该项目的具体当前流程，和所有学校是否都已经提交有关
		return pid;
	}

	public Map<String, Object> findProcessRate(String tid) throws Exception {
		SSPRatePageBean pageBean = new SSPRatePageBean();
		SSPRateDAO dao = new SSPRateDAO();
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowTime = dateFormat.format(date);
		//在项目学校表和项目表中查询唯一的一个该校已经开启的项目id
		String pid = dao.findPPid(tid, nowTime);
		if(pid == null)
			throw new Exception();
		String process = "";
		String nowRate = "";
		Map<String, Object> retMap = new HashMap<String, Object>();
		ProjectDao proDao = new ProjectDao();
		Project p = proDao.findOneByPid(pid);
		process = p.getPprocess();
		retMap.put("process", process);
		
		retMap.put("rate", proDao.findNowRateByPcmTidPid(tid, pid));
		return retMap;
	}
}
