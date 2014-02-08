package tlw.nes.server.tst02_lock;

import tlw.nes.server.ThreadServer;

/**
@author liwei.tang@magustek.com
@since 2014��2��8�� ����1:26:47
 */
public class ThreadServerLock extends ThreadServer {
	
	//��������
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
		//�����ÿִ��LOG_COUNT�ξ����һ�Σ������ж��Ƿ�һֱ��ִ��δ������
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
