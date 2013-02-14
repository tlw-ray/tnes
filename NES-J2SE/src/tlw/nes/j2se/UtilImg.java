package tlw.nes.j2se;

import java.awt.Image;
import java.awt.image.BufferedImage;

import tlw.nes.Globals;

public class UtilImg {
	public static Image toImage(int[] screen, int width, int height, int BufferedImage$TYPE){
		//TODO BufferedImage.getRaster()的setPixcel(),setPixcels(),setSample()等方法均失效的原因。
		BufferedImage img=new BufferedImage(Globals.PIXEL_X,Globals.PIXEL_Y,BufferedImage.TYPE_INT_RGB);
		for(int i=0;i<Globals.PIXEL_X;i++){
			for(int j=0;j<Globals.PIXEL_Y;j++){
				int color=screen[j*Globals.PIXEL_X+i];
				byte red=(byte) ((color & 0xFF0000) >> 16);
				byte green=(byte) ((color & 0xFF00) >> 8);
				byte blue=(byte) ((color & 0xFF) >> 0);
				color=blue << 16 | green << 8 | red << 0;
				img.setRGB(i, j, color);
			}
		}
		return img;
	}
}
