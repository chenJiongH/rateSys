package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import service.GetWelcomeNameService;

/**
 * Servlet implementation class GetWelcomeName
 */
@WebServlet("/GetWelcomeName/*")
public class GetWelcomeName extends BaseServlet {
	
	public void getName(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			HttpSession session = request.getSession();
//		专家
			String spid = (String) session.getAttribute("spid");
//		管理员
			String mid = (String) session.getAttribute("mid");
			String tid = (String) session.getAttribute("tid");
			GetWelcomeNameService getNameService = new GetWelcomeNameService();
			ObjectMapper mapper = new ObjectMapper();
			String name = "";
			if(null == spid)
				name = getNameService.getManaName(mid, tid);
			else 
				name = getNameService.getSpName(spid);
			String nameJson = mapper.writeValueAsString(name);
			response.getWriter().write(nameJson);
			
		} catch( Exception e) {
			e.printStackTrace();
		}
	}

}
