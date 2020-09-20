package servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import service.CCSService;

/**
 * Servlet implementation class CCSServlet
 */
@WebServlet("/CCSServlet/*")
public class CCSServlet extends BaseServlet {
	
	public void findCities(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		String csJson = "";//
		HttpSession session = request.getSession();
		csJson = (String) session.getAttribute("csJson");
		//为所有城市设置session，在导入操作之后删除该属性
		if(csJson == null || "".equals(csJson)) {
			Map<Integer, String> map = CCSService.findCitys();
			ObjectMapper mapper = new ObjectMapper();
			csJson = mapper.writeValueAsString(map);
			session.setAttribute("csJson", csJson);
		}
		response.getWriter().write(csJson);
		response.getWriter().close();
	}


}
