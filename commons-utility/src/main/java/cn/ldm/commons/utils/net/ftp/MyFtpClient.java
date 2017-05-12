package cn.ldm.commons.utils.net.ftp;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通过 url:
 *  "ftp://dxpanalysis:dxpanalysis@123@10.11.27.14/20150302/11/gn/gen_re/210_006_ps_gn_gene_201503021122_53.txt";
 *  这样的路径构建ftp连接.
 *
 */
public class MyFtpClient implements Closeable {
	private Logger log = LoggerFactory.getLogger(getClass());

	private String charset;
	private String targetPath;
	private String hostName = "";

	public MyFtpClient(String targetPath) throws IOException {
		this(targetPath, null);
	}

	public MyFtpClient(String targetPath, String charset) throws IOException {
		this.targetPath = targetPath;
		client = resolveFtp(targetPath);
	}

	private FTPClient client;

	public String[] listName() {
		try {
			return client.listNames();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public FTPFile[] listFiles() {
		try {
			FTPFile[] ffs = client.listFiles();
			return ffs;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public InputStream getIn() throws IOException {
		try {
			String targetFileName = resolveFileName(targetPath);
			InputStream is = client.retrieveFileStream(targetFileName);
			if(is == null)
				throw new IOException("获取ftp读取流失败！");
			return is;
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("获取ftp读取流失败,原因:" + e.getMessage());
		}
	}

	public OutputStream getOut() throws IOException {
		try {
			String targetFileName = resolveFileName(targetPath);
			OutputStream os = client.storeFileStream(targetFileName);
			if(os == null)
				throw new IOException("获取ftp写入流失败！");
			return os;
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("获取ftp写入流失败,原因:" + e.getMessage());
		}
		// return null;
	}

	public String resolveFileName(String targetPath) {
		int len = targetPath.lastIndexOf("/") + 1;
		return targetPath.substring(len);
	}

	public String resolveFilePath(String targetPath) {
		int len = targetPath.lastIndexOf("/");
		return targetPath.substring(0, len);
	}

	public FTPClient resolveFtp(String targetPath) throws IOException {

		FTPClient client = null;
		try {

			PathParser pp = new PathParser(targetPath);
			String user = pp.getUserName();
			String pass = pp.getPassword();
			String host = pp.getHostName();
			Integer port = pp.getPort();
			String dir = pp.getSubDir();
			// String paramStr = res[5];
			Map<String, String> params = pp.getParamMaps();

			client = new FTPClient();
			client.setConnectTimeout(15000);
			client.setDefaultTimeout(15000);
			client.setDataTimeout(15000);

			if (port == null) {
				client.connect(host);
			} else {
				client.connect(host, port);
			}

			int reply = client.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				throw new IllegalArgumentException("cant connect: " + reply);
			}

			if (!client.login(user, pass)) {
				throw new IllegalArgumentException("login failed");
			}

			// this.params = buildParams(paramStr);
			String pasv = params.get("passive");

			// passive mode
			if (pasv != null && pasv.equalsIgnoreCase("true"))
				client.enterLocalPassiveMode();

			String encoding = params.get("encoding");
			if (encoding != null && encoding.trim().length() != 0)
				client.setControlEncoding(encoding);

			client.setFileType(FTPClient.BINARY_FILE_TYPE);

			if(!client.changeWorkingDirectory(dir)){
				throw new IOException("切换ftp目录:["+dir+"] 失败!");
			}
		} catch (Exception e) {
			// e.printStackTrace();
			throw new IOException("FTPClient初始化失败[原因:" + e.getMessage() + "]", e);
		}
		return client;
	}

	public void close() {
			if (client != null) {
				try {
					client.logout();//
					//log.info("{} logout.......",hostName);
				} catch (IOException e) {
					log.warn("{} logout error",hostName, e.getMessage());
				}
				try {
					client.disconnect();
				} catch (IOException e) {
					log.warn("{} disconnect error",hostName, e.getMessage());
				}
			}
		
	}

}
