package tlw.nes.server.tst01_order;

import java.util.Random;

import tlw.nes.server.ThreadServer;

/**
@author liwei.tang@magustek.com
@since 2014��2��8�� ����11:33:44
 */
public class ThreadServerOrder extends ThreadServer {
	
	//����˿�ʼִ��ʱ��
	long beginProcess;
	long beginWait;
	
	int maxSleep=5000;
	
	public void beforeWait(){
		//ִ�з�����߼�
		
		//��¼����˿�ʼִ��ʱ��
		beginProcess=System.currentTimeMillis();
		
		try {
			//���߳����sleep�MAX_SLEEP����
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
