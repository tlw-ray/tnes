package tlw.nes.server.tst01_order;

import java.util.Random;

import tlw.nes.server.ThreadJoy;

/**
@author liwei.tang@magustek.com
@since 2014年2月8日 上午11:36:18
 */
public class ThreadClientOrder extends ThreadJoy {

	int maxSleep=5000;
	@Override
	public void process() {
		//手柄读写线程随机停顿最长MAX_SLEEP时间
		try {
			Random random=new Random();
			int realSleep=random.nextInt(maxSleep);
			System.out.println(getName()+ " well sleep "+realSleep);
			long start0=System.currentTimeMillis();
			sleep(realSleep);
			System.out.println(getName()+ " realy sleeped "+(System.currentTimeMillis()-start0));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void notifyServer() {
		System.out.println(getName()+" notify threadServer.");		
	}

}
