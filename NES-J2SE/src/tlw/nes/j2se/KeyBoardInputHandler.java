package tlw.nes.j2se;

import java.awt.event.*;

import tlw.nes.InputHandler;
import tlw.nes.NES;

public class KeyBoardInputHandler implements KeyListener, InputHandler {
    boolean[] allKeysState;
    int[] keyMapping;
    int id;
    NES nes;
    
    //连发
    boolean bPressed=false;
    ThreadBBB threadBBB=new ThreadBBB();
    
    int b=0;
    public KeyBoardInputHandler(NES nes, int id) {
        this.nes = nes;
        this.id = id;
        allKeysState = new boolean[255];
        keyMapping = new int[InputHandler.NUM_KEYS];
        threadBBB.start();
    }

    public short getKeyState(int padKey) {
        return (short) (allKeysState[keyMapping[padKey]] ? 0x41 : 0x40);
    }

    public void mapKey(int padKey, int keyBoardKeycode) {
        keyMapping[padKey] = keyBoardKeycode;
    }

    public void keyPressed(KeyEvent ke) {
        int kc = ke.getKeyCode();
        if (kc >= allKeysState.length) {
            return;
        }
    	
        if(kc==keyMapping[InputHandler.KEY_B]){
        	bPressed=true;
        }else{
        	allKeysState[kc] = true;
        }


        // Can't hold both left & right or up & down at same time:
        if (kc == keyMapping[InputHandler.KEY_LEFT]) {
            allKeysState[keyMapping[InputHandler.KEY_RIGHT]] = false;
        } else if (kc == keyMapping[InputHandler.KEY_RIGHT]) {
            allKeysState[keyMapping[InputHandler.KEY_LEFT]] = false;
        } else if (kc == keyMapping[InputHandler.KEY_UP]) {
            allKeysState[keyMapping[InputHandler.KEY_DOWN]] = false;
        } else if (kc == keyMapping[InputHandler.KEY_DOWN]) {
            allKeysState[keyMapping[InputHandler.KEY_UP]] = false;
        }
    }

    public void keyReleased(KeyEvent ke) {

        int kc = ke.getKeyCode();
        if (kc >= allKeysState.length) {
            return;
        }

        allKeysState[kc] = false;
        
        if (id == 0) {
            switch (kc) {
                case KeyEvent.VK_F5: {
                    // Reset game:
                    nes.reloadRom();
                    nes.startEmulation();
                    break;
                }
                case KeyEvent.VK_F10: {
                    // Just using this to display the battery RAM contents to user.
                    if (nes.getRom() != null) {
                        nes.getRom().close();
                    }
                    break;
                }
            }
        }
        //连发
        if (kc == keyMapping[InputHandler.KEY_B]){
        	bPressed=false;
        }
    }

    public void keyTyped(KeyEvent ke) {
        // Ignore.
    }

    public void reset() {
        allKeysState = new boolean[255];
    }

    public void destroy() {
        nes = null;
    }
    //连射线程
    class ThreadBBB extends Thread{
    	{
    		setPriority(MIN_PRIORITY);
    	}
    	public void run(){
    		while(true){
	    		try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	    		if(bPressed){
	    			allKeysState[keyMapping[InputHandler.KEY_B]] = !allKeysState[keyMapping[InputHandler.KEY_B]];
	    		}else{
	    			continue;
	    		}
    		}
    	}
    }
}