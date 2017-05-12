package cn.ldm.commons.utils.net.http;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;

public class IRetryHandler implements HttpRequestRetryHandler {
	private int retryTimes;

	public IRetryHandler(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	@Override
	public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
		if (executionCount >= retryTimes) {
			// Do not retry if over max retry count
			return false;
		}
		if (exception instanceof UnknownHostException) {
			// Unknown host
			return false;
		}
		return true;
	}
}
