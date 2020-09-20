package servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.fasterxml.jackson.databind.ObjectMapper;

import domain.CityCountySchool;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import service.InsertCCS;

/**
 * Servlet implementation class UploadServlet
 */
//导入文件
//构造临时文件来保存上传文件
@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// 上传文件存储目录
	private static final String UPLOAD_DIRECTORY = "upload";

	// 上传配置
	private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3; // 3MB
	private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
	private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB
	
	public ArrayList preview(String path) throws Exception {
		File file = new File(path);
		// 创建新的Excel 工作簿
		
		ArrayList<CityCountySchool> cs = new ArrayList<CityCountySchool>();
		Workbook rwb = null;
		rwb = Workbook.getWorkbook(file);
		Sheet[] sheet = rwb.getSheets();
		for(int page = 0; page < sheet.length; page++) {
			int rsRows = sheet[page].getRows();// 行数
//			System.out.println("行数" + rsRows);
			String simNumber = "";// 每个单元格中的数据
			for (int i = 1; i < rsRows; i++) {
				CityCountySchool c = new CityCountySchool();
				c.setCname(null);
				c.setDname(null);
				c.setSname(null);
				c.setType(null);
				Cell cell = sheet[page].getCell(0, i);
				simNumber = cell.getContents();
//				System.out.println(simNumber);
				if(!"".equals(simNumber)) {
					c.setCname(simNumber);
				}
				cell = sheet[page].getCell(1, i);
				simNumber = cell.getContents();
				if(!"".equals(simNumber)) {
					c.setDname(simNumber);
				}
				cell = sheet[page].getCell(2, i);
				simNumber = cell.getContents();
				if(!"".equals(simNumber)) {
					c.setSname(simNumber);
				}
				cell = sheet[page].getCell(3, i);
				simNumber = cell.getContents();
				if(!"".equals(simNumber)) {
					c.setType(simNumber);
				}
				cell = sheet[page].getCell(4, i);
				simNumber = cell.getContents();
				if(!"".equals(simNumber)) {
					c.setSidNum(simNumber);
				}
				cs.add(c);
			}
		}
		return cs;
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 检测是否为文件上传
		/*
		  在得到上传文件之前，首先要判断客户端<form>标记的enctype属性是否是“multipart/form-data"。
		  也可以说是判断是普通表单，还是带文件上传的表单。文件上传的表单值不能按普通表单接收值那样直接获取。
		 */
		/*
		 在 fileupload 包中， HTTP 请求中的复杂表单元素都被看做一个 FileItem
		 对象；FileItem 对象必须由 ServletFileUpload 类中的 parseRequest() 方法解析 HTTP 请求（即被包装之后的
		 HttpServletRequest 对象）出来，即分离出具体的文本表单和上传文件；而 ServletFileUpload 对象的创建需要依赖于
		 FileItemFactory 工厂将获得的上传文件 FileItem 对象保存至服务器硬盘，即 DiskFileItem 对象。
		 */
		String path = "";
		if (!ServletFileUpload.isMultipartContent(request)) {
			// 如果不是则停止
			PrintWriter writer = response.getWriter();
			writer.println("Error: 表单必须包含 enctype=multipart/form-data");
			writer.flush();
			return;
		}
		
		// 配置上传参数
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
		factory.setSizeThreshold(MEMORY_THRESHOLD);
		// 设置临时存储目录
		factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

		ServletFileUpload upload = new ServletFileUpload(factory);

		// 设置最大文件上传值
		upload.setFileSizeMax(MAX_FILE_SIZE);

		// 设置最大请求值 (包含文件和表单数据)
		upload.setSizeMax(MAX_REQUEST_SIZE);

		// 中文处理
		upload.setHeaderEncoding("UTF-8");

		// 构造临时路径来存储上传的文件
		// 这个路径相对当前应用的目录
		String uploadPath = "D:\\upload";

		// 如果目录不存在则创建
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}

		try {
			// 解析请求的内容提取文件数据
			@SuppressWarnings("unchecked")
			//upload是一个文件上传工厂，之前是在设置该工厂的初始参数，接下来工厂开始提取文件数据
			List<FileItem> formItems = upload.parseRequest(request);
			if (formItems != null && formItems.size() > 0) {
				// 迭代表单数据
				for (FileItem item : formItems) {
					//判断某个表单是否为普通类型，item.isFormField() == false 表示该表单项为file类型
					if (!item.isFormField()) {
						/*
						  fileItem.getName()获得上传时文件名， new File(fileItem.getName()) 通过文件名获得一个文件对象， 
						  (new File(fileItem.getName())).getName();调用文件对象的getName()方法，获得不带路径的文件名。
						 */
						String fileName = new File(item.getName()).getName();
						//所以当前上传文件放置在服务器的D:\\upload\\上传文件名 处
						String filePath = uploadPath + File.separator + fileName;
						File storeFile = new File(filePath);
						path = filePath;
						// 在控制台输出文件的上传路径
//						System.out.println("lujing:" + filePath);
						// 保存文件到硬盘
						item.write(storeFile);
						// 上传文件成功，无需输入输出流之间的交互，交给FileItem的write方法实现，传入参数为 文件类型的接受对象
//						request.setAttribute("message", "文件上传成功!");
					}
				}
			}
		} catch (Exception ex) {
//			request.setAttribute("message", "错误信息: " + ex.getMessage());
		}
		//把上传的excel文件，转成json形式回调
		try {
			response.setContentType("text/html;charset=UTF-8"); 
			List cs = this.preview(path);
			
			ObjectMapper mapper = new ObjectMapper();
			String pageBeanJson = mapper.writeValueAsString(cs);
			response.getWriter().write(pageBeanJson);
			//（更新）导入时不更新数据库，提交时往数据库更新数据
//			InsertCCS.insert1(cs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}