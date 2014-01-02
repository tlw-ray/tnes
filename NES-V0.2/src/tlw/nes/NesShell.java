package tlw.nes;

import java.io.InputStream;

/**
 * @author liwei
 * @since 2012
 * Ϊnes�ṩ�ⲿ�豸�ӿ�
 * 1.�ṩJoy1,Joy2��nes��ð���״̬��
 * 2.�ṩ��Ƶ���󣬹�nes������Ƶ���������
 * 3.�ṩ����һ����������ķ�����
 * 4.�ṩ����һ֡��Ƶ�ķ�����
 * 5.Ϊnes�ṩrom��������
 */

public interface NesShell {
	/**
	 * �����ֱ�1 �����ð���ӳ��
	 * @return
	 */
	InputHandler getJoy1();
	
	/**
	 * �����ֱ�2 �����ð���ӳ��
	 * @return
	 */
	InputHandler getJoy2();
	
	/**
	 * ������Ƶ���󣨹�nes���ص�rom������Ƶ��ʼ��������
	 * @return
	 */
	AudioTrack getAudioTrack();
	
	/**
	 * ����һ������ (��nes)
	 * @param soundBuffer
	 */
	void playSound(int[] soundBuffer);
	
	/**
	 * ����һ֡��Ƶ (��nes)
	 * @param frameBuffer
	 */
	void playFrame(int[] frameBuffer);
	
	/**
	 * ���� (��nes)
	 * @param rom
	 */
	void load(InputStream rom);
}
