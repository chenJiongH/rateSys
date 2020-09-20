package domain;

import java.util.List;
import java.util.Map;

public class SSPRatePageBean {
	List<Map<String, Object>> tableData;
	Map<String, Object> mana;
	Map<String, Object> overallReport;
	String reportDiv;
	//是否已经提交标识
	Integer flag;
	public Integer getFlag() {
		return flag;
	}
	public void setFlag(Integer flag) {
		this.flag = flag;
	}
	public Map<String, Object> getOverallReport() {
		return overallReport;
	}
	public String getReportDiv() {
		return reportDiv;
	}
	public void setReportDiv(String reportDiv) {
		this.reportDiv = reportDiv;
	}
	public void setOverallReport(Map<String, Object> overallReport) {
		this.overallReport = overallReport;
	}
	public List<Map<String, Object>> getTableData() {
		return tableData;
	}
	public void setTableData(List<Map<String, Object>> tabeData) {
		this.tableData = tabeData;
	}
	public Map<String, Object> getMana() {
		return mana;
	}
	public void setMana(Map<String, Object> mana) {
		this.mana = mana;
	}
	@Override
	public String toString() {
		return "SSPRatePageBean [tableData=" + tableData + ", mana=" + mana + ", overallReport=" + overallReport
				+ ", reportDiv=" + reportDiv + ", flag=" + flag + "]";
	}
	
}
