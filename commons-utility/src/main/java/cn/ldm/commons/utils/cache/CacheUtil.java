package cn.ldm.commons.utils.cache;

public class CacheUtil {
	public static String getCacheKey(String key) {
		return getCacheKey(null, key);
	}

	public static String getCacheKey(String namespace, String key) {
		if ((namespace == null) || (namespace.equals(""))) {
			return key;
		}
		return namespace + "_" + key;
	}
}
