package tlw.nes.android;

import java.util.TimerTask;

import tlw.nes.Globals;
import tlw.nes.NES;
import tlw.nes.core.IBufferView;
import tlw.nes.core.InputHandler;
import tlw.nes.core.UI;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;
import android.view.View;

public class ViewNES extends View implements UI{
	static String defaltROM=Environment.getExternalStorageDirectory().getPath()+"/nes/game.nes";
	protected NES nes=new NES(this);
	PainterFps painter=new PainterFps();
	private Paint paint=new Paint();
	public ViewNES(Context context) {
		super(context);
		nes.loadRom(defaltROM);
		nes.startEmulation();
	}
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int[] colors=getNES().getPpu().getBuffer();
		
		int value=0;
		for(int i=0;i<colors.length;i++){
			value+=colors[i];
		}
		Log.i("tlw",value+"");
		
//		canvas.drawBitmap(colors, 0, Globals.PIXEL_X, 0, 0, Globals.PIXEL_X, Globals.PIXEL_Y, false, paint);
		
//		Bitmap bm=Bitmap.createBitmap(colors, Globals.PIXEL_X, Globals.PIXEL_Y, Bitmap.Config.ARGB_8888);
//		bm.setPixels(colors, 0, 0, 0, 0, Globals.PIXEL_X, Globals.PIXEL_Y);
//		canvas.drawBitmap(bm,0,0,new Paint());
		
		painter.paintFPS(canvas);
	}
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBufferView getImgPalView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputHandler getJoy1() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputHandler getJoy2() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NES getNES() {
		// TODO Auto-generated method stub
		return nes;
	}

	@Override
	public IBufferView getNameTableView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBufferView getPatternView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRomFileSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IBufferView getScreenView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBufferView getSprPalView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getWindowCaption() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void println(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTitle(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWindowCaption(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showErrorMsg(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showLoadProgress(int arg0) {
		// TODO Auto-generated method stub
		
	}
}
