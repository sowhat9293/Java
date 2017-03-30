package TermProject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class InputView {
	JFrame InputFrame;
	Control _control;
	int[] numArr;
	InputView(Control control){
		numArr=new int[3];
		_control=control;
		for(int i=0; i<numArr.length; i++)
			numArr[i]=0;
		create();
	}
	public void create(){
		InputFrame = new JFrame("����ã�� ũ��");
		InputFrame.setLocation(500,200);
		JButton Send = new JButton("������");
		JLabel Row_L = new JLabel("����");
		JTextField Row_T = new JTextField(3);
		JLabel Col_L = new JLabel("����");
		JTextField Col_T = new JTextField(3);
		JLabel Mine_L = new JLabel("���� ����");
		JTextField Mine_T = new JTextField(3);
		InputFrame.setPreferredSize(new Dimension(400,70));
		Container InputContainer = InputFrame.getContentPane();
		InputContainer.setLayout(new FlowLayout());
		InputContainer.add(Row_L);
		InputContainer.add(Row_T);
		InputContainer.add(Col_L);
		InputContainer.add(Col_T);
		InputContainer.add(Mine_L);
		InputContainer.add(Mine_T);
		InputContainer.add(Send);
		
		Send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				numArr[0]=Integer.parseInt(Row_T.getText().toString());
				numArr[1]=Integer.parseInt(Col_T.getText().toString());
				numArr[2]=Integer.parseInt(Mine_T.getText().toString());
				_control.gameStart();
				InputFrame.removeNotify();
			}
		});
		InputFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		InputFrame.pack();
		InputFrame.setVisible(true);
	}
	
	public int[] getNumArr()
	{
		return numArr;
	}
}
