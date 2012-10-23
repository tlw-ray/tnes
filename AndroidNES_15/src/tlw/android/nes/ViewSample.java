package tlw.android.nes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ImageView;

public class ViewSample extends ImageView {
	Paint paint=new Paint();
	private PainterFPS paintFPS=new PainterFPS();
	public ViewSample(Context context) {
		super(context);
	}
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		canvas.drawColor(Color.GREEN);
		paint.setColor(Color.BLUE);
		canvas.drawRect(50, 50, 100, 100, paint);
		paintFPS.paintFPS(canvas);
	}

}
