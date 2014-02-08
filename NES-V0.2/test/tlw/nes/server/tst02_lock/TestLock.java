package tlw.nes.server.tst02_lock;
/**
@author liwei.tang@magustek.com
@since 2014年2月8日 下午1:26:55
如果没有加入读写锁，该测试无法正常运行
 */
public class TestLock {

	public static void main(String[] args) {
		ThreadServerLock threadServer=new ThreadServerLock();
		threadServer.initialize();
		threadServer.start();
	}

}
