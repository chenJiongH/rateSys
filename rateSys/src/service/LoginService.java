package service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dao.ManagersDao;
import dao.PCDspecialistsDao;
import dao.SSPRateDAO;
import domain.CSpecialists;
import domain.DSpecialists;
import domain.Managers;
import domain.PSpecialists;

public class LoginService {
	
	public Map<String, String> check(String username, String password) throws Exception {
		PCDspecialistsDao pcdDao = new PCDspecialistsDao();
		Map<String, String> map = new HashMap();
//		查找省市县专家账号表,只有大等于1个数的项目开启时，才能专家才能登录
		DSpecialists ds = new DSpecialists();
		ds = pcdDao.findOneDByUP(username, password);
		map.put("message", "允许登录");
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String nowTime = dateFormat.format(date);
		
		if(ds != null) {
			map.put("spid", ds.getDspid());
			DSPRateService dspService = new DSPRateService();
			//小于2的原因是，数组内部包含该专家信息。剩下的才为可评分项目
			if(dspService.findPr(ds.getDspid()).size() < 2)
				map.put("message", "当前时间暂无可评分项目，请等待。。。");
			return map;
		}
		CSpecialists cs = new CSpecialists();
		cs = pcdDao.findOneCByUP(username, password);
		if(cs != null) {
			map.put("spid", cs.getCspid());
			CSPRateService CSPService = new CSPRateService();
			if(CSPService.findPr(cs.getCspid()).size() < 2) {
				map.put("message", "当前时间暂无可评分项目，请等待。。。");
			}
			return map;
		}
		PSpecialists ps = new PSpecialists();
		ps = pcdDao.findOnePByUP(username, password);
		if(ps != null) {
			map.put("spid", ps.getPspid());
			PSPRateService PSPService = new PSPRateService();//判断当前是否有可评分的项目
			if(PSPService.findPr(ps.getPspid()).size() < 2) {//小于2的原因是，数组内部包含该专家信息。其他才为可评分项目
				map.put("message", "当前时间暂无可评分项目，请等待。。。");
			}
			return map;
		}
//		查找管理员账号表
		ManagersDao manaDao = new ManagersDao();
		Managers mana = new Managers();
		mana = manaDao.findOneByUP(username, password);
		if(mana != null) {
			map.put("mid", mana.getMid());
			map.put("tid", mana.getTid());
			//学校管理员用户的登录，需要判断是否有且仅有一个开启项目
			if(map.get("tid").charAt(0) == 'S') {
				SSPRateDAO dao = new SSPRateDAO();
				//在各级专家表和项目表中查询唯一的一个该校已经开启的项目id
				String pid = dao.findPPid(map.get("tid"), nowTime);
				if(pid == null)
					map.put("message", "当前时间暂无可评分项目，请等待。。。");
			}
			return map;
		}
		else 
			return null;
	} 

}
