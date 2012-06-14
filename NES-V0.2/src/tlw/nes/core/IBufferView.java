package tlw.nes.core;

import tlw.nes.NES;
/**
 * 
 * @author tlw
 * 将原先的BufferView转化为接口，以适应JPanel不存在的状况。
 * 这个接口就是NES的游戏画面
 */
public interface IBufferView {
	/**
	 * Global.PIXEL_X * Global.PIXEL_Y 的以int32方式描述的显示点阵。
	 * @return
	 */
	int[] getBuffer();
	/**
	 * 该方法触发重绘以及声音播放
	 */
	void drawFrame();
	/**
	 * 和nes的接口
	 * @return
	 */
	NES getNES();
}