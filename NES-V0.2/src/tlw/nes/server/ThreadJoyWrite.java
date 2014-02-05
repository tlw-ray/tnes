package tlw.nes.server;

import java.io.DataOutputStream;
import java.io.IOException;

/**
@author liwei.tang@magustek.com
@since 2014年2月5日 上午8:52:37
 */
public class ThreadJoyWrite extends ThreadClient {

	DataOutputStream out;
	
	@Override
	public void process() {
		try {
			out.writeInt(100);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public DataOutputStream getOut() {
		return out;
	}

	public void setOut(DataOutputStream out) {
		this.out = out;
	}
	
}
