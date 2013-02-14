package tlw.nes.tool;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import tlw.nes.vrom.ROM;

public class JPanelRomInfo extends JPanel{

	private static final long serialVersionUID = -6912837307361145965L;

	ROM rom;
	JLabel labelFilePath = new JLabel("File:");
	JLabel labelBatteryRam = new JLabel("HasBatteryRam:");
	JLabel labelRomBankCount = new JLabel("RomBankCount:");
	JLabel labelVRomBankCount = new JLabel("VRomBankCount:");
	JLabel labelTrainer = new JLabel("Trainer:");
	JLabel labelMapperType = new JLabel("MapperType:");
	JLabel labelMirrorType = new JLabel("MirrorType:");
	JLabel labelFourScreen = new JLabel("FourScreen:");
	
	JLabel labelFilePathValue = new JLabel();
	JLabel labelBatteryRamValue= new JLabel();
	JLabel labelRomBankCountValue = new JLabel();
	JLabel labelVRomBankCountValue = new JLabel();
	JLabel labelTrainerValue = new JLabel();
	JLabel labelMapperTypeValue = new JLabel();
	JLabel labelMirrorTypeValue = new JLabel();
	JLabel labelFourScreenValue = new JLabel();
	
	public ROM getRom() {
		return rom;
	}

	public void setRom(ROM rom) {
		this.rom = rom;
		labelFilePathValue.setText(rom.getFileName());
		labelBatteryRamValue.setText(rom.hasBatteryRam()+"");
		labelRomBankCountValue.setText(rom.getRomBankCount()+"");
		labelVRomBankCountValue.setText(rom.getVromBankCount()+"");
		labelTrainerValue.setText(rom.isTrainer()+"");
		labelFourScreenValue.setText(rom.isFourScreen()+"");
		String mirrorType="";
		if(rom.isFourScreen()){
			mirrorType="FOURSCREEN_MIRRORING";
		}else if(rom.getMirroring()==0){
			mirrorType="HORIZONTAL_MIRRORING";
		}else{
			mirrorType="VERTICAL_MIRRORING";
		}
		labelMirrorTypeValue.setText(mirrorType);
		labelMapperTypeValue.setText(rom.getMapperTypeDescription());
	}
	
	public JPanelRomInfo() {
		setLayout(new GridBagLayout());
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.insets=new Insets(5,25,5,5);
		gbc.gridx=0;
		gbc.gridy=GridBagConstraints.RELATIVE;
		gbc.weightx=1;
		gbc.anchor=GridBagConstraints.WEST;
		add(labelFilePath,gbc);
		add(labelBatteryRam,gbc);
		add(labelRomBankCount,gbc);
		add(labelVRomBankCount,gbc);
		add(labelTrainer,gbc);
		add(labelMapperType,gbc);
		add(labelMirrorType,gbc);
		add(labelFourScreen,gbc);
		
		gbc.gridx=1;
		gbc.weightx=2;
		add(labelFilePathValue,gbc);
		add(labelBatteryRamValue,gbc);
		add(labelRomBankCountValue,gbc);
		add(labelVRomBankCountValue,gbc);
		add(labelTrainerValue,gbc);
		add(labelMapperTypeValue,gbc);
		add(labelMirrorTypeValue,gbc);
		add(labelFourScreenValue,gbc);
		
		gbc.weighty=1;
		add(new JLabel(),gbc);
	}
}