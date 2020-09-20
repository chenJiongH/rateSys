package service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import dao.DSPRateDAO;
import dao.GroupSchoolTaskM;
import dao.PSPRateDAO;
import dao.SSPRateDAO;
import domain.PSPRatePageBean;
import util.JDBCUtils;

public class PSPRateService {

	public List<Map<String, Object>> findPr(String spid) throws Exception {
		PSPRateDAO dao = new PSPRateDAO();
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowTime = dateFormat.format(date);
		List<Map<String, Object>> pr = dao.findPr(spid, nowTime);
		// doubleCheck ，判断前面的前一级的管理员是否真的被提交。防止已被回退的现象。
		// 遍历项目，pcm表中该项目下的，以当前管理员id为mid项，并且sid为m开头（当前管理员的下属管理员）都处于已经提交状态，该项目才可以看见。
		GroupSchoolTaskM taskDao = new GroupSchoolTaskM();
		Iterator<Map<String, Object>> it = pr.iterator();
		String mid = "";
		while(it.hasNext()) {
			Map<String, Object> project = it.next();
			// 查找专家的管理员id
			mid = dao.findManaIdBySpid(spid);
			if(taskDao.checkPrByExistOneSubManaNotCommit(project.get("pid").toString(), mid)) {
				it.remove();
			}
		}
		pr.add(dao.findSP(spid));
		return pr;
	}
	
	
	public PSPRatePageBean findCByPid(String spid, String pid, int isOnSpot) throws Exception{
		PSPRateDAO dao = new PSPRateDAO();
		Date date = new Date();
		PSPRatePageBean pageBean = new PSPRatePageBean();
		pageBean.setSch(dao.findSchByPidCmid(pid, spid, isOnSpot));
		pageBean.setC(new ArrayList<Map<String, Object>>());
		for(Map s : pageBean.getSch()) {
			pageBean.getC().addAll(dao.findCByPidSpidSid(spid, pid,(String) s.get("sid"), isOnSpot));
		}
		return pageBean;
	}

	public String check(String spid, String pid, int isOnSpot) {
		String message = "提交评定失败，请重新登录或刷新页面";
		PSPRateDAO dao = new PSPRateDAO();
		if(dao.check(spid, pid, isOnSpot) == 1)
			message = "您已经提交过评分，不可中途修改提交";
		return message;
	}

	public void ratePr(Map<String, Object> cs, String spid, int isOnSpot, String pid) throws Exception {
		//开启事务
		System.out.println("here");
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(JDBCUtils.getDataSource());
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();  
	    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);  
	    TransactionStatus status = transactionManager.getTransaction(def);
		try {
			PSPRateDAO dao = new PSPRateDAO();
			dao.batchUpdate(cs, spid, isOnSpot);
			String draft = (String) cs.get("draft");
			if(draft != null) {
			    transactionManager.commit(status);
				return ;
			}
			// 修改评分标志
			setFlage(spid, pid, isOnSpot);
			// 修改流程已经评分标志
			SSPRateDAO SSPdao = new SSPRateDAO();
			String process = SSPdao.findPProcess(pid);
			String nowRate = "";
			if(isOnSpot == 0) {
				if(process.contains("抽查"))
					nowRate = "抽查";
				else 
					nowRate = "结束";
			} else
				nowRate = "结束";
			// 修改流程标志后，返回该县级管理员的ID，判断该县级管理员的所有专家是否已经都评分，都评分完毕，修改县级管理员记录
			String ProvinceMid = dao.setCommitPrBySpid(pid, spid, nowRate);
			dao.tryToSetDmana(ProvinceMid, nowRate, pid);
		    transactionManager.commit(status);
			
		} catch (Exception e) {
			e.printStackTrace();
			transactionManager.rollback(status);
			throw new Exception();
		}  
	}

	public void setFlage(String spid, String pid, int isOnSpot) throws Exception {
		PSPRateDAO dao = new PSPRateDAO();
		dao.setFlage(pid, spid, isOnSpot);
	}
	
	public void loginedPrBySpid(String pid, String spid) throws Exception {
		DSPRateDAO dao = new DSPRateDAO();
		dao.setloginedPrBySpid(pid, spid);
	}
}
