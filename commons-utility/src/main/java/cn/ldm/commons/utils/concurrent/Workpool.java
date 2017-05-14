package cn.ldm.commons.utils.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Workpool {
	public static int defaultInitPoolSize = //
	Integer.parseInt(System.getProperty("eastcom.threadPool.initSize", "16"));

	public static int defaultMaxPoolSize = //
	Integer.parseInt(System.getProperty("eastcom.threadPool.maxSize", String.valueOf(2 * defaultInitPoolSize)));

	private static String defaultThreadNamePrefix = "eastcom-workpool";

	private final Logger log = LoggerFactory.getLogger(getClass());

	public Workpool() {
		this(defaultInitPoolSize, defaultMaxPoolSize, defaultThreadNamePrefix, true);
	}

	public Workpool(int initSize, int maxSize) {
		this(initSize, maxSize, defaultThreadNamePrefix, true);
	}

	public Workpool(int initSize, int maxSize, String threadNamePrefix, boolean startMonitor) {
		executor = new ThreadPoolExecutor(initSize, maxSize, 300, //
				TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), //
				new NamedThreadFactory(threadNamePrefix));

		executor.allowCoreThreadTimeOut(true);

		executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				log.error("workpool---溢出消息处理失败，拒绝.");
			}
		});

		log.info("workpool inited. init size:[{}],max size:[{}].", initSize, maxSize);

		if (startMonitor) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (!Thread.interrupted()) {
						print();
						try {
							Thread.sleep(60000);
						} catch (InterruptedException e) {
						}
					}
				}
			}, "thread-pool-monitor").start();
		}
	}

	private ThreadPoolExecutor executor;

	public Executor getExecutor() {
		return executor;
	}

	public void print() {
		long scheduled = executor.getTaskCount();
		long completed = executor.getCompletedTaskCount();
		int active = executor.getActiveCount();
		log.info("总任务数[{}],完成任务数[{}],激活任务数[{}].", scheduled, completed, active);
	}

}
