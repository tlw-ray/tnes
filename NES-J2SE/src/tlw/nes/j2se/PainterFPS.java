package tlw.nes.j2se;

import java.awt.Color;
import java.awt.Graphics;

/**
@author liwei.tang@magustek.com
@since 2014��1��2�� ����9:28:22
 */
public class PainterFPS {
	private long prevFrameTime;
	private String fps;
	private int fpsCounter;
	public void paintFPS(int x, int y, Graphics g){
		// Update FPS count(ÿ45֡ͳ��һ��):
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
		g.setColor(Color.black);
		g.fillRect(x,y-g.getFontMetrics().getAscent(),g.getFontMetrics().stringWidth(fps)+3,g.getFontMetrics().getHeight());
		g.setColor(Color.cyan);
		g.drawString(fps,x,y);
	}
}
