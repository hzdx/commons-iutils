package cn.ldm.commons.utils.net.ftp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpPathHelper {

	private static Logger log = LoggerFactory.getLogger(FtpPathHelper.class);

	public static String parseConnectString(String targetPath) {

		int len = targetPath.indexOf("://");
		if (len == -1) {
			log.info("目标路径[" + targetPath + "]格式不正确.");
			return null;
		}

		String stp = targetPath.substring(len + 3);
		int slen = stp.indexOf("/");
		if (slen == -1)
			return targetPath;

		String connectString = targetPath.substring(0, len + 3 + slen);
		//log.info("连接字符串[" + connectString + "]");
		return connectString;

	}

	public static void main(String[] args) {

		String path = "ftp[passive:true]://ipms:IPms!23$@10.11.40.157/xdr/lte/lte_s1u/lte_s1u_email/20160412/17/105_201604121710_c_02.CSV";
		System.out.println("path=" + parseConnectString(path));
		//path=ftp[passive:true]://ipms:IPms!23$@10.11.40.157
	}

}
