package domain;

public class CityCountySchool {
	private String cname;
	private String dname;
	private String sname;
	private String type;
	private String sidNum;
	public String getSidNum() {
		return sidNum;
	}
	public void setSidNum(String sidNum) {
		this.sidNum = sidNum;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public String getDname() {
		return dname;
	}
	public void setDname(String dname) {
		this.dname = dname;
	}
	public String getSname() {
		return sname;
	}
	public void setSname(String sname) {
		this.sname = sname;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "CityCountySchool [cname=" + cname + ", dname=" + dname + ", sname=" + sname + ", type=" + type + "]";
	}
	

}
