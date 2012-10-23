package tlw.android.nes;

import tlw.nes.Globals;
import tlw.nes.NES;
import tlw.nes.core.IBufferView;
import tlw.nes.core.InputHandler;
import tlw.nes.core.UI;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.KeyEvent;
import android.view.View;

public class ViewNES extends View implements UI, IBufferView, InputHandler {
	
	protected NES nes=new NES(this);
	private int[] pix=new int[Globals.PIXEL_X*Globals.PIXEL_Y];
	Paint paint=new Paint(Color.BLACK);
	PainterFPS fpsPainter=new PainterFPS();
	public ViewNES(Context context) {
		super(context);
		init(true);
	}

	public int[] getBuffer() {
		return pix;
	}

	public void drawFrame() {
//		postInvalidate();
//		postInvalidate(0, 0, Globals.PIXEL_X, Globals.PIXEL_Y);
	}

	public NES getNES() {
		return nes;
	}

	public InputHandler getJoy1() {
		return this;
	}

	public InputHandler getJoy2() {
		// TODO Auto-generated method stub
		return null;
	}

	public IBufferView getScreenView() {
		return this;
	}

	public IBufferView getPatternView() {
		return this;
	}

	public IBufferView getSprPalView() {
		return this;
	}

	public IBufferView getNameTableView() {
		return this;
	}

	public IBufferView getImgPalView() {
		return this;
	}

	public void init(boolean showGui) {
		setFocusable(true);
		requestFocus();
		nes.getPpu().setBuffer(pix);
		
		// Set background color:
		for(int i=0;i<pix.length;i++){
			pix[i]=Globals.COLOR_BG;
		}
		
		mapKey(InputHandler.KEY_A,KeyEvent.KEYCODE_J);
		mapKey(InputHandler.KEY_B,KeyEvent.KEYCODE_K);
		mapKey(InputHandler.KEY_START,KeyEvent.KEYCODE_ENTER);
		mapKey(InputHandler.KEY_SELECT,KeyEvent.KEYCODE_PERIOD);
		mapKey(InputHandler.KEY_UP,KeyEvent.KEYCODE_W);
		mapKey(InputHandler.KEY_DOWN,KeyEvent.KEYCODE_S);
		mapKey(InputHandler.KEY_LEFT,KeyEvent.KEYCODE_A);
		mapKey(InputHandler.KEY_RIGHT,KeyEvent.KEYCODE_D);
		
		nes.loadRom("/tlw/android/nes/tank.nes");
		nes.startEmulation();
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		canvas.drawBitmap(pix, 0, Globals.PIXEL_X, 0, 0, Globals.PIXEL_X, Globals.PIXEL_Y, false, paint);
		fpsPainter.paintFPS(canvas);
	}

	public String getWindowCaption() {
		return null;
	}

	public void setWindowCaption(String s) {
		// TODO Auto-generated method stub

	}

	public void setTitle(String s) {
		// TODO Auto-generated method stub

	}

	public PointF getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getRomFileSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}

	public void println(String s) {
		// TODO Auto-generated method stub

	}

	public void showLoadProgress(int percentComplete) {
		// TODO Auto-generated method stub

	}

	public void showErrorMsg(String msg) {
		// TODO Auto-generated method stub

	}

    boolean[] allKeysState=new boolean[255];
    int[] keyMapping=new int[InputHandler.NUM_KEYS];

	public short getKeyState(int padKey) {
		return (short) (allKeysState[keyMapping[padKey]] ? 0x41 : 0x40);
	}

	public void mapKey(int padKey, int deviceKey) {
		keyMapping[padKey] = deviceKey;
	}

	public void reset() {
		allKeysState = new boolean[255];
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		System.out.println("onKeyDown:"+keyCode);
        if (keyCode >= allKeysState.length) {
            return false;
        }
        allKeysState[keyCode] = true;
        // Can't hold both left & right or up & down at same time:
        if (keyCode == keyMapping[InputHandler.KEY_LEFT]) {
            allKeysState[keyMapping[InputHandler.KEY_RIGHT]] = false;
        } else if (keyCode == keyMapping[InputHandler.KEY_RIGHT]) {
            allKeysState[keyMapping[InputHandler.KEY_LEFT]] = false;
        } else if (keyCode == keyMapping[InputHandler.KEY_UP]) {
            allKeysState[keyMapping[InputHandler.KEY_DOWN]] = false;
        } else if (keyCode == keyMapping[InputHandler.KEY_DOWN]) {
            allKeysState[keyMapping[InputHandler.KEY_UP]] = false;
        }
        return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		System.out.println("onKeyUp:"+keyCode);
        if (keyCode >= allKeysState.length) {
            return false;
        }

        allKeysState[keyCode] = false;
        switch (keyCode) {
            case KeyEvent.KEYCODE_V: {
                // Reset game:
                nes.reloadRom();
                nes.startEmulation();
                break;
            }
            case KeyEvent.KEYCODE_B: {
                // Just using this to display the battery RAM contents to user.
                if (nes.getRom() != null) {
                    nes.getRom().close();
                }
                break;
            }
        }
        return true;
	}
}
