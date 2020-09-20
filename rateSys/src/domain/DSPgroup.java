package domain;

public class DSPgroup {
	private String dspgid;
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
	public String getDspgid() {
		return dspgid;
	}
	public void setDspgid(String dspgid) {
		this.dspgid = dspgid;
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
		return "DSPgroup [dspgid=" + dspgid + ", spgname=" + spgname + ", pid=" + pid + ", mid=" + mid + "]";
	}
}
