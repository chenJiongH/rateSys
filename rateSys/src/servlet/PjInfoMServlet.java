package servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import domain.PjInfoMPageBean;
import domain.Project;
import service.PjInfoMService;

/**
 * Servlet implementation class PjInfoMServlet
 */
@WebServlet("/PjInfoMServlet/*")
public class PjInfoMServlet extends BaseServlet {

	public void changeCheckPnameByPid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String message = "该项目名称已存在";
	try {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		String pname = request.getParameter("pname");
		String pid = request.getParameter("pid");
		PjInfoMService pService = new PjInfoMService();
		//查找本次改动有没有改动项目名
		if(pService.changeCheckPnameByPid(pid).equals(pname)) {
			message = "success";
			return ;
		}
		message = pService.findPnameByPname(pname); 
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		response.getWriter().write("{\"message\":\"" + message + "\"}");
	}
}
	
	public void findPnameByPname(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	String message = "该项目名称已存在";
	try {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		String pname = request.getParameter("pname");
		PjInfoMService pService = new PjInfoMService();
		message = pService.findPnameByPname(pname); 
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		response.getWriter().write("{\"message\":\"" + message + "\"}");
	}
}
	
	public void findPageBean(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "数据请求失败，请重试";
		try {
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			int curpage = Integer.parseInt(request.getParameter("curpage"));
			PjInfoMService pService = new PjInfoMService();
			PjInfoMPageBean pageBean = pService.findPageBean(curpage); 
			
			ObjectMapper mapper = new ObjectMapper();
			//设置jackson转换日期类型为字符串的格式
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			mapper.setDateFormat(dateFormat);
			
			String pageBeanJson = mapper.writeValueAsString(pageBean);
			response.getWriter().write(pageBeanJson);
		} catch (Exception e) {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
			e.printStackTrace();
		} 
	}

	public void queryData(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "该查询条件无记录，请确认后再次查询";
		try {
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			Project p = new Project();
			p.setPname(request.getParameter("projectName"));
			p.setPprocess(request.getParameter("process"));
			p.setPisannex(request.getParameter("box1"));
			p.setPisstart(request.getParameter("box2"));
			p.setPdisplayExplain(request.getParameter("box3"));
			//获取起止日期
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String stime = null;
			String etime = null;
			if(!"".equals(request.getParameter("startTime"))) {
				stime = request.getParameter("startTime");
				p.setPstime(dateFormat.parse(request.getParameter("startTime")));
			}
			if(!"".equals(request.getParameter("endTime"))) {
				etime = request.getParameter("endTime");
				p.setPetime(dateFormat.parse(request.getParameter("endTime")));
			}
			//查找
			PjInfoMService pService = new PjInfoMService();
			PjInfoMPageBean pageBean = pService.queryPageBean(p, stime, etime); 
			
			ObjectMapper mapper = new ObjectMapper();
			//设置jackson转换日期类型为字符串的格式
			mapper.setDateFormat(dateFormat);
			String pageBeanJson = mapper.writeValueAsString(pageBean);
			response.getWriter().write(pageBeanJson);
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		}
	}
	
	public void addData(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String message = "添加失败，请确认该项目是否已经存在";
		try {
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			Project p = new Project();
			p.setPname(request.getParameter("projectName"));
			p.setPprocess(request.getParameter("process"));
			if("TRUE".equals(request.getParameter("box1"))) p.setPisannex("TRUE");
			else  p.setPisannex("FALSE");
			if("TRUE".equals(request.getParameter("box2"))) p.setPisstart("TRUE");
			else  p.setPisstart("FALSE");
			if("TRUE".equals(request.getParameter("box3"))) p.setPdisplayExplain("TRUE");
			else  p.setPdisplayExplain("FALSE");
			//获取起止日期
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			p.setPstime(dateFormat.parse((request.getParameter("startTime"))));
			p.setPetime(dateFormat.parse(request.getParameter("endTime")));
			//添加数据
			PjInfoMService pService = new PjInfoMService();
			pService.addData(p);
			message = "添加成功";
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		}
	}
	
	//点击修改记录
	public void changeData(HttpServletRequest request, HttpServletResponse response) {
		String message = "修改记录出错，请重试";
		try {
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			Project p = new Project();
			p.setPname(request.getParameter("projectName"));
			p.setPprocess(request.getParameter("process"));
			if("TRUE".equals(request.getParameter("box1"))) p.setPisannex("TRUE");
			else  p.setPisannex("FALSE");
			if("TRUE".equals(request.getParameter("box2"))) p.setPisstart("TRUE");
			else  p.setPisstart("FALSE");
			if("TRUE".equals(request.getParameter("box3"))) p.setPdisplayExplain("TRUE");
			else  p.setPdisplayExplain("FALSE");
			p.setPid(request.getParameter("pid"));
			//获取起止日期
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			p.setPstime(dateFormat.parse(request.getParameter("startTime")));
			p.setPetime(dateFormat.parse(request.getParameter("endTime")));
			//修改数据
			PjInfoMService pService = new PjInfoMService();
			pService.changeData(p);
			message = "修改成功";
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.getWriter().write("{\"message\":\"" + message + "\"}");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//点击删除后，先检查该记录是否有记录
	public void checkPByIndex(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 String message = "false";
		 try {
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			String pid = request.getParameter("pid");
			
			PjInfoMService pService = new PjInfoMService();
			message = pService.checkPByIndex(pid, message);
			System.out.println(message);
		 } catch(Exception e) {
			 e.printStackTrace();
		 } finally {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		 }
	}
	
	//点击删除记录
	public void delData(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 String message = "删除项目出错，请重试";
		 try {
			request.setCharacterEncoding("UTF-8");
			response.setContentType("text/html;charset=utf-8");
			String pid = request.getParameter("pid");
			String flag = request.getParameter("message");
			
			PjInfoMService pService = new PjInfoMService();
			pService.delData(pid, flag);
			message = "删除成功";
		 } catch(Exception e) {
			 e.printStackTrace();
		 } finally {
			response.getWriter().write("{\"message\":\"" + message + "\"}");
		 }
	}
	
	
}
