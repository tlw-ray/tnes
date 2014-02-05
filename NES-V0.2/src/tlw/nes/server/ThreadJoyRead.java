package tlw.nes.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import tlw.nes.interf.InputHandler;

/**
@author liwei.tang@magustek.com
@since 2014年2月5日 上午8:52:23
 */
public class ThreadJoyRead extends ThreadClient {

	InputStream in;
	
	@Override
	public void process() {
		try {
			for(int i=0;i<InputHandler.NUM_KEYS;i++){
				byte value=(byte)in.read();
				joy.setPadKeyState(value);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public InputStream getIn() {
		return in;
	}

	public void setIn(DataInputStream in) {
		this.in = in;
	}

}
