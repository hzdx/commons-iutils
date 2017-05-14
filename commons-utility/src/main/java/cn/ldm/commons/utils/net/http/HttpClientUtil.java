package cn.ldm.commons.utils.net.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtil {
	private static final Logger LOG = LoggerFactory.getLogger(HttpClientUtil.class);

	static final int RETRY_TIMES = 5;
	static final int TIME_OUT = 5 * 1000;
	static final String USER_AGENT = "Mozilla/5.0 Chrome/50.0.2661.75";

	static final CloseableHttpClient httpClient = HttpClientHolder.INSTANCE;

	public static String get(String url) {
		HttpGet httpget = new HttpGet(url);
		CloseableHttpResponse response = null;
		try {
			LOG.info("http fetching :" + url);
			response = httpClient.execute(httpget);
			StatusLine status = response.getStatusLine();
			LOG.info("url: {} ,status: {} ", url, status.toString());

			HttpEntity entity = response.getEntity();
			if (status.getStatusCode() == HttpStatus.SC_OK && entity != null) {
				return EntityUtils.toString(entity, "UTF-8");
			} else {
				LOG.error("fetch {} can't get correct content.", url);
				return null;
			}

		} catch (IOException e) {
			LOG.error("fetch {} occur an exception.", url, e);
			return null;
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
			}
		}
	}

	public static String post(String url, Map<String, String> keyValues) {
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpResponse response = null;
		try {
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			if (keyValues != null && keyValues.size() > 0) {
				for (Entry<String, String> entry : keyValues.entrySet()) {
					nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			response = httpClient.execute(httpPost);

			HttpEntity entity = response.getEntity();
			return EntityUtils.toString(entity, "UTF-8");
		} catch (IOException e) {
			LOG.error("post to url: [{}] error", url, e);
			return null;
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
			}
		}

	}

	private static class HttpClientHolder {
		private static final CloseableHttpClient INSTANCE = createHttpClient();
	}

	private static CloseableHttpClient createHttpClient() {
		RequestConfig requestConfig = RequestConfig.custom()//
				.setSocketTimeout(TIME_OUT)//
				.setConnectionRequestTimeout(TIME_OUT)//
				// .setProxy(new HttpHost("proxyHost",8090))// 设置代理服务器地址和端口
				.setConnectTimeout(TIME_OUT).build();

		HttpRequestRetryHandler retryHandler = new IRetryHandler(RETRY_TIMES);

		CloseableHttpClient httpClient = HttpClients.custom()//
				.setDefaultRequestConfig(requestConfig)//
				.setRetryHandler(retryHandler)//
				.setUserAgent(USER_AGENT)//
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)// 关闭ssl主机校验
				.build();

		return httpClient;
	}

	public static HttpClient getInstance() {
		return httpClient;
	}

	public static void close() throws IOException {
		if (httpClient != null)
			httpClient.close();
	}
}
