package servlet;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import domain.SSPRatePageBean;
import service.DocToManyImg;
import service.SSPRateService;

/**
 * Servlet implementation class SSPRateServlet
 */
@WebServlet("/SSPRateServlet/*")
public class SSPRateServlet extends BaseServlet {
	
	public void saveDraft(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String message = "保存草稿未成功，请检查上传文件格式和大小...";
	try {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		HttpSession session = request.getSession();
		String mid = (String) session.getAttribute("mid");
		String tid = (String) session.getAttribute("tid");
		SSPRateService service = new SSPRateService();
		//判断该校是否已经提交自评和总评
		int flag = 0;
		Map<String, Object> map = service.check(tid);
		map = service.checkOverall(tid);
		// 上传配置
		final int MEMORY_THRESHOLD = 1024 * 1024 * 15; // 15MB
		final int MAX_FILE_SIZE = 1024 * 1024 * 10; // 10MB
		final int MAX_REQUEST_SIZE = 1024 * 1024 * 160; // 160MB
		String path = "";
		// 创建工厂
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

		// 中文处理在文件上传请求的消息体中，除了普通表单域的值是文本内容外，文件上传字段中的文件路径也是文本内容，
//		在内存中保存的是它们的某种字符编码的字节数组。Apache上传组件中读取这些内容时，必须知道他们采用的字符集编码，才能将它们转换成正确的字符文本返回
		upload.setHeaderEncoding("UTF-8");
		//附件后面加上上交日期
//		Date date = new Date();
		String str = "yyy-MM-dd HH：mm：ss：SSS";
		SimpleDateFormat sdf = new SimpleDateFormat(str);
//        String saveTime = sdf.format(date);
		// 构造临时路径来存储上传的文件
		// 服务器绝对路径， 自评附件存放目录
		String uploadPath = "D:\\selfEval";

		// 如果目录不存在则创建
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}
		Map<String, String> fileMap = new HashMap<String, String>();
		Map<String, String> scoreMap = new HashMap<String, String>();
		Map<String, String> describeMap = new HashMap<String, String>();
		Map<String, String> allCri = new HashMap<String, String>();
		String overallImgPath = "";
		String overallFilePath = "";
		//使用ServletFileUpload 解析上传数据（包括文件和文本数据）
		List<FileItem> formItems = upload.parseRequest(request);
		if (formItems != null && formItems.size() > 0) {
			for (FileItem item : formItems) {
				//判断某个表单是否为普通类型，item.isFormField() == false 表示该表单项为file类型
				if (!item.isFormField()) {
					// 消掉富文本框的提交
					if("ditorValue".equals(item.getFieldName())) {
						continue;
					}
					allCri.put(item.getFieldName(), "true");
//					该项没有上传文件
					if(item.getName().equals("")) 
						continue;
					/*
					  FileItem.getName()获得上传时文件名， new File(FileItem.getName()) 通过文件名获得一个文件对象， 
					  (new File(fileItem.getName())).getName();调用文件对象的getName()方法，获得不带路径的文件名。
					 */
					//附件后面加上上交日期和三位随机数
					Date date = new Date();
			        String saveTime = sdf.format(date);
			        String random = UUID.randomUUID().toString().substring(0, 3);
			        saveTime += " " + random;
			        
					String fileName = new File(item.getName()).getName();
					String filePath = uploadPath + File.separator + fileName;
					//所以当前上传文件放置在服务器的D:\\selfEval\\上传文件名 精确到毫秒的时间
					File storeFile = new File(uploadPath + File.separator  + fileName + " br" + saveTime);
					//文件所在 再加上保存的时间
					fileMap.put(item.getFieldName(), filePath + "<br>" + saveTime);
					// 保存文件到硬盘
					item.write(storeFile);
				} else {
					if("ditorValue".equals(item.getFieldName())) {
						continue;
					}
					if("".equals(item.getFieldName())) {
						continue;
					}
//					获取总评文件地址
					if("overallImgLocation".equals(item.getFieldName())) {
						overallImgPath = item.getString("UTF-8");
						continue;
					}
					if("overallFileLocation".equals(item.getFieldName())) {
						overallFilePath = item.getString("UTF-8");
						continue;
					}
					//前端在name前面加了一个‘0’或‘1’ 表示 分数或描述
					allCri.put(item.getFieldName().substring(1), "true");
					//以utf-8编码获取当前item的文本内容，即自评分数
					if(item.getFieldName().charAt(0) == '0')
						scoreMap.put(item.getFieldName().substring(1), item.getString("UTF-8"));
					else describeMap.put(item.getFieldName().substring(1), item.getString("UTF-8"));
				}
			}
		}
		//插入考核评分
		service.rate(false, allCri, fileMap, scoreMap, describeMap, mid, tid, (String)map.get("spgid"), (String)map.get("mid"), overallImgPath, overallFilePath);
		message = "草稿材料保存成功";
		//t提交自评时，设置标志+1
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		response.getWriter().write("{\"message\":\"" + message + "\"}");
	}
}
	
	public void deleteFile(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "删除重传之前的文件失败";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			// 前端每次重传保存之前的值
			String deleteFileJson = request.getParameter("deleteFile");
			ObjectMapper mapper = new ObjectMapper();
			List<String> deleteFile = mapper.readValue(deleteFileJson, new TypeReference<List<String>>() {});
			System.out.println(deleteFile);
			for(int i = 0; i < deleteFile.size(); i++) {
				String filePath = deleteFile.get(i);
				filePath = filePath.replace("<br>", " br");
				System.out.println(filePath);
				File file = new File("D:\\selfEval\\" + filePath);
				if(file.exists()) {
					if(file.delete()) {
						System.out.println("删除旧文件成功");
					}
				}
			}
			
			response.getWriter().write("{\"message\":\"" + "删除成功" + "\"}");
		} catch (Exception e) {
			response.getWriter().write("{\"message\":\"" + "删除失败" + "\"}");
			e.printStackTrace();
		}
	}
	
	public void findProcessRate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "项目进度获取失败，请重新登录或刷新页面";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String mid = (String) session.getAttribute("mid");
			String tid = (String) session.getAttribute("tid");
			SSPRateService service = new SSPRateService();
			Map<String, Object> processRate = service.findProcessRate(tid);
			
			ObjectMapper mapper = new ObjectMapper();
			response.getWriter().write(mapper.writeValueAsString(processRate));
		} catch (Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
		}
	}

	public void rate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "提交自评未成功，请检查上传文件格式和大小...";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String mid = (String) session.getAttribute("mid");
			String tid = (String) session.getAttribute("tid");
			SSPRateService service = new SSPRateService();
