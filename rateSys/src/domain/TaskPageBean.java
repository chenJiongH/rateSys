package domain;

import java.util.List;
import java.util.Map;

public class TaskPageBean {
	List<Schools> ss;
	Managers mana;
	//spgsch专家组学校表内连接 专家表 和 学校表
	List<Map<String, Object>> spgsch;
	//专家组表
	public List<Schools> getSs() {
		return ss;
	}
	public void setSs(List<Schools> ss) {
		this.ss = ss;
	}
	public Managers getMana() {
		return mana;
	}
	public void setMana(Managers mana) {
		this.mana = mana;
	}
	public List<Map<String, Object>> getSpgsch() {
		return spgsch;
	}
	public void setSpgsch(List<Map<String, Object>> spgsch) {
		this.spgsch = spgsch;
	}
	@Override
	public String toString() {
		return "TaskPageBean [ss=" + ss + ", mana=" + mana + ", spgsch=" + spgsch + "]";
	}
	
}
