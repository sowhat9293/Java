package TermProject;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

public class View {
	Model _model;
	Control _control;
	JFrame frame;
	ImagePanel imagepanel;
	Image[] ImageStore = new Image[13];
	Timer timer = new Timer();
	boolean start = false;
	int Sec=0;
	JPanel panel;
	JLabel Timer_L;
	void make(){
		frame = new JFrame("���� ã��");
		frame.setLocation(500,200);
		frame.setPreferredSize(new Dimension(500,400));
		Timer_L = new JLabel();
		Container contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		panel = new JPanel();
		panel.add(Timer_L);
		imagepanel = new ImagePanel(_model,this);
		contentPane.add(imagepanel,BorderLayout.CENTER);
		contentPane.add(panel,BorderLayout.SOUTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
	View(Model model, Control control){
		_model = model;
		_control=control;
		make();
		load();
		imagepanel.repaint();
	}
	void load(){
		Toolkit toolkit = frame.getToolkit();
		ImageStore[0]=toolkit.getImage("zero.PNG");
		ImageStore[1]=toolkit.getImage("one.PNG");
		ImageStore[2]=toolkit.getImage("two.PNG");
		ImageStore[3]=toolkit.getImage("three.PNG");
		ImageStore[4]=toolkit.getImage("four.PNG");
		ImageStore[5]=toolkit.getImage("five.PNG");
		ImageStore[6]=toolkit.getImage("seix.PNG");
		ImageStore[7]=toolkit.getImage("seven.PNG");
		ImageStore[8]=toolkit.getImage("eight.PNG");
		ImageStore[9]=toolkit.getImage("mine.jpg");
		ImageStore[10]=toolkit.getImage("find.jpg");
		ImageStore[11]=toolkit.getImage("flag.jpg");
	}
	Image getImage(int index){
		return ImageStore[index];
	}
	
	int getSec()
	{
		return this.Sec;
	}
	
	void callDialog(boolean win)
	{
		 Dialog info = new Dialog(frame, "Dialog", true);
	       info.setSize(350,200);
	       //parent Frame�� �ƴ�, ȭ���� ��ġ�� ������ �ȴ�.
	       info.setLocation(650,300);
	       info.setLayout(new BorderLayout());
	       JLabel msg;
	      
	       if(win)			// �̰�����
	    	   msg=new JLabel("<html> ���ӿ��� �¸��Ͽ����ϴ�! <br/> Ŭ���� �ð� : "+this.Sec+"</html>", JLabel.CENTER); 
	       else
	    	   msg=new JLabel("���ӿ��� �й��Ͽ����ϴ�!");
	      
	       JPanel btnPanel=new JPanel();
	       btnPanel.setLayout(new FlowLayout());
	       JButton reMatchBtn = new JButton("�ٽ��ϱ�");
	       JButton quitBtn=new JButton("������");
	       btnPanel.add(reMatchBtn);		btnPanel.add(quitBtn);
	       quitBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
	       reMatchBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				info.dispose();
				frame.dispose();
				//_control.gameStart();
				Control temp = new Control();
				
			}
		});
	       info.add(msg, BorderLayout.CENTER);
	       info.add(btnPanel, BorderLayout.SOUTH);
	       info.setVisible(true);
	}
}
