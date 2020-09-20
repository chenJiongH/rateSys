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

import domain.CSPRatePageBean;
import service.CSPRateService;
import service.DSPRateService;

/**
 * Servlet implementation class DSPRateServlet
 */
@WebServlet("/CSPRateServlet/*")
public class CSPRateServlet extends BaseServlet {	
	
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
			CSPRateService service = new CSPRateService();
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
			CSPRateService service = new CSPRateService();
			CSPRatePageBean pageBean = service.findCByPid(spid, pid);
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
			CSPRateService service = new CSPRateService();
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
