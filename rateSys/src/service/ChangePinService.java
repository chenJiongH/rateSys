package service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.junit.Test;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;

import dao.ManagersDao;
import dao.PCDspecialistsDao;
import dao.SSPRateDAO;
import domain.Managers;
import servlet.LoginServlet;

public class ChangePinService {

	public boolean check(String username, String oldPassword) {
		ManagersDao manaDao = new ManagersDao();
		Managers mana = manaDao.findOneByUP(username, oldPassword);
		if(mana == null)
			return false;
		else 
			return true;
	}

	public void updatePin(String username, String oldPassword, String newPassword, String name, String mid, String phono) throws Exception{
		ManagersDao manaDao = new ManagersDao();
		manaDao.updatePin(newPassword, name, mid, phono);
	}

	public Map<String, Object> getUser(String cookieUser, String tid, String spid) throws Exception {
		ManagersDao manaDao = new ManagersDao();
		PCDspecialistsDao pcdDao = new PCDspecialistsDao();
		Map<String, Object> message = null;
		if(tid == null) 
			message = pcdDao.getUser(cookieUser, spid);
		else 
			message = manaDao.getUser(cookieUser, tid);
		if(tid != null && tid.charAt(0) == 'S') {
			SSPRateDAO dao = new SSPRateDAO();
			message.putAll(dao.findMana(tid));
		}
		return message;
	}
	// 使用 dom4j 技术修改 xml 文件
	 public void changeHead(String head) throws Exception{
		 XMLWriter writer = null;
		 XMLWriter writer2 = null;
		 FileOutputStream out = null;
        // 创建Document对象，读取已存在的Xml文件person.xml
		String path = LoginServlet.class.getResource("/").getPath();
		// 防止该文件不存在，先创建文件
		File file = new File(path + "head.xml");
		System.out.println(file.toString());
		org.dom4j.io.OutputFormat format = org.dom4j.io.OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		if(!file.exists()) {
			Document doc= DocumentHelper.createDocument();
			Element rootElem= doc.addElement("head");
			Element headElem= rootElem.addElement("headTest");
			headElem.setText("福建省教育厅教学项目评估系统");
			out = new FileOutputStream(file);
			writer = new XMLWriter(out, format);
			try {
				writer.write(doc);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				out.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		 Document document = new SAXReader().read(file);
		 Element element = document.getRootElement().element("headTest");
		 element.setText(head);
		 out = new FileOutputStream(file);
		 writer = new XMLWriter(out, format);
		try {
			writer.write(document);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			out.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
