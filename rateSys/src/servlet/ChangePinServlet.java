package servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.fasterxml.jackson.databind.ObjectMapper;

import service.ChangePinService;
import util.JDBCUtils;

/**
 * Servlet implementation class ChangePin
 */
@WebServlet("/ChangePinServlet/*")
public class ChangePinServlet extends BaseServlet {
	
	public void getUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		String tid = (String) session.getAttribute("tid");
		String spid = (String) session.getAttribute("spid");
		ChangePinService changePin = new ChangePinService();
		Map<String, Object> userMessage = changePin.getUser(cookieUser, tid, spid);
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(userMessage);
		response.getWriter().write(json);
	} catch(Exception e) {
		e.printStackTrace();
	} finally {
		
	}
}
	
	public void change(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "未成功修改";

		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(JDBCUtils.getDataSource());
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();  
	    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);  
	    TransactionStatus status = transactionManager.getTransaction(def);
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
			String mid = (String) session.getAttribute("mid");
			String username = request.getParameter("username");
			String oldPassword = request.getParameter("oldPassword");
			String newPassword = request.getParameter("newPassword");
			String name = request.getParameter("name");
			String phono = request.getParameter("phono");
			String head = request.getParameter("head");
			if(!username.equals(cookieUser)) {
				message = "请确认用户名是否为当前账号用户名";
				throw new Exception();
			}
			ChangePinService changePin = new ChangePinService();
			if(changePin.check(username, oldPassword) == false) {
				message = "用户名与原密码不匹配";
				throw new Exception();
			}
			changePin.updatePin(username, oldPassword, newPassword, name, mid, phono);
			// 修改标题 head
			if(head != null && !"".equals(head))
				changePin.changeHead(head);
			message = "修改成功";
		    transactionManager.commit(status);
		} catch(Exception e) {
			e.printStackTrace();
			transactionManager.rollback(status);
		} finally {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		}
	}

}

