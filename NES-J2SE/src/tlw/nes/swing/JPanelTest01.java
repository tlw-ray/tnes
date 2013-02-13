package tlw.nes.swing;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import tlw.nes.Globals;
import tlw.nes.NES;
import tlw.nes.core.IBufferView;
import tlw.nes.core.InputHandler;
import tlw.nes.core.UI;

public class JPanelTest01 extends JPanel implements UI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2384104577469999534L;
	static String defaltROM="game.nes";
	protected NES nes=new NES(this);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame=new JFrame();
		JPanelTest01 t1=new JPanelTest01();
		frame.add(t1);
		frame.setSize(500,300);
		frame.setVisible(true);
	}

	public JPanelTest01(){
		nes.loadRom(defaltROM);
		nes.startEmulation();
		new Thread(){
			public void run(){
				while(true){
					repaint();
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	public void paint(Graphics g){
		super.paint(g);
		int[] colors=getNES().getPpu().getBuffer();
		
		long value=0;
		for(int i=0;i<colors.length;i++){
			value+=colors[i];
		}
		System.out.println(value);
		
		Image img=UtilImg.toImage(colors, Globals.PIXEL_X, Globals.PIXEL_Y, BufferedImage.TYPE_INT_BGR);
		g.drawImage(img, 0, 0, null);
	}
	
	@Override
	public NES getNES() {
		return nes;
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
	public IBufferView getScreenView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBufferView getPatternView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBufferView getSprPalView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBufferView getNameTableView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IBufferView getImgPalView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(boolean showGui) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getWindowCaption() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setWindowCaption(String s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTitle(String s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getRomFileSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void println(String s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showLoadProgress(int percentComplete) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showErrorMsg(String msg) {
		// TODO Auto-generated method stub
		
	}

}
