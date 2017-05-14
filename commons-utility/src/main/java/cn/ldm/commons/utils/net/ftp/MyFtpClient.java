package cn.ldm.commons.utils.net.ftp;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通过 url:
 * "ftp://dxpanalysis:dxpanalysis@123@10.11.27.14/20150302/11/gn/gen_re/210_006_ps_gn_gene_201503021122_53.txt";
 * 这样的路径构建ftp连接.
 *
 */
public class MyFtpClient implements Closeable {
	private Logger LOG = LoggerFactory.getLogger(getClass());

	private String targetPath;

	private FtpConfig ftpConfig;

	private FTPClient client;

	public MyFtpClient(String fullPath) throws IOException {
		this.targetPath = fullPath;
		resolve(this.targetPath);
		connect();
	}

	public String[] listName() throws IOException {
		return client.listNames();

	}

	public FTPFile[] listFiles() throws IOException {
		FTPFile[] ffs = client.listFiles();
		return ffs;
	}

	public InputStream getIn() throws IOException {
		try {
			InputStream is = client.retrieveFileStream(ftpConfig.getTargetFileName());
			if (is == null)
				throw new IOException("获取ftp读取流失败！");// 当期目录不存在此文件
			return is;
		} catch (IOException e) {
			LOG.error("get ftp InputStream error", e);
			throw e;
		}
	}

	public OutputStream getOut() throws IOException {
		try {
			OutputStream os = client.storeFileStream(ftpConfig.getTargetFileName());
			if (os == null)
				throw new IOException("获取ftp写入流失败！");
			return os;
		} catch (IOException e) {
			LOG.error("get ftp OutputStream error", e);
			throw e;
		}
	}

	public void resolve(String fullPath) {
		PathParser pp = new PathParser(targetPath);
		ftpConfig = pp.toFtpConfig();
		// LOG.info("resolve ftpConfig: {}", ftpConfig);
	}

	public void connect() throws IOException {
		client = new FTPClient();
		client.setConnectTimeout(15000);
		client.setDefaultTimeout(15000);
		client.setDataTimeout(15000);

		if (ftpConfig.getPort() == null) {
			client.connect(ftpConfig.getIp());
		} else {
			client.connect(ftpConfig.getIp(), ftpConfig.getPort());
		}

		int reply = client.getReplyCode();

		if (!FTPReply.isPositiveCompletion(reply)) {
			throw new IOException("can't connect ftp, reply : " + reply);
		}

		if (!client.login(ftpConfig.getUserName(), ftpConfig.getPassword())) {
			throw new IOException("login failed");
		}

		client.enterLocalPassiveMode();

		client.setFileType(FTPClient.BINARY_FILE_TYPE);

		if (!client.changeWorkingDirectory(ftpConfig.getWorkDir())) {
			throw new IOException("切换ftp目录:[" + ftpConfig.getWorkDir() + "] 失败!");
		}

		// LOG.info("connect to ftp server:[{}] success.", ftpConfig.getIp());
	}

	public void close() {
		if (client != null) {
			try {
				client.logout();//
			} catch (IOException e) {
				LOG.warn("{} logout error", ftpConfig, e.getMessage());
			}
			try {
				client.disconnect();
			} catch (IOException e) {
				LOG.warn("{} disconnect error", ftpConfig, e.getMessage());
			}
		}

	}

}
