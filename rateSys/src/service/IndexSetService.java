package service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import dao.IndexSetDao;
import dao.ProjectDao;
import domain.Acriterion;
import domain.Bcriterion;
import domain.Ccriterion;
import domain.IndexPageBean;
import domain.Project;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import util.JDBCUtils;

public class IndexSetService {

	public String readExcel(String path) throws SQLException {
		String message = "数据读取失败，请检查文件数据和是否已导入该项目...";
		//保存所有的导入指标
		List<Acriterion> criAS = new ArrayList<Acriterion>();
		List<Bcriterion> criBS = new ArrayList<Bcriterion>();
		List<Ccriterion> criCS = new ArrayList<Ccriterion>();
		//开启事务
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(JDBCUtils.getDataSource());
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();  
	    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);  
	    TransactionStatus status = transactionManager.getTransaction(def);
		try {
			
			File file = new File(path);
			Workbook workbook = Workbook.getWorkbook(file);
			Sheet sheet = workbook.getSheet(0);
			int totalRows = sheet.getRows();
			Cell c = null;
			String Aid = "", Bid = "", Cid = ""; 
			//在此处提前获取首行单元格项目名称对应的pid
			String pname = sheet.getCell(0,0).getContents();
			System.out.println(sheet.getCell(0,0).getContents());
			ProjectDao pdao = new ProjectDao();
			// 根据名称查找项目
			Project p = pdao.findOneByPname(pname);
			String isannex = p.getPisannex();
			String displayExplain = p.getPdisplayExplain();
			String pid = p.getPid();
			IndexPageBean pageBean = new IndexPageBean();
			pageBean = findPageBean(pid);
			
			//false
//			System.out.println(pid + "\n" + pageBean.getAs());
			//先删除原有的指标数据
			IndexSetDao indexDao = new IndexSetDao(); 
			indexDao.deletePageBean(pageBean);
			int numA = 1, numB = 1, numC = 1;
			
			List<Acriterion> as1 = indexDao.findLastA();
			if(as1 != null && as1.size() > 0) numA = Integer.parseInt(as1.get(0).getAid().substring(1)) + 1;
			
			List<Bcriterion> bs1 = indexDao.findLastB();
			if(bs1 != null && bs1.size() > 0) numB = Integer.parseInt(bs1.get(0).getBid().substring(1)) + 1;
			
			List<Ccriterion> cs1 = indexDao.findLastC();
			if(cs1 != null && cs1.size() > 0) numC = Integer.parseInt(cs1.get(0).getCid().substring(1)) + 1;

				for(int i = 3; i < totalRows; i++) {
				c = sheet.getCell(0, i);
				if(!"".equals(c.getContents())) {
					Acriterion criA = new Acriterion();
					if(numA < 10) Aid = "A0000" + numA;
					else if(numA < 100) Aid = "A000" + numA;
					else if(numA < 1000) Aid = "A00" + numA;
					else if(numA < 10000) Aid = "A0" + numA;
					else Aid = "A" + numA;
					numA++;
					//把该A指标保存
					criA.setAid(Aid);
					criA.setAname(c.getContents());
					criA.setPid(pid);
					criAS.add(criA);
				}
				c = sheet.getCell(1, i);
				if(!"".equals(c.getContents())) {
					Bcriterion criB = new Bcriterion();
					if(numB < 10) Bid = "B0000" + numB;
					else if(numB < 100) Bid = "B000" + numB;
					else if(numB < 1000) Bid = "B00" + numB;
					else if(numB < 10000) Bid = "B0" + numB;
					else Bid = "B" + numB;
					//把该A指标保存
					criB.setBid(Bid);
					criB.setBname(c.getContents());
					criB.setAid(Aid);
					numB++;
					criBS.add(criB);
				}
				c = sheet.getCell(2, i);
				if(!"".equals(c.getContents())) {
					Ccriterion criC = new Ccriterion();
					if(numC < 10) Cid = "C0000" + numC;
					else if(numC < 100) Cid = "C000" + numC;
					else if(numC < 1000) Cid = "C00" + numC;
					else if(numC < 10000) Cid = "C0" + numC;
					else Cid = "C" + numC;
					numC++;
//					//把该A指标保存
					criC.setCid(Cid);
					criC.setCname(c.getContents());
					criC.setBid(Bid);
					criC.setAssessmethod(sheet.getCell(3, i).getContents());
					//如果为空，继承项目的项值
					if(sheet.getCell(7, i).getContents() == null || "".equals(sheet.getCell(7, i).getContents()))
						criC.setIsannex(isannex);
					else criC.setIsannex(sheet.getCell(7, i).getContents());
					if(sheet.getCell(8, i).getContents() == null || sheet.getCell(8, i).getContents().equals(""))
						criC.setIsexplain(displayExplain);
					else criC.setIsexplain(sheet.getCell(8, i).getContents());
					if(sheet.getCell(4, i).getContents() != "") 
						criC.setScore(Float.parseFloat(sheet.getCell(4, i).getContents()));
					if(sheet.getCell(5, i).getContents() != "") 
						criC.setSegscore(Float.parseFloat(sheet.getCell(5, i).getContents()));
					if(sheet.getCell(6, i).getContents() != "") 
						criC.setThreshhold(Float.parseFloat(sheet.getCell(6, i).getContents()));
					criCS.add(criC);
				}
			}
//			System.out.println(criAS + "\n" + criBS + "\n" + criCS + "\n");
			indexDao.batchAInsert(criAS);
			indexDao.batchBInsert(criBS);
			indexDao.batchCInsert(criCS);
			message = "导入成功";
		    transactionManager.commit(status);
		} catch(Exception e) {
			e.printStackTrace();
			transactionManager.rollback(status);
		}  
		
