package domain;

public class Ccriterion {
	private String cid;
	private String cname;
	private String bid;
	private String isexplain;
	private String isannex;
	private float score;
	private float segscore;
	private float threshhold;
	private String assessmethod;
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	public String getBid() {
		return bid;
	}
	public void setBid(String bid) {
		this.bid = bid;
	}
	public String getIsexplain() {
		return isexplain;
	}
	public void setIsexplain(String isexplain) {
		this.isexplain = isexplain;
	}
	public String getIsannex() {
		return isannex;
	}
	public void setIsannex(String isannex) {
		this.isannex = isannex;
	}
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	public float getSegscore() {
		return segscore;
	}
	public void setSegscore(float segscore) {
		this.segscore = segscore;
	}
	public float getThreshhold() {
		return threshhold;
	}
	public void setThreshhold(float threshhold) {
		this.threshhold = threshhold;
	}
	public String getAssessmethod() {
		return assessmethod;
	}
	public void setAssessmethod(String assessmethod) {
		this.assessmethod = assessmethod;
	}
	@Override
	public String toString() {
		return "Ccriterion [cid=" + cid + ", cname=" + cname + ", bid=" + bid + ", isexplain=" + isexplain
				+ ", isannex=" + isannex + ", score=" + score + ", segscore=" + segscore + ", threshhold=" + threshhold
				+ ", assessmethod=" + assessmethod + "]";
	}
	
}
