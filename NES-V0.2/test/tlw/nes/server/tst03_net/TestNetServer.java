package tlw.nes.server.tst03_net;


/**
@author liwei.tang@magustek.com
@since 2014��2��8�� ����1:30:52
 */
public class TestNetServer {

	public static void main(String[] args) {
		ThreadServerNet threadServer=new ThreadServerNet();
		threadServer.setPort(1234);
		threadServer.initialize();
		threadServer.start();
	}

}
