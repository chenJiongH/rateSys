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

import domain.AddMemberPageBean;
import service.AddMemberService;

/**
 * Servlet implementation class AddMemberServlet
 */
@WebServlet("/AddMemberServlet/*")
public class AddMemberServlet extends BaseServlet {

	public void findAllMember(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "无专家成员信息";
		try {
			
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String tid = (String) session.getAttribute("tid");
			String mid = (String) session.getAttribute("mid");
			
			AddMemberService memberService = new AddMemberService();
			List<Map<String, Object>> allMember = memberService.findAllMember(tid, mid);
			
			ObjectMapper mapper = new ObjectMapper();
			String allMemberJson = mapper.writeValueAsString(allMember);
			response.getWriter().write(allMemberJson);
		} catch(Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
		}
	}
	
	public void findPageBean(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "页面请求失败，请重新登录或刷新页面";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			int curpage = Integer.parseInt(request.getParameter("curpage"));
			HttpSession session = request.getSession();
			String tid = (String) session.getAttribute("tid");
			String mid = (String) session.getAttribute("mid");
			
			AddMemberService memberService = new AddMemberService();
			AddMemberPageBean pageBean = memberService.findPageBean(curpage, tid, mid);
			
			ObjectMapper mapper = new ObjectMapper();
			String pageBeanJson = mapper.writeValueAsString(pageBean);
			response.getWriter().write(pageBeanJson);
		} catch(Throwable e) {
			e.printStackTrace();
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		}
	}

	public void delData(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "删除失败，请重新登录或重新删除";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			String pspid = request.getParameter("pspid");
			String pspgid = request.getParameter("pspgid");
			AddMemberService memberService = new AddMemberService();
			memberService.delData(pspid, pspgid);
			message = "删除成功";
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		}
	}
	
	public void changeLeader(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		try {
			String pspid = request.getParameter("pspid");
			String pspgid = request.getParameter("pspgid");
			AddMemberService memberService = new AddMemberService();
			memberService.changeLeader(pspid, pspgid);	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addData(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "添加失败，请重新登录或重新添加";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			String pid = request.getParameter("pid");
			String pspid = request.getParameter("pspid");
			String pspgid = request.getParameter("pspgid");
			String isleader = request.getParameter("isleader");
			AddMemberService memberService = new AddMemberService();
			memberService.addData(pspid, pspgid, isleader, pid);	
			message = "添加成功";
		} catch (Exception e) {	
			message = "专家组成员重名";
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
			String pspid = "",pspgid = "",pname = "";
			pspid = request.getParameter("pspid");
			pname = request.getParameter("pname");
			// 这是专家组名称，不是id
			pspgid = request.getParameter("pspgid");
			AddMemberService memberService = new AddMemberService();
			AddMemberPageBean pageBean = memberService.queryData(mid, pspid, pspgid, tid, pname);
			ObjectMapper mapper = new ObjectMapper();
			String pageBeanJson = mapper.writeValueAsString(pageBean);
			response.getWriter().write(pageBeanJson);
		} catch(Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
		}
	}
}
