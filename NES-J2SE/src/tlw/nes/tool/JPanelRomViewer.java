package tlw.nes.tool;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import tlw.nes.vrom.ROM;

public class JPanelRomViewer extends JPanel{
	public JPanelRomViewer() {
		setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane);
		
		panelRomInfo = new JPanelRomInfo();
		tabbedPane.addTab("ROM Infomation:", null, panelRomInfo, null);
		
		panelTiles = new JPanelRomTiles();
		tabbedPane.addTab("Tiles:", null, panelTiles, null);
		
		panelMusic = new JPanelRomMusic();
		tabbedPane.addTab("Music:", null, panelMusic, null);
		
		panelPalette =new JPanelRomPalette();
		tabbedPane.addTab("Palette:", null, panelPalette, null);
		
		panelRom = new JPanelRomASM();
		tabbedPane.addTab("ROM:", panelRom);
		
		rom=new ROM();
		rom.load("target/ROM/坦克.nes", null);
		
		panelRomInfo.setRom(rom);
		panelTiles.setRom(rom);
		panelMusic.setRom(rom);
		panelRom.setRom(rom);
	}
	private static final long serialVersionUID = 3558422616720994295L;
	ROM rom;
	JFileChooser jfileChooser;
	Action actionFileOpen=new ActionFileOpen();
	
	JPanelRomMusic panelMusic;
	JPanelRomInfo panelRomInfo;
	JPanelRomTiles panelTiles;
	JPanelRomPalette panelPalette;
	JPanelRomASM panelRom;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JPanelRomViewer pane=new JPanelRomViewer();
		
		JMenuBar jmenuBar=new JMenuBar();
		JMenu jmenuFile=new JMenu("文件");
		jmenuBar.add(jmenuFile);
		jmenuFile.add(pane.getActionFileOpen());
		
		JFrame frame=new JFrame();
		frame.setJMenuBar(jmenuBar);
		frame.setContentPane(pane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600,400);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	public Action getActionFileOpen() {
		return actionFileOpen;
	}

	public void setActionFileOpen(Action actionFileOpen) {
		this.actionFileOpen = actionFileOpen;
	}

	class ActionFileOpen extends AbstractAction{
		private static final long serialVersionUID = -7458082835711612752L;
		public ActionFileOpen(){
			putValue(NAME,"打开");
		}
		public void actionPerformed(ActionEvent e){
			if(jfileChooser==null){
				FileNameExtensionFilter filter=new FileNameExtensionFilter("Nes ROM file. (*.nes)", new String[]{"nes"});
				jfileChooser=new JFileChooser();
				jfileChooser.setFileFilter(filter);
			}
			int result=jfileChooser.showOpenDialog(JPanelRomViewer.this);
			if(result==JFileChooser.APPROVE_OPTION){
				rom=new ROM();
				rom.load(jfileChooser.getSelectedFile().getAbsolutePath(), null);
				
				panelRomInfo.setRom(rom);
				panelTiles.setRom(rom);
				panelMusic.setRom(rom);
				panelRom.setRom(rom);
			}
		}
	}
}