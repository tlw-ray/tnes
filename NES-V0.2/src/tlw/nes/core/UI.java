package tlw.nes.core;
import tlw.nes.NES;

public interface UI{
	
	public NES getNES();
	public InputHandler getJoy1();
	public InputHandler getJoy2();
	public IBufferView getScreenView();
	public IBufferView getPatternView();
	public IBufferView getSprPalView();
	public IBufferView getNameTableView();
	public IBufferView getImgPalView();
	
	public void init(boolean showGui);
	public String getWindowCaption();
	public void setWindowCaption(String s);
	public void setTitle(String s);
//	public Point getLocation();
	public int getWidth();
	public int getHeight();
	public int getRomFileSize();
	public void destroy();
	public void println(String s);
	public void showLoadProgress(int percentComplete);
	public void showErrorMsg(String msg);
	
}