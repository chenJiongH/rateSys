package service;

import dao.GetWelcomeNameDao;

public class GetWelcomeNameService {

	private static GetWelcomeNameDao getNameDao = new GetWelcomeNameDao(); 
	
	public String getManaName(String mid, String tid) {
		
		return getNameDao.getManaName(mid, tid);
	}

	public String getSpName(String spid) {
		return getNameDao.getSpName(spid);
	}

}
