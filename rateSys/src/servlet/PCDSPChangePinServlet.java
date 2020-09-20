package servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import domain.PSpecialists;
import service.ExInfoMService;
import service.PCDSPChangePinService;

/**
 * Servlet implementation class PCDSPChangePinServlet
 */
@WebServlet("/PCDSPChangePinServlet/*")
public class PCDSPChangePinServlet extends BaseServlet {
       
	public void spChange(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "请验证原密码...";
		try {
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String spid = (String) session.getAttribute("spid");
			if(spid == null)
				throw new Exception() ;
			PSpecialists sp = new PSpecialists();
			sp.setPspid(spid);
			sp.setSpname(request.getParameter("txt1"));
			sp.setSpusername(request.getParameter("txt2"));
			sp.setSppassword(request.getParameter("oldPass"));
			String newPass = request.getParameter("txt3");
			sp.setSpphone(request.getParameter("txt4"));
			sp.setSporganization(request.getParameter("txt5"));
			sp.setSpspecialty(request.getParameter("txt6"));
			sp.setSpage(Integer.parseInt(request.getParameter("txt7")));
			sp.setSptitle(request.getParameter("txt8"));
			sp.setSprank(request.getParameter("txt9"));
			sp.setSpfields(request.getParameter("txt10"));
			sp.setSpgrade(request.getParameter("txt11"));

			PCDSPChangePinService service = new PCDSPChangePinService();
			service.change(sp, newPass);
			message = "修改成功";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		}
	}
    
	public void findSPMessage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "请重新刷新页面或者再次登录";
		try {
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
			String spid = (String) session.getAttribute("spid");
			if(spid == null)
				throw new Exception() ;
			PCDSPChangePinService service = new PCDSPChangePinService();
			ObjectMapper mapper = new ObjectMapper();
			String spMessageJson = mapper.writeValueAsString(service.findSPMessage(spid));
			response.getWriter().write(spMessageJson);
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		}
	}
	

}
