package cn.ldm.commons.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {

	public static void scheduleSingle(final String threadName, Runnable cmd, long initialDelay, long period,
			TimeUnit unit) {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
			public Thread newThread(Runnable runnable) {
				Thread thread = new Thread(runnable, threadName);
				thread.setDaemon(true);
				return thread;
			}
		});
		scheduler.scheduleAtFixedRate(cmd, initialDelay, period, unit);
	}

}
