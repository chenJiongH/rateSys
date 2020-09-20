package servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import dao.PCDspecialistsDao;
import domain.CSpecialists;
import domain.DSpecialists;
import domain.ExInfoPageBean;
import domain.PSpecialists;
import service.ExInfoMService;

/**
 * Servlet implementation class ExInfoMServelt
 */
@WebServlet("/exInfoMServlet/*")
public class ExInfoMServlet extends BaseServlet {
       
	public void fuzzySelect(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		HttpSession session = request.getSession();
		String tid = (String) session.getAttribute("tid");
		if(tid == null || "".equals(tid)) {
			response.getWriter().write("<script>alert('请打开浏览器cookie设置，本网站不会涉及您的信息安全'); window.location='" + request.getContextPath() + "'</script>");
		} else if(tid.charAt(0) == 'S') {
			response.getWriter().write("<script>alert('当前权限不能添加账号'); window.location='" + request.getContextPath() + "'</script>");
		}
		String mid = (String) session.getAttribute("mid");
		String name = request.getParameter("name");
		ExInfoMService exService = new ExInfoMService();
		List<Map<String, Object>> pageBean = exService.fuzzySelect(tid, mid, name);
		
		ObjectMapper mapper = new ObjectMapper();
		String pageJson = mapper.writeValueAsString(pageBean);
		
		response.getWriter().write(pageJson);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getPageBean(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		HttpSession session = request.getSession();
		String tid = (String) session.getAttribute("tid");
		if(tid == null || "".equals(tid)) {
			response.getWriter().write("<script>alert('请打开浏览器cookie设置，本网站不会涉及您的信息安全'); window.location='" + request.getContextPath() + "'</script>");
		} else if(tid.charAt(0) == 'S') {
			response.getWriter().write("<script>alert('当前权限不能添加账号'); window.location='" + request.getContextPath() + "'</script>");
		}
		String mid = (String) session.getAttribute("mid");
		ExInfoMService exService = new ExInfoMService();
		ExInfoPageBean pageBean = exService.getPageBean(tid, mid);
		
		ObjectMapper mapper = new ObjectMapper();
		String pageJson = mapper.writeValueAsString(pageBean);
		
		response.getWriter().write(pageJson);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void del(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			String spid = request.getParameter("spid");

			ExInfoMService exService = new ExInfoMService();
			exService.del(spid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "添加账号失败、请检查数据输入";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String tid = (String) session.getAttribute("tid");
			String mid = (String) session.getAttribute("mid");			
			PSpecialists sp = new PSpecialists();
			sp.setMid(mid);
//			if(!mid.equals(request.getParameter("txt12"))) {
//				message = "请检查添加级别";
//				throw new Exception();
//			}
			sp.setSpname(request.getParameter("txt1"));
			sp.setSpusername(request.getParameter("txt2"));
			sp.setSppassword(request.getParameter("txt3"));
			sp.setSpphone(request.getParameter("txt4"));
			sp.setSporganization(request.getParameter("txt5"));
			sp.setSpspecialty(request.getParameter("txt6"));
			if(!"".equals(request.getParameter("txt7")))
				sp.setSpage(Integer.parseInt(request.getParameter("txt7")));
			sp.setSptitle(request.getParameter("txt8"));
			sp.setSprank(request.getParameter("txt9"));
			sp.setSpfields(request.getParameter("txt10"));
			sp.setSpgrade(request.getParameter("txt11"));
			
			ExInfoMService exService = new ExInfoMService();
			message = exService.add(sp, tid, mid);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		}
	}
	
	public void change(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "修改账号失败、请检查数据输入";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String tid = (String) session.getAttribute("tid");
			String mid = (String) session.getAttribute("mid");			
			if(!mid.equals(request.getParameter("txt12"))) {
				message = "请检查修改账号的级别";
			}
			PSpecialists sp = new PSpecialists();
			sp.setMid(mid);
			sp.setPspid(request.getParameter("txt0"));
			sp.setSpname(request.getParameter("txt1"));
			sp.setSpusername(request.getParameter("txt2"));
			sp.setSppassword(request.getParameter("txt3"));
			sp.setSpphone(request.getParameter("txt4"));
			sp.setSporganization(request.getParameter("txt5"));
			sp.setSpspecialty(request.getParameter("txt6"));
			sp.setSpage(Integer.parseInt(request.getParameter("txt7")));
			sp.setSptitle(request.getParameter("txt8"));
			sp.setSprank(request.getParameter("txt9"));
			sp.setSpfields(request.getParameter("txt10"));
			sp.setSpgrade(request.getParameter("txt11"));
			
			ExInfoMService exService = new ExInfoMService();
			message = exService.change(sp, tid, mid);
		}  catch (Exception e) {
			e.printStackTrace();
		} finally {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		}
	}
	
	public void select(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "该查询条件无记录";
		try {
			System.out.println("Exinfo:select");
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String tid = (String) session.getAttribute("tid");
			String mid = (String) session.getAttribute("mid");			
//			if(!mid.equals(request.getParameter("txt12"))) {
//				message = "查询账号的级别不是当前级别";
//				throw new Exception();
//			}
			
			PSpecialists sp = new PSpecialists();
			sp.setMid(mid);
			sp.setPspid(request.getParameter("txt0"));
			sp.setSpname(request.getParameter("txt1"));
			sp.setSpusername(request.getParameter("txt2"));
			sp.setSppassword(request.getParameter("txt3"));
			sp.setSpphone(request.getParameter("txt4"));
			sp.setSporganization(request.getParameter("txt5"));
			sp.setSpspecialty(request.getParameter("txt6"));
			if(request.getParameter("txt7") != "") 
				sp.setSpage(Integer.parseInt(request.getParameter("txt7")));
			sp.setSptitle(request.getParameter("txt8"));
			sp.setSprank(request.getParameter("txt9"));
			sp.setSpfields(request.getParameter("txt10"));
			sp.setSpgrade(request.getParameter("txt11"));
			
			ExInfoMService exService = new ExInfoMService();
//			//判断当前单位和管理员账号的tid级别是否匹配
//			if(!sp.getSporganization().equals("")) {
//				System.out.println("单位： " + sp.getSporganization());
//				if(exService.selectCheckTid(tid, sp.getSporganization()) == false) {
//					message = "查询单位与本单位不匹配";
//					throw new Exception();
//				}
//				System.out.println(exService.selectCheckTid(tid, sp.getSporganization()));
//			}
//			if(tid.charAt(0) == 'P')
//				sp.setSporganization("福建省");
			//查找
			ExInfoPageBean pageBean = exService.selectPageBean(sp, tid);
			
			ObjectMapper mapper = new ObjectMapper();
			String pageJson = mapper.writeValueAsString(pageBean);
			response.getWriter().write(pageJson);
		} catch (Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
		} 
	}
}
