package servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.fasterxml.jackson.databind.ObjectMapper;

import domain.IndexPageBean;
import service.IndexSetService;

/**
 * Servlet implementation class IndexSetServlet
 */
@WebServlet("/IndexSetServlet/*")
public class IndexSetServlet extends BaseServlet {
	
	//页面加载时，请求页面数据
	public void findPageBean(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			System.out.println();
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			String pid = request.getParameter("pid");
			//刷新页面时不传入pid，程序在service中获取项目表的第一条项目id
			if(pid == null) {
				pid = "";
			}
			IndexSetService indexService = new IndexSetService();
			IndexPageBean pageBean = indexService.findPageBean(pid);
			ObjectMapper mapper = new ObjectMapper();
			String pageBeanJson = mapper.writeValueAsString(pageBean);
//			System.out.println(pageBeanJson);
			response.getWriter().write(pageBeanJson);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//上传文件
	public void upload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 上传配置
		final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
		final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB
		String message = "导入不成功";
		String path = "";
		if (!ServletFileUpload.isMultipartContent(request)) {
			// 如果不是则停止
			PrintWriter writer = response.getWriter();
			writer.println("Error: 表单必须包含 enctype=multipart/form-data");
			writer.flush();
			return;
		}
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		// 配置上传参数
		DiskFileItemFactory factory = new DiskFileItemFactory();
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
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}

		//保存文件到本地
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
						// 保存文件到硬盘
						item.write(storeFile);
						// 上传文件成功，无需输入输出流之间的交互，交给FileItem的write方法实现，传入参数为 文件类型的接受对象
					}
				}
			}
			IndexSetService indexService = new IndexSetService();
			message = indexService.readExcel(path);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		}
	}

	//生成并导出Excel文件
	public void export(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
	//		fileName为文件名称、uploadPath为文件目录、filePath为文件绝对路径；
			String fileName = "考核细则.xls";
			String pid = request.getParameter("exportPid");
			
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
			IndexSetService indexSevice = new IndexSetService();
			indexSevice.export(filePath, pid);
			
			//八进制流传送，可以传送任意文件
			response.addHeader("content-Type", "application/octet-stream");
			String agent = request.getHeader("User-Agent");
//			是以什么方式下载，如attachment为以附件方式下载
			if (agent.toLowerCase().indexOf("chrome") > 0) {
				response.addHeader("content-Disposition",
						"attachment;filename=" + new String(fileName.getBytes("UTF-8"), "ISO8859-1"));
			} else {
				response.addHeader("content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
			}
			
//			输入流FileInputStream直接关联服务器文件，然后用ServletOutputStream类型创建response.getOutputStream()；把输入流的数据传递给输出流，over；
			FileInputStream in = new FileInputStream(filePath);
			ServletOutputStream out = response.getOutputStream();
			byte[] bs = new byte[1024];
			int len = -1;
			while ((len = in.read(bs)) != -1) {
				out.write(bs, 0, len);
			}
			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//提交修改信息 
	public void submitChange(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "修改数据提交失败，请检查数据后再次提交";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			ObjectMapper mapper = new ObjectMapper();
			IndexPageBean pageBean = mapper.readValue(request.getParameter("data"), IndexPageBean.class);
			
			IndexSetService indexService = new IndexSetService();
			message = indexService.update(pageBean);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		}
		
	}
}
