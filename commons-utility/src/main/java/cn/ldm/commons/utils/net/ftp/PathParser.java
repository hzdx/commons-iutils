package cn.ldm.commons.utils.net.ftp;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathParser {

	//
	private String PRN_LOCAL = "$/(\\S*)";
	//
	private String PRN_WHOLE = "(\\w{1,6})\\[?([^\\[\\]]*)\\]?://(\\S+):(\\S+)@(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d{1,5})/(\\S*)";
	//
	private String PRN_NO_PORT = "(\\w{1,6})\\[?([^\\[\\]]*)\\]?://(\\S+):(\\S+)@(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})/(\\S*)";
	//
	private String PRN_NO_USER = "(\\w{1,6})\\[?([^\\[\\]]*)\\]?://(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d{1,5})/(\\S*)";
	//
	private String PRN_NO_USER_N_PORT = "(\\w{1,6})\\[?([^\\[\\]]*)\\]?://(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})/(\\S*)";

	private String targetPath;

	public PathParser(String targetPath) {
		this.targetPath = targetPath;
		this.paramMaps = new HashMap<String, String>();
		parse();
		// System.out.println("" + this);
	}

	private String connectString;

	private String protoType;
	private String paramsString;
	private String hostName;
	private Integer port;
	private String userName;
	private String password;
	private String subPath;
	private String subDir;
	private String fileName;

	private Map<String, String> paramMaps;

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[protoType:" + protoType).append("\n");
		sb.append("paramsString:" + paramsString).append("\n");
		sb.append("hostName:" + hostName).append("\n");
		sb.append("port:" + port).append("\n");
		sb.append("userName:" + userName).append("\n");
		sb.append("password:" + password).append("\n");
		sb.append("supPath:" + subPath).append("\n");
		sb.append("subDir:" + subDir).append("\n");
		sb.append("fileName:" + fileName).append("\n");
		sb.append("paramMaps:").append("\n");
		for (Entry<String, String> entry : paramMaps.entrySet()) {
			String name = entry.getKey();
			String value = entry.getValue();
			sb.append("name: " + name + " ,value: " + value).append("\n");
		}
		sb.append("]");
		return sb.toString();
	}

	public FtpConfig toFtpConfig() {
		if (protoType.contains("ftp")) { // ftp or sftp
			FtpConfig config = new FtpConfig();
			config.setIp(hostName);
			config.setPort(port);
			config.setUserName(userName);
			config.setPassword(password);
			config.setWorkDir(subDir);// TODO 绝对路径和相对路径的区分
			config.setTargetFileName(fileName);
			return config;
		} else
			return null;

	}

	public static void main(String[] args) {
		String connectString = "sftp[pass:true,aa1:sdb,aa2:sdc,aa3:sdd]://asdf:aa@#$@10.221.247.27:2342/q/a/b/bb/c";
		// String connectString =
		// "ftp://dxpanalysis:dxpanalysis@123@10.11.27.14/20150302/11/gn/gen_re/210_006_ps_gn_gene_201503021122_53.txt";

		// String connectString = "/asdf/tt";
		// PathParser cs = new PathParser(connectString);
		// System.out.println("生成结果1:" + cs);

		String paramArrayOfString = "ftp[pass:true,aa1:sdb,aa2:sdc,aa3:sdd]://asdf:aa@#$@10.221.247.27/q/a/b/bb/c";
		PathParser pp = new PathParser(paramArrayOfString);
		System.out.println("生成结果:" + pp);

		System.out.println(pp.toFtpConfig());
	}

	public String getConnectString() {
		if (connectString == null)
			connectString = buildConnectString();
		return connectString;
	}

	private void parse() {

		if (parseLocalPattern()) {

		} else if (parseWholePattern()) {

		} else if (parseNoPortPattern()) {

		} else if (parseNoUserPattern()) {

		} else if (parseNoPortNUserPattern()) {

		}

		parseParamsPattern();
		parseSubPath();
	}

	private void parseSubPath() {
		if (subPath == null)
			return;

		int len = subPath.lastIndexOf("/");
		if (len == -1) {
			subDir = "";
			fileName = subPath;
		} else {
			subDir = subPath.substring(0, len + 1);
			fileName = subPath.substring(len + 1);
		}
	}

	private String buildConnectString() {
		StringBuffer sb = new StringBuffer();
		if (protoType != null) {
			sb.append(protoType);
			sb.append("://");
		}
		if (userName != null && password != null) {
			sb.append(userName);
			sb.append(":");
			sb.append(password);
			sb.append("@");
		}
		if (hostName != null)
			sb.append(hostName);
		if (port != null) {
			sb.append(":");
			sb.append(port);
		}
		sb.append("/");
		return sb.toString();
	}

	private boolean parseLocalPattern() {
		int i = 1;
		Pattern p = Pattern.compile(PRN_LOCAL);
		Matcher m = p.matcher(targetPath);
		if (m.find()) {
			this.subPath = m.group(i++);
			return true;
		}
		return false;
	}

	private boolean parseWholePattern() {
		int i = 1;
		Pattern p = Pattern.compile(PRN_WHOLE);
		Matcher m = p.matcher(targetPath);
		if (m.find()) {
			this.protoType = m.group(i++);
			this.paramsString = m.group(i++);
			this.userName = m.group(i++);
			this.password = m.group(i++);
			this.hostName = m.group(i++);
			this.port = Integer.parseInt(m.group(i++));
			this.subPath = m.group(i++);
			return true;
		}
		return false;
	}

	private boolean parseNoPortPattern() {
		int i = 1;
		Pattern p = Pattern.compile(PRN_NO_PORT);
		Matcher m = p.matcher(targetPath);
		if (m.find()) {
			this.protoType = m.group(i++);
			this.paramsString = m.group(i++);
			this.userName = m.group(i++);
			this.password = m.group(i++);
			this.hostName = m.group(i++);
			this.subPath = m.group(i++);
			return true;
		}
		return false;
	}

	private boolean parseNoUserPattern() {
		int i = 1;
		Pattern p = Pattern.compile(PRN_NO_USER);
		Matcher m = p.matcher(targetPath);
		if (m.find()) {
			this.protoType = m.group(i++);
			this.paramsString = m.group(i++);
			this.hostName = m.group(i++);
			this.port = Integer.parseInt(m.group(i++));
			this.subPath = m.group(i++);
			return true;
		}
		return false;
	}

	private boolean parseNoPortNUserPattern() {
		int i = 1;
		Pattern p = Pattern.compile(PRN_NO_USER_N_PORT);
		Matcher m = p.matcher(targetPath);
		if (m.find()) {
			this.protoType = m.group(i++);
			this.paramsString = m.group(i++);
			this.hostName = m.group(i++);
			this.subPath = m.group(i++);
			return true;
		}
		return false;
	}

	private boolean parseParamsPattern() {

		if (paramsString == null || paramsString.trim().length() == 0)
			return false;

		String[] p1s = paramsString.split(",");
		for (String p1 : p1s) {
			String[] p2s = p1.split(":");
			String name = p2s[0];
			String val = p2s[1];
			paramMaps.put(name, val);
		}
		return true;
	}

	public String getProtoType() {
		if (protoType == null)
			protoType = "local";
		return protoType;
	}

	public void setProtoType(String protoType) {
		this.protoType = protoType;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSupPath() {
		return subPath;
	}

	public void setSupPath(String supPath) {
		this.subPath = supPath;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public String getSubPath() {
		return subPath;
	}

	public String getSubDir() {
		return subDir;
	}

	public String getFileName() {
		return fileName;
	}

	public String getParamsString() {
		return paramsString;
	}

	public void setParamsString(String paramsString) {
		this.paramsString = paramsString;
	}

	public Map<String, String> getParamMaps() {
		return paramMaps;
	}

	public void setParamMaps(Map<String, String> paramMaps) {
		this.paramMaps = paramMaps;
	}

}
