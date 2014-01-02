package tlw.nes;

import java.io.InputStream;


public interface NesShell {
	//�ֱ�
	InputHandler getJoy1();
	InputHandler getJoy2();
	
	//��Ƶ
	//��ȡ��Ƶ�������ó�ʼ��������
	AudioTrack getAudioTrack();
	//����һ������
	void playSound(int[] soundBuffer);
	
	//��Ƶ
	/**
	 * ����һ֡
	 * @param frameBuffer
	 */
	void playFrame(int[] frameBuffer);
	
	//����
	void load(InputStream rom);
}
