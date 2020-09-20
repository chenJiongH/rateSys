package service;

import java.util.List;

import dao.ManagersDao;
import dao.PCDspecialistsDao;
import domain.Managers;

public class SPChangePinService {

	public boolean check(String spid, String mid, String username, String oldPassword, String tid) {
		ManagersDao manaDao = new ManagersDao();
		String id = null; //根据账号id，加上用户名和旧密码为条件，如果能够查找到匹配记录，则check成功
		if(mid != null && tid != null && tid.charAt(0) == 'S')	{
			id = manaDao.checkUserBymid(mid, username, oldPassword);
			return id == null? false : true;
		}
		PCDspecialistsDao spdao = new PCDspecialistsDao();
		if(spid != null) {
			String spTableName = "PSpecialists";
			String spIDItemName = "PSPID";
			if(spid.charAt(0) == 'D') {
				spTableName = "DSpecialists";
				spIDItemName = "DSPID";
			} else if(spid.charAt(0) == 'C') {
				spTableName = "CSpecialists";
				spIDItemName = "CSPID";
			}
			id = spdao.checkUserBySpid(spid, username, oldPassword, spTableName, spIDItemName);
			return id == null? false : true;
		}
		return false;
	}

	public void updatePin(String spid, String mid, String newPassword, String tid, String name) throws Exception{
		ManagersDao manaDao = new ManagersDao();
		if(mid != null && tid != null && tid.charAt(0) == 'S')
			manaDao.updatePinByMid(mid, newPassword, name);
		PCDspecialistsDao spdao = new PCDspecialistsDao();
		if(spid != null) {
			String spTableName = "PSpecialists";
			String spIDItemName = "PSPID";
			if(spid.charAt(0) == 'D') {
				spTableName = "DSpecialists";
				spIDItemName = "DSPID";
			} else if(spid.charAt(0) == 'C') {
				spTableName = "CSpecialists";
				spIDItemName = "CSPID";
			}
			spdao.updatePinBySpid(spid, newPassword, spTableName, spIDItemName, name);
		}
	}
	
}
