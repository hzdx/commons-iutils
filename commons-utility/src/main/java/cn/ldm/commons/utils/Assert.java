package cn.ldm.commons.utils;

public class Assert {

	public static boolean isEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}

	public static boolean NotEmpty(String s) {
		return !isEmpty(s);
	}

}
