package tlw.nes.server.tst02_lock;

import tlw.nes.server.ThreadServer;

/**
@author liwei.tang@magustek.com
@since 2014年2月8日 下午1:26:47
 */
public class ThreadServerLock extends ThreadServer {
	
	//测试死锁
	int pc=0;
	int pc_pack=0;
	static final int LOG_COUNT=10000;

	@Override
	public void initialize() {
		ThreadJoyLock threadClientRead=new ThreadJoyLock();
		ThreadJoyLock threadClientWrite=new ThreadJoyLock();
		
		threadClientRead.setName("Thread-Read");
		threadClientRead.setThreadServer(this);
		threadClientRead.setThreadOtherClient(threadClientWrite);
		threadClientRead.setReadWriteLock(readWriteLock);
		
		threadClientWrite.setName("Thread-Write");
		threadClientWrite.setThreadServer(this);
		threadClientWrite.setThreadOtherClient(threadClientRead);
		threadClientWrite.setReadWriteLock(readWriteLock);
		
		setThreadJoyRead(threadClientRead);
		setThreadJoyWrite(threadClientWrite);
		
	}

	long start=System.currentTimeMillis();
	
	@Override
	public void beforeWait() {
		//服务端每执行LOG_COUNT次就输出一次，用来判定是否一直在执行未死锁。
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		pc++;
		if(pc>LOG_COUNT){
			System.out.println("running ... "+(pc_pack++)+"*"+LOG_COUNT+" times spend "+(System.currentTimeMillis()-start)+" ms.");
			pc=0;
			start=System.currentTimeMillis();
		}
	}

	@Override
	public void afterWait() {
		
	}

}
