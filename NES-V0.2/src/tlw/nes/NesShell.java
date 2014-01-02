package tlw.nes;

import java.io.InputStream;


public interface NesShell {
	//手柄
	InputHandler getJoy1();
	InputHandler getJoy2();
	
	//音频
	//获取音频对象（设置初始化参数）
	AudioTrack getAudioTrack();
	//播放一段声音
	void playSound(int[] soundBuffer);
	
	//视频
	/**
	 * 绘制一帧
	 * @param frameBuffer
	 */
	void playFrame(int[] frameBuffer);
	
	//卡带
	void load(InputStream rom);
}
