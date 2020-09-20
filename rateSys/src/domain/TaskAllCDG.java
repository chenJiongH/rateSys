package domain;

import java.util.List;
import java.util.Map;

public class TaskAllCDG {
	List<Cities> cs;
	List<Districts> ds;
	Managers mana;
	//项目表
	List<Map<String, Object>> pros;
	//专家组表
	List<Map<String, Object>> spg;
	public List<Map<String, Object>> getPros() {
		return pros;
	}
	public void setPros(List<Map<String, Object>> pros) {
		this.pros = pros;
	}
	public List<Cities> getCs() {
		return cs;
	}
	public void setCs(List<Cities> cs) {
		this.cs = cs;
	}
	public List<Districts> getDs() {
		return ds;
	}
	public void setDs(List<Districts> ds) {
		this.ds = ds;
	}
	public Managers getMana() {
		return mana;
	}
	public void setMana(Managers mana) {
		this.mana = mana;
	}
	public List<Map<String, Object>> getSpg() {
		return spg;
	}
	public void setSpg(List<Map<String, Object>> spg) {
		this.spg = spg;
	}
	@Override
	public String toString() {
		return "TaskAllCDG [cs=" + cs + ", ds=" + ds + ", mana=" + mana + ", pros=" + pros + ", spg=" + spg + "]";
	}

}
