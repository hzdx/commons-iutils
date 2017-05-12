package cn.ldm.commons.utils.net.ftp;

import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpClient {
	public Logger log = LoggerFactory.getLogger(getClass());

	private FtpConfig ftpConfig;
	private final FTPClient _conn;
	// private volatile boolean available;

	public FtpClient(FtpConfig config) {
		this.ftpConfig = config;
		_conn = new FTPClient();
	}

	public void connect() throws IOException {
		_conn.setControlEncoding("UTF-8");
		_conn.setDefaultTimeout(120000);
		_conn.setConnectTimeout(360000);
		_conn.setDataTimeout(120000);
		_conn.connect(ftpConfig.getIp(), ftpConfig.getPort());
		int reply = _conn.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			_conn.disconnect();
			throw new IOException("FTP server connection fail!");
		}
		if (_conn.login(ftpConfig.getUserName(), ftpConfig.getPassword())) {
			_conn.setFileType(FTP.BINARY_FILE_TYPE);
			_conn.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
			_conn.setControlKeepAliveReplyTimeout(30000);
			_conn.setControlKeepAliveTimeout(30);
			_conn.enterLocalPassiveMode();
			_conn.configure(DEFAULT_CONFIG);
			// available = true;
			return;
		} else {
			throw new IOException("FTP server login fail!");
		}
	}

	public void backMainDirectory() throws IOException {
		FtpUtil.ensureCd(_conn, ftpConfig.getWorkDir());
	}

	public void ensureConnect() throws FtpInvalidException {
		try {
			connect();
		} catch (IOException e) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
			}
			reConnect();
		}
	}

	public void checkConnectStatus() throws IOException {
		int reply;
		try {
			reply = _conn.noop();
			// reply = _connection.getReplyCode();
		} catch (IOException e) {
			reply = -1;
		}

		if (!FTPReply.isPositiveCompletion(reply))
			reConnect();
	}

	public void writeFtp(String folderName, String fileName, byte[] data) throws IOException {
		log.info("begin to write :{} , in:{}", fileName, ftpConfig.getIp());
		// FtpUtil.enterDirAndCreateFile(this, folderName, fileName, data);
		FtpUtil.write(this, folderName, fileName, data);
		log.info("write ftp file: {} complete,in :{}", fileName, ftpConfig.getIp());
	}

	public boolean reConnect() throws FtpInvalidException {
		for (int i = 0; i < 3; i++) {// 避免因为网络故障，无限重连ftp
			try {
				try {
					_conn.disconnect();
				} catch (Exception e) {
				}
				connect();
				// available = true;
				log.info("reconnect ftp server :{} success !", ftpConfig.getIp());
				return true;
			} catch (IOException e) {
				log.error("reconnect ftp server :{} failed !", ftpConfig.getIp(), e);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
				}
			}
		}

		// available = false;
		log.error("connect :{} failed over and over again! disable it!", ftpConfig.getIp());
		// return false;
		throw new FtpInvalidException(ftpConfig.getIp() + " connect failed to many times!");
	}

	public void close() {
		if (_conn != null) {
			try {
				_conn.logout();
			} catch (IOException e) {
				log.warn("logout error", e.getMessage());
			}
			try {
				_conn.disconnect();
			} catch (IOException e) {
				log.warn("disconnect error", e.getMessage());
			}
			// _connection = null;
		}
	}

	private static final FTPClientConfig DEFAULT_CONFIG = new FTPClientConfig(FTPClientConfig.SYST_UNIX);

	public FtpConfig getFtpConfig() {
		return ftpConfig;
	}

	public FTPClient getConnection() {
		return _conn;
	}

	public class FtpInvalidException extends IOException {
		private static final long serialVersionUID = -7971776511401790860L;

		public FtpInvalidException() {
			super();
		}

		public FtpInvalidException(String msg) {
			super(msg);
		}
	}

}
