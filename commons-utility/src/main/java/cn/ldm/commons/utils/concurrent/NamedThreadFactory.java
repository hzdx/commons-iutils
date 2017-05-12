package cn.ldm.commons.utils.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
	private static final AtomicInteger seq = new AtomicInteger(1);
	private String namePrefix;

	public NamedThreadFactory(String prefix) {
		this.namePrefix = prefix;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r,namePrefix + "-" + seq.getAndIncrement());
		t.setDaemon(false);
		return t;
	}

}