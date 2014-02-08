package tlw.nes.server.tst03_net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import tlw.nes.interf.InputHandler;
import tlw.nes.server.ThreadJoyRead;
import tlw.nes.server.ThreadJoyWrite;
import tlw.nes.server.ThreadServer;
import tlw.nes.server.tst.InputHandlerImpl;

/**
 * @author liwei.tang@magustek.com
 * @since 2014年2月8日 下午1:31:18
 */
public class ThreadServerNet extends ThreadServer {

	static final int LOG_COUNT=10000;
	
	//每执行LOG_COUNT次数输出一次
	int pc=0;
	int pc_pack=0;
	
	long start=System.currentTimeMillis();
	
	public void initialize() {
		//虚拟手柄
		joy1=new InputHandlerImpl();
		joy2=new InputHandlerImpl();
		joy1.setPadKeyState((byte)1);
		joy2.setPadKeyState((byte)2);
		
		// 联网模式
		if (port != null) {
			// 联网模式：主机模式 | 客户机模式
			Socket socket = null;
			InputHandler joyLocal, joyRemote;

			if (ip == null) {
				// 主机模式：以port为端口建立主机等待客户机加入；并建立相应的主机资源；
				Logger.getAnonymousLogger().info("Listen and wait client join at... " + port);
				try {
					// 建立Socket监听，等待客户机加入
					socket = new ServerSocket(port).accept();
					Logger.getAnonymousLogger().info("Client " + socket.getRemoteSocketAddress() + " join game ...");
				} catch (IOException e) {
					e.printStackTrace();
				}
				// 主机模式：手柄1作为本地，手柄2作为远程；
				joyLocal = joy1;
				joyRemote = joy2;
			} else {
				// 客户机模式：根据server,port尝试连接入主机；并建立相应的客户机资源
				System.out.println("Try to connect to ..." + ip + ":" + port);
				try {
					socket = new Socket(ip, port);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// 客户机模式：手柄1作为远程，手柄2作为本地；
				joyLocal = joy2;
				joyRemote = joy1;
			}

			try {
				// 手柄状态读取线程，将远程手柄状态读出到本地
				ThreadJoyRead threadJoyRead = new ThreadJoyRead();
				// 手柄状态写入线程，将本地手柄状态写入远端
				ThreadJoyWrite threadJoyWrite = new ThreadJoyWrite();

				threadJoyRead.setName("Thread-Read");
				threadJoyRead.setIn(new DataInputStream(socket.getInputStream()));
				threadJoyRead.setJoy(joyRemote);
				threadJoyRead.setThreadOtherClient(threadJoyWrite);
				threadJoyRead.setThreadServer(this);
				threadJoyRead.setReadWriteLock(readWriteLock);

				threadJoyWrite.setName("Thread-Write");
				threadJoyWrite.setOut(new DataOutputStream(socket.getOutputStream()));
				threadJoyWrite.setJoy(joyLocal);
				threadJoyWrite.setThreadOtherClient(threadJoyRead);
				threadJoyWrite.setThreadServer(this);
				threadJoyWrite.setReadWriteLock(readWriteLock);

				this.threadJoyRead = threadJoyRead;
				this.threadJoyWrite = threadJoyWrite;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void beforeWait() {
		//服务端每执行LOG_COUNT次就输出一次，用来判定是否一直在执行未死锁。
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
