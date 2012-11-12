package tlw.nes.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	ViewNES view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("tlw","onCreate");
        view=new ViewNES(this);
        setContentView(view);
        new Thread(){
        	public void run(){
                while(true){
                	view.postInvalidate();
                	try {
        				Thread.sleep(20);
        			} catch (InterruptedException e) {
        				e.printStackTrace();
        			}
                }
        	}
        }.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
}
