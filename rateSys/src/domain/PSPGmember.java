package domain;

public class PSPGmember {
	private String pspid;
	private String pspgid;
	private String isleader;
	public String getPspid() {
		return pspid;
	}
	public void setPspid(String pspid) {
		this.pspid = pspid;
	}
	public String getPspgid() {
		return pspgid;
	}
	public void setPspgid(String pspgid) {
		this.pspgid = pspgid;
	}
	public String getIsleader() {
		return isleader;
	}
	public void setIsleader(String isleader) {
		this.isleader = isleader;
	}
	@Override
	public String toString() {
		return "PSPGmember [pspid=" + pspid + ", pspgid=" + pspgid + ", isleader=" + isleader + "]";
	}

}
