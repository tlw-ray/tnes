package tlw.nes.swing;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import tlw.nes.NES;
import tlw.nes.vmemory.ByteBuffer;

public class ActionLoad extends AbstractAction {
	private static final long serialVersionUID = 3356569938638899971L;
	NES nes;
	ByteBuffer byteBuffer;
	public ActionLoad(NES nes,ByteBuffer bb){
		this.nes=nes;
		byteBuffer=bb;
		putValue(NAME,"Load");
	}
	public void actionPerformed(ActionEvent e) {
		nes.stateLoad(byteBuffer);
	}
}
