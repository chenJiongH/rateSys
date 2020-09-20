package domain;

import java.util.List;

public class IndexSetPageBean {
	List<Acriterion> ac;
	List<Bcriterion> bc;
	List<Ccriterion> cc;
	public List<Acriterion> getAc() {
		return ac;
	}
	public void setAc(List<Acriterion> ac) {
		this.ac = ac;
	}
	public List<Bcriterion> getBc() {
		return bc;
	}
	public void setBc(List<Bcriterion> bc) {
		this.bc = bc;
	}
	public List<Ccriterion> getCc() {
		return cc;
	}
	public void setCc(List<Ccriterion> cc) {
		this.cc = cc;
	}
	@Override
	public String toString() {
		return "IndexSetPageBean [ac=" + ac + ", bc=" + bc + ", cc=" + cc + "]";
	}
	
}