//			//判断该校是否已经提交自评和总评
			Map<String, Object> map = service.check(tid);
			message = (String) map.get("message");
			if(message.equals("您已提交过考核，不可中途修改考核")) {
				return ;
			}
			map = service.checkOverall(tid);
			message = (String) map.get("message");
			if(message.equals("您已经提交过考核，不可中途修改考核内容")) {
				return ;
			}
			message = "提交自评未成功，请检查上传文件格式和大小...";
			// 上传配置
			final int MEMORY_THRESHOLD = 1024 * 1024 * 15; // 15MB
			final int MAX_FILE_SIZE = 1024 * 1024 * 10; // 10MB
			final int MAX_REQUEST_SIZE = 1024 * 1024 * 160; // 160MB
			String path = "";
			// 创建工厂
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

			// 中文处理在文件上传请求的消息体中，除了普通表单域的值是文本内容外，文件上传字段中的文件路径也是文本内容，
//			在内存中保存的是它们的某种字符编码的字节数组。Apache上传组件中读取这些内容时，必须知道他们采用的字符集编码，才能将它们转换成正确的字符文本返回
			upload.setHeaderEncoding("UTF-8");
			//附件后面加上上交日期
//			Date date = new Date();
			String str = "yyy-MM-dd HH：mm：ss：SSS";
			SimpleDateFormat sdf = new SimpleDateFormat(str);
