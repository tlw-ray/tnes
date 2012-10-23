package tlw.nes.platform.jfc.tool;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import tlw.nes.vppu.PaletteTable;

public class JPanelRomPalette extends JPanel {

	private static final long serialVersionUID = 903968221532377714L;

	public JPanelRomPalette(){
		PaletteTable pal=new PaletteTable();
		pal.loadDefaultPalette();
		
		setLayout(new GridLayout(4,16));
		for(int i=0;i<64;i++){
			Color color=new Color(PaletteTable.origTable[i]);
			String strR=Integer.toHexString(color.getRed());
			String strG=Integer.toHexString(color.getGreen());
			String strB=Integer.toHexString(color.getBlue());
			String colorStr="[" + strR + "," + strG + "," + strB + "]";
			JLabel labelColor=new JLabel();
			labelColor.setOpaque(true);
			labelColor.setBackground(color);
			labelColor.setText(colorStr.toUpperCase());
			add(labelColor);
		}
	}
	
}
