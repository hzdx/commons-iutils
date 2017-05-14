package cn.ldm.commons.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 清除jd-gui反编译工具生成的源代码中包含的注释
 *
 */
public class DecompiledFileCleaner {

	static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
	static final String COMMENT_PREFIX = "/*";
	static final String COMMENT_SUFFIX = "*/";
	static final String COMMENT_ENDFLAG = "/* Location:";
	static final String JAVA_EXTENSION = ".java";
	
	static final String DIR_SUFFIX = ".new";

	private String cleanOneLine(String line) {
		if (line.startsWith(COMMENT_PREFIX))
			return line.substring(line.indexOf(COMMENT_SUFFIX) + 2);
		else
			return line;
	}

	public void cleanJavaFile(String inputFile, String outputFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		StringBuilder newData = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (line.contains(COMMENT_ENDFLAG))
				break;
			newData.append(cleanOneLine(line) + LINE_SEPARATOR);
		}
		reader.close();
		save(newData.toString(), outputFile);

		System.out.println("trans :" + inputFile + " to :" + outputFile);
	}

	public void cleanDirCreateDefault(String inputDir) throws IOException {
		if(inputDir.endsWith("/"))
			inputDir = inputDir.substring(0, inputDir.length() - 1);
		cleanDir(inputDir , inputDir + DIR_SUFFIX);
	}

	public void cleanDir(String inputDir, String outputDir) throws IOException {
		File target = new File(inputDir);
		if (!target.exists() || !target.isDirectory())
			throw new IOException("无效的文件夹 :" + inputDir);
		File[] files = target.listFiles();
		for (File f : files) {
			if (f.isDirectory())
				cleanDir(inputDir + File.separator + f.getName(), outputDir + File.separator + f.getName());
			else if (f.getName().endsWith(JAVA_EXTENSION))
				cleanJavaFile(f.getCanonicalPath(), outputDir + File.separator + f.getName());
			else {
				copyFile(f, new File(outputDir + File.separator + f.getName()));
			}
		}
	}

	private void copyFile(String srcFile, String destFile) throws IOException {
		copyFile(new File(srcFile), new File(destFile));
	}

	private void copyFile(File srcFile, File destFile) throws IOException {
		checkPath(destFile);
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile));
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFile));
		byte[] buff = new byte[512];
		int len = 0;
		while ((len = bis.read(buff)) != -1) {
			bos.write(buff, 0, len);
		}

		bis.close();
		bos.close();

		System.out.println("copy file :" + srcFile.getName() + " to:" + destFile.getName());
	}

	private void checkPath(String fileName) throws IOException {
		checkPath(new File(fileName));
	}

	private void checkPath(File file) throws IOException {
		File parrent = file.getParentFile();
		if (!parrent.exists())
			parrent.mkdirs();
	}

	private void save(String data, String outputFile) throws IOException {
		checkPath(outputFile);
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		writer.write(data);
		writer.close();
	}

	public static void main(String[] args) throws IOException {
		
		DecompiledFileCleaner cleaner = new DecompiledFileCleaner();
		
		cleaner.cleanDirCreateDefault("E:\\tmp\\search-mr-1.0.0-cdh5.6.0-job.src");
		//cleaner.cleanDir("E:\\tmp\\search-mr-1.0.0-cdh5.6.0-job.src","E:\\tmp\\search-mr-1.0.0-cdh5.6.0-job.src.new");
	}

}
