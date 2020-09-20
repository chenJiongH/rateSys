package service;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import dao.CCSDao;
import dao.ManagersDao;
import domain.CCS;
import domain.Cities;
import domain.Districts;
import domain.Schools;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import util.JDBCUtils;

public class CCSService {
	
	private static Set<String> usernameSet = new HashSet<String>();
	private static Set<String> passwordSet = new HashSet<String>();
	
	public static Map<Integer, String> findCitys() {
		CCSDao ccsdao = new CCSDao();
		List<Cities> cities = ccsdao.findCities();
		Map<Integer, String> map = new TreeMap<Integer, String>();
		for(Cities c : cities) {
			//将城市编号如D001，转换为整型1
			int cityNum = Integer.parseInt(c.getCid().substring(c.getCid().indexOf('C') + 1));
			map.put(cityNum, c.getCname());
		}
		return map;
	}

	public CCS findCCS() {
		CCSDao ccsDao = new CCSDao();
		CCS ccs = null;
		try {
			ccs = ccsDao.findCCS();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return ccs;
	}
	//acMServlet.java中传入文件绝对路径。创建Excel文件并生成默认数据
	public static void createInitData(String filePath) throws Exception{
		System.out.println("ccsService 服务器文件路径:" + filePath);
		File file = new File(filePath);		
		//开启事务
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(JDBCUtils.getDataSource());
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();  
	    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);  
	    TransactionStatus status = transactionManager.getTransaction(def);
		try {
			file.createNewFile();
			WritableWorkbook workbook = Workbook.createWorkbook(file);
			WritableSheet sheet = workbook.createSheet("sheet1", 0);
			sheet.setColumnView(4, 15);
			WritableFont font = new WritableFont(WritableFont.TIMES, 14, WritableFont.NO_BOLD);
			WritableCellFormat format = new WritableCellFormat(font);
			Label label = null;
			String[] title = {"MID", "姓名", "账号", "密码", "电话", "市", "县区", "学校", "级别", "市编号", "县区编号", "学校编号"};
			for(int i = 0; i < 12; i++) {
				label = new Label(i, 0, title[i], format);
				sheet.addCell(label);				
			}
			ManagersDao manadao = new ManagersDao();
			//获得最后一位的编号，开始编号
			String midStr = manadao.findLastOne();
			int mid = Integer.parseInt(midStr.substring(1));
			
			String username = "";
			String password = "";
			int row = 1;//用一行加一行
			//得到市级缺失管理员
			List<Map<String, Object>> cityLackMana = manadao.findCityLackMana();
			for(Map m : cityLackMana) {
				mid ++;
				username = createUniUsername("c");
				password = createUniPassword();
				manadao.initDataInsert(getMid(mid), username, password, m.get("CID").toString());
				//把该记录插入表格
				addRow(sheet, row, "市级", m, format, mid, username, password);
			}
			//得到县级缺失管理员
			List<Map<String, Object>> distLackMana = manadao.findDistLackMana();
			for(Map m : distLackMana) {
				mid ++;
				username = createUniUsername("d");
				password = createUniPassword();
				manadao.initDataInsert(getMid(mid), username, password,  m.get("DID").toString());
				//把该记录插入表格
				addRow(sheet, row, "县级", m, format, mid, username, password);
			}
			List<Map<String, Object>> schLackMana = manadao.findSchLackMana();
			for(Map m : schLackMana) {
				mid ++;
				username = createUniUsername("s");
				password = createUniPassword();
				manadao.initDataInsert(getMid(mid), username, password,  m.get("SID").toString());
				//把该记录插入表格
				addRow(sheet, row, "校级", m, format, mid, username, password);
			}
			//写入数据
			workbook.write();
			//关流
			workbook.close();
		    transactionManager.commit(status);
			}  catch (Exception e) {
				e.printStackTrace();
				transactionManager.rollback(status);
				throw new Exception();
			} 
	}
	
