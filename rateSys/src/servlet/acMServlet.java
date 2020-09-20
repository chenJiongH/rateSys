package servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import domain.AcmPageBean;
import domain.CCS;
import domain.CityCountySchool;
import domain.Managers;
import service.AcMService;
import service.CCSService;
import service.InsertCCS;

/**
 * Servlet implementation class acMServlet
 */
@WebServlet("/acMServlet/*")
public class acMServlet extends BaseServlet {
	
	/**
	 * 提交导入数据
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void ExcelUpdate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			CCSService ccs = new CCSService();
			String cds = request.getParameter("cds");
			ObjectMapper mapper = new ObjectMapper();
			List<CityCountySchool> cs = mapper.readValue(cds,  new TypeReference<List<CityCountySchool>>() {});
			InsertCCS.insert1(cs);
			response.getWriter().write("{\"json\":\"提交成功\"}");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void findSchByDid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		CCSService ccs = new CCSService();
		String dname = request.getParameter("dname");
		String cname = request.getParameter("cname");
		List<Map<String, Object>> ss = ccs.findSchByDid(dname, cname);
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(ss);
		response.getWriter().write(json);
	}

	public void findDistByCid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		CCSService ccs = new CCSService();
		String cid = request.getParameter("cname");
		List<Map<String, Object>> ds = ccs.findDistByCid(cid);
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(ds);
		response.getWriter().write(json);
	}
	
	public void findAllCity(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		CCSService ccs = new CCSService();
		List<Map<String, Object>> cs = ccs.findAllCity();
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(cs);
		response.getWriter().write(json);
	}
	//（已经弃用）页面加载时异步请求，查找所有的城市、县、校。显示在页面的三个select框中
	public void findCCS(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		System.out.println("acMservlet/findCCS");
		response.setContentType("text/html;charset=utf-8");
		CCSService ccs = new CCSService();
		CCS cs = ccs.findCCS();
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(cs);
		response.getWriter().write(json);
	}
	
	//点击生成默认数据按钮,返回默认数据模板.xls-Excel默认数据文件
	public void createInitData(HttpServletRequest request, HttpServletResponse response) {
		try {
			
//		System.out.println("here");
//		fileName为文件名称、uploadPath为文件目录、filePath为文件绝对路径；
		String fileName = "默认数据模板.xls";
		// 构造临时路径来存储上传的文件
		// 这个路径相对当前应用的目录
		String uploadPath = "D:\\download";
		// 如果目录不存在则创建
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}
		String filePath = uploadPath + File.separator + fileName;
		//调用Service创建初始数据Excel文件,并在其中把初始数据也更新到数据库中
		CCSService.createInitData(filePath);
		
		//八进制流传送，可以传送任意文件
		response.addHeader("content-Type", "application/octet-stream");
		String agent = request.getHeader("User-Agent");
//		是以什么方式下载，如attachment为以附件方式下载
		if (agent.toLowerCase().indexOf("chrome") > 0) {
			response.addHeader("content-Disposition",
					"attachment;filename=" + new String(fileName.getBytes("UTF-8"), "ISO8859-1"));
		} else {
			response.addHeader("content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
		}
//		输入流FileInputStream直接关联服务器文件，然后用ServletOutputStream类型创建response.getOutputStream()；把输入流的数据传递给输出流，over；
		FileInputStream in = new FileInputStream(filePath);
		ServletOutputStream out = response.getOutputStream();
		byte[] bs = new byte[1024];
		int len = -1;
		while ((len = in.read(bs)) != -1) {
			out.write(bs, 0, len);
		}
		in.close();
		out.close();
		} catch(Exception e) {
			try {
				response.sendRedirect("/rateSys/acM.jsp");} catch (IOException e1) {e1.printStackTrace();}
			e.printStackTrace();
		}
	}
	
	//导入管理员数据,保存文件在服务器上，调用service处理文件数据
	public void daoru(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// 上传配置
		final int MEMORY_THRESHOLD = 1024 * 1024 * 3; // 3MB
		final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
		final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB
		// 上传文件存储目录
		final String UPLOAD_DIRECTORY = "upload";

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
//			System.out.println(formItems);
			String filePath = "";
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
						filePath = uploadPath + File.separator + fileName;
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
//			System.out.println("acmservletHere");
			AcMService acmService = new AcMService();
			//通过文件往数据库中插入数据
			response.setContentType("text/html;charset=UTF-8");
			if(acmService.inDataByFile(filePath)) {
				response.getWriter().write("{\"json\":\"导入成功\"}");
			} else {
				response.getWriter().write(0);
			}
		} catch (Exception ex) {
			response.getWriter().write(0);
		} finally {
			response.getWriter().flush();
			response.getWriter().close();			
		}
	}

	//点击修改按钮。进行修改
	public void changeData(HttpServletRequest request, HttpServletResponse response){
		String message = "";
		try {
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			Managers m = new Managers();
			m.setMusername(request.getParameter("musername"));
			m.setMid(request.getParameter("mid"));
			m.setMname(request.getParameter("mname"));
			m.setMpassword(request.getParameter("mpassword"));
			m.setMphono(request.getParameter("mphono"));
			
			String rank = request.getParameter("rank");
			String cname = request.getParameter("city");
			String distname = request.getParameter("dist");
			String schname = request.getParameter("sch");
			AcMService acmService = new AcMService();
			message = acmService.changeData(m, rank, cname, distname, schname);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.getWriter().write("{\"message\":\"" + message + "\"}");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//点击查询按钮。进行查询
	public void selectData(HttpServletRequest request, HttpServletResponse response) {
		try {
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			Managers m = new Managers();
			m.setMusername(request.getParameter("musername"));
			m.setMname(request.getParameter("mname"));
			m.setMphono(request.getParameter("mphono"));
			
			String rank = request.getParameter("rank");
			String cname = request.getParameter("city");
			String distname = request.getParameter("dist");
			String schname = request.getParameter("sch");
			// 用户输入学校名称模糊查询
			String schoolName = request.getParameter("schoolName");
//			System.out.println(rank + " " + cname);
			AcMService service = new AcMService();
			List<Map<String, Object>> selectMana = service.selectData(m, rank, cname, distname, schname, schoolName);
			
			ObjectMapper mapper = new ObjectMapper();
			String pageBeanJson = mapper.writeValueAsString(selectMana);
			response.getWriter().write(pageBeanJson);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				response.getWriter().write("{\"message\":\"未查询到符合条件的账号\"}");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
	}
	
	
	//点击导出按钮，导出数据库信息
	public void export(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		//		fileName为文件名称、uploadPath为文件目录、filePath为文件绝对路径；
		String fileName = "管理员记录.xls";
		// 构造临时路径来存储上传的文件
		// 这个路径相对当前应用的目录
		String uploadPath = "D:\\download";
		// 如果目录不存在则创建
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}
		String filePath = uploadPath + File.separator + fileName;
		//调用Service从数据库中创建导出数据Excel文件，再下载该Excel文件
		AcMService acmService = new AcMService();
		acmService.export(filePath);
		
		//八进制流传送，可以传送任意文件
		response.addHeader("content-Type", "application/octet-stream");
		String agent = request.getHeader("User-Agent");
//		是以什么方式下载，如attachment为以附件方式下载
		if (agent.toLowerCase().indexOf("chrome") > 0) {
			response.addHeader("content-Disposition",
					"attachment;filename=" + new String(fileName.getBytes("UTF-8"), "ISO8859-1"));
		} else {
			response.addHeader("content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
		}
		
//		输入流FileInputStream直接关联服务器文件，然后用ServletOutputStream类型创建response.getOutputStream()；把输入流的数据传递给输出流，over；
		FileInputStream in = new FileInputStream(filePath);
		ServletOutputStream out = response.getOutputStream();
		byte[] bs = new byte[1024];
		int len = -1;
		while ((len = in.read(bs)) != -1) {
			out.write(bs, 0, len);
		}
		in.close();
		out.close();
		} catch(Exception e) {
			try {
				response.sendRedirect("/rateSys/acM.jsp");} catch (IOException e1) {e1.printStackTrace();}
			e.printStackTrace();
		}
	}
	
//	点击分页或页面更新
	public void findCityManaByCid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<Map<String, Object>> oneCityMana = new ArrayList<Map<String,Object>>();
		try {
			response.setContentType("text/html;charset=utf-8");
			String cid = request.getParameter("cid");
			AcMService acmService = new AcMService();
			oneCityMana = acmService.findCityManaByCid(cid);
			
			ObjectMapper mapper = new ObjectMapper();
			String oneCityManaJson = mapper.writeValueAsString(oneCityMana);
			response.getWriter().write(oneCityManaJson);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("findPageBean异常");
			ObjectMapper mapper = new ObjectMapper();
			String oneCityManaJson = mapper.writeValueAsString(oneCityMana);
			response.getWriter().write(oneCityManaJson);
		}
		
	}
	
	
}
