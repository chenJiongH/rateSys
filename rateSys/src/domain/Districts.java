package domain;

public class Districts {
	private String did;
	private String dname;
	private String cid;
	public String getDid() {
		return did;
	}
	public void setDid(String did) {
		this.did = did;
	}
	public String getDname() {
		return dname;
	}
	public void setDname(String dname) {
		this.dname = dname;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	@Override
	public String toString() {
		return "Districts [did=" + did + ", dname=" + dname + ", cid=" + cid + "]";
	}
}
