package cn.ldm.commons.utils.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//使用Lock,Condition实现
public abstract class WaitWorkThread implements Runnable {
	private boolean isOccupied = false;
	private Object data;
	private String threadName;

	private Lock lock = new ReentrantLock();
	private Condition busy = lock.newCondition();// 取数据条件
	private Condition vacant = lock.newCondition();// delivery条件

	public WaitWorkThread(String threadName) {
		Thread thread = new Thread(this);
		thread.setName(threadName);
		thread.start();
	}

	public abstract void handler(Object data);

	protected void handerException(Throwable e) {
		e.printStackTrace(System.err);
	}

	@Override
	public void run() {
		while (true) {
			Object data = waitData();
			try {
				handler(data);
			} catch (Throwable e) {
				handerException(e);
			}
		}
	}

	private Object waitData() {
		lock.lock();
		try {
			while (!isOccupied) {
				try {
					busy.await();
				} catch (InterruptedException e) {
				}
			}

			Object getData = data;
			isOccupied = false;
			vacant.signal();
			return getData;
		} finally {
			lock.unlock();
		}
	}

	public void delivery(Object ndata) {
		lock.lock();
		try {
			while (isOccupied) {
				try {
					vacant.await();
				} catch (InterruptedException e) {
				}
			}

			data = ndata;
			isOccupied = true;
			busy.signal();
		} finally {
			lock.unlock();
		}

	}

	public String getThreadName() {
		return threadName;
	}

}
