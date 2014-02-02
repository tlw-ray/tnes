package tlw.nes.interf;

import java.io.InputStream;

/**
 * @author liwei
 * @since 2012
 * 为nes提供外部设备接口
 * 1.提供Joy1,Joy2供nes获得按键状态。
 * 2.提供音频对象，供nes配置音频输出参数。
 * 3.提供播放一段声音缓冲的方法。
 * 4.提供播放一帧视频的方法。
 * 5.为nes提供rom加载流。
 */

public interface NesShell {
	/**
	 * 返回手柄1 供配置按键映射
	 * @return
	 */
	InputHandler getJoy1();
	
	/**
	 * 返回手柄2 供配置按键映射
	 * @return
	 */
	InputHandler getJoy2();
	
	/**
	 * 返回音频对象（供nes加载的rom设置音频初始化参数）
	 * @return
	 */
	AudioTrack getAudioTrack();
	
	Display getDisplay();
	
	/**
	 * 卡带 (供nes)
	 * @param rom
	 */
	void load(InputStream rom);
}
