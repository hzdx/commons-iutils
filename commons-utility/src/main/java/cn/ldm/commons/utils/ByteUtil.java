package cn.ldm.commons.utils;

import java.math.BigInteger;

public class ByteUtil {
	public static final char[] HexDict = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f' };

	public static char numToChar(int n) {
		if (n > 15)
			throw new IllegalArgumentException("只能把0-15的数字输出成字符:" + n);
		return HexDict[n];
	}

	public static String bytesToHexString(byte[] src) {
		if (src == null || src.length == 0) {
			return "null";
		}
		int bytelen = src.length;
		char[] chars = new char[bytelen << 1];
		for (int i = 0; i < bytelen; i++) {
			chars[2 * i] = HexDict[(src[i] >> 4) & 0x0f];
			chars[2 * i + 1] = HexDict[src[i] & 0x0f];
		}
		return new String(chars);
	}

	// 0~15 -1
	public static int hexToNum(char c) {
		if (c >= '0' && c <= '9')
			return c - '0';
		else if (c >= 'a' && c <= 'f')
			return c - 'a' + 10;
		else if ('A' <= c && c <= 'F')
			return c - 'A' + 10;
		throw new IllegalArgumentException("不合法的16进制字符:" + c);
	}

	public static byte[] hexStrToBytes(String hexStr) {
		if (hexStr == null || hexStr.trim().length() == 0)
			return null;
		if (hexStr.startsWith("0x"))
			hexStr = hexStr.substring(2);
		char[] chars = hexStr.toCharArray();
		int byteNum = chars.length >> 1;
		byte[] bytes = new byte[byteNum];
		for (int i = 0; i < byteNum; i++) {
			int high4bit = hexToNum(chars[2 * i]);
			int low4bit = hexToNum(chars[2 * i + 1]);
			bytes[i] = (byte) ((high4bit << 4) + low4bit);
		}
		return bytes;
	}

	public static byte[] joinByteArray(byte[]... byteArrs) {
		int totalLen = 0;
		for (byte[] byteArr : byteArrs)
			totalLen = totalLen + byteArr.length;
		byte[] newbyte = new byte[totalLen];
		int copiedlen = 0;
		for (int i = 0; i < byteArrs.length; i++) {
			int currentArrayLength = byteArrs[i].length;
			System.arraycopy(byteArrs[i], 0, newbyte, copiedlen, currentArrayLength);
			copiedlen = copiedlen + currentArrayLength;
		}
		return newbyte;
	}

	/**
	 * 获取子数组
	 */
	public static byte[] getSubBytes(byte[] bytes, int startIndex, int endIndex) {
		int len = endIndex - startIndex;
		byte[] subByte = new byte[len];
		System.arraycopy(bytes, startIndex, subByte, 0, len);
		return subByte;
	}

	/**
	 * 字节转数字 返回String类型,可以任意大，无符号
	 *
	 */
	public static String byteToNum(byte[] bytes) {
		int len = bytes.length;
		// 对于8字节以上数组 超出long数值上限的处理
		if (len >= 8) {
			BigInteger bigInt = new BigInteger(bytes);
			return bigInt.toString();
		}
		long result = 0;
		for (int i = 0; i < len; i++) {
			if (i != 0)
				result <<= 8;
			result = result + (bytes[i] & 0xff);
		}
		return String.valueOf(result);
	}

	public static byte[] numToByte(long num, int byteNum) {
		byte[] by = new byte[byteNum];
		for (int i = 0; i < byteNum; i++) {
			by[i] = (byte) (num >> 8 * (byteNum - 1 - i));
		}
		return by;
	}

	public static byte[] intToByte(int num) {
		return numToByte(num, 4);
	}

	public static byte[] shortToByte(short num) {
		return numToByte(num, 2);
	}

	/**
	 * 判断字节数组是否是全0或全f
	 */
	public static boolean isDefalutVal(int i, byte[] bytes) {
		if (i == 0) {
			for (int n = 0; n < bytes.length; n++) {
				if (bytes[n] != (byte) 0)
					return false;
			}
			return true;
		} else if (i == -1) {
			for (int n = 0; n < bytes.length; n++) {
				if (bytes[n] != (byte) 0xff)
					return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * byte -> ip
	 * 
	 */
	public static String getIpStrFromByte(byte[] bytes) throws IllegalArgumentException {

		StringBuffer sb = new StringBuffer();
		int len = bytes.length;
		if (len != 16)
			throw new IllegalArgumentException("只能将16个字节长度的数据转换为ip地址！");
		if (isDefalutVal(-1, getSubBytes(bytes, 0, 12))) {
			// 如果是ipv4 前12字节全为0xff
			for (int i = 12; i < len; i++) {
				short n = (short) (bytes[i] & 0x00ff);
				sb.append(n + ".");
			}
			String ipStr = sb.toString();
			return ipStr.substring(0, ipStr.length() - 1);
			// 不包含最后一个位置的"".",或者":"
		} else {// ipv6
			for (int i = 0; i < len / 2; i++) {
				String str = bytesToHexString(new byte[] { bytes[2 * i], bytes[2 * i + 1] });
				if ("0000".equals(str)) {
					sb.append(":");
				} else {
					sb.append(str.replaceAll("^0{1,3}", "") + ":");// 去掉前面的0
				}
			}
			String result = sb.toString();
			return result.substring(0, result.length() - 1)// 去掉最后一个：
					.replaceAll(":{3,}", "::");// 将多个：替换成两个

		}

	}

}
