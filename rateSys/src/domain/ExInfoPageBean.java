package domain;

import java.util.List;

public class ExInfoPageBean {
//	curpage+totalpage+List<PSpecialists>+List<CSpecialists>+List<DSpecialists>三个集合最多有一个不为空。全为空即是该管理员没有添加下属专家
//	[curpage=0, totalpage=0, ps=[], cs=null, ds=null]
//  {"curpage":0,"totalpage":0,"ps":[],"cs":null,"ds":null}
	private int curpage;
	private int totalpage;
	private List<PSpecialists> ps;
	private List<CSpecialists> cs;
	private List<DSpecialists> ds;
	public int getCurpage() {
		return curpage;
	}
	public void setCurpage(int curpage) {
		this.curpage = curpage;
	}
	public int getTotalpage() {
		return totalpage;
	}
	public void setTotalpage(int totalpage) {
		this.totalpage = totalpage;
	}
	public List<PSpecialists> getPs() {
		return ps;
	}
	public void setPs(List<PSpecialists> ps) {
		this.ps = ps;
	}
	public List<CSpecialists> getCs() {
		return cs;
	}
	public void setCs(List<CSpecialists> cs) {
		this.cs = cs;
	}
	public List<DSpecialists> getDs() {
		return ds;
	}
	public void setDs(List<DSpecialists> ds) {
		this.ds = ds;
	}
	@Override
	public String toString() {
		return "exInfoPageBean [curpage=" + curpage + ", totalpage=" + totalpage + ", ps=" + ps + ", cs=" + cs + ", ds="
				+ ds + "]";
	}
	
}
