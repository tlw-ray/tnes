package tlw.nes.server;


/**
@author liwei.tang@magustek.com
@since 2014年2月5日 上午8:52:56
主线程产生数据
 */
public class ThreadServer extends Thread{
	
	private String server;
	
	private int port=1234;
	
	ThreadJoyRead threadJoyRead=new ThreadJoyRead();
	ThreadJoyWrite threadJoyWrite=new ThreadJoyWrite();
	
	Thread cpuThread;
	
	public void ready(){
		if(isHost()){
			//Socket Listen
			
		}else{
			//Socket Connect
			
		}
		
		threadJoyRead.start();
		threadJoyWrite.start();
		start();
	}
	
	public void run(){
		//exchange joy status
		while(true){
			synchronized (threadJoyWrite) {
				synchronized(threadJoyRead){
					
					//doSomethine
					
					//VBlank
					
					threadJoyWrite.notify();
					threadJoyRead.notify();
					try {
						threadJoyWrite.wait();
						threadJoyRead.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public boolean isHost() {
		return server!=null;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
}
