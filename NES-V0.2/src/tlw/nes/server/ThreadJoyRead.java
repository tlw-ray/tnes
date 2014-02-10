package tlw.nes.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
@author liwei.tang@magustek.com
@since 2014��2��5�� ����8:52:23
 */
public class ThreadJoyRead extends ThreadJoy {

	InputStream in;
	
	@Override
	public void process() {
		try {
			byte value=(byte)in.read();
			joy.setPadKeyState(value);
//			System.out.println("Read:"+value);
		} catch (IOException e) {
			//TODO ����Ӧ�����˳��������ڶ����socket�ѶϿ�
			e.printStackTrace();
		}
	}

	public InputStream getIn() {
		return in;
	}

	public void setIn(DataInputStream in) {
		this.in = in;
	}

	@Override
	public void notifyServer() {
		// TODO Auto-generated method stub
		
	}

}
