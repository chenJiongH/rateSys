package domain;

public class Managers {
	private String mid;
	private String mname;
	private String musername;
	private String mpassword;
	private String mphono;
	private String tid;
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getMname() {
		return mname;
	}
	public void setMname(String mname) {
		this.mname = mname;
	}
	public String getMusername() {
		return musername;
	}
	public void setMusername(String musername) {
		this.musername = musername;
	}
	public String getMpassword() {
		return mpassword;
	}
	public void setMpassword(String mpassword) {
		this.mpassword = mpassword;
	}
	public String getMphono() {
		return mphono;
	}
	public void setMphono(String mphono) {
		this.mphono = mphono;
	}
	public String getTid() {
		return tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	@Override
	public String toString() {
		return "Managers [mid=" + mid + ", mname=" + mname + ", musername=" + musername + ", mpassword=" + mpassword
				+ ", mphono=" + mphono + ", tid=" + tid + "]";
	}
	
}
