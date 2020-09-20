package service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import domain.CityCountySchool;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class ExcelExportService {
	
	public static boolean export(List<CityCountySchool> cs, String path) {
		//新建一个市县区校excel，然后导入数据到excel中，然后发送该excel
		try {
			File fileWrite = new File(path); 
			fileWrite.createNewFile();
			OutputStream os = new FileOutputStream(fileWrite);
		
			WritableWorkbook wwb = Workbook.createWorkbook(os);// 创建文件，得到工作薄
			int sheetNum = 0;//工作表编号
			int sheetRow = 0;//工作表行
			WritableSheet ws = null; //城市工作表
			WritableFont font1 = new WritableFont(WritableFont.TIMES, 10, WritableFont.NO_BOLD);
			WritableCellFormat format1 = new WritableCellFormat(font1);
			for(int i = 0; i < cs.size(); i++) {
				if(cs.get(i).getCname() != null) {
					sheetRow = 0;
					ws = wwb.createSheet(cs.get(i).getCname(), sheetNum);// 得到当前城市工作表
					ws.setColumnView( 2 , 30 );
					ws.setColumnView( 3 , 30 );
					// 第一行基本上属性。
					Label label = new Label(0, sheetRow, "城市");
					ws.addCell(label);
					label = new Label(1, sheetRow, "县区");// 第二列，第一行，姓名
					ws.addCell(label);
					label = new Label(2, sheetRow, "校");
					ws.addCell(label);
					label = new Label(3, sheetRow, "类型");
					ws.addCell(label);
					sheetNum++;
					sheetRow++;
				} 
				Label label = new Label(0, sheetRow, cs.get(i).getCname());
				ws.addCell(label);
				label = new Label(1, sheetRow, cs.get(i).getDname());// 第二列，第一行，姓名
				ws.addCell(label);
				label = new Label(2, sheetRow, cs.get(i).getSname());
				ws.addCell(label);
				label = new Label(3, sheetRow, cs.get(i).getType());
				ws.addCell(label);
				sheetRow++;
			}
			//保存文件
			wwb.write();
			wwb.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
