package domain;

public class CSPgroup {
	private String cspgid;
	private String spgname;
	private String pid;
	private String mid;
	private String isonspot;

	public String getIsonspot() {
		return isonspot;
	}
	public void setIsonspot(String isonspot) {
		this.isonspot = isonspot;
	}
	public String getCspgid() {
		return cspgid;
	}
	public void setCspgid(String cspgid) {
		this.cspgid = cspgid;
	}
	public String getSpgname() {
		return spgname;
	}
	public void setSpgname(String spgname) {
		this.spgname = spgname;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	@Override
	public String toString() {
		return "CSPgroup [cspgid=" + cspgid + ", spgname=" + spgname + ", pid=" + pid + ", mid=" + mid + "]";
	}
}
