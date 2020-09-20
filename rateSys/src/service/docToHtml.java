package service;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.w3c.dom.Document;


public class docToHtml {
	/**
	 * word文档   doc格式转为html   
	 * @param rootPath   
	 * 			项目的路径
	 * @param fileUrl    
	 * 			数据库存放文件的相对路径   (例：/file/aa.doc)
	 * @param last      
	 * 			获取fileUrl的长度
	 * @param typeLength
	 * 			文件类型的长度
	 * @return    
	 * 			返回html的路径
	 */
	public static String docToHtml(String rootPath,String fileUrl,int last,int typeLength) {
		InputStream input = null;
		String htmlUrl = "";
		// 下面有很多try catch   如果感觉麻烦就直接用一个 ,catch里面变为Exception即可
		try {
			input = new FileInputStream(rootPath + fileUrl);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		//  HWPFDocument是poi中用来读取doc文件的
		HWPFDocument wordDocument = null;
		try {
			wordDocument = new HWPFDocument(input);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// 官网地址：http://poi.apache.org/apidocs/dev/org/apache/poi/hwpf/converter/WordToHtmlConverter.html
		// 用来将word转为html
		WordToHtmlConverter wordToHtmlConverter = null;
		try {
			wordToHtmlConverter = new WordToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		// setPicturesManager用来处理图片
		wordToHtmlConverter.setPicturesManager(new PicturesManager() {
			public String savePicture(byte[] content, PictureType pictureType,
					String suggestedName, float widthInches, float heightInches) {
				return suggestedName;
			}
		});
		wordToHtmlConverter.processDocument(wordDocument);
		List pics = wordDocument.getPicturesTable().getAllPictures();
		if (pics != null) {
			for (int i = 0; i < pics.size(); i++) {
				Picture pic = (Picture) pics.get(i);
				try {
					pic.writeImageContent(new FileOutputStream(rootPath
							+ pic.suggestFullFileName()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// 该Document接口表示整个HTML或XML文档。从概念上讲，它是文档树的根，并提供对文档数据的主要访问。
		Document htmlDocument = wordToHtmlConverter.getDocument();
		// 字节数组输出流
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		// 以文档对象模型（DOM）树的形式充当转换源树的持有者。
		DOMSource domSource = new DOMSource(htmlDocument);
		// 充当转换结果的持有者，可以是XML，纯文本，HTML或其他形式的标记。
		StreamResult streamResult = new StreamResult(outStream);
		TransformerFactory tf = TransformerFactory.newInstance();
		// 处理来自各种源的XML，并将转换输出写入各种接收器。
		Transformer serializer = null;
		try {
			serializer = tf.newTransformer();
		} catch (TransformerConfigurationException e1) {
			e1.printStackTrace();
		}
		// 设置对转换有效的输出属性
		serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		serializer.setOutputProperty(OutputKeys.INDENT, "yes");
		serializer.setOutputProperty(OutputKeys.METHOD, "html");
		try {
			serializer.transform(domSource, streamResult);
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		try {
			outStream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String content = new String(outStream.toByteArray());
		// 对url的解释   数据库存的地址是相对路径，打算将html也放再这个路径里面，所以需要将文件的名字和后缀去掉，-32是文件通过uuid动态创建的
		String url = fileUrl.substring(0, last - typeLength - 3);
		// 设置文件名称
		String s = UUID.randomUUID().toString();
		// 去掉-
		String aString = s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24);
		// 设置html文件的名称
		String htmlName = aString + ".html";
		// 打算存入数据库的相对路径,该路径返回前端页面，直接在前面拼接项目路径即可
		htmlUrl = url + htmlName;
		String Path = (rootPath + url).replace("\\", "/");
		try {
			/**
			 * FileUtils.writeStringToFile(file, data, encoding),把字符串写进对应的文件中
			 * file是新建的文件    data是写入的内容    encoding是编码格式
			 */
			FileUtils.writeStringToFile(new File(Path, htmlName),content, "utf-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return htmlUrl;
	}
	public static void main(String[] args) {
		// 项目中应该这样获取 String rootPath = request.getSession().getServletContext().getRealPath("");
		String rootPath = "D:";
		String fileUrl = "/abc.doc";
		 // 截取文件后缀
		int begin = fileUrl.lastIndexOf(".");
		int last = fileUrl.length();
		String type = fileUrl.substring(begin, last);
		int typeLength = type.length();
		 
		String htmlUrl = docToHtml(rootPath, fileUrl, last, typeLength);
		System.out.println(htmlUrl);    //输出结果为： /doc/5198c3b9f6e04458901bacbf98bf4776.html
	}
	
}
