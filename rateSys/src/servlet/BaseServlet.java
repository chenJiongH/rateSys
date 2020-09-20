package servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class BaseServlet
 */
//利用反射进行方法的分发
@WebServlet("/BaseServlet")
public class BaseServlet extends HttpServlet {

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//		获取请求路径
		String uri = req.getRequestURI();
//		获取请求方法
		String methodName = uri.substring(uri.lastIndexOf('/') + 1);
	
		try {
//			this表示的是调用service方法的类对象，并不是BaseServlet类，因为BaseServlet是一个的祖宗类，并不创建对象，反射与执行方法
			Method method = this.getClass().getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
			method.invoke(this, req, resp);
			
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	

}
