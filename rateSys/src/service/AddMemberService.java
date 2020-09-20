package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import dao.AddMemberDao;
import dao.ProjectDao;
import domain.AddMemberPageBean;
import domain.PSPGmember;
import domain.PSPgroup;
import domain.Project;
import util.JDBCUtils;

public class AddMemberService {

	public AddMemberPageBean findPageBean(int curpage, String tid, String mid) throws Throwable{
		AddMemberPageBean pageBean = new AddMemberPageBean();
		AddMemberDao memberDao = new AddMemberDao();
		ProjectDao pDao = new ProjectDao();
		// 查找该管理员下的所有已经可分配的项目名称
		pageBean.setAllProject(ExGroupsService.findListProject(tid, mid));
		// 查找该管理员下的所有专家组和专家组成员的姓名、编号
		if(tid != null && tid.charAt(0) == 'P') {
			// 查找该管理员下的专家组成员姓名
			pageBean.setPeople(memberDao.findPeoplePByMid(mid));
		}
		if(tid != null && tid.charAt(0) == 'C') {
			pageBean.setPeople(memberDao.findPeopleCByMid(mid));
		}
		if(tid != null && tid.charAt(0) == 'D') {
			pageBean.setPeople(memberDao.findPeopleDByMid(mid));
		}
		// 防止在 addAll 方法时，抛出空指针异常
		pageBean.setGroup(new ArrayList<PSPgroup>());
		// 查找该管理员下的专家组
		for(Project project : pageBean.getAllProject()) {
			if(tid != null && tid.charAt(0) == 'P')
				pageBean.getGroup().addAll(memberDao.findGroupPByMidPid(mid, project.getPid()));
			if(tid != null && tid.charAt(0) == 'C')
				pageBean.getGroup().addAll(memberDao.findGroupCByMidPid(mid, project.getPid()));
			if(tid != null && tid.charAt(0) == 'D')
				pageBean.getGroup().addAll(memberDao.findGroupDByMidPid(mid, project.getPid()));
		}
		
		pageBean.setMember(new ArrayList<PSPGmember>());
		
		//查找每个专家组下的专家组成员编号
		if(pageBean.getGroup() != null)
		for(PSPgroup g : pageBean.getGroup()) {
			if(tid != null && tid.charAt(0) == 'P')
				pageBean.getMember().addAll(memberDao.findMemberByPGid(g.getPspgid()));
			if(tid != null && tid.charAt(0) == 'C')
				pageBean.getMember().addAll(memberDao.findMemberByCGid(g.getPspgid()));
			if(tid != null && tid.charAt(0) == 'D')
				pageBean.getMember().addAll(memberDao.findMemberByDGid(g.getPspgid()));
		}
//		
//		if(tid != null && tid.charAt(0) == 'P')
//			pageBean.getMember().addAll(memberDao.findMemberByPMid(mid));
//		if(tid != null && tid.charAt(0) == 'C')
//			pageBean.getMember().addAll(memberDao.findMemberByCMid(mid));
//		if(tid != null && tid.charAt(0) == 'D')
//			pageBean.getMember().addAll(memberDao.findMemberByDMid(mid));
		return pageBean;
	
	}

