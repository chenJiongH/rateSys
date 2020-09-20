package service;

import java.util.Map;

import dao.PCDSPChangePinDao;
import domain.PSpecialists;

public class PCDSPChangePinService {

	public Map<String, Object> findSPMessage(String spid) throws Exception {
		PCDSPChangePinDao dao = new PCDSPChangePinDao();
		return dao.findSPMessage(spid);
	}

	public void change(PSpecialists sp, String newPass) throws Exception{
		PCDSPChangePinDao dao = new PCDSPChangePinDao();
		dao.changeMessage(sp, newPass);
	}

}
