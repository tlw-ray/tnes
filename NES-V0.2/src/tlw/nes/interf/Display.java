package tlw.nes.interf;
/**
@author liwei.tang@magustek.com
@since 2014年2月2日 下午10:47:39
 */
public interface Display {
	/**
	 * 绘制一帧视频 (供nes)
	 * @param frameBuffer
	 */
	void playFrame(int[] frameBuffer);
}
