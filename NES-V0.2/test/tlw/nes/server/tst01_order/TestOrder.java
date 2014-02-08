package tlw.nes.server.tst01_order;
/**
@author liwei.tang@magustek.com
@since 2014年2月8日 下午1:01:56
 */
public class TestOrder {

	public static void main(String[] args) {
		ThreadServerOrder threadServer=new ThreadServerOrder();
		threadServer.maxSleep=2000;
		threadServer.setPort(0);
		threadServer.initialize();
		threadServer.start();
	}

}
