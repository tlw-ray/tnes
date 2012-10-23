package tlw.nes.platform.jfc;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import tlw.nes.NES;
import tlw.nes.vmemory.ByteBuffer;

public class ActionSave extends AbstractAction {
	private static final long serialVersionUID = -1624101514797196503L;
	NES nes;
	ByteBuffer byteBuffer;
	public ActionSave(NES nes, ByteBuffer bb){
		this.nes=nes;
		this.byteBuffer=bb;
		putValue(NAME,"Save");
	}
	public void actionPerformed(ActionEvent e) {
		nes.stateSave(byteBuffer);
	}

}
