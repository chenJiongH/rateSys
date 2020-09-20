package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dao.ExDisDAO;

public class ExDisService {

	public List<Map<String, Object>> findGroup(String tid, String mid, String pid) throws Exception {
		ExDisDAO dao = new ExDisDAO();
		List<Map<String, Object>> groupMap = new ArrayList<Map<String, Object>>();
		if(tid.charAt(0) == 'P') 
			groupMap = dao.findPGroupByMid(mid, pid);
		else if(tid.charAt(0) == 'C')
			groupMap = dao.findCGroupByMid(mid, pid);
		else if(tid.charAt(0) == 'D')
			groupMap = dao.findDGroupByMid(mid, pid);
		return groupMap;
	}

	public List<Map<String, Object>> findMember(String tid, String gid) throws Exception {
		ExDisDAO dao = new ExDisDAO();
		List<Map<String, Object>> memberMap = new ArrayList<Map<String, Object>>();
		if(tid.charAt(0) == 'P') 
			memberMap = dao.findPMemberByGid(gid);
		else if(tid.charAt(0) == 'C')
			memberMap = dao.findCMemberByGid(gid);
		else if(tid.charAt(0) == 'D')
			memberMap = dao.findDMemberByGid(gid);
		return memberMap;
	}

	public List<Map<String, Object>> findIndex(String tid, String gid) throws Exception{
		ExDisDAO dao = new ExDisDAO();
		List<Map<String, Object>> Index = new ArrayList<Map<String, Object>>();
		if(tid.charAt(0) == 'P') 
			Index = dao.findPIndex(gid);
		else if(tid.charAt(0) == 'C')
			Index = dao.findCIndex(gid);
		else if(tid.charAt(0) == 'D')
			Index = dao.findDIndex(gid);
		return Index;
	}

	public List<Map<String, Object>> findMi(String tid, String spid, String gid) throws Exception{
		ExDisDAO dao = new ExDisDAO();
		List<Map<String, Object>> mi = new ArrayList<Map<String, Object>>();
		if(tid.charAt(0) == 'P') 
			mi = dao.findPMI(spid, gid);
		else if(tid.charAt(0) == 'C')
			mi = dao.findCMI(spid, gid);
		else if(tid.charAt(0) == 'D')
			mi = dao.findDMI(spid, gid);
		return mi;
	}

	public void addMemIndex(String mid, String tid, String gid, String spid, String cid) throws Exception {
		ExDisDAO dao = new ExDisDAO();
		if(tid.charAt(0) == 'P') 
			dao.addPMemIndex(gid, spid, mid, cid);
		else if(tid.charAt(0) == 'C')
			dao.addCMemIndex(gid, spid, mid, cid);
		else if(tid.charAt(0) == 'D')
			dao.addDMemIndex(gid, spid, mid, cid);
	}

	public void delMemIndex(String mid, String tid, String gid, String spid, String cid) throws Exception {
		ExDisDAO dao = new ExDisDAO();
		if(tid.charAt(0) == 'P') 
			dao.delPMemIndex(gid, spid, mid, cid);
		else if(tid.charAt(0) == 'C')
			dao.delCMemIndex(gid, spid, mid, cid);
		else if(tid.charAt(0) == 'D')
			dao.delDMemIndex(gid, spid, mid, cid);
	}

	public List<Map<String, Object>> findAllPr(String tid, String mid) throws Exception {
		GroupSchoolMService anoterService = new GroupSchoolMService();
		return anoterService.findAllProORGroupByPid(mid, tid, null);
	}
}
