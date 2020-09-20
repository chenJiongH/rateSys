package util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//直接请求 只能访问登录页面 
@WebFilter(value="*.jsp", dispatcherTypes = {javax.servlet.DispatcherType.REQUEST })
public class RequestFilter implements Filter  {
 
	@Override
	public void doFilter(ServletRequest req, ServletResponse rep, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) rep;
	    String uri = request.getRequestURI();
//	    System.out.println(uri);
	    //判断用户是否已经登录
		String username = "";
		Cookie[] cs = request.getCookies();
		for(int i = 0; cs != null && i < cs.length; i++) {
			if("username".equals(cs[i].getName())) {
				username = cs[i].getValue();
			}
		}
		if(uri.contains("/login.jsp") || uri.contains("/loginServlet") || uri.contains("/css/") || uri.contains("/js/") || uri.contains("/images/")) {
    		chain.doFilter(request, response);
		} else {
		    if( username == null || "".equals(username)) { //未登录过，不放行
		    	response.sendRedirect(request.getContextPath() + "/login.jsp");
	    	} else {
	    		chain.doFilter(request, response);
	    	}
		}
	}
}
