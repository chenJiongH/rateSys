package servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import domain.TaskPageBean;
import service.GroupSchoolMService;

/**
 * Servlet implementation class TaskMServlet
 */
@WebServlet("/GroupSchoolM/*")
public class GroupSchoolM extends BaseServlet {
	//查找所有某个管理员的所有专家组、市和县
	public void findAllProORGroupByPid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "请重新刷新页面或者再次登录";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String mid = (String) session.getAttribute("mid");
			String tid = (String) session.getAttribute("tid");
			// pid为空，查找项目。pid不为空，查找该pid下的专家组。复用同一个方法
			String pid = (String) request.getParameter("pid");
			GroupSchoolMService service = new GroupSchoolMService();
			List<Map<String, Object>> projectORGroup = service.findAllProORGroupByPid(mid, tid, pid);
			ObjectMapper mapper = new ObjectMapper();
			
			String projectORGroupJson = mapper.writeValueAsString(projectORGroup);
			response.getWriter().write(projectORGroupJson);
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
			// 已经改动，从did到gid
			String gid = request.getParameter("gid");
			String pid = request.getParameter("pid");
			String flag = request.getParameter("getAllspgsch");//是否需要获取所有项目剩余学校，如果是专家组改动，则可以不用重新获取
			
			TaskPageBean  pageBean = new TaskPageBean();
			GroupSchoolMService service = new GroupSchoolMService();
			pageBean = service.findPageBean(gid, pid, tid, mid, flag); 
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
			String gid = request.getParameter("gid");
			String pid = request.getParameter("pid");
			String sid = request.getParameter("sids");
			//获取待插入的批量数据
			ObjectMapper mapper = new ObjectMapper();
			List<String> sids = mapper.readValue(sid , new TypeReference<List<String>>() {});
			TaskPageBean  pageBean = new TaskPageBean();
			GroupSchoolMService service = new GroupSchoolMService();
			service.addSpgsch(pid, sids, mid, tid, gid);
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
			String gid = request.getParameter("gid");
			String pid = request.getParameter("pid");
			String sid = request.getParameter("sids");
			ObjectMapper mapper = new ObjectMapper();
			List<String> sids = mapper.readValue(sid , new TypeReference<List<String>>() {});
			TaskPageBean  pageBean = new TaskPageBean();
			GroupSchoolMService service = new GroupSchoolMService();
			service.delSpgsch(pid, sids, mid, tid, gid);
//			pageBean = service.findPageBean(did, gid, tid, mid, "true"); 
			
			String pageBeanJson = mapper.writeValueAsString(pageBean);
			response.getWriter().write(pageBeanJson);
		} catch (Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
		}
	}
}
