package tlw.nes.server.tst;

import tlw.nes.interf.InputHandler;

/**
@author liwei.tang@magustek.com
@since 2014年2月7日 上午10:52:37
 */
public class InputHandlerImpl implements InputHandler {
	byte padKeyState;
	public short getKeyState(int padKey) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void mapKey(int padKey, int deviceKey) {
		// TODO Auto-generated method stub

	}

	public void reset() {
		// TODO Auto-generated method stub

	}

	public byte getPadKeyState() {
		return padKeyState;
	}

	public void setPadKeyState(byte value) {
		padKeyState=value;
	}

}
