package test;

import java.util.Random;

public class RemoteDebugTest {
	public static int n = 10;

	public static void main(String[] args) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				for (;;) {
					changeN();
					System.out.println("n changed!");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();

		System.out.println("app startup....");

		for (;;)
			try {
				Thread.sleep(Integer.MAX_VALUE);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	public static void changeN() {
		n = new Random().nextInt(50);// 0~49
	}
}
