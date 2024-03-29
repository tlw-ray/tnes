package tlw.nes.j2se;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

import tlw.nes.NES;
import tlw.nes.interf.AudioTrack;
import tlw.nes.interf.Display;
import tlw.nes.interf.InputHandler;
import tlw.nes.interf.NesShell;
import tlw.nes.j2se.device.AudioTrackJ2se;
import tlw.nes.j2se.device.KeyBoardInputHandler;
import tlw.nes.j2se.plugin.PainterFPS;
import tlw.nes.vmemory.ByteBuffer;
import tlw.nes.vppu.PPU;
//视频：60帧/秒
//像素:240*256=61440*32bit/帧
//声音:44100Hz*(16bit/32bit)=22050*32bit/秒=735*32bit/帧
//必须继承自JPanel，使用Canvas,JComponent时弹出菜单显示会有问题。
public class JPanelNES extends JPanel implements NesShell, Display{
	private static final long serialVersionUID = -2779554736088106527L;
	static String defaltROM = "game.nes";
	public static void main(String[] args) {
	
		JPanelNES jpanelNes = new JPanelNES();

		JMenuBar jmenuBar = new JMenuBar();

		JMenu jmenuRom = new JMenu("ROM");
		jmenuBar.add(jmenuRom);
		jmenuRom.add(jpanelNes.actionLoadRom);
		jmenuRom.addSeparator();
		jmenuRom.add(jpanelNes.actionReset);

		JMenu jmenuArchive = new JMenu("Archive");
		jmenuBar.add(jmenuArchive);
		jmenuArchive.add(jpanelNes.actionSave);
		jmenuArchive.addSeparator();
		jmenuArchive.add(jpanelNes.actionLoad);

		JMenu jmenuSound = new JMenu("Sound");
		jmenuBar.add(jmenuSound);
		JRadioButtonMenuItem jmenuItemSound = new JRadioButtonMenuItem(jpanelNes.actionSound);
		jmenuSound.add(jmenuItemSound);
		
//		JMenu jmenuTool = new JMenu("Tools");
//		jmenuBar.add(jmenuTool);
//		jmenuTool.add(jpanelNes.jframePaintRecorder.getActionScreenRecord());
		

		JFrame frame = new JFrame();
		frame.setSize(610, 450);
		frame.setJMenuBar(jmenuBar);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().add(jpanelNes, BorderLayout.CENTER);
		
		JPanelServerInfo jpanelServerInfo=new JPanelServerInfo();
		JOptionPane.showMessageDialog(frame, jpanelServerInfo, "", JOptionPane.PLAIN_MESSAGE);
		
		if(jpanelServerInfo.jradioButtonServer.isSelected()){
			//主机模式
			int port=(Integer)jpanelServerInfo.jspinnerPort.getValue();
			jpanelNes.getNES().getCpu().setPort(port);
		}else if(jpanelServerInfo.jradioButtonClient.isSelected()){
			//客户机模式
			String ip=jpanelServerInfo.jtextFieldIP.getText();
			int port=(Integer)jpanelServerInfo.jspinnerPort.getValue();
			jpanelNes.getNES().getCpu().setPort(port);
			jpanelNes.getNES().getCpu().setServer(ip);
		}

		frame.setVisible(true);
		jpanelNes.init(false);
		
	}
	
	
	private int[] pix=new int[PPU.PIXEL_X*PPU.PIXEL_Y];
	KeyBoardInputHandler kbJoy1;
	KeyBoardInputHandler kbJoy2;
	//TODO 这里数字改小点
	ByteBuffer byteBuffer=new ByteBuffer(0x20000,ByteBuffer.BO_BIG_ENDIAN);
	AudioTrack audioTrack=new AudioTrackJ2se();
	
	protected NES nes=new NES(this);
	
	ActionLoadRom actionLoadRom=new ActionLoadRom(nes);
	ActionReset actionReset=new ActionReset(nes);
	ActionLoad actionLoad=new ActionLoad(nes, byteBuffer);
	ActionSave actionSave=new ActionSave(nes, byteBuffer);
	ActionSound actionSound=new ActionSound(nes);
	
	//录屏软件
//	JFramePaintRecorder jframePaintRecorder = new JFramePaintRecorder();
	
	PainterFPS painterFPS=new PainterFPS();
	
	public NES getNES() {
		return nes;
	}

