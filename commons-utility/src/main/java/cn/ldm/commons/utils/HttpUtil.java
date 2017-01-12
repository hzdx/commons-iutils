package cn.ldm.commons.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class HttpUtil {
	private static final String METHOD_GET = "GET";
	private static final String METHOD_POST = "POST";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

	public static String get(String url, Map<String, String> requestHead) throws IOException {
		BufferedReader in = null;
		HttpURLConnection conn = null;
		try {
			URL realUrl = new URL(url);
			conn = (HttpURLConnection) realUrl.openConnection();
			conn.setRequestMethod(METHOD_GET);

			if (requestHead != null && requestHead.size() > 0) {
				for (Entry<String, String> en : requestHead.entrySet()) {
					conn.setRequestProperty(en.getKey(), en.getValue());
				}
			}
			conn.setUseCaches(false);
			conn.setDoOutput(false);

			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			return read(in);
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (in != null)
					in.close();
				if (conn != null)
					conn.disconnect();
			} catch (IOException e) {
			}
		}
	}

	public static String postJson(String url, String json) throws IOException {
		Map<String, String> map = new TreeMap<>();
		map.put("Content-Type", "application/json;charset=UTF-8");
		return post(url, map, json);
	}

	public static String post(String url, Map<String, String> requestHead, String requestBody) throws IOException {
		PrintWriter out = null;
		BufferedReader in = null;
		HttpURLConnection conn = null;
		try {
			URL realUrl = new URL(url);
			conn = (HttpURLConnection) realUrl.openConnection();
			conn.setRequestMethod(METHOD_POST);
			if (requestHead != null && requestHead.size() > 0) {
				for (Entry<String, String> en : requestHead.entrySet()) {
					conn.setRequestProperty(en.getKey(), en.getValue());
				}
			}
			conn.setUseCaches(false);
			conn.setDoOutput(true);

			if (Strings.NotEmpty(requestBody)) {
				out = new PrintWriter(conn.getOutputStream());
				out.print(requestBody);
				out.flush();
			}

			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			return read(in);
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
				if (conn != null)
					conn.disconnect();
			} catch (IOException e) {
			}
		}
	}

	private static String read(BufferedReader reader) throws IOException {
		StringBuilder sb = new StringBuilder();
		String s = null;
		while ((s = reader.readLine()) != null) {
			sb.append(s + LINE_SEPARATOR);
		}
		return sb.toString();
	}

}
