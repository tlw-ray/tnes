package tlw.nes.server.tst02_lock;
/**
@author liwei.tang@magustek.com
@since 2014��2��8�� ����1:26:55
���û�м����д�����ò����޷���������
 */
public class TestLock {

	public static void main(String[] args) {
		ThreadServerLock threadServer=new ThreadServerLock();
		threadServer.initialize();
		threadServer.start();
	}

}
