package tlw.nes.j2se;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import tlw.nes.NES;

public class ActionLoadRom extends AbstractAction {
	private static final long serialVersionUID = -2217210462340600507L;
	NES nes;
	JFileChooser jfc=new JFileChooser(System.getProperty("user.dir"));
	public ActionLoadRom(NES nes){
		putValue(NAME,"Load ROM");
		this.nes=nes;
	}
	public void actionPerformed(ActionEvent e) {
		Component comp=(Component)e.getSource();
		Component root=SwingUtilities.getRoot(comp);
		FileNameExtensionFilter fneFilter=new FileNameExtensionFilter("NES Ä£ÄâÆ÷ROM£¨*.rom, *.nes£©ÎÄ¼þ¡£", new String[]{"rom","nes"});
		jfc.setFileFilter(fneFilter);
		int result=jfc.showOpenDialog(root);
		if(result==JFileChooser.APPROVE_OPTION){
			String file=jfc.getSelectedFile().getPath();
			nes.loadRom(file);
			nes.startEmulation();
		}
	}

}
