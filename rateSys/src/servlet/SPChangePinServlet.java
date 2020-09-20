package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import service.ChangePinService;
import service.SPChangePinService;

/**
 * Servlet implementation class ChangePin
 */
@WebServlet("/SPChangePinServlet/*")
public class SPChangePinServlet extends BaseServlet {
	
	public void change(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "未成功修改";
		try {
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			Cookie[] cs = request.getCookies();
			Cookie c = null;
			String cookieUser = "";
			for(int i = 0; i < cs.length; i++) {
				c = cs[i];
				if("username".equals(c.getName())){
					cookieUser = c.getValue();
				}
			}
			HttpSession session = request.getSession();
			String spid = (String) session.getAttribute("spid");
			String mid = (String) session.getAttribute("mid");
			String tid = (String) session.getAttribute("tid");
			
			String username = request.getParameter("username");
			String oldPassword = request.getParameter("oldPassword");
			String newPassword = request.getParameter("newPassword");
			String name = request.getParameter("name");
			if(!username.equals(cookieUser)) {
				System.out.println(username);
				System.out.println(cookieUser);
				message = "请确认用户名是否为当前账号用户名";
				throw new Exception();
			}
			SPChangePinService changePin = new SPChangePinService();
			if(changePin.check(spid, mid, username, oldPassword, tid) == false) {
				message = "用户名与原密码不匹配";
				throw new Exception();
			}
			changePin.updatePin(spid, mid, newPassword, tid, name);
			message = "修改成功";
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		}
		
	}

}
