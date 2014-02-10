package tlw.nes.server.tst;
/**
@author liwei.tang@magustek.com
@since 2014年2月10日 上午9:03:40
 */
public class TimeStat {
	long start=System.currentTimeMillis();
	int times=0;
	int MAX_TIMES=10000;
	public void call(){
		System.out.println(Thread.currentThread().getName()+" spend "+(System.currentTimeMillis()-start));
		start=System.currentTimeMillis();
	}
	
}
