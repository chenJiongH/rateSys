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

import domain.PSPgroup;
import domain.exGroupsPageBean;
import service.ExGroupsService;

/**
 * Servlet implementation class ExGroupsServlet
 */
@WebServlet("/ExGroupsServlet/*")
public class ExGroupsServlet extends BaseServlet {
	
	//修改专家组名称和实地考察类型
	public void changeData(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "用户名重复";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String tid = (String) session.getAttribute("tid");
			String mid = (String) session.getAttribute("mid");
			String groupName = request.getParameter("groupName");
			String gid = request.getParameter("gid");
			String projectName = request.getParameter("projectName");
			int isonspot = request.getParameter("onSpot") == null? 0 : 1;
			ExGroupsService groupService = new ExGroupsService();
			message = groupService.changeData(tid, mid, groupName, gid, projectName, isonspot);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		}
	}
	
	//模糊查询
	public void fuzzySelect(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String tid = (String) session.getAttribute("tid");
			String mid = (String) session.getAttribute("mid");
			String gName = request.getParameter("gName");
			ExGroupsService groupService = new ExGroupsService();
			List<Map<String, Object>> fuzzyData = groupService.fuzzySelect(tid, mid, gName);
			
			ObjectMapper mapper = new ObjectMapper();
			String pageBeanJson = mapper.writeValueAsString(fuzzyData);
			response.getWriter().write(pageBeanJson);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//页面加载时，请求页面数据
	public void findPageBean(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "页面请求失败，请重新登录或刷新页面";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			int curpage = Integer.parseInt(request.getParameter("curpage"));
			HttpSession session = request.getSession();
			String tid = (String) session.getAttribute("tid");
			String mid = (String) session.getAttribute("mid");
			
			ExGroupsService groupService = new ExGroupsService();
			exGroupsPageBean pageBean = groupService.findPageBean(curpage, tid, mid);
			
			ObjectMapper mapper = new ObjectMapper();
			String pageBeanJson = mapper.writeValueAsString(pageBean);
			response.getWriter().write(pageBeanJson);
		} catch(Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
		}
	}

	public void addData(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "添加失败，请重新登录或重新添加";
		try {
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			PSPgroup group = new PSPgroup();
			HttpSession session = request.getSession();
			group.setMid((String) session.getAttribute("mid"));
			String tid = (String) session.getAttribute("tid");
			group.setPid(request.getParameter("projectName"));
			group.setPspgid("");
			group.setSpgname(request.getParameter("groupName"));
			int isOnSpot = 0;
			if(request.getParameter("onSpot") != null)
				isOnSpot = 1;
			ExGroupsService groupService = new ExGroupsService();
			message = groupService.addData(group, tid, isOnSpot);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		}
	}
	
	public void queryData(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "此查询条件无记录";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String tid = (String) session.getAttribute("tid");
			String mid = (String) session.getAttribute("mid");
			PSPgroup group = new PSPgroup();
			group.setMid(mid);
			group.setPid(request.getParameter("projectName"));
			group.setSpgname(request.getParameter("groupName"));
			ExGroupsService groupService = new ExGroupsService();
			exGroupsPageBean pageBean = groupService.queryData(group, tid, mid);
			ObjectMapper mapper = new ObjectMapper();
			String pageBeanJson = mapper.writeValueAsString(pageBean);
			response.getWriter().write(pageBeanJson);
		} catch(Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
		}
	}
	
	public void delData(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "删除失败，请重新登录或重新删除";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			String gid = request.getParameter("spgid");
			ExGroupsService groupService = new ExGroupsService();
			groupService.delData(gid, true);
			message = "删除成功";
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		}
	}
}
