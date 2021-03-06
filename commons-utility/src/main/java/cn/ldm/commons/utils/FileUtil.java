package cn.ldm.commons.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.springframework.util.ResourceUtils;

public class FileUtil {

	public static String getClasspathFileLocation(String path) throws IOException {
		return getClasspathFile(path).getAbsolutePath().replace("\\", "/");
		// windows目录以 '\' 分隔，用replace转换为linux下的'/' 分隔.
	}

	public static File getClasspathFile(String path) throws IOException {
		return ResourceUtils.getFile("classpath:" + path);
	}

	public static String readFile(String path) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(path));
		StringBuffer sb = new StringBuffer();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		reader.close();
		return sb.toString();
	}

	public static String read(String path) throws IOException {
		return new String(readBytesNio(path), Constants.UTF8);
	}

	public static String read(String path, String charSet) throws IOException {
		return new String(readBytesNio(path), charSet);
	}

	public static byte[] readBytesNio(String path) throws IOException {
		return Files.readAllBytes(Paths.get(path));
	}

	@Deprecated
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
		write(path, true, content, Constants.UTF8);
	}

	public static void write(String path, String content) throws IOException {
		write(path, false, content, Constants.UTF8);
	}

	public static void write(String path, String content, String charSet) throws IOException {
		write(path, false, content, charSet);
	}

	public static void write(String path, boolean append, String content, String charSet) throws IOException {
		byte[] data = charSet == null ? content.getBytes() : content.getBytes(charSet);
		writeBytesNio(path, data, append);
	}

	public static void writeBytes(String path, byte[] data) throws IOException {
		writeBytesNio(path, data, false);
	}

	public static void writeBytesNio(String path, byte[] data, boolean append) throws IOException {
		Path url = Paths.get(path);
		// 不会自动创建文件
		if (!Files.exists(url)) {
			Files.createFile(url);
		}
		if (append) {
			Files.write(url, data, StandardOpenOption.APPEND);
		} else {
			Files.write(url, data, StandardOpenOption.WRITE);
		}
	}

	@Deprecated
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
