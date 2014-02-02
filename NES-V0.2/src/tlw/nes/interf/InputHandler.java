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
	 * ��õ�ǰ�����
	 * @param padKey
	 * @return
	 */
	public short getKeyState(int padKey);
	
	/**
	 * �������豸�ļ���Ԥ�趨�ļ�����ӳ��
	 * @param padKey ��Ϸ�ֱ���
	 * @param deviceKey �����豸�ļ�
	 */
	public void mapKey(int padKey, int deviceKey);
	
	/**
	 * ��״̬���ã������豸����δ�����κΰ�ť��״̬
	 */
	public void reset();
	
}