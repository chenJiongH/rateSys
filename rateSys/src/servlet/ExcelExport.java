package servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import domain.CityCountySchool;
import service.ExcelExportService;
import service.InsertCCS;

/**
 * Servlet implementation class ExcelExport
 */
@WebServlet("/ExcelExport")
public class ExcelExport extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ExcelExport() {
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
		HttpSession session = request.getSession();
		String json = (String)session.getAttribute("json");
		ObjectMapper mapper = new ObjectMapper();
		//将json字符串转换为java Bean对象数组
		List<CityCountySchool> cs = mapper.readValue(json,  new TypeReference<List<CityCountySchool>>() {});
		

		
		//以下为导出该Excel文件
		//DownLoadServlet.java保存为Excel文件
		String path = "D:/CCS.xls";
		String filename = "CCS.xls";
		ExcelExportService.export(cs, path);
		//传送该Excel文件
		response.addHeader("content-Type", "application/octet-stream");

		String agent = request.getHeader("User-Agent");
//		是以什么方式下载，如attachment为以附件方式下载
		if (agent.toLowerCase().indexOf("chrome") > 0) {
			response.addHeader("content-Disposition",
					"attachment;filename=" + new String(filename.getBytes("UTF-8"), "ISO8859-1"));
		} else {
			response.addHeader("content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
		}
//		输入流FileInputStream直接关联服务器文件，然后用ServletOutputStream类型创建response.getOutputStream()；把输入流的数据传递给输出流，over；
		FileInputStream in = new FileInputStream("D:/" + filename);
		ServletOutputStream out = response.getOutputStream();
		byte[] bs = new byte[1024];
		int len = -1;
		while ((len = in.read(bs)) != -1) {
			out.write(bs, 0, len);
		}
		in.close();
		out.close();
		
		//往数据库更新数据
		if(InsertCCS.insert1(cs) == false) {
//			response.setContentType("text/html;charset=UTF-8");
//			response.getWriter().write("<script>alert('导出失败，请再次留心数据格式。。。')</script>");
		}
//		response.sendRedirect("deriInfo.jsp");

	}

}
