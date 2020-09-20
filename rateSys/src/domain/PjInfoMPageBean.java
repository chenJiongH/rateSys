package domain;

import java.util.List;

public class PjInfoMPageBean {
	private int curpage;
	private List<Project> pj;
	public int getCurpage() {
		return curpage;
	}
	public void setCurpage(int curpage) {
		this.curpage = curpage;
	}
	public List<Project> getPj() {
		return pj;
	}
	public void setPj(List<Project> pj) {
		this.pj = pj;
	}
	@Override
	public String toString() {
		return "PjInfoMPageBean [curpage=" + curpage + ", pj=" + pj + "]";
	}
	
}
