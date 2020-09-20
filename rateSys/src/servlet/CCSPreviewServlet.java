package servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import domain.CityCountySchool;
import service.CCSPreviewService;

/**
 * Servlet implementation class CCSPreviewServlet
 */
@WebServlet("/CCSPreviewServlet")
public class CCSPreviewServlet extends HttpServlet {


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String cname = request.getParameter("cname");
			CCSPreviewService preview = new CCSPreviewService();
			List<Map<String, Object>> ccs = preview.getJson(cname);
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writeValueAsString(ccs);
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(json);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
