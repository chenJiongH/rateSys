package domain;

import java.io.Serializable;

public class Archives implements Serializable{
	private Integer fid;
	private String pname;
	private String aname;
	private String bname;
	private String cname;
	private Float score;
	private Float schoolScore;
	private String description;
	private String annexLocation;
	private String cityName;
	private String distName;
	private String schoolName;
	private String reportLocation;
	private String overallFileName;
	private Float distScore;
	private String distExplain;
	private String DspOrganization;
	private String dspName;
	private String dspPhone;
	private Float cityScore;
	private String cityExplain;
	private String CspOrganization;
	private String cspName;
	private String cspPhone;
	private Float pspScore;
	private String pspExplain;
	private String PspOrganization;
	private String pspName;
	private String pspPhone;
	private Float checkScore;
	private String checkExplain;
	private String checkspOrganization;
	private String checkspName;
	private String checkPhone;
	public String getOverallFileName() {
		return overallFileName;
	}
	public void setOverallFileName(String overallFileName) {
		this.overallFileName = overallFileName;
	}
	public Integer getFid() {
		return fid;
	}
	public void setFid(Integer fid) {
		this.fid = fid;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public String getAname() {
		return aname;
	}
	public void setAname(String aname) {
		this.aname = aname;
	}
	public String getBname() {
		return bname;
	}
	public void setBname(String bname) {
		this.bname = bname;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public Float getScore() {
		return score;
	}
	public void setScore(Float score) {
		this.score = score;
	}
	public Float getSchoolScore() {
		return schoolScore;
	}
	public void setSchoolScore(Float schoolScore) {
		this.schoolScore = schoolScore;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String temp() {
		return annexLocation;
	}
	public String getAnnexLocation() {
		return annexLocation;
	}
	public void setAnnexLocation(String annexLocation) {
		this.annexLocation = annexLocation;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getDistName() {
		return distName;
	}
	public void setDistName(String distName) {
		this.distName = distName;
	}
	public String getSchoolName() {
		return schoolName;
	}
	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}
	public String getReportLocation() {
		return reportLocation;
	}
	public void setReportLocation(String reportLocation) {
		this.reportLocation = reportLocation;
	}
	public Float getDistScore() {
		return distScore;
	}
	public void setDistScore(Float distScore) {
		this.distScore = distScore;
	}
	public String getDistExplain() {
		return distExplain;
	}
	public void setDistExplain(String distExplain) {
		this.distExplain = distExplain;
	}
	public String getDspOrganization() {
		return DspOrganization;
	}
	public void setDspOrganization(String dspOrganization) {
		DspOrganization = dspOrganization;
	}
	public String getDspName() {
		return dspName;
	}
	public void setDspName(String dspName) {
		this.dspName = dspName;
	}
	public String getDspPhone() {
		return dspPhone;
	}
	public void setDspPhone(String dspPhone) {
		this.dspPhone = dspPhone;
	}
	public Float getCityScore() {
		return cityScore;
	}
	public void setCityScore(Float cityScore) {
		this.cityScore = cityScore;
	}
	public String getCityExplain() {
		return cityExplain;
	}
	public void setCityExplain(String cityExplain) {
		this.cityExplain = cityExplain;
	}
	public String getCspOrganization() {
		return CspOrganization;
	}
	public void setCspOrganization(String cspOrganization) {
		CspOrganization = cspOrganization;
	}
	public String getCspName() {
		return cspName;
	}
	public void setCspName(String cspName) {
		this.cspName = cspName;
	}
	public String getCspPhone() {
		return cspPhone;
	}
	public void setCspPhone(String cspPhone) {
		this.cspPhone = cspPhone;
	}
	public Float getPspScore() {
		return pspScore;
	}
	public void setPspScore(Float pspScore) {
		this.pspScore = pspScore;
	}
	public String getPspExplain() {
		return pspExplain;
	}
	public void setPspExplain(String pspExplain) {
		this.pspExplain = pspExplain;
	}
	public String getPspOrganization() {
		return PspOrganization;
	}
	public void setPspOrganization(String pspOrganization) {
		PspOrganization = pspOrganization;
	}
	public String getPspName() {
		return pspName;
	}
	public void setPspName(String pspName) {
		this.pspName = pspName;
	}
	public String getPspPhone() {
		return pspPhone;
	}
	public void setPspPhone(String pspPhone) {
		this.pspPhone = pspPhone;
	}
	public Float getCheckScore() {
		return checkScore;
	}
	public void setCheckScore(Float checkScore) {
		this.checkScore = checkScore;
	}
	public String getCheckExplain() {
		return checkExplain;
	}
	public void setCheckExplain(String checkExplain) {
		this.checkExplain = checkExplain;
	}
	public String getCheckspOrganization() {
		return checkspOrganization;
	}
	public void setCheckspOrganization(String checkspOrganization) {
		this.checkspOrganization = checkspOrganization;
	}
	public String getCheckspName() {
		return checkspName;
	}
	public void setCheckspName(String checkspName) {
		this.checkspName = checkspName;
	}
	@Override
	public String toString() {
		return "Archives [fid=" + fid + ", pname=" + pname + ", aname=" + aname + ", bname=" + bname + ", cname="
				+ cname + ", score=" + score + ", schoolScore=" + schoolScore + ", description=" + description
				+ ", annexLocation=" + annexLocation + ", cityName=" + cityName + ", distName=" + distName
				+ ", schoolName=" + schoolName + ", reportLocation=" + reportLocation + ", distScore=" + distScore
				+ ", distExplain=" + distExplain + ", DspOrganization=" + DspOrganization + ", dspName=" + dspName
				+ ", dspPhone=" + dspPhone + ", cityScore=" + cityScore + ", cityExplain=" + cityExplain
				+ ", CspOrganization=" + CspOrganization + ", cspName=" + cspName + ", cspPhone=" + cspPhone
				+ ", pspScore=" + pspScore + ", pspExplain=" + pspExplain + ", PspOrganization=" + PspOrganization
				+ ", pspName=" + pspName + ", pspPhone=" + pspPhone + ", checkScore=" + checkScore + ", checkExplain="
				+ checkExplain + ", checkspOrganization=" + checkspOrganization + ", checkspName=" + checkspName
				+ ", checkPhone=" + checkPhone + "]";
	}
	public String getCheckPhone() {
		return checkPhone;
	}
	public void setCheckPhone(String checkPhone) {
		this.checkPhone = checkPhone;
	}
}
