package servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import domain.ProjectProcessFallbackPageBean;
import service.ProjectProcessFallbackService;

/**
 * Servlet implementation class ProjectProcessFallback
 */
@WebServlet("/ProjectProcessFallback/*")
public class ProjectProcessFallback extends BaseServlet {
	
	public void processFallback(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "项目回退失败，请重新回退或刷新页面";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String tid = (String) session.getAttribute("tid");
			String mid = (String) session.getAttribute("mid");
			ProjectProcessFallbackPageBean queryForm = new ProjectProcessFallbackPageBean();
			String pid = request.getParameter("pid");
			String sid = request.getParameter("sid");
			String process = request.getParameter("process");
			String nowRate = request.getParameter("nowRate");
			ProjectProcessFallbackService service = new ProjectProcessFallbackService();
			message = service.processFallback(tid, pid, sid, mid, process, nowRate);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("here");
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		}
	}

	public void queryData(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "页面请求失败，请重新登录或刷新页面";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String tid = (String) session.getAttribute("tid");
			String mid = (String) session.getAttribute("mid");
			ProjectProcessFallbackPageBean queryForm = new ProjectProcessFallbackPageBean();
			queryForm.setPname(request.getParameter("projectName"));
			queryForm.setPetime(request.getParameter("txt2"));
			queryForm.setProcess(request.getParameter("txt3"));
			queryForm.setNowRate(request.getParameter("txt7"));
			queryForm.setUpLevelRate(request.getParameter("txt4"));
			ProjectProcessFallbackService service = new ProjectProcessFallbackService();
			List<Map<String, Object>> pageBean = service.queryPageBean(mid, tid, queryForm);
			
			ObjectMapper mapper = new ObjectMapper();

			//设置jackson转换日期类型为字符串的格式
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			mapper.setDateFormat(dateFormat);
			String pageBeanJson = mapper.writeValueAsString(pageBean);
			response.getWriter().write(pageBeanJson);
		} catch (Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
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
			ProjectProcessFallbackService service = new ProjectProcessFallbackService();
			int curpage = Integer.parseInt(request.getParameter("curpage"));
			List<Map<String, Object>> pageBean = service.findPageBean(mid, tid, curpage);
			
			ObjectMapper mapper = new ObjectMapper();
			//设置jackson转换日期类型为字符串的格式
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			mapper.setDateFormat(dateFormat);
			String pageBeanJson = mapper.writeValueAsString(pageBean);
			response.getWriter().write(pageBeanJson);
		} catch (Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
		}
	}

}
