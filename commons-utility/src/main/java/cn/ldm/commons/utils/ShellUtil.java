package cn.ldm.commons.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShellUtil {
	private static Logger LOG = LoggerFactory.getLogger(ShellUtil.class);

	public static void callShell(String cmd, String[] params) throws Exception {
		Process p0 = Runtime.getRuntime().exec(cmd, params);
		// 读取标准输出流
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(p0.getInputStream()));
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			LOG.info("inputStream:" + line);
		}
		// 读取标准错误流
		BufferedReader brError = new BufferedReader(new InputStreamReader(p0.getErrorStream()));
		String errline = null;
		while ((errline = brError.readLine()) != null) {
			LOG.info("errorStream:" + errline);
		}

		int exitValue = p0.waitFor();// 如果正常结束，Process的waitFor()方法返回0

		if (0 != exitValue) {
			LOG.error("call shell failed. error code is :{}", exitValue);
			throw new Exception("call shell failed. error code is :" + exitValue);
		}

	}

	public static void main(String[] args) throws Exception {

	}

}
