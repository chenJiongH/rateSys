package domain;

public class Acriterion {
	private String aid;
	private String aname;
	private String pid;
	public String getAid() {
		return aid;
	}
	public void setAid(String aid) {
		this.aid = aid;
	}
	public String getAname() {
		return aname;
	}
	public void setAname(String aname) {
		this.aname = aname;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	@Override
	public String toString() {
		return "Acriterion [aid=" + aid + ", aname=" + aname + ", pid=" + pid + "]";
	}
	
}
