package tlw.nes.platform.jfc;

import java.awt.Component;
import java.awt.ItemSelectable;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import tlw.nes.NES;

public class ActionSound extends AbstractAction {
	private static final long serialVersionUID = 8392133292388388333L;
	NES nes;
	public ActionSound(NES nes){
		this.nes=nes;
		putValue(NAME,"Sound");
	}
	public void actionPerformed(ActionEvent e) {
		Component source=(Component)e.getSource();
		ItemSelectable is=(ItemSelectable)source;
		Object[] objs=is.getSelectedObjects();
		nes.enableSound(objs!=null);
	}
}