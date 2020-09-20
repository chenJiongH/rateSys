package servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import domain.CityCountySchool;
import domain.TaskAllCDG;
import domain.TaskPageBean;
import service.TaskService;

/**
 * Servlet implementation class TaskMServlet
 */
@WebServlet("/TaskMServlet/*")
public class TaskMServlet extends BaseServlet {
	//查找所有某个管理员的所有专家组、市和县 
	public void findCDG(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "请重新刷新页面或者再次登录";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String mid = (String) session.getAttribute("mid");
			String tid = (String) session.getAttribute("tid");
			
			TaskAllCDG taskCDG = new TaskAllCDG();
			TaskService service = new TaskService();
			taskCDG = service.findCDG(mid, tid);
			ObjectMapper mapper = new ObjectMapper();
			
			String taskCDGJson = mapper.writeValueAsString(taskCDG);
			response.getWriter().write(taskCDGJson);
		} catch (Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
		}
	}
	
	//根据市、县查找下面的所有学校。根据专家组查找该专家组管理的属于该市、县下的。不查找该专家组管理的其他市县的学校（子查询多行单列）
	public void findPageBean(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "请重新刷新页面或者再次登录";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String mid = (String) session.getAttribute("mid");
			String tid = (String) session.getAttribute("tid");
			String did = request.getParameter("did");
			String pid = request.getParameter("pid");
			String flag = request.getParameter("getAllspgsch");//是否需要获取所有专家组学校
			
			TaskPageBean  pageBean = new TaskPageBean();
			TaskService service = new TaskService();
			pageBean = service.findPageBean(did, pid, tid, mid, flag); 
			ObjectMapper mapper = new ObjectMapper();
			
			String pageBeanJson = mapper.writeValueAsString(pageBean);
			response.getWriter().write(pageBeanJson);
		} catch (Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
		}
	}

	public void addSpgsch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "请重新刷新页面或者再次登录";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String mid = (String) session.getAttribute("mid");
			String tid = (String) session.getAttribute("tid");
			String did = request.getParameter("did");
			String pid = request.getParameter("pid");
			String sid = request.getParameter("sids");
			//获取待插入的批量数据
			ObjectMapper mapper = new ObjectMapper();
			List<String> sids = mapper.readValue(sid , new TypeReference<List<String>>() {});
			TaskPageBean  pageBean = new TaskPageBean();
			TaskService service = new TaskService();
			service.addSpgsch(pid, sids, mid, tid);
			//查找带显示的批量数据
//			pageBean = service.findPageBean(did, gid, tid, mid, "true"); 
			
			String pageBeanJson = mapper.writeValueAsString(pageBean);
			response.getWriter().write(pageBeanJson);
		} catch (Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
		}
	}

	public void delSpgsch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "请重新刷新页面或者再次登录";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String mid = (String) session.getAttribute("mid");
			String tid = (String) session.getAttribute("tid");
			String did = request.getParameter("did");
			String pid = request.getParameter("pid");
			String sid = request.getParameter("sids");
			ObjectMapper mapper = new ObjectMapper();
			List<String> sids = mapper.readValue(sid , new TypeReference<List<String>>() {});
//			System.out.println(sids);
			TaskPageBean  pageBean = new TaskPageBean();
			TaskService service = new TaskService();
			service.delSpgsch(pid, sids, mid, tid);
//			pageBean = service.findPageBean(did, gid, tid, mid, "true"); 
			
			String pageBeanJson = mapper.writeValueAsString(pageBean);
			response.getWriter().write(pageBeanJson);
		} catch (Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
		}
	}
}
