package domain;

import java.util.List;
import java.util.Map;

public class ExDisPageBean {
	//某个专家组所有指标
	List<Map<String, Object>> index;
	//某个成员的所有指标
	List<Map<String, Object>> mi;
	@Override
	public String toString() {
		return "ExDisPageBean [index=" + index + ", mi=" + mi + "]";
	}
	public List<Map<String, Object>> getIndex() {
		return index;
	}
	public void setIndex(List<Map<String, Object>> index) {
		this.index = index;
	}
	public List<Map<String, Object>> getMi() {
		return mi;
	}
	public void setMi(List<Map<String, Object>> mi) {
		this.mi = mi;
	}
}
