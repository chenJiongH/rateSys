package servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import domain.CityCountySchool;
import service.InsertCCS;

/**
 * Servlet implementation class SubmitCCS
 */
@WebServlet("/SubmitCCS")
public class SubmitCCS extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SubmitCCS() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		String json = request.getParameter("jsonObj");
		
		ObjectMapper mapper = new ObjectMapper();
		//将json字符串转换为java Bean对象数组
		List<CityCountySchool> cs = mapper.readValue(json,  new TypeReference<List<CityCountySchool>>() {});
		

		HttpSession session = request.getSession();
		session.setAttribute("json", json);
		
		//往数据库更新数据
		if(InsertCCS.insert1(cs) == true) {
			response.sendRedirect("acM.jsp");
		} else {
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write("<script>alert('提交失败，请再次留心数据格式。。。')</script>");
			response.sendRedirect("CSet.jsp");
		}
	}

}
