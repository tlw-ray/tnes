package tlw.nes.interf;
public interface InputHandler{
	
	// Joypad keys:
	public static final int KEY_A =      0;
	public static final int KEY_B =      1;
	public static final int KEY_SELECT = 2;
	public static final int KEY_START =  3;
	public static final int KEY_UP =     4;
	public static final int KEY_DOWN =   5;
	public static final int KEY_LEFT =   6;
	public static final int KEY_RIGHT =  7;
	
	// Key count:
	public static final int NUM_KEYS  =  8;
	
	/**
	 * 获得当前输入键
	 * @param padKey
	 * @return
	 */
	public short getKeyState(int padKey);
	
	/**
	 * 将输入设备的键与预设定的键进行映射
	 * @param padKey 游戏手柄键
	 * @param deviceKey 输入设备的键
	 */
	public void mapKey(int padKey, int deviceKey);
	
	/**
	 * 键状态重置，输入设备进入未按下任何按钮的状态
	 */
	public void reset();
	
}