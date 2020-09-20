package domain;

public class CSpecialists {
	private String cspid;
	private String spname;
	private String spusername;
	private String sppassword;
	private String spphone;
	private String sporganization;
	private String spspecialty;
	private int spage;
	private String sptitle;
	private String sprank;
	private String spfields;
	private String spgrade;
	private String mid;
	
	public String getCspid() {
		return cspid;
	}
	public void setCspid(String cspid) {
		this.cspid = cspid;
	}
	public String getSpname() {
		return spname;
	}
	public void setSpname(String spname) {
		this.spname = spname;
	}
	public String getSpusername() {
		return spusername;
	}
	public void setSpusername(String spusername) {
		this.spusername = spusername;
	}
	public String getSppassword() {
		return sppassword;
	}
	public void setSppassword(String sppassword) {
		this.sppassword = sppassword;
	}
	public String getSpphone() {
		return spphone;
	}
	public void setSpphone(String spphone) {
		this.spphone = spphone;
	}
	public String getSporganization() {
		return sporganization;
	}
	public void setSporganization(String sporganization) {
		this.sporganization = sporganization;
	}
	public String getSpspecialty() {
		return spspecialty;
	}
	public void setSpspecialty(String spspecialty) {
		this.spspecialty = spspecialty;
	}
	public int getSpage() {
		return spage;
	}
	public void setSpage(int spage) {
		this.spage = spage;
	}
	public String getSptitle() {
		return sptitle;
	}
	public void setSptitle(String sptitle) {
		this.sptitle = sptitle;
	}
	public String getSprank() {
		return sprank;
	}
	public void setSprank(String sprank) {
		this.sprank = sprank;
	}
	public String getSpfields() {
		return spfields;
	}
	public void setSpfields(String spfields) {
		this.spfields = spfields;
	}
	public String getSpgrade() {
		return spgrade;
	}
	public void setSpgrade(String spgrade) {
		this.spgrade = spgrade;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	@Override
	public String toString() {
		return "CSpecialists [cspid=" + cspid + ", spname=" + spname + ", spusername=" + spusername + ", sppassword="
				+ sppassword + ", spphone=" + spphone + ", sporganization=" + sporganization + ", spspecialty="
				+ spspecialty + ", spage=" + spage + ", sptitle=" + sptitle + ", sprank=" + sprank + ", spfields="
				+ spfields + ", spgrade=" + spgrade + ", mid=" + mid + "]";
	}
	
	
}
