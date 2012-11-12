package tlw.nes.android;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class PainterFps {
	static final int SAMPLE_RATE=30;
	private long prevFrameTime;
	private String fps;
	private int fpsCounter;
	private Paint paint=new Paint();
	private Paint paintBG=new Paint();
	{
		paint.setColor(Color.GREEN);
		paintBG.setColor(Color.BLACK);
	}
	
	public void paintFPS(Canvas g){
		// Update FPS count:
		if(--fpsCounter<=0){
			long ct = System.currentTimeMillis();
			long frameT = (ct-prevFrameTime)/SAMPLE_RATE;
			if(frameT == 0){
				fps = "FPS: -";
			}else{
				fps = "FPS: "+(1000/frameT);
			}
			fpsCounter=SAMPLE_RATE;
			prevFrameTime = ct;
		}
		
		// Draw FPS.
		g.drawRect(25, 25, 100, 50, paintBG);
		g.drawText(fps, 50, 50, paint);
	}
}