//	        String saveTime = sdf.format(date);

			// 构造临时路径来存储上传的文件
			// 服务器绝对路径， 自评附件存放目录
			String uploadPath = "D:\\selfEval";

			// 如果目录不存在则创建
			File uploadDir = new File(uploadPath);
			if (!uploadDir.exists()) {
				uploadDir.mkdir();
			}
			Map<String, String> fileMap = new HashMap<String, String>();
			Map<String, String> scoreMap = new HashMap<String, String>();
			Map<String, String> describeMap = new HashMap<String, String>();
			Map<String, String> allCri = new HashMap<String, String>();
			String overallFilePath = "";
			String overallImgPath = "";
			//使用ServletFileUpload 解析上传数据（包括文件和文本数据）
			List<FileItem> formItems = upload.parseRequest(request);
			if (formItems != null && formItems.size() > 0) {
				for (FileItem item : formItems) {
					//判断某个表单是否为普通类型，item.isFormField() == false 表示该表单项为file类型
					if (!item.isFormField()) {
						// 消掉富文本框的提交
						if("ditorValue".equals(item.getFieldName())) {
							continue;
						}
						//总评改成UEditor的形式提交
//						if(item.getFieldName().equals("overallFile")) {
//							overallFilePath = uploadPath + File.separator + item.getName();
//							File storeFile = new File(overallFilePath);
//							// 保存总评文件到硬盘
//							item.write(storeFile);
//							continue;
//						}
						
						allCri.put(item.getFieldName(), "true");
//						该项没有上传文件
						if(item.getName().equals("")) 
							continue;
						/*
						  FileItem.getName()获得上传时文件名， new File(FileItem.getName()) 通过文件名获得一个文件对象， 
						  (new File(fileItem.getName())).getName();调用文件对象的getName()方法，获得不带路径的文件名。
						 */
						//附件后面加上上交日期和三位随机数
						Date date = new Date();
				        String saveTime = sdf.format(date);
				        String random = UUID.randomUUID().toString().substring(0, 3);
				        saveTime += " " + random;
				        
						String fileName = new File(item.getName()).getName();
						//所以当前上传文件放置在服务器的D:\\selfEval\\上传文件名 精确到毫秒值 处
						String filePath = uploadPath + File.separator + fileName;
						File storeFile = new File(uploadPath + File.separator  + fileName + " br" + saveTime );
						fileMap.put(item.getFieldName(), filePath + "<br>" + saveTime);
						// 保存文件到硬盘
						item.write(storeFile);
					} else {
						if("ditorValue".equals(item.getFieldName())) {
							continue;
						}
//						获取总评文件地址
						if("overallImgLocation".equals(item.getFieldName())) {
							overallImgPath = item.getString("UTF-8");
							continue;
						}
						if("overallFileLocation".equals(item.getFieldName())) {
							overallFilePath = item.getString("UTF-8");
							continue;
						}
						//前端在name前面加了一个‘0’或‘1’ 表示 分数或描述
						allCri.put(item.getFieldName().substring(1), "true");
						//以utf-8编码获取当前item的文本内容，即自评分数
						if(item.getFieldName().charAt(0) == '0')
							scoreMap.put(item.getFieldName().substring(1), item.getString("UTF-8"));
						else 
							describeMap.put(item.getFieldName().substring(1), item.getString("UTF-8"));
					}
				}
			}
			//插入考核评分
			service.rate(true, allCri, fileMap, scoreMap, describeMap, mid, tid, (String)map.get("spgid"), (String)map.get("mid"), overallImgPath, overallFilePath);
			message = "上传成功";
			//t提交自评时，设置标志+1
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		}
	}

	public void findPageBean(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String message = "页面请求失败，请重新登录或刷新页面";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String mid = (String) session.getAttribute("mid");
			String tid = (String) session.getAttribute("tid");

			SSPRateService service = new SSPRateService();
			//判断该校是否已经提交自评和总评
			int flag = 0;
			Map<String, Object> map = service.check(tid);
			message = (String) map.get("message");
			if(message.equals("您已提交过考核，不可中途修改考核")) {
				flag ++;
			} 
			map = service.checkOverall(tid);
			message = (String) map.get("message");
			if(message.equals("您已经提交过考核，不可中途修改考核内容")) {
				flag += 10;
			}
			 message = "页面请求失败，请重新登录或刷新页面";
			
			SSPRatePageBean pageBean = service.findPageBean(tid);
//			flag == 11 表示自评和总评都已经提交
			pageBean.setFlag(flag);
			
			ObjectMapper mapper = new ObjectMapper();
			String pageBeanJson = mapper.writeValueAsString(pageBean);
			response.getWriter().write(pageBeanJson);
		} catch (Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
		}
	}

	public void glanceOverall(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String message = "总评文件预览失败，请检查文件格式和大小";
	try {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		HttpSession session = request.getSession();
		SSPRateService service = new SSPRateService();
		//判断该校是否已经提交自评
		
		// 上传配置
		final int MEMORY_THRESHOLD = 1024 * 1024 * 10; // 3MB
		final int MAX_FILE_SIZE = 1024 * 1024 * 5; // 5MB
		final int MAX_REQUEST_SIZE = 1024 * 1024 * 80; // 50MB
		String path = "";
		// 创建工厂
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

		// 中文处理在文件上传请求的消息体中，除了普通表单域的值是文本内容外，文件上传字段中的文件路径也是文本内容，
//		在内存中保存的是它们的某种字符编码的字节数组。Apache上传组件中读取这些内容时，必须知道他们采用的字符集编码，才能将它们转换成正确的字符文本返回
		upload.setHeaderEncoding("UTF-8");

		// 构造临时路径来存储上传的文件
		// 服务器绝对路径， 自评附件存放目录
//		String uploadPath = "D:/overallAppraisals";
		String uploadPath = getServletContext().getRealPath("/");
		
		// 如果目录不存在则创建
		File uploadDir = new File(uploadPath);
		if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}
		
		String fileName = "";
		String filePath = "";
		//使用ServletFileUpload 解析上传数据（包括文件和文本数据）
		List<FileItem> formItems = upload.parseRequest(request);
		if (formItems != null && formItems.size() > 0) {
			for (FileItem item : formItems) {
				//判断某个表单是否为普通类型，item.isFormField() == false 表示该表单项为file类型
				if (!item.isFormField()) {
//					该项没有上传文件
					if(item.getName().equals("")) 
						continue;
					/*
					  FileItem.getName()获得上传时文件名， new File(FileItem.getName()) 通过文件名获得一个文件对象， 
					  (new File(fileItem.getName())).getName();调用文件对象的getName()方法，获得不带路径的文件名。
					 */
					fileName = new File(item.getName()).getName();
					//所以当前上传文件放置在服务器的D:\\selfEval\\上传文件名 处
					filePath = uploadPath  + fileName;
					File storeFile = new File(filePath);
					// 保存文件到硬盘
					item.write(storeFile);
				}
			}
		}
		DocToManyImg convert = new DocToManyImg();
		System.out.println(uploadPath);
		String imgUrl = convert.doc2OneImg(filePath, uploadPath);
		//前端插入链接只能以相对地址的形式，不能以绝对地址的形式 
		imgUrl = imgUrl.substring(imgUrl.length() - 36);
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("filePath", filePath);
		retMap.put("imgUrl", imgUrl);
		System.out.println(imgUrl);
		ObjectMapper mapper = new ObjectMapper();
		
		response.getWriter().write(mapper.writeValueAsString(retMap));
//		Map<String, Object> map = convert.getHtmlDomByDocxFilePath(uploadPath, "/" + fileName);
//		service.overall(mid, tid, filePath);
//		service.toSetOverallFlage(tid, (String)map.get("spgid"), (String)map.get("mid"));
	} catch (Exception e) {
		response.getWriter().write("{\"message\":\"" + message + "\"}");
		e.printStackTrace();
	} finally {
		
	}
}
	
	public void overall(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "总评文件提交未成功，请重新登录或再次提交";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String mid = (String) session.getAttribute("mid");
			String tid = (String) session.getAttribute("tid");
			SSPRateService service = new SSPRateService();
			//判断该校是否已经提交自评
			Map<String, Object> map = service.checkOverall(tid);
			message = (String) map.get("message");
			if(message.equals("您已经提交过总评文件，不可中途修改文件内容")) {
				return ;
			} 
			
			// 上传配置
			final int MEMORY_THRESHOLD = 1024 * 1024 * 10; // 3MB
			final int MAX_FILE_SIZE = 1024 * 1024 * 5; // 5MB
			final int MAX_REQUEST_SIZE = 1024 * 1024 * 80; // 50MB
			String path = "";
			// 创建工厂
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

			// 中文处理在文件上传请求的消息体中，除了普通表单域的值是文本内容外，文件上传字段中的文件路径也是文本内容，
//			在内存中保存的是它们的某种字符编码的字节数组。Apache上传组件中读取这些内容时，必须知道他们采用的字符集编码，才能将它们转换成正确的字符文本返回
			upload.setHeaderEncoding("UTF-8");

			// 构造临时路径来存储上传的文件
			// 服务器绝对路径， 自评附件存放目录
			String uploadPath = "D:\\overallAppraisals";

			// 如果目录不存在则创建
			File uploadDir = new File(uploadPath);
			if (!uploadDir.exists()) {
				uploadDir.mkdir();
			}
			String fileName = "";
			String filePath = "";
			//使用ServletFileUpload 解析上传数据（包括文件和文本数据）
			List<FileItem> formItems = upload.parseRequest(request);
			if (formItems != null && formItems.size() > 0) {
				for (FileItem item : formItems) {
					//判断某个表单是否为普通类型，item.isFormField() == false 表示该表单项为file类型
					if (!item.isFormField()) {
//						该项没有上传文件
						if(item.getName().equals("")) 
							continue;
						/*
						  FileItem.getName()获得上传时文件名， new File(FileItem.getName()) 通过文件名获得一个文件对象， 
						  (new File(fileItem.getName())).getName();调用文件对象的getName()方法，获得不带路径的文件名。
						 */
						fileName = new File(item.getName()).getName();
						//所以当前上传文件放置在服务器的D:\\selfEval\\上传文件名 处
						filePath = uploadPath + File.separator + fileName;
						File storeFile = new File(filePath);
						// 保存文件到硬盘
						item.write(storeFile);
					}
				}
			}
			service.overall(mid, tid, filePath, "已废弃");
			service.toSetOverallFlage(tid, (String)map.get("spgid"), (String)map.get("mid"));
			response.getWriter().write("{\"fileName\":\"" + fileName + "\"}");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		}
	}
}
