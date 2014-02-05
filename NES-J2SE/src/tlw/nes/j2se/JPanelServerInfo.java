package tlw.nes.j2se;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;

/**
@author liwei.tang@magustek.com
@since 2014年2月5日 下午4:17:15
 */
public class JPanelServerInfo extends JPanel implements ActionListener{

	private static final long serialVersionUID = -5936797970800883135L;

	public static void main(String[] args) {
		JFrame frame=new JFrame();
		JPanelServerInfo pane=new JPanelServerInfo();
		frame.add(pane);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(320,230);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	JLabel jlabelIP=new JLabel("IP:");
	JTextField jtextFieldIP=new JTextField("127.0.0.1");
	
	JLabel jlabelPort=new JLabel("端口:");
	JSpinner jspinnerPort=new JSpinner(new SpinnerNumberModel(1234, 1000, 10000, 1));
	
	JRadioButton jradioButtonServer=new JRadioButton("建主");
	JRadioButton jradioButtonClient=new JRadioButton("连线");
	JRadioButton jradioButtonSingle=new JRadioButton("单机");
	JTextArea jtextAreaDesc=new JTextArea();
	
	public JPanelServerInfo(){
		
		JPanel jpanelRadioButtons=new JPanel();
		jpanelRadioButtons.setLayout(new GridLayout(1,3));
		jpanelRadioButtons.add(jradioButtonServer);
		jpanelRadioButtons.add(jradioButtonClient);
		jpanelRadioButtons.add(jradioButtonSingle);
		
		ButtonGroup bg=new ButtonGroup();
		bg.add(jradioButtonServer);
		bg.add(jradioButtonClient);
		bg.add(jradioButtonSingle);
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.insets=new Insets(0,10,10,10);
		gbc.gridx=0;
		gbc.gridy=GridBagConstraints.RELATIVE;
		gbc.anchor=GridBagConstraints.EAST;
		add(jlabelIP, gbc);
		add(jlabelPort, gbc);
		
		gbc.gridx=1;
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.weightx=1;
		add(jtextFieldIP, gbc);
		add(jspinnerPort, gbc);
		
		gbc.gridx=0;
		gbc.gridwidth=2;
		add(jpanelRadioButtons, gbc);
		gbc.weighty=1;
		gbc.fill=GridBagConstraints.BOTH;
		add(jtextAreaDesc, gbc);
		
		setBorder(BorderFactory.createTitledBorder("Login"));
		jtextAreaDesc.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		jradioButtonServer.addActionListener(this);
		jradioButtonClient.addActionListener(this);
		jradioButtonSingle.addActionListener(this);
		
		jtextAreaDesc.setEditable(false);
		jtextAreaDesc.setWrapStyleWord(true);
		
		jradioButtonServer.doClick();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source=e.getSource();
		if(source==jradioButtonServer){
			jtextFieldIP.setEnabled(false);
			jspinnerPort.setEnabled(true);
			jtextAreaDesc.setText("根据端口建立主机，等待玩家加入游戏。");
		}else if(source==jradioButtonClient){
			jtextFieldIP.setEnabled(true);
			jspinnerPort.setEnabled(true);
			jtextAreaDesc.setText("连接进入IP和端口的主机进行游戏。");
		}else if(source==jradioButtonSingle){
			jtextFieldIP.setEnabled(false);
			jspinnerPort.setEnabled(false);
			jtextAreaDesc.setText("进入单机游戏。");
		}
	}

	public JTextField getJTextFieldServer() {
		return jtextFieldIP;
	}

	public JSpinner getJSpinnerPort() {
		return jspinnerPort;
	}

	public JRadioButton getJRadioButtonServer() {
		return jradioButtonServer;
	}

	public JRadioButton getJRadioButtonClient() {
		return jradioButtonClient;
	}

	public JRadioButton getJRadioButtonSingle() {
		return jradioButtonSingle;
	}
	
}
