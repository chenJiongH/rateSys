package servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import domain.ExDisPageBean;
import service.ExDisService;

/**
 * Servlet implementation class ExDis
 */
@WebServlet("/ExDisServlet/*")
public class ExDisServlet extends BaseServlet {
	
	public void findAllPr(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "请重新刷新页面或者再次登录";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String mid = (String) session.getAttribute("mid");
			String tid = (String) session.getAttribute("tid");
			ExDisService service = new ExDisService();
			List<Map<String, Object>> groupMap = service.findAllPr(tid, mid);
			
			ObjectMapper mapper = new ObjectMapper();
			String groupMapJson = mapper.writeValueAsString(groupMap);
			response.getWriter().write(groupMapJson);
		} catch (Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
		}
	}

	public void findGroup(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "请重新刷新页面或者再次登录";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String mid = (String) session.getAttribute("mid");
			String tid = (String) session.getAttribute("tid");
			String pid = request.getParameter("pid");
			ExDisService service = new ExDisService();
			List<Map<String, Object>> groupMap = service.findGroup(tid, mid, pid);
			
			ObjectMapper mapper = new ObjectMapper();
			String groupMapJson = mapper.writeValueAsString(groupMap);
			response.getWriter().write(groupMapJson);
		} catch (Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
		}
	}

	public void findMember(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String tid = (String) session.getAttribute("tid");
			String gid = request.getParameter("gid");
			ExDisService service = new ExDisService();
			List<Map<String, Object>> memberMap = service.findMember(tid, gid);
			
			ObjectMapper mapper = new ObjectMapper();
			String memberMapJson = mapper.writeValueAsString(memberMap);
			response.getWriter().write(memberMapJson);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void findIndex(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String tid = (String) session.getAttribute("tid");
			//查询专家组的所有指标
			String gid = request.getParameter("gid");
			//查询专家组成员已经分配指标
			String spid = request.getParameter("spid");
			ExDisService service = new ExDisService();
			ExDisPageBean pageBean = new ExDisPageBean();
			pageBean.setIndex(service.findIndex(tid, gid));
			pageBean.setMi(service.findMi(tid, spid, gid));
			ObjectMapper mapper = new ObjectMapper();
			String pageBeanJson = mapper.writeValueAsString(pageBean);
			response.getWriter().write(pageBeanJson);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addMemIndex(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String mid = (String) session.getAttribute("mid");
			String tid = (String) session.getAttribute("tid");
			String gid = request.getParameter("gid");
			String cid = request.getParameter("cid");
			String spid = request.getParameter("spid");
			ExDisService service = new ExDisService();
			service.addMemIndex(mid, tid, gid, spid, cid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void delMemIndex(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String mid = (String) session.getAttribute("mid");
			String tid = (String) session.getAttribute("tid");
			String gid = request.getParameter("gid");
			String cid = request.getParameter("cid");
			String spid = request.getParameter("spid");
			ExDisService service = new ExDisService();
			service.delMemIndex(mid, tid, gid, spid, cid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
