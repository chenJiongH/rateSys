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

import dao.CSPRateDAO;
import dao.DSPRateDAO;
import dao.GroupSchoolTaskM;
import dao.SSPRateDAO;
import domain.CSPRatePageBean;
import util.JDBCUtils;

public class CSPRateService {

	public List<Map<String, Object>> findPr(String spid) throws Exception {
		CSPRateDAO dao = new CSPRateDAO();
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
	public CSPRatePageBean findCByPid(String spid, String pid) throws Exception{
		CSPRateDAO dao = new CSPRateDAO();
		Date date = new Date();
		CSPRatePageBean pageBean = new CSPRatePageBean();
		String mid = dao.findcityIdBySpid(spid);
		pageBean.setSch(dao.findSchByPidCmid(pid, mid, spid));
		
		pageBean.setC(new ArrayList<Map<String, Object>>());
		for(Map s : pageBean.getSch()) {
			pageBean.getC().addAll(dao.findCByPidSpidSid(spid, pid,(String) s.get("sid")));
		}
		return pageBean;
	}

	public String check(String spid, String pid) {
		String message = "提交评定失败，请重新登录或刷新页面";
		CSPRateDAO dao = new CSPRateDAO();
		if(dao.check(spid, pid) == 1)
			message = "您已经提交过评分，不可中途修改提交";
		return message;
	}

	public void ratePr(Map<String, Object> cs, String spid, String pid) throws Exception {
		//开启事务
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(JDBCUtils.getDataSource());
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();  
	    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);  
	    TransactionStatus status = transactionManager.getTransaction(def);
		try {
			CSPRateDAO dao = new CSPRateDAO();
			dao.batchUpdate(cs, spid);
			
			String draft = (String) cs.get("draft");
			if(draft != null) {
			    transactionManager.commit(status);
				return ;
			}
			
			// 修改评分标志
			setFlage(spid, pid);
			// 修改流程已经评分标志
			SSPRateDAO SSPdao = new SSPRateDAO();
			String process = SSPdao.findPProcess(pid);
			String nowRate = "";
			int distPosition = process.indexOf("市");
			if(distPosition == process.length() -1)
				nowRate = "结束";
			else
				nowRate = process.substring(distPosition + 2, distPosition + 3);
			if("抽".equals(nowRate))
				nowRate = "抽查";
			// 修改流程标志后，返回该市级管理员的ID，判断该市级管理员的所有专家是否已经都评分，都评分完毕，修改市级管理员记录
			String distMid = dao.setCommitPrBySpid(pid, spid, nowRate);
			dao.tryToSetDmana(distMid, nowRate, pid);
		    transactionManager.commit(status);
		} catch (Exception e) {
			e.printStackTrace();
			transactionManager.rollback(status);
			throw new Exception();
		} 
	}

	public void setFlage(String spid, String pid) throws Exception {
		CSPRateDAO dao = new CSPRateDAO();
		dao.setFlage(pid, spid);
	}
	
	public void loginedPrBySpid(String pid, String spid) throws Exception {
		DSPRateDAO dao = new DSPRateDAO();
		dao.setloginedPrBySpid(pid, spid);
	}
	
}
