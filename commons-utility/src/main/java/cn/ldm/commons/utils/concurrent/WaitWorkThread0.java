package cn.ldm.commons.utils.concurrent;

//使用synchronized实现
public abstract class WaitWorkThread0 implements Runnable {
	private boolean available = false;
	private Object data;
	private String threadName;
	private Thread thread;

	public WaitWorkThread0(String threadName) {
		this.threadName = threadName;
		thread = new Thread(this);
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

	private synchronized Object waitData() {
		while (!available) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		Object getData = this.data;
		available = false;
		notifyAll();
		return getData;
	}

	public synchronized void delivery(Object ndata) {
		while (available) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		this.data = ndata;
		available = true;
		notifyAll();
	}

	/**
	 * @return the threadName
	 */
	public String getThreadName() {
		return threadName;
	}

}
