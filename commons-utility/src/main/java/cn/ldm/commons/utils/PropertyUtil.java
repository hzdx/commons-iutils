package cn.ldm.commons.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertyUtil {

	public static Properties load(String propertiesFile) throws IOException {
		InputStream resourceAsStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(propertiesFile);
		Properties p = null;
		if (resourceAsStream != null) {
			p = new Properties();
			try {
				p.load(resourceAsStream);
			} catch (IOException e) {
				throw e;
			} finally {
				try {
					resourceAsStream.close();
				} catch (IOException e) {
					// Ignored
				}
			}
		}
		return p;
	}

	public static Map<String, String> property2Map(Properties p) {
		if (p == null || p.size() < 1) {
			throw new IllegalArgumentException("properties is null or empty!");
		}

		Map<String, String> map = new HashMap<>();
		Enumeration<?> en = p.propertyNames();
		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			String value = p.getProperty(key);
			map.put(key, value);
		}

		return map;
	}
}
