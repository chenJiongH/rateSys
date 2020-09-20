package servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import domain.Archives;
import domain.CityCountySchool;
import service.PlaceOnFileService;

/**
 * Servlet implementation class DSPRateServlet
 */
@WebServlet("/PlaceOnFileServlet/*")
public class PlaceOnFileServlet extends BaseServlet {

	public void findPr(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "页面请求失败，请重新登录或刷新页面";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			String prType = request.getParameter("prType");
			HttpSession session = request.getSession();
			String tid = (String) session.getAttribute("tid"); // P、 C、D 三级别
			String mid = (String) session.getAttribute("mid"); // P、 C、D 三级别
			
			PlaceOnFileService service = new PlaceOnFileService();
			List<Map<String, Object>> Pr = service.findPr(prType, mid);
			
			ObjectMapper mapper = new ObjectMapper();
			String pageBeanJson = mapper.writeValueAsString(Pr);
			response.getWriter().write(pageBeanJson);
		} catch (Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
		}
	}
	public void findSchoolByPid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "页面请求失败，请重新登录或刷新页面";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			String pid = request.getParameter("pid");
			PlaceOnFileService service = new PlaceOnFileService();
			List<Map<String, Object>> schools = service.findSchoolByPid(pid);
			
			ObjectMapper mapper = new ObjectMapper();
			String pageBeanJson = mapper.writeValueAsString(schools);
			response.getWriter().write(pageBeanJson);
		} catch (Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
		}
	}
	
	public void findCByPidSid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "页面请求失败，请重新登录或刷新页面";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String pid = request.getParameter("pid");
			String sid = request.getParameter("sid");
			
			PlaceOnFileService service = new PlaceOnFileService();
			List<Archives> pageBean = service.findCByPidSid(sid, pid);
			ObjectMapper mapper = new ObjectMapper();
			String pageBeanJson = mapper.writeValueAsString(pageBean);
			response.getWriter().write(pageBeanJson);
		} catch (Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
		}
	}
	
	public void exportRate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "导出出错，请重试";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String tid = (String) session.getAttribute("tid"); // P、 C、D 三级别
			String c = request.getParameter("data");
			//转换json数组为java数组对象
			ObjectMapper mapper = new ObjectMapper(); 
			Map map = request.getParameterMap();
			List<Archives> cs = mapper.readValue(c, new TypeReference<List<Archives>>() {});
			String filePath = request.getSession().getServletContext().getRealPath("");
			String project = cs.get(cs.size() - 1).getBname(); // 获取项目名
			String school = cs.get(cs.size() - 1).getCname(); // 获取项目名
			filePath = filePath + project + "-" + school + ".xls"; // 文件绝对路径
			PlaceOnFileService service = new PlaceOnFileService();
			service.exportRate(filePath, cs, tid);
			String fileName = (project + "-" + school + ".xls");
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
			message = "导出成功。";
		} catch (Throwable e) {
			try {
				response.sendRedirect("/rateSys/place-on-file.jsp");} catch (IOException e1) {e1.printStackTrace();}
			e.printStackTrace();
		} 
	}
	static int num = 0;

	public void exportScore(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "导出出错，请重试";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			String c = request.getParameter("data");
			System.out.println("here：" + c);
			//转换json数组为java数组对象
			HttpSession session = request.getSession();
			String tid = (String) session.getAttribute("tid"); // P、 C、D 三级别
			
			ObjectMapper mapper = new ObjectMapper(); 
//			Map map = request.getParameterMap();
//			System.out.println(map.toString());
			List<Archives> cs = mapper.readValue(c, new TypeReference<List<Archives>>() {});
			String filePath = request.getSession().getServletContext().getRealPath("");
			 // 获取项目下有多少个C指标，然后根据C指标数量，来进行多学校的偏移输出
			int coffset = 0;
			if(cs.get(cs.size() - 1).getAname() != null )
				coffset = Integer.valueOf(cs.get(cs.size() - 1).getAname());
			String project = cs.get(cs.size() - 1).getBname(); 
			filePath = filePath + project + ".xls"; // 文件绝对路径
			PlaceOnFileService service = new PlaceOnFileService();
			// 生成 Excel 分数文件
			service.exportScore(filePath, cs, coffset, tid);
			
			String fileName = (project + ".xls");
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
			message = "导出成功。";
		} catch (Throwable e) {
			try {
				response.sendRedirect("/rateSys/place-on-file.jsp");} catch (IOException e1) {e1.printStackTrace();}
			e.printStackTrace();
		} 
	}
}
