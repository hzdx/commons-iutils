package cn.ldm.commons.utils.net.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtil {
	private static final Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

	static final int RETRY_TIMES = 5;
	static final int TIME_OUT = 5 * 1000;
	static final String USER_AGENT = "Mozilla/5.0 Chrome/50.0.2661.75";

	static final CloseableHttpClient httpClient = HttpClientHolder.INSTANCE;

	public static String fetchUrl(String url) {
		HttpGet httpget = new HttpGet(url);
		CloseableHttpResponse response = null;
		try {
			log.info("httpClient fetching :" + url);
			response = httpClient.execute(httpget);
			StatusLine status = response.getStatusLine();
			log.info("url: {} ,status: {} ", url, status.toString());

			HttpEntity entity = response.getEntity();
			if (status.getStatusCode() == HttpStatus.SC_OK && entity != null) {
				return EntityUtils.toString(entity, "UTF-8");
			} else {
				log.error("fetch {} can't get correct content.", url);
				return null;
			}

		} catch (IOException e) {
			log.error("fetch {} occur an exception.", url, e);
			return null;
		} finally {
			try {
				if (response != null)
					response.close();
			} catch (IOException e) {
				e.printStackTrace();
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
				.setConnectTimeout(TIME_OUT).build();

		HttpRequestRetryHandler retryHandler = new IRetryHandler(RETRY_TIMES);

		CloseableHttpClient httpClient = HttpClients.custom()//
				.setDefaultRequestConfig(requestConfig)//
				.setRetryHandler(retryHandler)//
				.setUserAgent(USER_AGENT)//
				.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)//关闭ssl主机校验
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
