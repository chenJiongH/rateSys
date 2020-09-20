package domain;

import java.util.List;
import java.util.Map;

public class CCS {
	private List<Map<String, Object>> city;
	private List<Map<String, Object>> dist;
	private List<Map<String, Object>> sch;
	
	public List<Map<String, Object>> getCity() {
		return city;
	}
	@Override
	public String toString() {
		return "CCS [city=" + city + ", dist=" + dist + ", sch=" + sch + "]";
	}
	public void setCity(List<Map<String, Object>> city) {
		this.city = city;
	}
	public List<Map<String, Object>> getDist() {
		return dist;
	}
	public void setDist(List<Map<String, Object>> dist) {
		this.dist = dist;
	}
	public List<Map<String, Object>> getSch() {
		return sch;
	}
	public void setSch(List<Map<String, Object>> sch) {
		this.sch = sch;
	}

 
	
}
