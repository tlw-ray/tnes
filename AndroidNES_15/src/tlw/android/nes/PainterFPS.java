package tlw.android.nes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class PainterFPS {
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
		int x=50;
		int y=50;
		// Update FPS count:
		if(--fpsCounter<=0){
			long ct = System.currentTimeMillis();
			long frameT = (ct-prevFrameTime)/45;
			if(frameT == 0){
				fps = "FPS: -";
			}else{
				fps = "FPS: "+(1000/frameT);
			}
			fpsCounter=45;
			prevFrameTime = ct;
		}
		
		// Draw FPS.
		g.drawRect(25, 25, 100, 50, paintBG);
		g.drawText(fps, x, y, paint);
	}
}
