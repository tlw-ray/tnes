package tlw.nes.platform.jfc.tool;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import tlw.nes.vppu.Tile;
import tlw.nes.vrom.ROM;

public class JPanelRomTiles extends JPanel {
	private static final long serialVersionUID = -7581229524671152601L;
	ROM rom;
	JComboBox comboBoxVROM = new JComboBox();
	JPanel panelTiles = new JPanel();
	JScrollPane jscrollPane=new JScrollPane(panelTiles);
	public JPanelRomTiles() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblRom = new JLabel("VROM:");
		GridBagConstraints gbc_lblRom = new GridBagConstraints();
		gbc_lblRom.insets = new Insets(0, 0, 5, 5);
		gbc_lblRom.anchor = GridBagConstraints.EAST;
		gbc_lblRom.gridx = 0;
		gbc_lblRom.gridy = 0;
		add(lblRom, gbc_lblRom);
		
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 1;
		gbc_comboBox.gridy = 0;
		add(comboBoxVROM, gbc_comboBox);
		
		
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.weighty = 1.0;
		gbc_panel.weightx = 1.0;
		gbc_panel.gridwidth = 2;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		add(jscrollPane, gbc_panel);
		
		init();
	}

	public void init(){
		panelTiles.setLayout(new GridLayout(8,8));
		comboBoxVROM.addActionListener(new ActionVROMTiles());
	}

	public ROM getRom() {
		return rom;
	}

	public void setRom(ROM rom) {
		this.rom = rom;
		int vromCount=rom.getVromBankCount();
		Integer[] vromIdxs=new Integer[vromCount];
		for(int i=0;i<vromCount;i++){
			vromIdxs[i]=i;
		}
		comboBoxVROM.setModel(new DefaultComboBoxModel(vromIdxs));
	}

	class ActionVROMTiles extends AbstractAction{
		private static final long serialVersionUID = -3172213047006027338L;
		public ActionVROMTiles(){
			putValue(NAME,"Tiles");
		}
		public void actionPerformed(ActionEvent e) {
			Object item=comboBoxVROM.getSelectedItem();
			if(item!=null){
				int idx=(Integer)item;
				Tile[][] tiles=rom.getVromTile();
				
//				NES nes=new NES(null);
//				nes.loadRom(rom.getFileName());
//				tiles[0]=nes.getPpu().getPtTile();
				
				panelTiles.removeAll();
				for(int i=0;i<tiles[idx].length;i++){
					Tile tile=tiles[idx][i];
					BufferedImage bi=new BufferedImage(8,8,BufferedImage.TYPE_INT_RGB);
					int[] data=tile.getPix();
					
//					int[] data=tile.renderSimple(0, 0, 0, PaletteTable.origTable);
//					for(int x=0;x<8;x++){
//						for(int y=0;y<8;y++){
//							int id=x*8+y;
//							int val=data[id];
//							bi.setRGB(y, x, val);
//						}
//					}
					
					for(int x=0;x<8;x++){
						for(int y=0;y<8;y++){
							int id=x*8+y;
							int val=data[id];
							switch(val){
							case 0:bi.setRGB(y, x, 0xFFFFFF);break;
							case 1:bi.setRGB(y, x, 0xFF);break;
							case 2:bi.setRGB(y, x, 0xFF00);break;
							case 3:bi.setRGB(y, x, 0xFF0000);break;
							}
						}
					}
					
					BufferedImage bi4=new BufferedImage(32,32,BufferedImage.TYPE_INT_RGB);
					bi4.getGraphics().drawImage(bi, 0, 0, 32, 32, null);
					
					JLabel labelImg=new JLabel();
					labelImg.setOpaque(true);
					labelImg.setIcon(new ImageIcon(bi4));
					labelImg.setBorder(BorderFactory.createLineBorder(Color.BLACK));
					panelTiles.add(labelImg);
				}
				validate();
			}
		}
	}
}
