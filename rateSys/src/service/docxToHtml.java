package service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.poi.xwpf.converter.core.BasicURIResolver;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;


public class docxToHtml {
	
	public static String getHtmlByUrl(String url) throws Exception {
//		URL url = this.getClass().getClassLoader().getResource("templates/" + pageName + ".html");
		// File("E:\\code_svn\\srp_trunk\\target\\classes\\templates\\404.html");
		StringBuffer sb = new StringBuffer();
		BufferedInputStream bis = null;
		File f = new File(url);
		FileInputStream fis = new FileInputStream(f);
		bis = new BufferedInputStream(fis);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String s = "";
		while ((s = br.readLine()) != null) {
			sb.append(s).append("\n");
		}
		
		if (bis != null) {
			bis.close();
		}
		return sb.toString();
	}
	
	public static String docxToHtmlFun(String rootPath,String fileUrl,int last, int typeLength) throws Exception {
//		String url = fileUrl.substring(0, last - typeLength - 5);
		String url = "/";
		String htmlPath = (rootPath + url).replace("\\", "/");
		System.out.println(url + " : " + htmlPath + " : " + last);
		String s = UUID.randomUUID().toString();
		// 去掉-
		String aString = s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24);
		String htmlName = aString + ".html";
		String imagePath = htmlPath + "image";
		String htmlUrl = url + htmlName;
		// 判断html文件是否存在
		File htmlFile = new File(htmlPath + htmlName);

		// 1) 加载word文档生成 XWPFDocument对象
		InputStream input = new FileInputStream(rootPath + fileUrl);
		XWPFDocument document = new XWPFDocument(input);

		// 2) 解析 XHTML配置 (这里设置URIResolver来设置图片存放的目录)
		File imgFolder = new File(imagePath);
		XHTMLOptions options = XHTMLOptions.create();
		options.setExtractor(new FileImageExtractor(imgFolder));
		// html中图片的路径 相对路径
		options.URIResolver(new BasicURIResolver("image"));
		options.setIgnoreStylesIfUnused(false);
		options.setFragment(true);

		// 3) 将 XWPFDocument转换成XHTML
		// 生成html文件上级文件夹
		File folder1 = new File(htmlPath);
		if (!folder1.exists()) {
			folder1.mkdirs();
		}
		OutputStream out = new FileOutputStream(htmlFile);
		System.out.println(XHTMLConverter.getInstance() + ":" + document + ":" + out + ":" + options);
		XHTMLConverter.getInstance().convert(document, out, options);

		return htmlUrl;
	}
	public Map getHtmlDomByDocxFilePath(String rootPath, String fileUrl) throws Exception{
		// 项目中应该这样获取 String rootPath = request.getSession().getServletContext().getRealPath("");
//		String rootPath = "D:";
//		String fileUrl = "/tes12312312t2.docx";
		 // 截取文件后缀
		int begin = fileUrl.lastIndexOf(".");
		int last = fileUrl.length();
		String type = fileUrl.substring(begin, last);
		int typeLength = type.length();
		 
		// docx -》 html
		String	htmlUrl = docxToHtmlFun(rootPath, fileUrl, last, typeLength);
		
//			System.out.println(htmlUrl);   // 控制台输出  /doc/5ee643de6ba64fa589ec34cbddda3097.html
//			System.out.println(rootPath + htmlUrl);
		Map<String, Object> map = new HashMap<String, Object>();
		//HtmlLocation
		map.put("filePath", rootPath + htmlUrl);
		// 获取html文档内容
		map.put("div", getHtmlByUrl(rootPath + htmlUrl));
		return map;
	}
}
