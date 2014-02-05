package tlw.nes.android;

import tlw.nes.NES;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceView;

public class MainActivity extends Activity {
	ViewNES view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(new SurfaceView(this));
        
        Log.i("tlw","onCreate");
        
        String defaltROM=Environment.getExternalStorageDirectory().getPath()+"/nes/game.nes";
        
        NES nes=new NES(null);
        nes.loadRom(defaltROM);
        nes.startEmulation();
        
        while(true){
        	int[] buffer=nes.getPpu().getBuffer();
        	long value=0;
        	for(int i=0;i<buffer.length;i++){
        		value+=buffer[i];
        	}
        	Log.i("tlw",value+"");
        	try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        
        
        
//        view=new ViewNES(this);
//        setContentView(view);
//        new Thread(){
//        	public void run(){
//                while(true){
//                	view.postInvalidate();
//                	try {
//        				Thread.sleep(20);
//        			} catch (InterruptedException e) {
//        				e.printStackTrace();
//        			}
//                }
//        	}
//        }.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    
}
