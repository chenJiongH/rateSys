package domain;

public class Bcriterion {
	private String bid;
	private String bname;
	private String aid;
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public String getBname() {
		return bname;
	}
	public void setBname(String bname) {
		this.bname = bname;
	}
	public String getAid() {
		return aid;
	}
	public void setAid(String aid) {
		this.aid = aid;
	}
	@Override
	public String toString() {
		return "Bcriterion [bid=" + bid + ", bname=" + bname + ", aid=" + aid + "]";
	}
	
}
