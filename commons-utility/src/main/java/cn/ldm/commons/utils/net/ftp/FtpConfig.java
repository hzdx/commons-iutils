package cn.ldm.commons.utils.net.ftp;

import java.util.Properties;

public class FtpConfig {
	private String userName;
	private String password;
	private String ip;
	private Integer port;
	private String workDir;
	private String targetFileName;

	public FtpConfig(){}
	
	public FtpConfig(int idx, Properties prop) {
		userName = prop.getProperty("ftp" + idx + ".userName");
		password = prop.getProperty("ftp" + idx + ".password");
		ip = prop.getProperty("ftp" + idx + ".ip");
		port = Integer.parseInt(prop.getProperty("ftp" + idx + ".port"));
		workDir = prop.getProperty("ftp" + idx + ".targetDir");
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		
		this.port = port == null ? 22 : port;//默认22端口
	}

	public String getWorkDir() {
		return workDir;
	}

	public void setWorkDir(String workDir) {
		this.workDir = workDir;
	}

	public String getTargetFileName() {
		return targetFileName;
	}

	public void setTargetFileName(String targetFileName) {
		this.targetFileName = targetFileName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FtpConfig {userName=").append(userName).append(", password=").append(password).append(", ip=")
				.append(ip).append(", port=").append(port).append(", workDir=").append(workDir)
				.append(", targetFileName=").append(targetFileName).append("}");
		return builder.toString();
	}

	

}
