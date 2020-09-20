package servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import domain.DSPRatePageBean;
import service.DSPRateService;

/**
 * Servlet implementation class DSPRateServlet
 */
@WebServlet("/DSPRateServlet/*")
public class DSPRateServlet extends BaseServlet {
	
	public void findProcessRate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "项目当前流程请求失败，请重新登录或刷新页面";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String spid = (String) session.getAttribute("spid");
			String pid = request.getParameter("pid");
			DSPRateService service = new DSPRateService();
			Map<String, Object> processRate = service.findProcessRate(pid, spid);
			
			ObjectMapper mapper = new ObjectMapper();
			String pageBeanJson = mapper.writeValueAsString(processRate);
			response.getWriter().write(pageBeanJson);
		} catch (Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
		}
	}
	
	public void findPr(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "页面请求失败，请重新登录或刷新页面";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String spid = (String) session.getAttribute("spid");
			DSPRateService service = new DSPRateService();
			List<Map<String, Object>> Pr = service.findPr(spid);
			//设置当前专家已经查看了该项目
			service.loginedPrBySpid(Pr.get(0).get("pid").toString(), spid);
			ObjectMapper mapper = new ObjectMapper();
			String pageBeanJson = mapper.writeValueAsString(Pr);
			response.getWriter().write(pageBeanJson);
		} catch (Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
		}
	}
	
	public void findCByPid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "页面请求失败，请重新登录或刷新页面";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String spid = (String) session.getAttribute("spid");
			String pid = request.getParameter("pid");
			DSPRateService service = new DSPRateService();
			DSPRatePageBean pageBean = service.findCByPid(spid, pid);
			//设置当前专家已经查看了该项目
			service.loginedPrBySpid(pid, spid);
			ObjectMapper mapper = new ObjectMapper();
			String pageBeanJson = mapper.writeValueAsString(pageBean);
			response.getWriter().write(pageBeanJson);
		} catch (Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
		}
	}
	
	public void download(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		String location = request.getParameter("location");
		//传送该Excel文件
		response.addHeader("content-Type", "application/octet-stream");
		String agent = request.getHeader("User-Agent");
		String filename = location.substring(location.lastIndexOf('\\') + 1);
		filename = filename.substring(0, filename.indexOf("<br>"));
//		是以什么方式下载，如attachment为以附件方式下载
		if (agent.toLowerCase().indexOf("chrome") > 0) {
			response.addHeader("content-Disposition",
					"attachment;filename=" + new String(filename.getBytes("UTF-8"), "ISO8859-1"));
		} else {
			response.addHeader("content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
		}
//		输入流FileInputStream直接关联服务器文件，然后用ServletOutputStream类型创建response.getOutputStream()；把输入流的数据传递给输出流，over；
		location = location.replace("<br>", " br"); // 服务器路径和保存路径之间的区别，因为卷标不能有特殊字符 < >
		// 以下两行是1.0版本系统，保存文件时，只保存了文件的名称。没有在后面拼接" br 时间"
		File file = new File(location);
		if (!file.exists()) {
			location = location.substring(0, location.indexOf(" br"));
		}
		System.out.println("文件存在：" + file.exists());
		System.out.println("文件路径：" + location);
		
		FileInputStream in = new FileInputStream(location);
		ServletOutputStream out = response.getOutputStream();
		byte[] bs = new byte[1024];
		int len = -1;
		while ((len = in.read(bs)) != -1) {
			out.write(bs, 0, len);
		}
		
		try {
			in.close();			
		} catch (Exception e) {
			
		}
		out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void rate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "提交评定失败，请重新登录或刷新页面";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			String c = request.getParameter("c");
			HttpSession session = request.getSession();
			String spid = (String) session.getAttribute("spid");
			System.out.println(c);
			//转换json数组为java数组对象
			ObjectMapper mapper = new ObjectMapper(); 
			Map<String, Object> cs = mapper.readValue(c,  new TypeReference<Map<String, Object>>() {});
			String pid = (String) cs.get("pid");
			DSPRateService service = new DSPRateService();
			message = service.check(spid, pid);
			if("您已经提交过评分，不可中途修改提交".equals(message)) {
				return ;
			}
			service.ratePr(cs, spid, pid);
			message = "评定提交成功。";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		}
	}
}
