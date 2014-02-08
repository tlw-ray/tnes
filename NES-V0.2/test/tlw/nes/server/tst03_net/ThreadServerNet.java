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
 * @since 2014��2��8�� ����1:31:18
 */
public class ThreadServerNet extends ThreadServer {

	static final int LOG_COUNT=10000;
	
	//ÿִ��LOG_COUNT�������һ��
	int pc=0;
	int pc_pack=0;
	
	long start=System.currentTimeMillis();
	
	public void initialize() {
		//�����ֱ�
		joy1=new InputHandlerImpl();
		joy2=new InputHandlerImpl();
		joy1.setPadKeyState((byte)1);
		joy2.setPadKeyState((byte)2);
		
		// ����ģʽ
		if (port != null) {
			// ����ģʽ������ģʽ | �ͻ���ģʽ
			Socket socket = null;
			InputHandler joyLocal, joyRemote;

			if (ip == null) {
				// ����ģʽ����portΪ�˿ڽ��������ȴ��ͻ������룻��������Ӧ��������Դ��
				Logger.getAnonymousLogger().info("Listen and wait client join at... " + port);
				try {
					// ����Socket�������ȴ��ͻ�������
					socket = new ServerSocket(port).accept();
					Logger.getAnonymousLogger().info("Client " + socket.getRemoteSocketAddress() + " join game ...");
				} catch (IOException e) {
					e.printStackTrace();
				}
				// ����ģʽ���ֱ�1��Ϊ���أ��ֱ�2��ΪԶ�̣�
				joyLocal = joy1;
				joyRemote = joy2;
			} else {
				// �ͻ���ģʽ������server,port������������������������Ӧ�Ŀͻ�����Դ
				System.out.println("Try to connect to ..." + ip + ":" + port);
				try {
					socket = new Socket(ip, port);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				// �ͻ���ģʽ���ֱ�1��ΪԶ�̣��ֱ�2��Ϊ���أ�
				joyLocal = joy2;
				joyRemote = joy1;
			}

			try {
				// �ֱ�״̬��ȡ�̣߳���Զ���ֱ�״̬����������
				ThreadJoyRead threadJoyRead = new ThreadJoyRead();
				// �ֱ�״̬д���̣߳��������ֱ�״̬д��Զ��
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
		//�����ÿִ��LOG_COUNT�ξ����һ�Σ������ж��Ƿ�һֱ��ִ��δ������
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
