package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import service.ChangeSubPinService;

/**
 * Servlet implementation class ChangeSubPinServlet
 */
@WebServlet("/ChangeSubPinServlet/*")
public class ChangeSubPinServlet extends BaseServlet {
    
	public void change(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			String name = request.getParameter("name");
			String rankName = request.getParameter("rankName");
			String username = request.getParameter("username");
			String newPass = request.getParameter("newPass");
			
			HttpSession session = request.getSession();
			String tid = (String) session.getAttribute("tid");
			if(null == tid) {
				response.getWriter().write("<script>alert('请打开浏览器cookie设置，本网站不会涉及您的信息安全'); window.location='" + request.getContextPath() + "'</script>");
				return ;
			} 
			ChangeSubPinService changePin = new ChangeSubPinService();
			changePin.solve(tid,name,rankName,username,newPass);
			response.getWriter().write("<script>alert('密码修改成功'); window.location='" + request.getContextPath() + "/exInfoM.jsp'</script>");
		} catch (Exception e) {
			response.getWriter().write("<script>alert('" + "密码修改未成功,请再次检查输入" + "'); window.location='" + request.getContextPath() + "/changeSubPin.jsp'</script>");
			e.printStackTrace();
		}
	}
}
