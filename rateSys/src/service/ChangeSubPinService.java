package service;

import dao.CCSDao;
import dao.ManagersDao;
import domain.Cities;
import domain.Districts;
import domain.Schools;

public class ChangeSubPinService {

	public void solve(String tid, String name, String rankName, String username, String newPass) throws Exception {
		CCSDao ccsDao = new CCSDao();
		ManagersDao manaDao = new ManagersDao();
		char initTid = tid.charAt(0);
		if(initTid == 'P') {
//			省级管理员账号
			Cities city = ccsDao.findOndCityByCname(rankName);
			manaDao.changeOneByUTN(username, city.getCid(), name, newPass);
		} else if(initTid == 'C') {
			Districts dist = ccsDao.findOndDistByDname(rankName, tid);
			manaDao.changeOneByUTN(username, dist.getDid(), name, newPass);
		} else if(initTid == 'D') {
			Schools sch = ccsDao.findOneSchBySname(rankName, tid);
			manaDao.changeOneByUTN(username, sch.getSid(), name, newPass);
		} else if(initTid == 'S') {
			throw new Exception("校级管理员没有下级管理员");
		}
	}
}
