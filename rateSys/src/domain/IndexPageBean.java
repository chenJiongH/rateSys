package domain;

import java.util.List;

public class IndexPageBean {
	String curPid;
	List<Project> pj ;
	List<Acriterion> as;
	List<Bcriterion> bs;
	List<Ccriterion> cs;
	public String getCurPid() {
		return curPid;
	}
	public void setCurPid(String curPid) {
		this.curPid = curPid;
	}
	public List<Project> getPj() {
		return pj;
	}
	public void setPj(List<Project> pj) {
		this.pj = pj;
	}
	public List<Acriterion> getAs() {
		return as;
	}
	public void setAs(List<Acriterion> as) {
		this.as = as;
	}
	public List<Bcriterion> getBs() {
		return bs;
	}
	public void setBs(List<Bcriterion> bs) {
		this.bs = bs;
	}
	public List<Ccriterion> getCs() {
		return cs;
	}
	public void setCs(List<Ccriterion> cs) {
		this.cs = cs;
	}
	@Override
	public String toString() {
		return "IndexPageBean [curPid=" + curPid + ", pj=" + pj + ", as=" + as + ", bs=" + bs + ", cs=" + cs + "]";
	}
	
}
