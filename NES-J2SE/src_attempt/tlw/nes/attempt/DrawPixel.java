package tlw.nes.attempt;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

/**
@author liwei.tang@magustek.com
@since 2013年12月17日 下午9:34:30
练习使用BufferedImage.setRGB(...)方法
 */
public class DrawPixel extends JFrame{

	private static final long serialVersionUID = -7471024391533608421L;
	public static void main(String[] args) {
		DrawPixel frame=new DrawPixel();
		frame.setSize(400,400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	BufferedImage img=new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
	{
		int[] pixels=new int[256*256];
		for(int i=0;i<256;i++){
			for(int j=0;j<256;j++){
				if(j<128){
					pixels[i*256+j]=0xFF;
				}else{
					pixels[i*256+j]=0xFF0000;
				}
			}
		}
		img.setRGB(0, 0, 256, 256, pixels, 0, 0);
	}
	public void paint(Graphics g){
//		super.paint(g);//注视掉在resize鼠标释放时就不会有闪烁
		g.drawImage(img, 25, 50, this);
	}
}
