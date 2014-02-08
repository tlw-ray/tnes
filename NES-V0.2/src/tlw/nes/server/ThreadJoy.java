package tlw.nes.server;

import java.util.concurrent.locks.ReadWriteLock;

import tlw.nes.interf.InputHandler;

/**
@author liwei.tang@magustek.com
@since 2014年2月5日 下午12:54:03
客户端线程持有主线程
 */
public abstract class ThreadJoy extends Thread{
	
	InputHandler joy;
	
	Thread threadServer;
	
	ThreadJoy threadOtherClient;
	
	protected boolean finished=true;
	
	ReadWriteLock readWriteLock;
	
	public void run(){
		while(true){
			
			synchronized(this){
				try{
					readWriteLock.readLock().lock();
					
					process();
					
					synchronized(ThreadJoy.class){
						finished=true;
						if(threadOtherClient.finished){
							synchronized(threadServer){
								notifyServer();
								threadServer.notify();
							}
						}
					}
					
				}finally{
					readWriteLock.readLock().unlock();
				}
				
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
					
			}
		}
	}
	
	public abstract void process();
	public abstract void notifyServer();

	public InputHandler getJoy() {
		return joy;
	}

	public void setJoy(InputHandler joy) {
		this.joy = joy;
	}

	public Thread getThreadServer() {
		return threadServer;
	}

	public void setThreadServer(Thread threadServer) {
		this.threadServer = threadServer;
	}

	public ThreadJoy getThreadOtherClient() {
		return threadOtherClient;
	}

	public void setThreadOtherClient(ThreadJoy threadOtherClient) {
		this.threadOtherClient = threadOtherClient;
	}

	public ReadWriteLock getReadWriteLock() {
		return readWriteLock;
	}

	public void setReadWriteLock(ReadWriteLock readWriteLock) {
		this.readWriteLock = readWriteLock;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	
}
