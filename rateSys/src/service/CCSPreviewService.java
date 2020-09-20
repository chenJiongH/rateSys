package service;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;

import dao.CCSDao;
import domain.CityCountySchool;

public class CCSPreviewService {
	
	public List<Map<String, Object>> getJson(String cname) throws JsonProcessingException {
		CCSDao ccsDao = new CCSDao();
		List<Map<String, Object>> cs = ccsDao.getDSByCname(cname);
		return cs;
	}
}
