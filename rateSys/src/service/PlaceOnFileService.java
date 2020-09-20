package service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dao.PlaceOnFileDao;
import domain.Archives;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class PlaceOnFileService {

	PlaceOnFileDao dao = new PlaceOnFileDao();
	
	public List<Map<String, Object>> findPr(String prType, String mid) {
		// 查找仍然在评和已经归档的项目
		List<Map<String, Object>> prList = new ArrayList<Map<String, Object>>();
		if("exist".equals(prType)) {
			// 省级管理员的获取流程不变，市、县管理员只获取自己地区的数据
			if("M00001".equals(mid)) 
				prList.addAll(dao.findExistPr());
			else 
				prList.addAll(dao.findCityDistExistPr(mid));
		}
		else
			prList.addAll(dao.findArchivePr());
		return prList;
	}

	public List<Map<String, Object>> findSchoolByPid(String pid) {
		// 可能是归档项目，则 pid = pname。可能是在评项目，则pid 以 P开头
		
		if(pid.charAt(0) == 'P') {
			return dao.findExistSchool(pid);
		} else 
			return dao.findArchiveSchool(pid);
	}

	public List<Archives> findCByPidSid(String sid, String pid) {
		/*
		 * 归档表字段如下：
		 * 项目名	a指标名	b指标名	c指标名	 分值	自评分	 描述	附件文件位置	 市名	区名  	校名	总评文件位置	 
		 * 县级专家分数	县级专家分数说明	  县级专家单位	县级专家姓名	县级专家电话	 
		 * 市级专家分数	 市级专家分数说明  市级专家单位	市级专家姓名	市级专家电话	
		 * 省级专家分数	省级专家分数说明	省级专家单位	省级专家姓名	省级专家电话	
		 * 抽检专家分数	抽检专家分数说明	 抽检专家单位	抽检专家姓名	抽检专家电话
		 */
		// 可能是归档项目，则 pid = pname。可能是在评项目，则pid 以 P开头
		if(pid.charAt(0) == 'P') {
			return dao.findExistCByPidSid(pid, sid);
		} else 
			return dao.findArchiveCByPidSid(pid, sid);
	}

	public String exportRate(String filePath, List<Archives> cs, String tid) throws Throwable {
		String sid = cs.get(cs.size() - 1).getAname(); // 学校id 或 名称;
		String pid = cs.get(cs.size() - 1).getPname(); // 项目id 或项目名
		String pname = cs.get(cs.size() - 1).getBname(); // 项目id 或项目名
		// 获取学校所在的市县，从市县校表，或者是从归档表
		Map<String, Object> schoolPath;
		if(sid.charAt(0) == 'S') {
			schoolPath = dao.findAllPathSchoolBySid(sid);
		} else {
			schoolPath = dao.findAllPathSchoolByPnameSname(pname, sid);
		}
		File file = new File(filePath);	
		file.createNewFile();
		WritableWorkbook workbook = Workbook.createWorkbook(file);
		WritableSheet sheet = workbook.createSheet("sheet1", 0);
		sheet.setColumnView(0, 20);
		sheet.setColumnView(10, 20);
		WritableFont font = new WritableFont(WritableFont.TIMES, 14, WritableFont.NO_BOLD);
		WritableCellFormat format = new WritableCellFormat(font);
		Label label = null;
		
		String[] title = {"项目名", "a指标名", "b指标名", "c指标名", "分值", "自评分", "描述", "附件文件位置", "市名", "区名", "校名", "总评文件位置", "县级专家分数",
							"县级专家分数说明", "县级专家单位", "县级专家姓名", "县级专家电话", "市级专家分数", "市级专家分数说明", "市级专家单位", "市级专家姓名", "市级专家电话",
							"省级专家分数", "省级专家分数说明", "省级专家单位", "省级专家姓名","省级专家电话", "抽检专家分数", "抽检专家分数说明", "抽检专家单位", "抽检专家姓名", "抽检专家电话"};
		for(int i = 0; i < 32; i++) {
			label = new Label(i, 0, title[i], format);
			sheet.addCell(label);				
		}
		int sheetRow = 0;
		String temp = "";
		cs.remove(cs.size() - 1);
		for(Archives c : cs) {
			sheetRow++;
			label = new Label(0, sheetRow, pname, format);
			sheet.addCell(label);
			label = new Label(1, sheetRow, c.getAname(), format);
			sheet.addCell(label);
			label = new Label(2, sheetRow, c.getBname(), format);
			sheet.addCell(label);
			label = new Label(3, sheetRow, c.getCname(), format);
			sheet.addCell(label);
			temp = c.getScore() == null? "" : c.getScore().toString();
			label = new Label(4, sheetRow, temp, format);
			sheet.addCell(label);
			temp = c.getSchoolScore() == null? "" : c.getSchoolScore().toString();
			label = new Label(5, sheetRow, temp, format);
			sheet.addCell(label);
			label = new Label(6, sheetRow, c.getDescription(), format);
			sheet.addCell(label);
			label = new Label(7, sheetRow, c.getAnnexLocation(), format);
			sheet.addCell(label);
			label = new Label(8, sheetRow, schoolPath.get("cityName").toString(), format);
			sheet.addCell(label);
			label = new Label(9, sheetRow, schoolPath.get("distName").toString(), format);
			sheet.addCell(label);
			label = new Label(10, sheetRow, schoolPath.get("schoolName").toString(), format);
			sheet.addCell(label);
			label = new Label(11, sheetRow, c.getOverallFileName(), format);
			sheet.addCell(label);
			temp = c.getDistScore() == null? "" : c.getDistScore().toString();
			label = new Label(12, sheetRow, temp, format);
			sheet.addCell(label);
			label = new Label(13, sheetRow, c.getDistExplain(), format);
			sheet.addCell(label);
			label = new Label(14, sheetRow, c.getDspOrganization(), format);
			sheet.addCell(label);
			label = new Label(15, sheetRow, c.getDspName(), format);
			sheet.addCell(label);
			label = new Label(16, sheetRow, c.getDspPhone(), format);
			sheet.addCell(label);
			temp = c.getCityScore() == null? "" : c.getCityScore().toString();
			label = new Label(17, sheetRow, temp, format);
			sheet.addCell(label);
			label = new Label(18, sheetRow, c.getCityExplain(), format);
			sheet.addCell(label);
			label = new Label(19, sheetRow, c.getCspOrganization(), format);
			sheet.addCell(label);
			label = new Label(20, sheetRow, c.getCspName(), format);
			sheet.addCell(label);
			label = new Label(21, sheetRow, c.getCspPhone(), format);
			sheet.addCell(label);
			temp = c.getPspScore() == null? "" : c.getPspScore().toString();
			label = new Label(22, sheetRow, temp, format);
			sheet.addCell(label);
			label = new Label(23, sheetRow, c.getPspExplain(), format);
			sheet.addCell(label);
			label = new Label(24, sheetRow, c.getPspOrganization(), format);
			sheet.addCell(label);
			label = new Label(25, sheetRow, c.getPspName(), format);
			sheet.addCell(label);
			label = new Label(26, sheetRow, c.getPspPhone(), format);
			sheet.addCell(label);
			temp = c.getCheckScore() == null? "" : c.getCheckScore().toString();
			label = new Label(27, sheetRow, temp, format);
			sheet.addCell(label);
			label = new Label(28, sheetRow, c.getCheckExplain(), format);
			sheet.addCell(label);
			label = new Label(29, sheetRow, c.getCheckspOrganization(), format);
			sheet.addCell(label);
			label = new Label(30, sheetRow, c.getCheckspName(), format);
			sheet.addCell(label);
			label = new Label(31, sheetRow, c.getCheckPhone(), format);
			sheet.addCell(label);
		}
		//写入数据
		workbook.write();
		//关流
		workbook.close();
		return null;
	}

	public void exportScore(String filePath, List<Archives> cs, int coffset, String tid) throws Throwable {
		String pname = cs.get(cs.size() - 1).getBname(); // 项目id 或项目名
		// 获取学校所在的市县，从市县校表，或者是从归档表
		
		File file = new File(filePath);	
		file.createNewFile();
		WritableWorkbook workbook = Workbook.createWorkbook(file);
		WritableSheet sheet = workbook.createSheet("sheet1", 0);
		sheet.setColumnView(0, 30);
		sheet.setColumnView(1, 35);
		sheet.setColumnView(1, 40);
		WritableFont font = new WritableFont(WritableFont.TIMES, 14, WritableFont.NO_BOLD);
		WritableCellFormat format = new WritableCellFormat(font);
		format.setAlignment(Alignment.CENTRE);
		format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
		Label label = null;
		String[] title = {"考核指标", "考核内容"};
		// 处理表头
			label = new Label(0, 0, title[0], format);
			sheet.addCell(label);
			label = new Label(0, 1, "A级指标", format);
			sheet.addCell(label);
			label = new Label(1, 1, "B级指标", format);
			sheet.addCell(label);
			label = new Label(2, 1, "C级指标", format);
			sheet.addCell(label);
			// 合并单元格
			sheet.mergeCells(0, 0, 1, 0);
			label = new Label(2, 0, title[1], format);
			sheet.addCell(label);
		// 存放临时的分数项
		String temp = "";
		cs.remove(cs.size() - 1);
		// 计算该项目下有多少个学校
		int schoolNum = cs.size() / coffset;
		int sheetRow = 1;
		 // A 指标从第二行开始，用于合并
		int Arow = 2;
		 // B 指标从第二行开始，用于合并
		int Brow = 2;
		String Aname = cs.get(0).getAname(),
				Bname = cs.get(0).getBname();
		for(int i = 0; i < coffset; i++) {
			Archives c = cs.get(i);
			System.out.println(c.getSchoolName());
			// 换了一个新的A指标，则把之前的A指标都合并
			if(!Aname.equals(c.getAname())) {
				Aname = c.getAname();
				// 合并单元格
				sheet.mergeCells(0, Arow, 0, 1 + i);
				Arow = i + 2;
			}
			// 换了一个新的B指标，则把之前的B指标都合并
			if(!Bname.equals(c.getBname())) {
				Bname = c.getBname();
				// 合并单元格
				sheet.mergeCells(1, Brow, 1, 1 + i);
				Brow = i + 2;
			}
			sheetRow++;
			// 当行输出 ABC 指标名称
			label = new Label(0, sheetRow, Aname, format);
			sheet.addCell(label);
			label = new Label(1, sheetRow, Bname, format);
			sheet.addCell(label);
			label = new Label(2, sheetRow, c.getCname(), format);
			sheet.addCell(label);
			for(int j = 0; j < schoolNum; j++) {
				c = cs.get(i + (j * coffset));
				
				// 在第一行输出各自学校的名字
				label = new Label(3 + (j * 5), 0, c.getSchoolName(), format);
				sheet.addCell(label);
				label = new Label(4 + (j * 5), 0, c.getSchoolName(), format);
				sheet.addCell(label);
				// 县级管理员导出到县级为止
				if(tid.charAt(0) != 'D') {
					label = new Label(5 + (j * 5), 0, c.getSchoolName(), format);
					sheet.addCell(label);
					// 市级管理员导出到市级为止
					if(tid.charAt(0) != 'C') {
						label = new Label(6 + (j * 5), 0, c.getSchoolName(), format);
						sheet.addCell(label);
						label = new Label(7 + (j * 5), 0, c.getSchoolName(), format);
						sheet.addCell(label);
					}
				}
				
				// 在一行内输出多个学校的分数
				temp = c.getSchoolScore() == null? "" : c.getSchoolScore().toString();
				label = new Label(3 + (j * 5), sheetRow, temp, format);
				sheet.addCell(label);
				label = new Label(3 + (j * 5), 1, "自评分", format);
				sheet.addCell(label);
				temp = c.getDistScore() == null? "" : c.getDistScore().toString();
				label = new Label(4 + (j * 5), sheetRow, temp, format);
				sheet.addCell(label);
				label = new Label(4 + (j * 5), 1, "县评分", format);
				sheet.addCell(label);
				// 县级管理员导出到县级为止
				if(tid.charAt(0) != 'D') {
					temp = c.getCityScore() == null? "" : c.getCityScore().toString();
					label = new Label(5 + (j * 5), sheetRow, temp, format);
					sheet.addCell(label);
					label = new Label(5 + (j * 5), 1, "市评分", format);
					sheet.addCell(label);
					// 市级管理员导出到市级为止
					if(tid.charAt(0) != 'C') {
						temp = c.getPspScore() == null? "" : c.getPspScore().toString();
						label = new Label(6 + (j * 5), sheetRow, temp, format);
						sheet.addCell(label);
						label = new Label(6 + (j * 5), 1, "省评分", format);
						sheet.addCell(label);
						temp = c.getCheckScore() == null? "" : c.getCheckScore().toString();
						label = new Label(7 + (j * 5), sheetRow, temp, format);
						sheet.addCell(label);
						label = new Label(7 + (j * 5), 1, "抽检评分", format);
						sheet.addCell(label);						
					}
				}
			}
		}
		sheet.mergeCells(0, Arow, 0, sheetRow);
		sheet.mergeCells(1, Brow, 1, sheetRow);
		int span; // 因为每个级别的管理员输出的级别分数不同，故第一行合并的跨度 span 不同 
//		第一行对于同个学校的名字进行合并
		for(int i = 0; i < schoolNum; i++) {
			if(tid.charAt(0) == 'D') {
				span = 2;
			} else if(tid.charAt(0) == 'C') {
				span = 3;
			} else 
				span = 5;
			sheet.mergeCells(3 + (i * span), 0, 3 + ((i + 1) * span) - 1, 0);
		}
		//写入数据
		workbook.write();
		//关流
		workbook.close();
	}
	
	
}
