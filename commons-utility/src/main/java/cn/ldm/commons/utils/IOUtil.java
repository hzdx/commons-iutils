package cn.ldm.commons.utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class IOUtil {

	public static void closeStream(Closeable stream) {
		if (stream != null)
			try {
				stream.close();
			} catch (IOException e) {
				// ignore
			}
	}

	public static String read(BufferedReader reader) throws IOException {
		StringBuilder sb = new StringBuilder();
		String s = null;
		while ((s = reader.readLine()) != null) {
			sb.append(s + Constants.LINE_SEPARATOR);
		}
		return sb.toString();
	}

	public static String read(InputStream is) throws IOException {
		return read(is, 256);
	}

	public static String read(InputStream is, int buffSize) throws IOException {
		if (is == null)
			throw new NullPointerException("inputStream is null");
		try {
			StringBuilder sb = new StringBuilder();
			byte[] buf = new byte[buffSize];// 使用自己的buf,字符的边界会产生编码异常,不能处理全角字符
			int n = 0;
			while ((n = is.read(buf)) > 0) {
				sb.append(new String(buf, 0, n));
			}
			return sb.toString();
		} catch (IOException ex) {
			throw ex;
		}
	}

	public static void write(OutputStream os, String s) throws IOException {
		write(os, s, Constants.UTF8);
	}

	public static void write(OutputStream os, String s, String charSet) throws IOException {
		byte[] data = charSet == null ? s.getBytes() : s.getBytes(charSet);
		try {
			os.write(data);
		} catch (Exception e) {
			throw e;
		} finally {
			os.flush();
		}
	}
	
	public static byte[] readBytes(InputStream is) throws IOException {
		return readBytes(is, 4096);
	}

	public static byte[] readBytes(InputStream is, int bufSize) throws IOException {
		byte[] buf = new byte[bufSize];
		int len = is.read(buf);

		if (len <= 0)
			return "null".getBytes();
		else if (len < bufSize) {// buf一次能够读完.
			return Arrays.copyOf(buf, len);
		} else {// buf一次不能够读完.
			byte[] total = buf;
			int totalLen = bufSize;
			int times = 2;// 要读几次
			while (len >= bufSize) {
				//System.out.println(Thread.currentThread().getName()+" method availbe:"+is.available());
				if(is.available() <= 0){//应对上一次刚好读完,解决read的阻塞问题,但不准确,很多时候(一般在第一次read之前)有数据还会返回0	
					break;
				}
				
				total = Arrays.copyOf(total, bufSize * times);// 增加一倍容量,扩容
					
				len = is.read(buf);// 如果上一次刚好读完,会阻塞在这里.总的字节数是bufSize的整数倍,就会有这种情况
				if (len > 0) {
					totalLen += len;
					System.arraycopy(buf, 0, total, bufSize * (times - 1), len);
				}
				times++;
			}
			return Arrays.copyOf(total, totalLen);
		}
	}

}
