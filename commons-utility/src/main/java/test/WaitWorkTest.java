package test;

import java.util.concurrent.ThreadLocalRandom;

import cn.ldm.commons.utils.concurrent.WaitWorkThread;

public class WaitWorkTest extends WaitWorkThread{

	public WaitWorkTest(String threadName) {
		super(threadName);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		WaitWorkTest t = new WaitWorkTest("wait-work-test");
		int n = 0;
		for(;;){
			String msg = "test" + n++;
			System.out.println("delivery :" + msg);
			t.delivery(msg);
			try {
				Thread.sleep(ThreadLocalRandom.current().nextInt(2,3) * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	@Override
	public void handler(Object data) {
		sop(Thread.currentThread().getName() + " begin to hander..." + data);
		try {
			Thread.sleep(ThreadLocalRandom.current().nextInt(2,3) * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sop(Thread.currentThread().getName() + " hander done!  " + data);
		
	}
	
	public static void sop(String s){
		System.out.println(s);
	}
}
