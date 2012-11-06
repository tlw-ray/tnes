package tlw.nes.swing;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import tlw.nes.NES;

public class ActionReset extends AbstractAction {
	private static final long serialVersionUID = 4907115127383561531L;
	NES nes;
	public ActionReset(NES nes){
		this.nes=nes;
		putValue(NAME,"Reset");
	}
	public void actionPerformed(ActionEvent e) {
		Component comp=(Component)e.getSource();
		Component root=SwingUtilities.getRoot(comp);
		int result=JOptionPane.showConfirmDialog(root, "未保存的游戏将丢失，是否重置?","提示",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
		if(result==JOptionPane.OK_OPTION){
            nes.reloadRom();
            nes.startEmulation();
		}
	}
}
