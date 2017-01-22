package cn.ldm.commons.etc;

import java.util.concurrent.ThreadFactory;

public class FixNameThreadFactory implements ThreadFactory {
	String threadName;

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r, threadName);
		thread.setDaemon(true);
		return thread;
	}

	public FixNameThreadFactory(String threadName) {
		this.threadName = threadName;
	}

	public static FixNameThreadFactory name(String threadName) {
		return new FixNameThreadFactory(threadName);
	}
}