		return message;
		
	}

	public IndexPageBean findPageBean(String pid) throws Exception{
		IndexPageBean pageBean = new IndexPageBean();
		IndexSetDao indexDao = new IndexSetDao();
		ProjectDao prodao = new ProjectDao();
		//刷新页面此时pid为空，默认显示项目表的第一条项目指标，因为项目页面有删除操作，则第一条项目的id不一定是P00001.
		if("".equals(pid)) {
			pid = prodao.findFirstOne().getPid();
		}
		pageBean.setCurPid(pid);
		pageBean.setPj(prodao.findAllPro());
		pageBean.setAs(indexDao.findPidAIndex(pid));

		List<Bcriterion> bs = new ArrayList<Bcriterion>();
		for(Acriterion a : pageBean.getAs()) {
			bs.addAll(indexDao.findBIndexByAid(a.getAid()));
		}
		pageBean.setBs(bs);
		
		List<Ccriterion> cs = new ArrayList<Ccriterion>();
		for(Bcriterion b : pageBean.getBs()) {
			cs.addAll(indexDao.findCIndexByBid(b.getBid()));
		}
		pageBean.setCs(cs);
		return pageBean;
	}

	public void export(String filePath, String pid) throws Exception{
		File file = new File(filePath);
		file.createNewFile();
		OutputStream os = new FileOutputStream(file);
		
		WritableWorkbook workbook = Workbook.createWorkbook(os);
		WritableSheet sheet = workbook.createSheet("sheet1", 0);
		//设置列宽
		sheet.setColumnView(0, 30);
		sheet.setColumnView(1, 40);
		sheet.setColumnView(2, 150);
		sheet.setColumnView(7, 30);
		sheet.setColumnView(8, 30);
		WritableFont font = new WritableFont(WritableFont.TIMES, 14, WritableFont.NO_BOLD);
		WritableCellFormat format = new WritableCellFormat(font);
		//水平垂直居中对齐
		format.setAlignment(Alignment.CENTRE);
		format.setVerticalAlignment(VerticalAlignment.CENTRE);
		//从数据库中查找ABC三级指标数据
		IndexPageBean pageBean = findPageBean(pid);
		
		ProjectDao pdao = new ProjectDao();
		//根据A级指标的pid项从项目表中获取项目名称
		String projectName = pdao.findOneByPid(pageBean.getAs().get(0).getPid()).getPname();
		//从(0, 0)单元格到(8, 0)单元格合并。第一行的0格合并到8格
		sheet.mergeCells(0, 0, 8, 0);
		sheet.addCell(new Label(0, 0, projectName, format));
		sheet.mergeCells(0, 1, 1, 1);
		sheet.addCell(new Label(0, 1, "考核指标", format));
		sheet.addCell(new Label(2, 1, "考核内容", format));
		sheet.mergeCells(3, 1, 3, 2);
		sheet.addCell(new Label(3, 1, "考核方式", format));
		sheet.mergeCells(4, 1, 4, 2);
		sheet.addCell(new Label(4, 1, "分值", format));
		sheet.mergeCells(5, 1, 5, 2);
		sheet.addCell(new Label(5, 1, "分段值", format));
		sheet.mergeCells(6, 1, 6, 2);
		sheet.addCell(new Label(6, 1, "阈值", format));
		sheet.mergeCells(7, 1, 7, 2);
		sheet.addCell(new Label(7, 1, "是否附件", format));
		sheet.mergeCells(8, 1, 8, 2);
		sheet.addCell(new Label(8, 1, "是否评分说明", format));
		sheet.addCell(new Label(0, 2, "A级指标", format));
		sheet.addCell(new Label(1, 2, "B级指标", format));
		sheet.addCell(new Label(2, 2, "C级指标", format));
		int nowRow = 3;
		List<Acriterion> as = pageBean.getAs();
		List<Bcriterion> bs = pageBean.getBs();
		List<Ccriterion> cs = pageBean.getCs();
		int i = 0, j = 0, k = 0;
		for(; i < as.size(); i++) {
			//在C循环中，每输出一行nowRow++;
			//计算进入C指标循环后，执行了多少次。则当退出B指标循环后， 从startA行一直合并到那时候的 nowRow - 1 行
			int startA = nowRow;
			sheet.addCell(new Label(0, nowRow, as.get(i).getAname(), format));
			//循环结束条件：bs循环到末尾，或者是当前这个b指标不是隶属于当前的a指标
			for(; j < bs.size() && as.get(i).getAid().equals(bs.get(j).getAid()); j++) {
				sheet.addCell(new Label(1, nowRow, bs.get(j).getBname(), format));
				int startB = nowRow;
				//计算进入C指标循环后，执行了多少次。则当退出C指标循环后， 从startB行一直合并到 startB + BcountC 行
				int BcountC = 0;
				//循环结束条件：cs循环到末尾，或者是当前这个c指标不是隶属于当前的b指标
				for(; k < cs.size() && bs.get(j).getBid().equals(cs.get(k).getBid()); k++) {
					sheet.setRowView(nowRow, 1000 , false);
					sheet.addCell(new Label(2, nowRow, cs.get(k).getCname(), format));
					sheet.addCell(new Label(3, nowRow, cs.get(k).getAssessmethod(), format));
					sheet.addCell(new Label(4, nowRow, String.valueOf(cs.get(k).getScore()), format));
					sheet.addCell(new Label(5, nowRow, String.valueOf(cs.get(k).getSegscore()), format));
					sheet.addCell(new Label(6, nowRow, String.valueOf(cs.get(k).getThreshhold()), format));
					sheet.addCell(new Label(7, nowRow, cs.get(k).getIsannex(), format));
					sheet.addCell(new Label(8, nowRow, cs.get(k).getIsexplain(), format));
					nowRow++;
				}
				//结束C指标循环，合并隶属于该B指标的所有C指标行
				sheet.mergeCells(1, startB, 1, nowRow - 1);
			}
			//结束B指标循环，合并隶属于该A指标的所有B指标行
			sheet.mergeCells(0, startA, 0, nowRow - 1);
		}
		workbook.write();
		workbook.close();
	}

	public String update(IndexPageBean pageBean) throws SQLException {
		//开启事务
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(JDBCUtils.getDataSource());
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();  
	    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);  
	    TransactionStatus status = transactionManager.getTransaction(def);
		try {
			IndexSetDao indexDao = new IndexSetDao();
			indexDao.update(pageBean);

		    transactionManager.commit(status);
			return "修改数据提交成功";
		}  catch (Exception e) {
			e.printStackTrace();
			transactionManager.rollback(status);
			return "修改数据提交失败，请检查数据后再次提交"; 
		}  
	}

}
