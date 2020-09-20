package domain;

import java.util.Date;

public class Project {
	private String pid;
	private String pname;
	private Date pstime;
	private Date petime;
	private String pisannex;
	private String pisstart;
	private String pdisplayExplain;
	private String pprocess;
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public Date getPstime() {
		return pstime;
	}
	public void setPstime(Date pstime) {
		this.pstime = pstime;
	}
	public Date getPetime() {
		return petime;
	}
	public void setPetime(Date petime) {
		this.petime = petime;
	}
	public String getPisannex() {
		return pisannex;
	}
	public void setPisannex(String pisannex) {
		this.pisannex = pisannex;
	}
	public String getPisstart() {
		return pisstart;
	}
	public void setPisstart(String pisstart) {
		this.pisstart = pisstart;
	}
	public String getPdisplayExplain() {
		return pdisplayExplain;
	}
	public void setPdisplayExplain(String pdisplayExplain) {
		this.pdisplayExplain = pdisplayExplain;
	}
	public String getPprocess() {
		return pprocess;
	}
	public void setPprocess(String pprocess) {
		this.pprocess = pprocess;
	}
	@Override
	public String toString() {
		return "Project [pid=" + pid + ", pname=" + pname + ", pstime=" + pstime + ", petime=" + petime + ", pisannex="
				+ pisannex + ", pisstart=" + pisstart + ", pdisplayExplain=" + pdisplayExplain + ", pprocess="
				+ pprocess + "]";
	}
	
}
