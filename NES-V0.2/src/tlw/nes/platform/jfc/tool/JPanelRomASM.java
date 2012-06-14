package tlw.nes.platform.jfc.tool;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import tlw.nes.vrom.ROM;

public class JPanelRomASM extends JPanel {
	private static final long serialVersionUID = 2327256846910603473L;
	ROM rom;
	JLabel labelRom=new JLabel("ROM:");
	JComboBox comboRom=new JComboBox();
	JTextPane textPaneASM=new JTextPane();
	JScrollPane scrollASM=new JScrollPane(textPaneASM);
	public JPanelRomASM(){
		setLayout(new GridBagLayout());
		GridBagConstraints gbc=new GridBagConstraints();
		add(labelRom,gbc);
		gbc.weightx=1;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		add(comboRom,gbc);
		gbc.fill=GridBagConstraints.BOTH;
		gbc.weighty=1;
		gbc.gridx=0;
		gbc.gridy=1;
		gbc.gridwidth=2;
		add(scrollASM,gbc);
		
		textPaneASM.setFont(new Font(Font.MONOSPACED,Font.PLAIN,14));
		
		comboRom.addActionListener(new ActionRomSelected());
	}
	
	public ROM getRom() {
		return rom;
	}

	public void setRom(ROM rom) {
		this.rom = rom;
		int romCount=rom.getRom().length;
		Integer[] romIndexs=new Integer[romCount];
		for(int i=0;i<romCount;i++){
			romIndexs[i]=i;
		}
		DefaultComboBoxModel model=new DefaultComboBoxModel(romIndexs);
		comboRom.setModel(model);
	}
	static final DecimalFormat DF=new DecimalFormat("000000 ");
	static final int LINE_LENGTH=16;
	class ActionRomSelected extends AbstractAction{
		private static final long serialVersionUID = 1175821860973890319L;
		public ActionRomSelected(){
			putValue(NAME,"ROM");
		}
		public void actionPerformed(ActionEvent e) {
			Object selected=comboRom.getSelectedItem();
			if(selected!=null){
				int idx=(Integer)selected;
				short[] romContent=rom.getRom()[idx];
				StringBuilder asm=new StringBuilder();
				for(int i=0;i<romContent.length;i+=LINE_LENGTH){
					int lineNumber=i;
					String lineNumberStr=DF.format(lineNumber);
					asm.append(lineNumberStr);
					for(int j=0;j<LINE_LENGTH;j++){
						String code=Integer.toHexString(romContent[i]).toUpperCase();
						if(code.length()<2){
							asm.append("0");
						}
						asm.append(code);
						asm.append(" ");
						if( j==8){
							asm.append("\t");
						}
					}
					asm.append("\n");
				}
				textPaneASM.setText(asm.toString());
			}
		}
	}
}
