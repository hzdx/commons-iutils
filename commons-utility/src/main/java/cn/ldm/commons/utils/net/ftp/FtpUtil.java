package cn.ldm.commons.utils.net.ftp;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpUtil {
	public static final Logger log = LoggerFactory.getLogger(FtpUtil.class);
	private static final int cmdRetryTimes = 15;

	public static void main(String[] args) throws Exception {

	}

	public static void createRemoteFile(FTPClient ftpConnection, String fileName, byte[] data) throws IOException {
		try {
			ByteArrayInputStream byteArrIs = new ByteArrayInputStream(data);
			boolean result = ftpConnection.storeFile(fileName, byteArrIs);
			byteArrIs.close();
			if (!result)
				throw new IOException("ftp create file unCompleted!");
		} catch (IOException e) {
			log.error("createRemoteFile {} error", fileName, e);
			throw e;
		}
	}

	@Deprecated
	public static String estimateAndCreateFile(FTPClient ftpConnection, String fileNameHead, byte[] data)
			throws IOException {
		try {
			String[] allFileInDir = ensureLs(ftpConnection);
			String firstFileIn5Min = fileNameHead + "00" + ".csv";
			if (arrayContains(allFileInDir, firstFileIn5Min)) {// 判断是否包含第一个文件
				createRemoteFile(ftpConnection, firstFileIn5Min, data);
				return firstFileIn5Min;
			}
			int maxFileNameIdx = 0;
			for (String fileName : allFileInDir) {
				if (fileName.startsWith(fileNameHead)) {
					int underlineIdx = fileName.lastIndexOf("_");
					int dotIdx = fileName.lastIndexOf(".");
					String indexStr = fileName.substring(underlineIdx + 1, dotIdx);
					int index = Integer.parseInt(indexStr);
					if (index > maxFileNameIdx)
						maxFileNameIdx = index;
				}
			}
			int currentIdx = maxFileNameIdx + 1;
			String currentIdxStr = currentIdx + "";
			if (currentIdxStr.length() == 1)
				currentIdxStr = "0" + currentIdxStr;
			String currentFileName = fileNameHead + currentIdxStr + ".csv";
			createRemoteFile(ftpConnection, currentFileName, data);
			return currentFileName;
		} catch (IOException e) {
			log.error("estimateAndCreateFile {} error", fileNameHead, e);
			throw e;
		}
	}

	// 不需要建目录
	public static void write(FtpClient ftpClient, String folderName, String fileName, byte[] data) throws IOException {
		try {
			ftpClient.checkConnectStatus();
			FTPClient conn = ftpClient.getConnection();
			String fullLocalPath = ftpClient.getFtpConfig().getWorkDir() + "/" + folderName + "/" + fileName;
			createRemoteFile(conn, fullLocalPath, data);
		} catch (IOException e) {
			throw e;
		}

	}

	// 保证文件夹存在,并进入(不存在创建),观察文件夹内的文件名称确定目标文件名称，并创建文件
	public static void enterDirAndCreateFile(FtpClient ftpClient, String folderName, String fileName, byte[] data)
			throws IOException {
		try {
			ftpClient.checkConnectStatus();
			FTPClient connection = ftpClient.getConnection();
			String currentDir = ensurePwd(connection);
			if (currentDir != null && currentDir.endsWith(folderName)) {
				// 当前工作目录就是目标文件夹
				// return estimateAndCreateFile(connection, fileNameHead, data);
				createRemoteFile(connection, fileName, data);
				// return fileName;
			}
			// 1.回到默认文件夹
			ftpClient.backMainDirectory();

			String[] dirs = folderName.split("/");
			// 2.搜索所有文件夹，查找是否目的文件夹已存在
			for (int i = 0, len = dirs.length; i < len; i++) {
				String[] currentDirs = ensureLs(connection);
				if (arrayContains(currentDirs, dirs[i])) {// 当前目录内存在目的文件夹
					ensureCd(connection, dirs[i]);
				} else {
					// 3.不存在就创建，并进入目的文件夹
					ensureMakeDir(connection, dirs[i]);
					ensureCd(connection, dirs[i]);
				}
			}
			// return estimateAndCreateFile(connection, fileNameHead, data);
			createRemoteFile(connection, fileName, data);
			// return fileName;

		} catch (IOException e) {
			log.error("enterDirAndCreateFile {} error", folderName, e);
			// return
			enterDirAndCreateFile(ftpClient, folderName, fileName, data);
		}

	}

	public static String[] ensureLs(FTPClient ftpConnection) throws IOException {
		String[] allFileInDir = null;
		for (int i = 0; i < cmdRetryTimes; i++) {
			allFileInDir = ftpConnection.listNames();
			if (allFileInDir != null)
				return allFileInDir;
		}
		throw new IOException("ftp can't list file...");
	}

	public static String ensurePwd(FTPClient ftpConnection) throws IOException {
		String nowDir = null;
		for (int i = 0; i < cmdRetryTimes; i++) {
			nowDir = ftpConnection.printWorkingDirectory();
			if (nowDir != null)
				return nowDir;
		}
		throw new IOException("ftp can't print current workingDirectory...");
	}

	public static boolean ensureCd(FTPClient ftpConnection, String dir) throws IOException {
		for (int i = 0; i < cmdRetryTimes; i++) {
			if (ftpConnection.changeWorkingDirectory(dir))
				return true;
		}
		throw new IOException("ftp can't enter dir: " + dir + ".");
	}

	public static boolean ensureMakeDir(FTPClient ftpConnection, String dirName) throws IOException {
		for (int i = 0; i < cmdRetryTimes; i++) {
			if (ftpConnection.makeDirectory(dirName))
				return true;
		}
		throw new IOException("ftp can't make dir: " + dirName + ".");
	}

	public static boolean arrayContains(String[] arr, String ele) {
		if (arr == null || ele == null)
			return false;
		for (int i = 0, len = arr.length; i < len; i++) {
			if (arr[i].equals(ele))
				return true;
		}
		return false;
	}

}