	public void delData(String pspid, String pspgid) throws Exception {
		//开启事务
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(JDBCUtils.getDataSource());
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();  
	    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);  
	    TransactionStatus status = transactionManager.getTransaction(def);
		try {
			// 获取项目的项目流程			
			Map<String, Object> project = new HashMap<String, Object>();
			AddMemberDao memberDao = new AddMemberDao();
			if(pspid.charAt(0) == 'P') 
				project = memberDao.findOneProjectByPSPGid(pspgid);
			else if(pspid.charAt(0) == 'C') 
				project = memberDao.findOneProjectByCSPGid(pspgid);
			else if(pspid.charAt(0) == 'D') 
				project = memberDao.findOneProjectByDSPGid(pspgid);
			String pid = project.get("pid").toString();
			// 删除专家组成员项目流程记录
			memberDao.delOnePcmByPidSpid(pspid, pid);
			// 删除专家组成员记录
			if(pspid.charAt(0) == 'P') {
				memberDao.delOnePByPspid(pspid, pspgid);
			}
			else if(pspid.charAt(0) == 'C') {
				memberDao.delOneCByCspid(pspid, pspgid);
			}
			else if(pspid.charAt(0) == 'D') {
				memberDao.delOneDByDspid(pspid, pspgid);
			}
			memberDao.delCByPSpid(pspid, pspgid);
		    transactionManager.commit(status);
		} catch (Exception e) {
			e.printStackTrace();
			transactionManager.rollback(status);
			throw new Exception();
		} 
	}

	public void changeLeader(String pspid, String pspgid) throws Exception {
		AddMemberDao memberDao = new AddMemberDao();
		if(pspid.charAt(0) == 'P')
			memberDao.changeOnePLeader(pspid, pspgid);
		else if(pspid.charAt(0) == 'C')
			memberDao.changeOneCLeader(pspid, pspgid);
		else if(pspid.charAt(0) == 'D')
			memberDao.changeOneDLeader(pspid, pspgid);
	}

	public void addData(String pspid, String pspgid, String isleader, String pid) throws Exception{
		//开启事务
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(JDBCUtils.getDataSource());
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();  
	    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);  
	    TransactionStatus status = transactionManager.getTransaction(def);
		try {
			
			AddMemberDao memberDao = new AddMemberDao();
			// 获取项目的项目流程			
			Map<String, Object> project = new HashMap<String, Object>();
			if(pspid.charAt(0) == 'P') 
				project = memberDao.findOneProjectByPSPGid(pspgid);
			else if(pspid.charAt(0) == 'C') 
				project = memberDao.findOneProjectByCSPGid(pspgid);
			else if(pspid.charAt(0) == 'D') 
				project = memberDao.findOneProjectByDSPGid(pspgid);
			String process = project.get("pprocess").toString();
//			String pid = project.get("pid").toString();
			
			List<String> rateNum = new ArrayList<String>();
			findAllRate(rateNum, process);
			
			// 每个流程对应的管理员级别
			Map<String, String> strRateMap = new HashMap<String, String>();
			strRateMap.put("县", "dmid");
			strRateMap.put("市", "cmid");
			strRateMap.put("省", "pmid");
			
			Map<String, Object> pcmMap = new HashMap();
			pcmMap.put("pmid", "M00001");
			
			if(pspid.charAt(0) == 'P') {
				memberDao.addDataByPid(pspid, pspgid, isleader);
				// 添加项目流程管理记录
				// 根据专家id获取专家组 是 抽查还是省级
				int spotFlag = memberDao.findSpotOrProvinByPSpgid(pspgid);
				System.out.println(spotFlag + " : " + spotFlag);
				// 根据该项目的所有流程，添加项目流程管理记录
				memberDao.addPspidPCM(pid, pspid, spotFlag);
			}
			else if(pspid.charAt(0) == 'C') {
				memberDao.addDataByCid(pspid, pspgid, isleader);
				// 根据专家id获取市、省管理员信息
				pcmMap.putAll(memberDao.findMultiMidByCSpid(pspid));
				// 根据该项目的所有流程，添加项目流程管理记录
				memberDao.addCspidPCM(pid, pspid, pcmMap, rateNum, strRateMap);
			}
			else if(pspid.charAt(0) == 'D') {
				memberDao.addDataByDid(pspid, pspgid, isleader);
				
				// 根据专家id获取县、市、省管理员信息
				pcmMap.putAll(memberDao.findMultiMidByDSpid(pspid));
				// 根据该项目的所有流程，添加项目流程管理记录
				memberDao.addDspidPCM(pid, pspid, pcmMap, rateNum, strRateMap);
			}
		    transactionManager.commit(status);
		} catch (Exception e) {
			e.printStackTrace();
			transactionManager.rollback(status);
			throw new Exception();
		} 
	}

	private void findAllRate(List<String> rateNum, String process) {
		rateNum.add("校");
		char nowRate = 0; 
		// 截取所有的流程, 放到list数组中
		while( process.length() >= 3) {
			process = process.substring(2);
			if(process.charAt(0) == '抽' && nowRate == '省') {
				break;
			}
			if(process.charAt(0) == '抽' && nowRate != '省') {
				rateNum.add("省");
				break;
			}
			else
				rateNum.add("" + process.charAt(0));
			nowRate = process.charAt(0);
		}
	}
	
	public AddMemberPageBean queryData(String mid, String pspid, String pspgid, String tid, String pname) {
		AddMemberPageBean pageBean = new AddMemberPageBean();
		AddMemberDao memberDao = new AddMemberDao();
		ProjectDao pDao = new ProjectDao();
		//查找该管理员下的所有专家组和专家组成员的姓名、编号
		if(tid != null && tid.charAt(0) == 'P') {
			//查找该管理员下的专家组成员姓名
			pageBean.setPeople(memberDao.findPeoplePByMid(mid));
            //根据是否有专家组这个条件来分不同的查询条件。查找该管理员下的专家组
			if(pspgid == null || "".equals(pspgid))
				pageBean.setGroup(memberDao.findGroupPByMid(mid, pname));
			else 
				pageBean.setGroup(memberDao.findGroupPByMidGid(mid, pspgid, pname));
		}
		if(tid == null || tid.charAt(0) == 'C') {
			pageBean.setPeople(memberDao.findPeopleCByMid(mid));
            //根据是否有专家组这个条件来分不同的查询条件。查找该管理员下的专家组
			if(pspgid == null || "".equals(pspgid))
				pageBean.setGroup(memberDao.findGroupCByMid(mid, pname));
			else pageBean.setGroup(memberDao.findGroupCByMidGid(mid, pspgid, pname));
		}
		if(tid != null && tid.charAt(0) == 'D') {
			pageBean.setPeople(memberDao.findPeopleDByMid(mid));
            //根据是否有专家组这个条件来分不同的查询条件。查找该管理员下的专家组
			if(pspgid == null || "".equals(pspgid))
				pageBean.setGroup(memberDao.findGroupDByMid(mid, pname));
			else pageBean.setGroup(memberDao.findGroupDByMidGid(mid, pspgid, pname));
		}
		//根据用户是否有查询专家姓名这个条件 来分条件查询
		pageBean.setMember(new ArrayList<PSPGmember>());
		//查找每个专家组下的专家组成员编号
		for(PSPgroup g : pageBean.getGroup()) {
			if(tid != null && tid.charAt(0) == 'P') { 
				if(pspid == null || "".equals(pspid))
					pageBean.getMember().addAll(memberDao.findMemberByPGid(g.getPspgid()));
				else pageBean.getMember().addAll(memberDao.findMemberByPGidPid(g.getPspgid(), pspid));
			}
			if(tid != null && tid.charAt(0) == 'C') {
				if(pspid == null || "".equals(pspid))
					pageBean.getMember().addAll(memberDao.findMemberByCGid(g.getPspgid()));
				else pageBean.getMember().addAll(memberDao.findMemberByCGidPid(g.getPspgid(), pspid));
			}
			if(tid != null && tid.charAt(0) == 'D') {
				if(pspid == null || "".equals(pspid))
					pageBean.getMember().addAll(memberDao.findMemberByPGid(g.getPspgid()));
				else pageBean.getMember().addAll(memberDao.findMemberByDGidPid(g.getPspgid(), pspid));
			}
		}
		return pageBean;
	}

	public List<Map<String, Object>> findAllMember(String tid, String mid) {
		AddMemberPageBean pageBean = new AddMemberPageBean();
		AddMemberDao memberDao = new AddMemberDao();
		List<Map<String, Object>> allMember = null;
		//查找该管理员下的所有专家组和专家组成员的姓名、编号
		if(tid != null && tid.charAt(0) == 'P') {
			//查找该管理员下的专家组成员姓名
			allMember = memberDao.findAllMemberPByMid(mid);
		}
		if(tid != null && tid.charAt(0) == 'C') {
			allMember = memberDao.findAllMemberCByMid(mid);
		}
		if(tid != null && tid.charAt(0) == 'D') {
			allMember = memberDao.findAllMemberDByMid(mid);
		}
		return allMember;
	}
	
}
