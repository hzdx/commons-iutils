package cn.ldm.commons.utils;

public class Strings {

	public static final String CHARSET_UTF8 = "utf-8";

	public static boolean isEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}

	public static boolean NotEmpty(String s) {
		return !isEmpty(s);
	}

}
