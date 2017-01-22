package cn.ldm;

import java.io.IOException;

import org.junit.Test;

import cn.ldm.commons.utils.FileUtil;
import cn.ldm.commons.utils.HttpUtil;

public class SimpleTest {

	@Test
	public void testHttpGet() throws IOException {
		String s = HttpUtil.get("http://www.baidu.com", null);
		System.out.println(s);
	}

	@Test
	public void testFile() throws IOException {
//		String s = FileUtil.read("e:/目录规划.txt");
//		System.out.println(s);
//		FileUtil.write("e:/2.txt", s);
//		byte[] bs = "abcdegg你好，！".getBytes();
//		FileUtil.writeBytes("d:/b.txt", bs);
		
//		byte[] bs1 = FileUtil.readBytes("d:/b.txt");
//		String s = new String(bs1); 
//		System.out.println(s);
		
//		String s2 = FileUtil.read("d:/b.txt");
//		System.out.println(s2);
		
//		String s3 = "abcdegg你好，！111";
//		FileUtil.append("d:/g.txt", s3);
//		
		String s2 = FileUtil.read("d:/c.txt","gbk");
		System.out.println(s2.length());
		System.out.println(s2);
	}

}
