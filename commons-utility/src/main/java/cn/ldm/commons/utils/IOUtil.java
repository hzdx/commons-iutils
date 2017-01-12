package cn.ldm.commons.utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtil {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

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
			sb.append(s + LINE_SEPARATOR);
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
		write(os, s, Strings.CHARSET_UTF8);
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

}
