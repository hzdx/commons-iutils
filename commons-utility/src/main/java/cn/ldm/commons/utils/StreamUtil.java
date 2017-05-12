package cn.ldm.commons.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamUtil {

	public static void closeQuietly(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	public static String readString(InputStream in) throws IOException {
		return new String(readBytes(in), "UTF-8");
	}

	/**
	 * {@link org.springframework.util.StreamUtils#copyToByteArray()}
	 */
	public static byte[] readBytes(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
		copy(in, out);
		return out.toByteArray();
	}

	public static int copy(InputStream in, OutputStream out) throws IOException {
		if (in == null || out == null) {
			throw new IOException("InputStream or OutputStream is not specified");
		}
		int byteCount = 0;
		byte[] buffer = new byte[4096];
		int bytesRead = -1;
		while ((bytesRead = in.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
			byteCount += bytesRead;
		}
		out.flush();
		return byteCount;
	}

}
