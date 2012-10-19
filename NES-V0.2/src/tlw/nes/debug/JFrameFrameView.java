package tlw.nes.debug;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JFrameFrameView extends JFrame {

	private static final long serialVersionUID = 976650737076425633L;
	List<Image> imgs;
	JSpinner jspinner=new JSpinner();
	JButton jbutton=new JButton("run");
	public JFrameFrameView(){
		setSize(600,400);
		add(jspinner,BorderLayout.NORTH);
		add(jbutton,BorderLayout.SOUTH);
		jspinner.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				repaint();
			}
		});
		jbutton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				new Thread(){
					public void run(){
						for(Image img:imgs){
							Graphics g=getGraphics();
							g.drawImage(img,0,0,null);
							try {
								Thread.sleep(15);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}.start();
			}
		});
	}
	public void paint(Graphics g){
		super.paint(g);
		Integer value=(Integer)jspinner.getValue();
		if(imgs!=null && imgs.size()>0){
			Image img=imgs.get(value);
			if(img!=null){
				g.drawImage(img, 20, 60, null);
			}
		}
	}
	public List<Image> getImgs() {
		return imgs;
	}
	public void setImgs(List<Image> imgs) {
		this.imgs = imgs;
	}
}
