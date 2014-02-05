package tlw.nes.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
@author liwei.tang@magustek.com
@since 2014年2月5日 上午8:52:37
 */
public class ThreadJoyWrite extends ThreadClient {

	OutputStream out;
	
	@Override
	public void process() {
		try {
			byte value=joy.getPadKeyState();
			out.write(value);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public OutputStream getOut() {
		return out;
	}

	public void setOut(DataOutputStream out) {
		this.out = out;
	}
	
}
