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

import domain.PSPRatePageBean;
import service.PSPRateService;

/**
 * Servlet implementation class DSPRateServlet
 */
@WebServlet("/PSPRateServlet/*")
public class PSPRateServlet extends BaseServlet {

	public void findPr(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "页面请求失败，请重新登录或刷新页面";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String spid = (String) session.getAttribute("spid");
			PSPRateService service = new PSPRateService();
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
			int isOnSpot = Integer.parseInt(request.getParameter("isOnSpot"));
			PSPRateService service = new PSPRateService();
			PSPRatePageBean pageBean = service.findCByPid(spid, pid, isOnSpot);
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
			//转换json数组为java数组对象
			ObjectMapper mapper = new ObjectMapper(); 
			Map<String, Object> cs = mapper.readValue(c,  new TypeReference<Map<String, Object>>() {});
			String pid = (String) cs.get("pid");
			int isOnSpot =  Integer.parseInt(cs.get("isOnSpot").toString());
			PSPRateService service = new PSPRateService();
			message = service.check(spid, pid, isOnSpot);
			if("您已经提交过评分，不可中途修改提交".equals(message)) {
				return ;
			}
			service.ratePr(cs, spid, isOnSpot, pid);
			message = "评定提交成功。";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		}
	}
	
}
