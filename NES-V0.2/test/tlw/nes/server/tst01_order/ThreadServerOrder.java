package tlw.nes.server.tst01_order;

import java.util.Random;

import tlw.nes.server.ThreadServer;

/**
@author liwei.tang@magustek.com
@since 2014年2月8日 上午11:33:44
 */
public class ThreadServerOrder extends ThreadServer {
	
	//服务端开始执行时间
	long beginProcess;
	long beginWait;
	
	int maxSleep=5000;
	
	public void beforeWait(){
		//执行服务端逻辑
		
		//记录服务端开始执行时间
		beginProcess=System.currentTimeMillis();
		
		try {
			//主线程随机sleep最长MAX_SLEEP毫秒
			Random random=new Random();
			int wellSleep=random.nextInt(maxSleep);
			long start1=System.currentTimeMillis();
			System.out.println(getName()+ " well sleep "+wellSleep);
			sleep(wellSleep);
			System.out.println(getName()+" realy sleeped "+ (System.currentTimeMillis()-start1));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		beginWait=System.currentTimeMillis();
	}

	@Override
	public void afterWait() {
		
		System.out.println(getName()+" waite (Read and Write) spend:"+(System.currentTimeMillis()-beginWait));
		
		System.out.println(getName()+" total spend:"+(System.currentTimeMillis()-beginProcess));
		
		System.out.println("------------------");
		
	}

	@Override
	public void initialize() {
		ThreadClientOrder threadClientRead=new ThreadClientOrder();
		ThreadClientOrder threadClientWrite=new ThreadClientOrder();
		
		threadClientRead.setName("Thread-Read");
		threadClientRead.setThreadServer(this);
		threadClientRead.setThreadOtherClient(threadClientWrite);
		threadClientRead.setReadWriteLock(readWriteLock);
		threadClientRead.maxSleep=maxSleep;
		
		threadClientWrite.setName("Thread-Write");
		threadClientWrite.setThreadServer(this);
		threadClientWrite.setThreadOtherClient(threadClientRead);
		threadClientWrite.setReadWriteLock(readWriteLock);
		threadClientWrite.maxSleep=maxSleep;
		
		setThreadJoyRead(threadClientRead);
		setThreadJoyWrite(threadClientWrite);
	}
	
}
