package cn.ldm.commons.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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

	public static Map<String, String> propertyToMap(Properties p) {
		if (p == null || p.size() < 1) {
			throw new IllegalArgumentException("properties is null or empty!");
		}

		Map<String,String> map = new HashMap<>();
		Set<String> keys = p.stringPropertyNames();
		for(String key : keys){
			map.put(key, p.getProperty(key));
		}

		return map;
	}
}
