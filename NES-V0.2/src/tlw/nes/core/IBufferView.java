package tlw.nes.core;

import tlw.nes.NES;
/**
 * 
 * @author tlw
 * ��ԭ�ȵ�BufferViewת��Ϊ�ӿڣ�����ӦJPanel�����ڵ�״����
 * ����ӿھ���NES����Ϸ����
 */
public interface IBufferView {
	/**
	 * Global.PIXEL_X * Global.PIXEL_Y ����int32��ʽ��������ʾ����
	 * @return
	 */
	int[] getBuffer();
	/**
	 * �÷��������ػ��Լ���������
	 */
	void drawFrame();
	/**
	 * ��nes�Ľӿ�
	 * @return
	 */
	NES getNES();
}