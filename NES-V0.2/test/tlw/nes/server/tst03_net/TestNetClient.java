package tlw.nes.server.tst03_net;
/**
@author liwei.tang@magustek.com
@since 2014年2月8日 下午1:31:02
 */
public class TestNetClient {

	public static void main(String[] args) {
		ThreadServerNet threadServer=new ThreadServerNet();
		threadServer.setIP("127.0.0.1");
		threadServer.setPort(1234);
		threadServer.initialize();
		threadServer.start();
	}

}
