package cn.ldm.commons.utils.net.ftp;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

public class SftpClient implements Closeable {
	private Logger log = LoggerFactory.getLogger(getClass());

	private String host;
	private String username;
	private String password;
	private Integer port;
	private String workDir;
	private String fileName;

	private Session sshSession;
	private ChannelSftp sftp;

	// "sftp[pass:true,aa1:sdb,aa2:sdc,aa3:sdd]://asdf:aa@#$@10.221.247.27:2342/q/a/b/bb/c";

	public SftpClient(String targetUrl) throws Exception {
		try {
			PathParser parser = new PathParser(targetUrl);
			host = parser.getHostName();
			port = parser.getPort();
			if (port == null)
				port = 22;

			username = parser.getUserName();
			password = parser.getPassword();

			String dir = parser.getSubDir();
			workDir = dir.indexOf("/") != 0 ? "/" + dir : dir;

			fileName = parser.getFileName();

			connect();
		} catch (Exception e) {
			log.error("connect sftp:{} error", targetUrl, e);
			throw e;
		}
	}

	public void connect() throws Exception {
		JSch jsch = new JSch();
		sshSession = jsch.getSession(username, host, port);
		sshSession.setPassword(password);

		Properties sshConfig = new Properties();
		sshConfig.put("StrictHostKeyChecking", "no");
		sshSession.setConfig(sshConfig);
		sshSession.connect();// 不指定超时时间，仍会报超时异常

		Channel channel = sshSession.openChannel("sftp");
		channel.connect();
		sftp = (ChannelSftp) channel;

		sftp.cd(workDir);

		log.info("Connected to sftp server:{} success!", host);
	}

	@SuppressWarnings("unchecked")
	public List<LsEntry> list() {
		try {
			return sftp.ls(workDir);
		} catch (SftpException e) {
			log.error("sftp list entry error", e);
			return null;
		}
	}

	public InputStream getIn() throws Exception {
		try {
			if (fileName == null)
				throw new NullPointerException("fileName is null!");
			InputStream is = sftp.get(fileName);
			if (is == null)
				throw new IOException("获取读取流为空!");

			return is;
		} catch (Exception e) {
			// e.printStackTrace();
			log.error("从:{} 获取sftp读取流失败", host, e);
			throw new IOException("获取sftp读取流失败,原因:" + e.getMessage());
		}
	}

	public void close() {
		try {
			if (sftp != null) {
				sftp.disconnect();
			}
			if (sshSession != null) {
				sshSession.disconnect();
			}
		} catch (Exception e) {
			log.error("sftp close error", e);
		}
	}

	@Override
	public String toString() {
		return "SftpClient [host=" + host + ", port=" + port + ", username=" + username + ", password=" + password
				+ ", workDir=" + workDir + ", fileName=" + fileName + "]";
	}

	public static void main(String[] args) throws Exception {
		// String url =
		// "sftp[pass:true,aa1:sdb,aa2:sdc,aa3:sdd]://asdf:aa@#$@10.221.247.27:2342/q/a/b/bb/c";
		// String url =
		// "ftp://dxpanalysis:dxpanalysis@123@10.11.27.19/20161108/10/gn/dns/";
		// String url =
		// "sftp://ipms:SHms!50*@10.221.247.50/home/ipms/shpm-service/shpm-schedule/logs/debug.log.3";
		String url = "sftp://nettest:nettest!^*@10.11.60.159/data/Flux_00_201611132150_10.11.60.159_000.AVL";
		if (args != null && args[0] != null)
			url = args[0];
		SftpClient client = new SftpClient(url);
		System.out.println(client);

		List<LsEntry> v = client.list();
		System.out.println("文件列表：==================================");
		for (LsEntry entry : v) {
			String fileName = entry.getFilename();
			SftpATTRS attr = entry.getAttrs();
			System.out.println(fileName + ".." + attr.getSize() + ".." + new Date(((long) attr.getMTime()) * 1000L));
		}

		System.out.println("文件内容：===================================");
		InputStream is = client.getIn();
		byte[] buf = new byte[512];
		StringBuilder sb = new StringBuilder();
		int len = 0;
		while ((len = is.read(buf)) > 0) {
			sb.append(new String(buf, 0, len, "UTF-8"));

		}

		System.out.println(sb.toString());

		client.close();
		// 测试程序 java -Djava.ext.dirs=../lib:${JAVA_HOME}/jre/lib/ext -classpath
		// shpm-task-v1.1-20161114.jar
		// com.eastcom.pm.manager.common.io.SftpClient
		// 'sftp://nettest:nettest!^*@10.11.60.159/data/Flux_00_201611132150_10.11.60.159_000.AVL'

	}

}
