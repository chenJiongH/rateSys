package domain;

public class SSPRateScore {
	private String cid;
	private float schoolScore;
	private String annexLocation;
	private String describe;
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public float getSchoolScore() {
		return schoolScore;
	}
	public void setSchoolScore(float schoolScore) {
		this.schoolScore = schoolScore;
	}
	public String getAnnexLocation() {
		return annexLocation;
	}
	public void setAnnexLocation(String annexLocation) {
		this.annexLocation = annexLocation;
	}
	@Override
	public String toString() {
		return "SSPRateScore [cid=" + cid + ", schoolScore=" + schoolScore + ", annexLocation=" + annexLocation
				+ ", describe=" + describe + "]";
	}
}
