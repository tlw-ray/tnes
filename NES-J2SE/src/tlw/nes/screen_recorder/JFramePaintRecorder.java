package tlw.nes.screen_recorder;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//录屏软件
//用来分析PPU生成屏幕数据
//1.IMAGE_COUNT 为会记录的屏幕数量。
//2.getImages().add(Image)能够将图像加入到该窗体程序。
//3.按钮Play会将加入的图像依次播放，每张之间sleep(15)。
//4.最大帧数,用来控制记录的最大帧数。
//5.当前帧数,用来显示第x帧的图像。

public class JFramePaintRecorder extends JFrame {

	private static final long serialVersionUID = 976650737076425633L;
	
	final int IMAGE_LEFT=20;
	final int IMAGE_TOP=60;
	
	List<Image> imgs=new Vector<Image>();
	
	JToolBar jtoolBar=new JToolBar();
	
	//最大帧数
	JLabel jlabelFrameCount=new JLabel("最大帧:");
	SpinnerNumberModel spinnerModelFrameCount=new SpinnerNumberModel(300, 0, 600, 100);
	JSpinner jspinnerFrameCount=new JSpinner(spinnerModelFrameCount);
	
	//当前显示帧
	JLabel jlabelFrameIndex=new JLabel("当前帧:");
	SpinnerNumberModel spinnerModelFrameIndex=new SpinnerNumberModel(0, 0, 600, 1);
	JSpinner jspinnerFrameIndex=new JSpinner(spinnerModelFrameIndex);
	
	ActionPlay actionPlay=new ActionPlay();
	ActionScreenRecorder actionScreenRecord=new ActionScreenRecorder();
	
	public JFramePaintRecorder(){
		JPanel jpanelFrameInfo=new JPanel();
		jpanelFrameInfo.setLayout(new GridBagLayout());
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.gridx=0;
		gbc.gridy=GridBagConstraints.RELATIVE;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		jpanelFrameInfo.add(jlabelFrameCount, gbc);
		jpanelFrameInfo.add(jlabelFrameIndex, gbc);
		gbc.gridx=1;
		gbc.weightx=1;
		jpanelFrameInfo.add(jspinnerFrameCount, gbc);
		jpanelFrameInfo.add(jspinnerFrameIndex, gbc);
		
		jtoolBar.add(actionPlay);
		
		jspinnerFrameIndex.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				repaint();
			}
		});
		
		setTitle("T-NES屏幕录制软件");
		setSize(600,400);
		add(jtoolBar,BorderLayout.NORTH);
		add(jpanelFrameInfo,BorderLayout.SOUTH);
	}
	public void paint(Graphics g){
		super.paint(g);
		Integer value=(Integer)jspinnerFrameIndex.getValue();
		if(imgs!=null && imgs.size()>0){
			Image img=imgs.get(value);
			if(img!=null){
				g.drawImage(img, IMAGE_LEFT, IMAGE_TOP, null);
			}
		}
	}

	public synchronized void addImage(Image img){
		Integer maxFrameCount=(Integer)jspinnerFrameCount.getValue();
		if(imgs.size()<maxFrameCount){
			imgs.add(img);
			spinnerModelFrameIndex.setMaximum(imgs.size()-1);
			spinnerModelFrameIndex.setValue(imgs.size()-1);
		}
	}
	
	public ActionScreenRecorder getActionScreenRecord() {
		return actionScreenRecord;
	}

	class ActionScreenRecorder extends AbstractAction{

		private static final long serialVersionUID = 297185206662005761L;

		public ActionScreenRecorder(){
			putValue(NAME, "录屏软件");
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			setVisible(true);
		}
		
	}
	
	class ActionPlay extends AbstractAction{

		private static final long serialVersionUID = 3475433122941047617L;

		public ActionPlay(){
			putValue(NAME, "播放");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			new Thread(){
				public void run(){
					for(Image img:imgs){
						Graphics g=getGraphics();
						g.drawImage(img,IMAGE_LEFT, IMAGE_TOP,null);
						try {
							Thread.sleep(15);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();			
		}
	}
}
