package domain;

import java.util.List;

public class exGroupsPageBean {
	List<Project> ps;
	List<PSPgroup> pgs;
	List<CSPgroup> cgs;
	List<DSPgroup> dgs;
	public List<Project> getPs() {
		return ps;
	}
	public void setPs(List<Project> ps) {
		this.ps = ps;
	}
	public List<PSPgroup> getPgs() {
		return pgs;
	}
	public void setPgs(List<PSPgroup> pgs) {
		this.pgs = pgs;
	}
	public List<CSPgroup> getCgs() {
		return cgs;
	}
	public void setCgs(List<CSPgroup> cgs) {
		this.cgs = cgs;
	}
	public List<DSPgroup> getDgs() {
		return dgs;
	}
	public void setDgs(List<DSPgroup> dgs) {
		this.dgs = dgs;
	}
	@Override
	public String toString() {
		return "exGroupsPageBean [ps=" + ps + ", pgs=" + pgs + ", cgs=" + cgs + ", dgs=" + dgs + "]";
	}

	
}
