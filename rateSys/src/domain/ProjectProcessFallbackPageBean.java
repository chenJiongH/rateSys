package domain;

public class ProjectProcessFallbackPageBean {
	private String pname;
	private String petime;
	private String process;
	private String nowRate;
	private String upLevelRate;
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public String getPetime() {
		return petime;
	}
	public void setPetime(String petime) {
		this.petime = petime;
	}
	public String getProcess() {
		return process;
	}
	public void setProcess(String process) {
		this.process = process;
	}
	public String getNowRate() {
		return nowRate;
	}
	public void setNowRate(String nowRate) {
		this.nowRate = nowRate;
	}
	public String getUpLevelRate() {
		return upLevelRate;
	}
	public void setUpLevelRate(String upLevelRate) {
		this.upLevelRate = upLevelRate;
	}
	@Override
	public String toString() {
		return "ProjectProcessFallbackPageBean [pname=" + pname + ", petime=" + petime + ", process=" + process
				+ ", nowRate=" + nowRate + ", upLevelRate=" + upLevelRate + "]";
	}
	
}
