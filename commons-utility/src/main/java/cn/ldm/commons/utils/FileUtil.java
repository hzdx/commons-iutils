package cn.ldm.commons.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {

	public static final String CHARSET_UTF8 = "utf-8";

	public static String read(String path) throws IOException {
		return new String(readBytes(path), CHARSET_UTF8);
	}

	public static String read(String path, String charSet) throws IOException {
		return new String(readBytes(path), charSet);
	}

	public static byte[] readBytes(String path) throws IOException {
		FileInputStream is = null;
		try {
			is = new FileInputStream(path);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(8192);
			byte[] buf = new byte[8192];
			int n = 0;
			while ((n = is.read(buf)) != -1) {
				bos.write(buf, 0, n);
			}
			return bos.toByteArray();
		} catch (IOException e) {
			throw e;
		} finally {
			if (is != null)
				try {
					is.close();
				} catch (IOException ex) {
				}
		}
	}

	public static void append(String path, String content) throws IOException {
		write(path, true, content, CHARSET_UTF8);
	}

	public static void write(String path, String content) throws IOException {
		write(path, false, content, CHARSET_UTF8);
	}

	public static void write(String path, String content, String charSet) throws IOException {
		write(path, false, content, charSet);
	}

	public static void write(String path, boolean append, String content, String charSet) throws IOException {
		byte[] data = charSet == null ? content.getBytes() : content.getBytes(charSet);
		writeBytes(path, data, append);
	}

	public static void writeBytes(String path, byte[] data, boolean append) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path, append);
			fos.write(data);
			fos.flush();
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
