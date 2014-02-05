package tlw.nes.server;

import tlw.nes.interf.InputHandler;

/**
@author liwei.tang@magustek.com
@since 2014年2月5日 下午12:54:03
 */
public abstract class ThreadClient extends Thread{
	
	Thread threadMain;
	
	InputHandler joy;
	
	public void run(){
		while(true){
			synchronized(this){
				
				process();
				
				notify();
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public abstract void process();

	public Thread getThreadMain() {
		return threadMain;
	}

	public void setThreadMain(Thread threadMain) {
		this.threadMain = threadMain;
	}

	public InputHandler getJoy() {
		return joy;
	}

	public void setJoy(InputHandler joy) {
		this.joy = joy;
	}
	
}
