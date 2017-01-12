package cn.ldm.commons.etc;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
	private final AtomicInteger threadNum = new AtomicInteger(1);
	private final String prefix;
	private final boolean daemon;

	public static NamedThreadFactory namePrefix(String prefix) {
		return new NamedThreadFactory(prefix);
	}

	public NamedThreadFactory(String prefix) {
		this(prefix, false);
	}

	public NamedThreadFactory(String prefix, boolean daemo) {
		this.prefix = prefix;
		this.daemon = daemo;
	}

	@Override
	public Thread newThread(Runnable runnable) {
		String name = this.prefix + "-" + threadNum.getAndIncrement();
		Thread t = new Thread(runnable, name);
		t.setDaemon(this.daemon);
		return t;
	}

}