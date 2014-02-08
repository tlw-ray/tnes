package tlw.nes.server;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import tlw.nes.interf.InputHandler;


/**
@author liwei.tang@magustek.com
@since 2014年2月5日 上午8:52:56
用来验证线程模型
 */
public abstract class ThreadServer extends Thread{
	
	protected String ip;
	protected Integer port;
	
	protected ThreadJoy threadJoyRead;
	protected ThreadJoy threadJoyWrite;
	
	protected InputHandler joy1,joy2;
	
	protected ReadWriteLock readWriteLock=new ReentrantReadWriteLock();
	
	public ThreadServer(){
		setName("Thread-Server");
	}
	
	public abstract void initialize();
	
	public void run(){
		
		//exchange joy status
		while(true){
			
			synchronized(this){
				
				try{
					readWriteLock.writeLock().lock();
					
					beforeWait();
					
					if(!isSingleMode()){
					
						if(!threadJoyWrite.isAlive()){
							threadJoyWrite.start();
						}
						
						if(!threadJoyRead.isAlive()){
							threadJoyRead.start();
						}
						
						synchronized(threadJoyWrite){
							threadJoyWrite.finished=false;
							threadJoyWrite.notify();
						}
						
						synchronized(threadJoyRead){
							threadJoyRead.finished=false;
							threadJoyRead.notify();
						}
					
					}
					
				}finally{
					readWriteLock.writeLock().unlock();
				}
				
				if(!isSingleMode()){
					
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}finally{
						afterWait();
					}
					
				}
						
			}
		}
	}
	
	public abstract void beforeWait();
	public abstract void afterWait();

	public boolean isHost() {
		return ip!=null;
	}

	public String getIP() {
		return ip;
	}

	public void setIP(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public ThreadJoy getThreadJoyRead() {
		return threadJoyRead;
	}

	public void setThreadJoyRead(ThreadJoy threadJoyRead) {
		this.threadJoyRead = threadJoyRead;
	}

	public ThreadJoy getThreadJoyWrite() {
		return threadJoyWrite;
	}

	public void setThreadJoyWrite(ThreadJoy threadJoyWrite) {
		this.threadJoyWrite = threadJoyWrite;
	}

	public InputHandler getJoy1() {
		return joy1;
	}

	public void setJoy1(InputHandler joy1) {
		this.joy1 = joy1;
	}

	public InputHandler getJoy2() {
		return joy2;
	}

	public void setJoy2(InputHandler joy2) {
		this.joy2 = joy2;
	}
	
	/**
	 * 是否单机模式
	 * @return
	 */
	public boolean isSingleMode(){
		return port==null;
	}
	
}
