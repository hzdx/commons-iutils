package cn.ldm.commons.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {

	public static void scheduleSingle(final String threadName, Runnable cmd, long initialDelay, long period,
			TimeUnit unit) {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(FixNameThreadFactory.name(threadName));
		scheduler.scheduleAtFixedRate(cmd, initialDelay, period, unit);
	}

	public static class FixNameThreadFactory implements ThreadFactory {
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
}
