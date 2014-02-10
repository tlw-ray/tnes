package tlw.nes.server.tst02_lock;

import tlw.nes.server.ThreadServer;
import tlw.nes.server.tst.CallStat;

/**
@author liwei.tang@magustek.com
@since 2014��2��8�� ����1:26:47
 */
public class ThreadServerLock extends ThreadServer {
	
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

	CallStat callState=new CallStat();
	
	@Override
	public void beforeWait() {
		//�����ÿִ��LOG_COUNT�ξ����һ�Σ������ж��Ƿ�һֱ��ִ��δ������
//		try {
//			Thread.sleep(1);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		callState.call();
	}

	@Override
	public void afterWait() {
		
	}

}
