package cn.ldm.commons.utils.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.ldm.commons.utils.StreamUtil;

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

			// System.out.println(Thread.currentThread().getName() +" availble
			// "+in.available());

			System.out.print(Thread.currentThread().getName() + ":\r\n" + new String(StreamUtil.readBytes(in)));
			// System.out.print(Thread.currentThread().getName()+":\r\n" + new
			// String(readBytes(in)));

			OutputStream out = socket.getOutputStream();
			out.write(RESPONSE_STR.getBytes(StandardCharsets.UTF_8));
			out.flush();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			socket.close();
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