	public InputHandler getJoy1() {
		return kbJoy1;
	}

	public InputHandler getJoy2() {
		return kbJoy2;
	}
	public int[] getBuffer(){
		return pix;
	}
	
	
//	TODO VolatileImage img = createVolatileImage(Globals.PIXEL_X,Globals.PIXEL_Y);;
	public void init(boolean showGui) {
		setFocusable(true);
		requestFocus();
		setBackground(Color.BLACK);
		
		//当失去焦点时暂停游戏
//		this.addFocusListener(new FocusListener(){
//			public void focusGained(FocusEvent e) {
//				nes.getCpu().setPause(false);
//			}
//			public void focusLost(FocusEvent e) {
//				nes.getCpu().setPause(true);
//			}
//		});
		
		// Set background color:
//		for(int i=0;i<pix.length;i++){
//			pix[i]=Globals.COLOR_BG;
//		}

		
		// Map keyboard input keys for joypad 1:
		kbJoy1 = new KeyBoardInputHandler(nes,0);
		kbJoy1.mapKey(InputHandler.PAD_KEY_A,KeyEvent.VK_X);
		kbJoy1.mapKey(InputHandler.PAD_KEY_B,KeyEvent.VK_Z);
		kbJoy1.mapKey(InputHandler.PAD_KEY_START,KeyEvent.VK_ENTER);
		kbJoy1.mapKey(InputHandler.PAD_KEY_SELECT,KeyEvent.VK_CONTROL);
		kbJoy1.mapKey(InputHandler.PAD_KEY_UP,KeyEvent.VK_UP);
		kbJoy1.mapKey(InputHandler.PAD_KEY_DOWN,KeyEvent.VK_DOWN);
		kbJoy1.mapKey(InputHandler.PAD_KEY_LEFT,KeyEvent.VK_LEFT);
		kbJoy1.mapKey(InputHandler.PAD_KEY_RIGHT,KeyEvent.VK_RIGHT);
		addKeyListener(kbJoy1);

		// Map keyboard input keys for joypad 2:
		kbJoy2 = new KeyBoardInputHandler(nes,1);
		kbJoy2.mapKey(InputHandler.PAD_KEY_A,KeyEvent.VK_NUMPAD7);
		kbJoy2.mapKey(InputHandler.PAD_KEY_B,KeyEvent.VK_NUMPAD9);
		kbJoy2.mapKey(InputHandler.PAD_KEY_START,KeyEvent.VK_NUMPAD1);
		kbJoy2.mapKey(InputHandler.PAD_KEY_SELECT,KeyEvent.VK_NUMPAD3);
		kbJoy2.mapKey(InputHandler.PAD_KEY_UP,KeyEvent.VK_NUMPAD8);
		kbJoy2.mapKey(InputHandler.PAD_KEY_DOWN,KeyEvent.VK_NUMPAD2);
		kbJoy2.mapKey(InputHandler.PAD_KEY_LEFT,KeyEvent.VK_NUMPAD4);
		kbJoy2.mapKey(InputHandler.PAD_KEY_RIGHT,KeyEvent.VK_NUMPAD6);
		addKeyListener(kbJoy2);
		
		nes.loadRom(defaltROM);
		nes.startEmulation();
	}
	
	//注意:是BGR，不是RGB;
	BufferedImage img=new BufferedImage(PPU.PIXEL_X, PPU.PIXEL_Y, BufferedImage.TYPE_INT_BGR) ;

	@Override
	public void playFrame(int[] frameBuffer) {
		//注意：这里是纵向点阵绘制，不是横向;
		for(int x=0;x<256;x++){
			for(int y=0;y<240;y++){
				img.getRaster().getDataBuffer().setElem(y*PPU.PIXEL_X+x, frameBuffer[y*PPU.PIXEL_X+x]);
			}
		}
		
		//Test: paintFPS
		painterFPS.paintFPS(110, 20, img.getGraphics());
		
		Graphics g=getGraphics();
		
		//scale
		g.drawImage(img,0,0,getWidth(),getHeight(),null);
		//no scale
//		g.drawImage(img,0,0,null);
		
//		jframePaintRecorder.addImage(img);
		
	}

	@Override
	public void load(InputStream rom) {

	}

	@Override
	public AudioTrack getAudioTrack() {
		return audioTrack;
	}

	@Override
	public Display getDisplay() {
		return this;
	}
	
}
