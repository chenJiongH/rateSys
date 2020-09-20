package domain;

import java.util.List;
import java.util.Map;

public class PSPRatePageBean {
	List<Map<String, Object>> sch;
	List<Map<String, Object>> c;
	public List<Map<String, Object>> getSch() {
		return sch;
	}
	public void setSch(List<Map<String, Object>> sch) {
		this.sch = sch;
	}
	public List<Map<String, Object>> getC() {
		return c;
	}
	public void setC(List<Map<String, Object>> c) {
		this.c = c;
	}
	@Override
	public String toString() {
		return "PSPRatePageBean [sch=" + sch + ", c=" + c + "]";
	}
	
}
