package tlw.android.nes;

import android.app.Activity;
import android.os.Bundle;

public class AndroidNES_15Activity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		final ViewNES view=new ViewNES(this);
//		final ViewSample view=new ViewSample(this);
        setContentView(view);
		new Thread(){
			public void run(){
				while(true){
         			try {
    						Thread.sleep(15);
    					} catch (InterruptedException e) {
    						e.printStackTrace();
    					}
         			view.postInvalidate();
				}
			}
		}.start();
    }

}