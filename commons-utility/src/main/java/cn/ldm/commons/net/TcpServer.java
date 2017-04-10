package cn.ldm.commons.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TcpServer {
	public static void main(String[] args) throws IOException {
		new TcpServer().start(8081);
	}

	public void start(int port) throws IOException {
		ServerSocket listener = new ServerSocket(port);
		ExecutorService executor = new ThreadPoolExecutor(4, 16, 30L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(), new ThreadPoolExecutor.DiscardPolicy());
		// 固定了线程数和缓存队列大小的线程池，多余的请求采用丢弃（DiscardPolicy）策略

		try {
			while (true) {
				Socket socket = listener.accept();
				try {
					executor.submit(new HandleRequestRunnable(socket));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} finally {
			listener.close();
		}

	}

	final static String RESPONSE_STR = "HTTP/1.0 200 OK\r\n" + "Content-type: text/plain\r\n" + "\r\n"
			+ "Hello World\r\n";

	final static int bufferSize = 1024 * 1024 * 8;

	public void handleRequest(Socket socket) throws IOException {
		// Read the input stream, and return "200 OK"
		try {
			InputStream in = socket.getInputStream();
			
			//System.out.println(Thread.currentThread().getName() +" availble "+in.available());

			System.out.print(Thread.currentThread().getName()+":\r\n" + new String(readBytes(in)));
			//System.out.print(Thread.currentThread().getName()+":\r\n" + new String(readBytes(in)));

			OutputStream out = socket.getOutputStream();
			out.write(RESPONSE_STR.getBytes(StandardCharsets.UTF_8));
			out.flush();
		}catch(Throwable e){
			e.printStackTrace();
		} finally {
			socket.close();
		}
	}

	private byte[] readBytes(InputStream is) throws IOException {
		return readBytes(is, 4096);
	}

	private byte[] readBytes(InputStream is, int bufSize) throws IOException {
		byte[] buf = new byte[bufSize];
		int len = is.read(buf);

		if (len <= 0)
			return "null".getBytes();
		else if (len < bufSize) {// buf一次能够读完.
			return Arrays.copyOf(buf, len);
		} else {// buf一次不能够读完.
			byte[] total = null;
			int totalLen = bufSize;
			int times = 2;// 要读几次
			while (len >= bufSize) {
				if (times == 2) {
					total = Arrays.copyOf(buf, bufSize * 2);
				} else {
					total = Arrays.copyOf(total, bufSize * times);// 增加一倍容量,扩容
				}

				//System.out.println(Thread.currentThread().getName()+" method availbe:"+is.available());
				if(is.available() <= 0){//解决read的阻塞问题,但不准确,很多时候(一般在第一次read之前)有数据还会返回0	
					break;
				}
				
				len = is.read(buf);// 如果上一次刚好读完,会阻塞在这里.总的字节数是bufSize的整数倍,就会有这种情况
				if (len > 0) {
					totalLen += len;
					System.arraycopy(buf, 0, total, bufSize * (times - 1), len);
				}
				times++;
			}
			return Arrays.copyOf(total, totalLen);
		}
	}

	public class HandleRequestRunnable implements Runnable {
		final Socket socket;

		public HandleRequestRunnable(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				handleRequest(socket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
