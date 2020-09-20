package domain;

import java.util.List;

public class AcmPageBean {
	private int curpage;
	private int totalpage;
	private List<Managers> managers;
	private List<Cities> cities;
	private List<Districts> dist;
	private List<Schools> schools;
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
	public List<Managers> getManagers() {
		return managers;
	}
	public void setManagers(List<Managers> managers) {
		this.managers = managers;
	}
	public List<Cities> getCities() {
		return cities;
	}
	public void setCities(List<Cities> cities) {
		this.cities = cities;
	}
	public List<Districts> getDist() {
		return dist;
	}
	public void setDist(List<Districts> dist) {
		this.dist = dist;
	}
	public List<Schools> getSchools() {
		return schools;
	}
	public void setSchools(List<Schools> schools) {
		this.schools = schools;
	}
	
	
}
