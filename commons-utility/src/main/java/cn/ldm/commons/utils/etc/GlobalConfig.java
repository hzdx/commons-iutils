package cn.ldm.commons.utils.etc;

import java.util.List;
import java.util.Properties;

public class GlobalConfig {
	private static String env;
	private static String projectCode;
	private static Properties prop = new Properties();

	static {
		try {
			prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("global.properties"));
			env = initEnv();
			projectCode = initProjectCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String initEnv() {
		String productionIp = prop.getProperty("production_ip", "");
		String integrationIp = prop.getProperty("integration_ip", "");
		String developmentIp = prop.getProperty("development_ip", "");
		String testIp = prop.getProperty("test_ip", "");
		String previewIp = prop.getProperty("preview_ip", "");
		String tenv = null;
		List localIps = IPUtil.getLocalIp();
		if (containsIp(productionIp, localIps)) {
			tenv = "production";
		} else if (containsIp(integrationIp, localIps)) {
			tenv = "integration";
		} else if (containsIp(testIp, localIps)) {
			tenv = "test";
		} else if (containsIp(developmentIp, localIps)) {
			tenv = "development";
		} else if (containsIp(previewIp, localIps)) {
			tenv = "preview";
		} else {
			tenv = prop.getProperty("env", "");
			if ((tenv.isEmpty()) || ((!tenv.equals("development")) && (!tenv.equals("production"))
					&& (!tenv.equals("preview")) && (!tenv.equals("test")) && (!tenv.equals("integration")))) {
				tenv = "development";
			}
		}
		return tenv;
	}

	private static boolean containsIp(String ipSet, List<String> localIps) {
		if ((ipSet == null) || (ipSet.isEmpty()))
			return false;

		String[] ips = ipSet.split("\\|\\|");
		for (String ip : ips) {
			for (String local : localIps) {
				if (local.startsWith(ip)) {
					return true;
				}
			}
		}
		return false;
	}

	public static String initProjectCode() {
		String projectCode = prop.getProperty("projectCode", "");
		if (projectCode.isEmpty()) {
			projectCode = prop.getProperty("appCode");
		}
		return projectCode;
	}

	public static String getEnv() {
		return env;
	}

	public static String getProjectCode() {
		return projectCode;
	}

	public static String getValue(String key) {
		return prop.getProperty(key);
	}
}
