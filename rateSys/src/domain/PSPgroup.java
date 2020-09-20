package domain;

public class PSPgroup {
	private String pspgid;
	private String spgname;
	private String pid;
	private String mid;
	private String isonspot;
	private String pname;
	public String getIsonspot() {
		return isonspot;
	}  
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public void setIsonspot(String isonspot) {
		this.isonspot = isonspot;
	}
	public String getPspgid() {
		return pspgid;
	}
	public void setPspgid(String pspgid) {
		this.pspgid = pspgid;
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
		return "PSPgroup [pspgid=" + pspgid + ", spgname=" + spgname + ", pid=" + pid + ", mid=" + mid + ", isonspot="
				+ isonspot + ", pname=" + pname + "]";
	}
}
