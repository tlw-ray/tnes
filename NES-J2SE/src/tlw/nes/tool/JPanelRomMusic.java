package tlw.nes.tool;

import javax.swing.JPanel;

import tlw.nes.vrom.ROM;

public class JPanelRomMusic extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3899029264508612162L;
	ROM rom;
	
	public ROM getRom() {
		return rom;
	}

	public void setRom(ROM rom) {
		this.rom = rom;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
}
