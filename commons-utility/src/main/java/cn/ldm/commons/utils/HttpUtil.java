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

import cn.ldm.commons.Constants;

public class HttpUtil {
	private static final String METHOD_GET = "GET";
	private static final String METHOD_POST = "POST";
	
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36";
	
	public static String get(String url, Map<String, String> requestHead) throws IOException {
		BufferedReader in = null;
		HttpURLConnection conn = null;
		try {
			URL realUrl = new URL(url);
			conn = (HttpURLConnection) realUrl.openConnection();
			conn.setRequestMethod(METHOD_GET);

			conn.setRequestProperty("User-Agent",USER_AGENT);
			if (requestHead != null && requestHead.size() > 0) {
				for (Entry<String, String> en : requestHead.entrySet()) {
					conn.setRequestProperty(en.getKey(), en.getValue());
				}
			}
			conn.setUseCaches(false);
			conn.setDoOutput(false);

			in = new BufferedReader(new InputStreamReader(conn.getInputStream(),Constants.UTF8));
			String response = read(in);
			return response;
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
			
			conn.setRequestProperty("User-Agent",USER_AGENT);
			if (requestHead != null && requestHead.size() > 0) {
				for (Entry<String, String> en : requestHead.entrySet()) {
					conn.setRequestProperty(en.getKey(), en.getValue());
				}
			}
			conn.setUseCaches(false);
			conn.setDoOutput(true);

			if (Assert.NotEmpty(requestBody)) {
				out = new PrintWriter(conn.getOutputStream());
				out.print(requestBody);
				out.flush();
			}
			
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String response = read(in);
			return response;
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
			sb.append(s + Constants.LINE_SEPARATOR);
		}
		return sb.toString();
	}

}
