package servlet;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import service.LoginService;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet/*")
public class LoginServlet extends BaseServlet {

	public void getWebSiteHead(HttpServletRequest request, HttpServletResponse response) throws IOException, DocumentException {
		String message = "未成功获取标题";
		try {
			String path = LoginServlet.class.getResource("/").getPath();
			request.setCharacterEncoding("utf-8");
			response.setContentType("text/html;charset=utf-8");
			File file = new File(path + "head.xml");
			if(file.exists()) {
				Document document = new SAXReader().read(file);
				Element element = document.getRootElement().element("headTest");
				message = element.getText();
			} else 
				message = "福建省教育厅教学项目评估系统";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		}
	}

	public void sub(HttpServletRequest request, HttpServletResponse response) throws IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		HttpSession session = request.getSession();
		Cookie user = new Cookie("username", request.getParameter("username"));
		user.setPath("/");
		response.addCookie(user);
		//设置记住用户名cookie
		Cookie rem = new Cookie("rem", "rem");
		if("rem".equals(request.getParameter("rem"))) {
		} else { rem.setMaxAge(0); }
		rem.setPath("/");
		response.addCookie(rem);
		String message = "登录出错，请重新登录";
		try {
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			LoginService logServie = new LoginService();
			
			Map<String, String> map = logServie.check(username,password);
			if(map == null) {
				message = "用户名或密码错误";
				return ;
			}
			
			message = map.get("message");
			if(!"允许登录".equals(message)) {
				throw new Exception();
			}
			//区分省级管理员页面和其他级别管理员页面
			if(map.get("spid") != null){
				session.removeAttribute("mid");
				session.removeAttribute("tid");
				session.setAttribute("spid", map.get("spid"));
				if(map.get("spid").charAt(0) == 'D') {
					message = "DSPRate.jsp";
					return ;
				} else if(map.get("spid").charAt(0) == 'C') {
					message = "CSPRate.jsp";
					return ;
				}
				message = "PSPRate.jsp";
			} else {
				session.removeAttribute("spid");
				session.setAttribute("mid", map.get("mid"));
				session.setAttribute("tid", map.get("tid"));
				//以秒为单位
				session.setMaxInactiveInterval(60 * 60 * 24);
				//null登录失败、P开头Tid为省级管理员账号、其他为市县校管理员
				if("M00001".equals(map.get("mid"))) {
					//省级管理员
					message = "CSet.jsp";
				} else if(map.get("tid").charAt(0) != 'S'){
					if(map.get("tid").charAt(0) == 'D')
						message = "DexInfoM.jsp";
					if(map.get("tid").charAt(0) == 'C')
						message = "CexInfoM.jsp";
				} else if(map.get("tid").charAt(0) == 'S') {
					message = "SSPRate.jsp";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		}
	}


}