	private static void addRow(WritableSheet sheet, int row, String rank, Map m, WritableCellFormat format, int mid, String username, String password) throws Exception {
		Label label = null;
		label = new Label(0, row, getMid(mid), format);
		sheet.addCell(label);
		label = new Label(2, row, username, format);
		sheet.addCell(label);
		label = new Label(3, row, password, format);
		sheet.addCell(label);
		if("市级".equals(rank)) {
			label = new Label(5, row, m.get("CNAME").toString(), format);
			sheet.addCell(label);
			label = new Label(9, row, m.get("CID").toString(), format);
			sheet.addCell(label);
			
		} else if("县级".equals(rank)) {
			CCSDao dao = new CCSDao();
			Cities city = dao.findOneCByCid(m.get("CID").toString());
			label = new Label(5, row, city.getCname(),format);
			sheet.addCell(label);
			label = new Label(6, row, m.get("DNAMFE").toString(),format);
			sheet.addCell(label);
			
			label = new Label(9, row, m.get("CID").toString(),format);
			sheet.addCell(label);
			label = new Label(10, row, m.get("DID").toString(), format);
			sheet.addCell(label);
			
		} else if("校级".equals(rank)) {
			CCSDao dao = new CCSDao();
			Map<String, Object> cdsview = dao.findCDSNameBySid(m.get("SID").toString());
			label = new Label(5, row, cdsview.get("cname").toString(),format);
			sheet.addCell(label);
			label = new Label(6, row, cdsview.get("dname").toString(),format);
			sheet.addCell(label);
			label = new Label(7, row, cdsview.get("sname").toString(), format);
			sheet.addCell(label);
			
			label = new Label(9, row, cdsview.get("cid").toString(),format);
			sheet.addCell(label);
			label = new Label(10, row, cdsview.get("did").toString(), format);
			sheet.addCell(label);
			label = new Label(11, row, cdsview.get("sid").toString(), format);
			sheet.addCell(label);
		}
		label = new Label(8, row, rank, format);//各行的级别
		sheet.addCell(label);
	}

	private static String getMid(int mid) {
		String midStr = "";
		if(mid < 10) 
			midStr = "M0000" + mid;
		else if(mid < 100)
			midStr = "M000" + mid;
		else if(mid < 1000)
			midStr = "M00" + mid;
		else if(mid < 10000)
			midStr = "M0" + mid;
		else midStr = "M" + mid;
		return midStr;
	}

//	（账号随机6位，市首字母为小写c，县校首字母分别为小写d、s。后二位为字母，最后三位数字。字母大小写皆可）
//	（密码是第一位是字母，后面5位是字母和数字的组合。字母大小写皆可）
	//生成唯一的账号
	public static String createUniUsername(String init) {
		String id = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";//0到61。字母0到51，数字52到61  
//		random.nextInt() % (n-m+1)+m; 生成[m,n]之间的整数
		Random random = new Random();
		String username = "";
		do {
			username = init;
			for(int i = 0; i < 2; i++) 
				username += "" + id.charAt(random.nextInt(52));
			for(int i = 0; i < 3; i++)
				username += "" + id.charAt((random.nextInt(10))+52);
		} while(!usernameSet.add(username));
		return username;
	}
	//生成唯一的密码
	public static String createUniPassword() {
		String id = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";//0到61。字母0到51，数字52到61  
		String password = "";
//		random.nextInt() % (n-m+1)+m; 生成[m,n]之间的整数
		Random random = new Random();
		do {
			password = "";
			password += "" + id.charAt(random.nextInt(52));
			for(int i = 0; i < 4; i++)
				password += "" + id.charAt(random.nextInt(62));			
		} while(!passwordSet.add(password));
		return password;
	}

	public List<Map<String, Object>> findAllCity() {
		CCSDao ccsdao = new CCSDao();
		List<Map<String, Object>> cs = ccsdao.findAllCity();
		return cs;
	}

	public List<Map<String, Object>> findDistByCid(String cid) {
		CCSDao ccsdao = new CCSDao();
		List<Map<String, Object>> ds = ccsdao.findDistByCidNow(cid);
		return ds;
	}

	public List<Map<String, Object>> findSchByDid(String dname, String cname) {
		CCSDao ccsdao = new CCSDao();
		List<Map<String, Object>> ss = ccsdao.findSchByDid(dname, cname);
		return ss;
	}
	
}
