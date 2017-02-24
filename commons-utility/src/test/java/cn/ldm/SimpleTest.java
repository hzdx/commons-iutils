package cn.ldm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import cn.ldm.commons.utils.FileUtil;
import cn.ldm.commons.utils.HttpUtil;
import cn.ldm.commons.utils.IOUtil;

public class SimpleTest {
	@Test
	public void testDefaultByte() throws FileNotFoundException, IOException{
		byte[] buf = new byte[4];
		//System.out.println(buf[0]);//0
		//System.out.println(buf[1]);//0
		
		byte[] bs = IOUtil.readBytes(new FileInputStream("d:/c.txt"),5);
		System.out.println(new String(bs));
	}
	
	@Test
	public void testHttpPost() throws IOException {
		String s = HttpUtil.postJson("http://localhost:8081/aaa/bb", "hello server!123");
		System.out.println(s);
	}

	@Test
	public void testHttpGet() throws IOException {
		String s = HttpUtil.get("http://localhost:8081/aaa/bb", null);
		System.out.println(s);
	}

	@Test
	public void testFile() throws IOException {
//		String s = FileUtil.read("e:/目录规划.txt");
//		System.out.println(s);
//		FileUtil.write("e:/2.txt", s);
		
//		byte[] bs = "**************************".getBytes();
//		FileUtil.writeBytesNio("d:/c.txt", bs,false);
		
//		byte[] bs1 = FileUtil.readBytes("d:/b.txt");
//		String s = new String(bs1); 
//		System.out.println(s);
		
//		String s2 = FileUtil.read("d:/b.txt");
//		System.out.println(s2);
		
//		String s3 = "abcdegg你好，！111";
//		FileUtil.append("d:/g.txt", s3);
//		
		String s2 = FileUtil.read("d:/c.txt");
//		System.out.println(s2.length());
		System.out.println(s2);
	}

}