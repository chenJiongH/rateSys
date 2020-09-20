package service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import com.aspose.words.*;

public class DocToManyImg {
	/**
	 * 转换word文件成多张图片，在outDir下生成多张图片 ,返回多张文件的绝对地址数组
	 * @param inPath
	 * @param outDir
	 */
    public static List<String> doc2ManyImg(String inPath, String outDir){
        try {
			long old = System.currentTimeMillis();
            // word文档
            Document doc = new Document(inPath);
            // 支持RTF HTML,OpenDocument, PDF,EPUB, XPS转换
            ImageSaveOptions options = new ImageSaveOptions(SaveFormat.PNG);
            int pageCount = doc.getPageCount();
            
            List<String> imageList = new ArrayList<>();
            for (int i = 0; i < pageCount; i++) {
            	String imgUrl = outDir+"/"+i+".png";
                File file = new File(imgUrl);
                OutputStream os = new FileOutputStream(file);
                options.setPageIndex(i);
                doc.save(os, options);
                imageList.add(imgUrl);
            }
			long now = System.currentTimeMillis();
			System.out.println("word转多图共耗时：" + ((now - old) / 1000.0) + "秒"); // 转化用时
			return imageList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 合并任数量的图片成一张图片
     * @param piclist 图片绝对地址
     * @param outPath 合并图片绝对地址
     */
	public static void mergeImgByVertical(List<String> piclist, String outPath) {// 纵向处理图片
		if (piclist == null || piclist.size() <= 0) {
			System.out.println("图片数组为空!");
			return;
		}
		try {
			int height = 0, // 总高度
			width = 0, // 总宽度
			_height = 0, // 临时的高度 , 或保存偏移高度
			__height = 0, // 临时的高度，主要保存每个高度
			picNum = piclist.size();// 图片的数量
			File fileImg = null; // 保存读取出的图片
			int[] heightArray = new int[picNum]; // 保存每个文件的高度
			BufferedImage buffer = null; // 保存图片流
			List<int[]> imgRGB = new ArrayList<int[]>(); // 保存所有的图片的RGB
			int[] _imgRGB; // 保存一张图片中的RGB数据
			for (int i = 0; i < picNum; i++) {
				fileImg = new File(piclist.get(i));
				buffer = ImageIO.read(fileImg);
				heightArray[i] = _height = buffer.getHeight();// 图片高度
				if (i == 0) {
					width = buffer.getWidth();// 图片宽度
				}
				height += _height; // 获取总高度
				_imgRGB = new int[width * _height];// 从图片中读取RGB
				_imgRGB = buffer.getRGB(0, 0, width, _height, _imgRGB, 0, width);
				imgRGB.add(_imgRGB);
			}
			_height = 0; // 设置偏移高度为0
			// 生成新图片
			BufferedImage imageResult = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			for (int i = 0; i < picNum; i++) {
				__height = heightArray[i];
				if (i != 0) _height += __height; // 计算偏移高度
				imageResult.setRGB(0, _height, width, __height, imgRGB.get(i), 0, width); // 写入流中
			}
			File outFile = new File(outPath);
			ImageIO.write(imageResult, "png", outFile);// 写图片
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    /**
     * 传入文件所在绝对地址 和 输出目录
     * 调用2ManyImg方法生成多个图片，再调用mergeImg方法合并图片
     * 返回合并图片所在的绝对地址
     * @throws IOException 
     * 
     */
    public static String doc2OneImg(String inPath, String outDir) throws IOException{
    	
        List<String> imgsUrl = new ArrayList<>();
        imgsUrl = doc2ManyImg(inPath, outDir);

		String s = UUID.randomUUID().toString();
		String aString = s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24);
		String outPath = outDir + aString + ".png";
		System.out.println("outPath: " + outPath);
        mergeImgByVertical(imgsUrl, outPath);
        return outPath;
    }
    
    /**
     * 转换word文件成pdf文件
     * @param Address
     * @param outPath
     */
	public static void doc2pdf(String Address, String outPath) {

		try {
			long old = System.currentTimeMillis();
			File file = new File(outPath); // 新建一个空白pdf文档
			FileOutputStream os = new FileOutputStream(file);
			Document doc = new Document(Address); // Address是将要被转化的word文档
			doc.save(os, SaveFormat.PDF);// 全面支持DOC, DOCX, OOXML, RTF HTML,
			// OpenDocument, PDF, EPUB, XPS, SWF
			// 相互转换
			long now = System.currentTimeMillis();
			System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒"); // 转化用时
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 
	public static void test() {
		
//		doc2pdf("D:/src.docx", "D:/pdf1.pdf");
//		doc2ManyImg("D:/src.docx", "D:/test");
		try {
			doc2OneImg("D:/src.docx", "D:/");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
