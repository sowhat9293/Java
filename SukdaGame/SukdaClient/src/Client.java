import java.util.HashSet;
import java.awt.*;
import java.io.*;
import java.math.*;
import java.util.Random;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Vector;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

import javax.swing.*;

import java.awt.event.*;
import java.beans.*;

import javax.swing.border.*;

import java.util.StringTokenizer;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
 
 
 
public class Client
{
	public static ClientThread client;
	public static MainGui gui;
	public static LoginGui loginGui;
	public static LobbyGui lobbyGui;
	public static int betMoney=0;	// ����� ���ñ�
	public static int plusMoney=0;	// �߰��� �����ϴ� �ݾ�
	public static int totalMoney=0;
	public static int myMoney=0;
	public static int yourMoney=0;
	public static Card[] myCard=new Card[2];
	public static Card[] yourCard=new Card[2];
	public static HashMap<Integer, String> roomList;	// �� ��ȣ�� �� ������ �������� �ڷᱸ��
	public static boolean isLobby=true;	// ���� �÷��̾ �κ�� true, ���̸� false
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		try
		{
		roomList=new HashMap<Integer,String>();
		loginGui=LoginGui.getInst();
		gui=MainGui.getInst("����");
		Socket socket=new Socket("117.17.143.70", 7777);	// ������ ����
		client=new ClientThread();
		client.setSocket(socket);
		lobbyGui=LobbyGui.getInst();
		
		myCard[0]=new Card();
		myCard[1]=new Card();
		yourCard[0]=new Card();
		yourCard[1]=new Card();
		
/*		while (true)
		{
			if (client.in.readUTF().compareTo("START#") == 0)
				break;
		}*/
		
		client.start();
		client.join();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
			client.socket.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
 
}
 
class ClientThread extends Thread
{
	Socket socket=null;
	DataInputStream in;
	DataOutputStream out;
	public String myId;
	public void run()
	{
		while(true)	//������ ���� �����ٴ� �޽����� ���� �ߴ�
		{
			try
			{
			String msg=in.readUTF();
			filteringMsg(msg);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	public void filteringMsg(String msg) throws IOException	// �����κ��� ���� Msg�� ���ڷ� ������ �׿� �ش��ϴ� ������ �Ѵ�.
	{
		System.out.println("���� : " +msg);
		if(msg.startsWith("CARD#") == true)	//���� �ʹ� �� ī�� ������ ���� �� ��
		{
			msg=msg.substring(5);
			StringTokenizer st=new StringTokenizer(msg, ":");
			Client.myCard[0].num=new Integer(st.nextToken());
			Client.myCard[1].num=new Integer(st.nextToken());
			Client.yourCard[0].num=new Integer(st.nextToken());
			Client.yourCard[1].num=new Integer(st.nextToken());
			Client.gui.panel1.changeCardImg(1);
			//	���� �ҽ� �Է�
		}
		else if (msg.startsWith("MONEY#") == true)	// �� �����ݾ� ����
		{
			msg=msg.substring(6);
			StringTokenizer st=new StringTokenizer(msg, ":");
			Client.myMoney=new Integer(st.nextToken()).intValue();
			Client.yourMoney=new Integer(st.nextToken()).intValue();
			Client.totalMoney=new Integer(st.nextToken()).intValue();
			Client.gui.showAllMoney();
		}
		else if (msg.startsWith("CHAT#") == true)
		{
			System.out.println("ê����"+msg);
			msg=msg.substring(5);
			if(Client.isLobby == true)	//�κ��̸�
			{
				Client.lobbyGui.msgArea.append(msg);
			}
			else		//���̸�
			{
				Client.gui.showChatMsg(msg);
			}
		}
		else if(msg.startsWith("ROOM#") == true)	// ��ü �� ����Ʈ�� �� ��
		{
			msg=msg.substring(5);
			StringTokenizer st=new StringTokenizer(msg,":");
			int roomNum=new Integer(st.nextToken()).intValue();
			String roomName=st.nextToken();
			Client.roomList.put(roomNum, roomName);
		}
		else if(msg.startsWith("BET#") == true)
		{
			msg=msg.substring(4);
			Client.betMoney=new Integer(msg).intValue();	// �⺻ ���� �ݾ�
			Client.gui.showAllMoney();
			if(Client.myMoney - Client.betMoney < 0 && Client.betMoney != 0)
			{
				Client.gui.setBetBtn(true, 1);	//���� ��ư�� Ȱ��ȭ ��Ű��, �ݸ� �� �� �ִ�
				Client.betMoney=Client.myMoney;
				Client.gui.panel3.refreshBtn();
			}
			else
			{
			Client.gui.setBetBtn(true, 0);	// ��ư Ȱ��ȭ
			Client.gui.panel3.refreshBtn();
			}
		}
		else if (msg.startsWith("RESULT#") == true)
		{
			Client.gui.changeCardImg(0);   //����и� �����ش�.
			msg=msg.substring(7);
			StringTokenizer st=new StringTokenizer(msg, ":");
			String setP1=st.nextToken();
			String setP2=st.nextToken();
			Client.gui.showResultAlarm("���\n" +"�÷��̾�1 : " +setP1 +"\n" + "�÷��̾�2 : " + setP2 +"\n");
		}
		else if(msg.startsWith("THEEND#") == true)		//���� 2���Ǽ� ������ ������ ��
		{
			Client.gui.panel3.setBetBtn(false, 0);		//���� ��ư�� ����ȭ ��Ų��.
		}
		else if (msg.startsWith("END!") == true)	// ���� �� ���� ������ �� ���������� ������ ������ �޽���
		{
			Client.gui.panel3.setStartBtn(true);
			Client.gui.panel1.resetCard();//����� �� �� �ְ� ī�� 4���� �����´�.
			Client.betMoney=0;
			Client.gui.panel3.refreshBtn();
		}
		else if(msg.startsWith("ERROR#")== true)
		{
			String errorMsg=msg.substring(6);
			JOptionPane.showMessageDialog(Client.gui, errorMsg, "Error", JOptionPane.INFORMATION_MESSAGE);		// ����â�� ����ش�.
			Client.gui.dispose();
		}
		else if(msg.startsWith("START#") == true)
		{
			Client.gui.panel3.setStartBtn(false);	//������ ���۵Ǹ� �غ� ��ư�� ��Ȱ��ȭ ��Ų��.
		}
		else if(msg.startsWith("SET#BTNON") == true)
		{
			Client.gui.setBetBtn(true, 0);
		}
		else if(msg.startsWith("ROOMLIST#")==true)
		{
			msg=msg.substring(9);
			Client.lobbyGui.setRoomList(msg);
			Client.lobbyGui.refreshList();
		}
		else if(msg.startsWith("IDLIST#") == true)
		{
			msg=msg.substring(7);
			Client.lobbyGui.setPlayerList(msg);
			Client.lobbyGui.refreshList();
		}
		else if(msg.startsWith("GUESTOUT#")==true)
		{
			Client.gui.panel3.setStartBtn(true);
			Client.gui.panel1.resetCard();//����� �� �� �ְ� ī�� 4���� �����´�.
			Client.betMoney=0;
			Client.gui.panel3.refreshBtn();
		}
	}
	
	public void sendToServer(String str)	// �������� �޽����� ���� �Ѵ�.
	{
		try
		{
			out.writeUTF(str);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setSocket(Socket socket)
	{
		this.socket=socket;
		try
		{
		in=new DataInputStream(socket.getInputStream());
		out=new DataOutputStream(socket.getOutputStream());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}

class Card
{
	Integer num;	//���� ���� 1~20
}
 
 
class GamePanel extends JPanel
{
	Border bd1=BorderFactory.createEtchedBorder();
	ImageIcon backgroundImg = new ImageIcon("img/back.jpg");
	ImageIcon cardBackImg = new ImageIcon("img/backcard.jpg");	// ī�� �޸�
	
	JButton upBtn1=null;
	JButton upBtn2=null;
	JButton downBtn1=null;
	JButton downBtn2=null;
	public GamePanel() 
	{
		this.setLayout(new GridLayout(2, 2, 0, 300));
		this.setBorder(bd1);
		
		upBtn1=new JButton(cardBackImg);
		upBtn1.setBackground(Color.WHITE);
		upBtn1.setBorderPainted(false);
		upBtn2=new JButton(cardBackImg);
		upBtn2.setBackground(Color.WHITE);
		upBtn2.setBorderPainted(false);
		downBtn1=new JButton(cardBackImg);
		downBtn2=new JButton(cardBackImg);
		
		downBtn1.setBackground(Color.WHITE);
		downBtn1.setBorderPainted(false);
		downBtn2.setBackground(Color.WHITE);
		downBtn2.setBorderPainted(false);
		
		
		this.add(upBtn1);
		this.add(upBtn2);
		this.add(downBtn1);
		this.add(downBtn2);
		
	}
	
	@Override
	protected void paintComponent(Graphics g) 	//����� ��� �޼ҵ�
	{
		// TODO Auto-generated method stub
		g.drawImage(backgroundImg.getImage(), -50, 0, null);
		setOpaque(false);
		super.paintComponent(g);
	}
	
	public void changeCardImg(int pin)	// pin ��ȣ�� �޾� �ش��ϴ� �÷��̾��� ī�� �̹����� �ٲ۴�.
	{ 
		if(pin==1)	// �� �϶�
		{
		String num1="img/card/"+Client.myCard[0].num+".jpg";
		String num2="img/card/"+Client.myCard[1].num+".jpg";
		downBtn1.setIcon(new ImageIcon(num1));
		downBtn2.setIcon(new ImageIcon(num2));
		}
		else if (pin == 0)  // �����϶�
		{
			String num1="img/card/"+Client.yourCard[0].num+".jpg";
			String num2="img/card/"+Client.yourCard[1].num+".jpg";
			upBtn1.setIcon(new ImageIcon(num1));
			upBtn2.setIcon(new ImageIcon(num2));
		}
	}
	
	public void resetCard()	// ������ �ٽ� ���ۉ��� �� ī�� 4���� Back img�� ����� �޼ҵ�
	{
		upBtn1.setIcon(cardBackImg);
		upBtn2.setIcon(cardBackImg);
		downBtn1.setIcon(cardBackImg);
		downBtn2.setIcon(cardBackImg);
	}
	
}
 
class ChatPanel extends JPanel
{
	Border bd2=BorderFactory.createEtchedBorder();
	Border alarmBorder;
	Border chatBorder;
	JTextArea alarm;
	JScrollPane alarmScroll;
	JTextArea chatShow;	//ä�� ����� ������ �Ƹ���
	JScrollPane chatShowScroll;
	
	JPanel alarmPanel;	//�˸�â�� ���� �г�
	JPanel chatPanel;	//ä�� â�� inputchatpanel�� ���� �г�
	JButton sendBtn;
	JPanel inputChatPanel;	// ������ �޽����� ��ư�� ���� �г�
	JTextArea sendText;	// ä�� �޽����� �Է��� �ؽ�Ʈ �Ƹ���
	JScrollPane chatSend;
	
	public ChatPanel()
	{
		this.setLayout(new GridLayout(2, 1));
		
		alarmBorder=BorderFactory.createEtchedBorder();
		alarmBorder=BorderFactory.createTitledBorder(alarmBorder, "�˸�â");
		chatBorder=BorderFactory.createEtchedBorder();
		chatBorder=BorderFactory.createTitledBorder(chatBorder, "ä��â");
			
		alarm=new JTextArea(8,14);		//������ ������ �˸� ǥ�� â
		alarmScroll=new JScrollPane(alarm, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		chatShow=new JTextArea(8,14);		// ��� �÷��̾���� ä�� â
		chatShowScroll=new JScrollPane(chatShow, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		alarm.setLineWrap(true);
		alarm.setWrapStyleWord(true);
		chatShow.setLineWrap(true);
		chatShow.setWrapStyleWord(true);
		
		alarmPanel=new JPanel();	//�˸�â�� ���� �г�
		alarmPanel.setLayout(new BorderLayout());
		alarmPanel.add(alarmScroll, BorderLayout.CENTER);
		alarmPanel.setBorder(alarmBorder);
		chatPanel=new JPanel();	//ä�� â�� inputchatpanel�� ���� �г�
		chatPanel.setLayout(new BorderLayout());;
		chatPanel.setBorder(chatBorder);
		sendBtn=new JButton("����");
		sendBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String str=sendText.getText();	//�Է��� �޽����� �޾� �´�.
				sendText.setText("");	// �Է��� �޽����� ��� �����.
				chatShow.append(Client.client.myId+" : "+str+"\n");	// ���� �Է��� �޽����� �ٷ� ä��â�� �����ش�.
				Client.client.sendToServer("CHAT#"+Client.client.myId+" : "+str+"\n");	//�������� ���� �Է��� �޽����� �����ش�.
			}
		});
		inputChatPanel=new JPanel();	// ������ �޽����� ��ư�� ���� �г�
		inputChatPanel.setLayout(new BorderLayout());
		sendText=new JTextArea(3,5);	// �޽����� �Է��� �ؽ�Ʈ �Ƹ���
		chatSend=new JScrollPane(sendText, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sendText.setLineWrap(true);
		sendText.setWrapStyleWord(true);
		inputChatPanel.add(chatSend, BorderLayout.CENTER);
		inputChatPanel.add(sendBtn, BorderLayout.SOUTH);
		chatPanel.add(chatShowScroll, BorderLayout.CENTER);
		chatPanel.add(inputChatPanel, BorderLayout.SOUTH);
		
 
		
		this.add(alarmPanel);
		this.add(chatPanel);
		this.setBorder(bd2);
		
	}
	
	public void setAllMoney()	// GUi Ŭ������ ���� ���ƿ� String�� ä��â ȭ�鿡 �ѷ��ش�.
	{
		alarm.setText("");
		alarm.append("���� �� : "+Client.myMoney+"\n");
		alarm.append("����� �� : "+Client.yourMoney+"\n");
		alarm.append("�� �� : "+Client.totalMoney+"\n");
	}
	
	public void setChatMsg(String str)	// GUiŬ�����κ��� ���ƿ� string�� ä��â�� ����Ѵ�.
	{
		chatShow.append(str);
	}
}
 
class ButtonPanel extends JPanel
{
	Border bd3=BorderFactory.createEtchedBorder();
	JButton btn1;
	JButton btn2;
	JButton btn3;
	JButton btn4;
	JButton btn5;
	JTextField callText;
	JPanel southBtnPanel;
	
	public ButtonPanel()
	{
		this.setLayout(new GridLayout(1, 5));
		
		btn1 = new JButton("�غ�");
		btn1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try
				{
				Client.client.out.writeUTF("READY#");
				}
				catch(IOException p)
				{
					p.printStackTrace();
				}
			}
		});
		btn2=new JButton("�� +\n +("+new Integer(Client.betMoney).toString()+")");
		btn2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try
				{
					if(Client.myMoney == Client.betMoney)	// �� �����ӴϿ� bet�Ӵ� �ݾ��� ���� ��
						throw new Exception();					// bet�ӴϷ� ���� �ݾ��� �� �����ݾ׺��� ������
				Client.client.sendToServer("CALL#" + Client.betMoney);	// bet�Ӵϸ� mymoney�ݾ����� �ٲ۴�.
				Client.myMoney -= Client.betMoney;
				Client.totalMoney += Client.betMoney;
				}
				catch(Exception m)
				{
					Client.client.sendToServer("ALLIN#" + Client.myMoney);
					Client.totalMoney += Client.myMoney;
					Client.myMoney=0;
				}
				Client.gui.showAllMoney();
				Client.gui.setBetBtn(false, 0);	// ��ư ��Ȱ��ȭ
			}
		});
		btn3=new JButton("���̽�");
		btn3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String money=callText.getText();
				callText.setText("");
				if(money.length() == 0)	// call + �� �ƹ����ڵ� �Է����� �ʰ� ������ �� �׳� �ݷ� �ν��Ѵ�.
				{
					Client.client.sendToServer("CALL#"+Client.betMoney);
					Client.myMoney -= Client.betMoney;
					Client.totalMoney += Client.betMoney;
					Client.gui.showAllMoney();
					Client.gui.setBetBtn(false, 0);	// ��ư ��Ȱ��ȭ
				}
				else
				{
					if(new Integer(money).intValue() + Client.betMoney > Client.myMoney )
					{
						JOptionPane.showMessageDialog(Client.gui, "�ݾ��� �����ϹǷ� �߰� ������ �Ұ��մϴ�.", "Error", JOptionPane.INFORMATION_MESSAGE);
					}
					else
					{
						Client.client.sendToServer("RAISE#"+money);
						Client.betMoney += new Integer(money).intValue();
						Client.myMoney -= Client.betMoney;
						Client.totalMoney += Client.betMoney;
						Client.gui.showAllMoney();
						Client.gui.setBetBtn(false, 0);	// ��ư ��Ȱ��ȭ
					}
				}
			}
		});
		btn4=new JButton("����");
		btn4.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Client.client.sendToServer("DIE#");
			}
		});
		btn5=new JButton("������");
		btn5.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Client.client.sendToServer("EXIT#");
				Client.gui.dispose();
				Client.lobbyGui=new LobbyGui();
				Client.lobbyGui.setVisible(true);
				Client.isLobby=true;
			}
		});
		
		btn2.setEnabled(false);
		btn3.setEnabled(false);
		btn4.setEnabled(false);
		
		callText=new JTextField();
		southBtnPanel=new JPanel();
		southBtnPanel.setLayout(new GridLayout(2, 1));
		southBtnPanel.add(btn3);
		southBtnPanel.add(callText);
		
		this.setBorder(bd3);
		this.add(btn1);
		this.add(btn2);
		this.add(southBtnPanel);
		this.add(btn4);
		this.add(btn5);
		
	}
	
	
	public void setStartBtn(boolean bl)
	{
		if ( bl == true)
			btn1.setEnabled(true);
		else
			btn1.setEnabled(false);
	}
	
	public void setBetBtn(boolean bl, int expt)		// ���� ���� �� ���ð��� ��ư�� ��� Ȱ��ȭ ��Ű�� �޼ҵ�
	{
		if(expt != 1)
		{
		btn2.setEnabled(bl);
		btn3.setEnabled(bl);
		btn4.setEnabled(bl);
		}
		else if(expt == 1)	// ������ ���ñݾ� ���� ���� �� ���̽� ��ư�� �ݴ´�.
		{
			btn2.setEnabled(bl);
			btn3.setEnabled(false);
			btn4.setEnabled(bl);
		}
	}
	
	public void refreshBtn()
	{
		btn2.setText("�� +\n +("+new Integer(Client.betMoney).toString()+")");
	}
}
 
class MainGui extends JFrame	// ���� GUI , ��� �г��� ��� ����.
{
	public static MainGui gui=null;
	
	public DataOutputStream out;
	
	GamePanel panel1; // ���� ��
	ChatPanel panel2;	// ä�ù�
	ButtonPanel panel3;	// ��ư��
	
	public MainGui(String str)
	{
		super(str);
		this.setBounds(360, 360, 600, 600);
		this.setLayout(new BorderLayout());
		
		panel1 = new GamePanel(); // ���� ��
		panel2 = new ChatPanel();	// ä�ù�
		panel3 =new ButtonPanel();
		//panel3 �ʱ�ȭ
		
		
		//�ʱ�ȭ ��
		
		this.add(panel1, BorderLayout.CENTER);
		this.add(panel2, BorderLayout.EAST);
		this.add(panel3, BorderLayout.SOUTH);
 
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		//this.setVisible(true);
	}
	
	public static  MainGui getInst(String str)
	{
		if(gui == null)
			gui=new MainGui(str);
		
		return gui;
	}
	
	public void showAllMoney()		//���� �ڱ�, ����� �ڱ�, �� ���� �ݾ��� �ٽ� ����Ѵ�.
	{
		panel2.setAllMoney();
	}
	
	public void showChatMsg(String str)	// ä�� �޽����� ����Ѵ�.
	{
		panel2.setChatMsg(str);
	}
	
	public void sendString(String str)	// 	GUI�� ���� �������� String�� �����Ѵ�.(��ư�� ������ ��, ä�� �޽����� �Է� �� ������ ��)
	{
		try
		{
		out.writeUTF(str);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
 
	public void changeCardImg(int pin)
	{
		panel1.changeCardImg(pin);
	}
	
	public void setStartBtn(boolean bl)
	{
		panel3.setStartBtn(bl);
	}
	
	public void setBetBtn(boolean bl, int expt)		// ���� ���� �� ���ð��� ��ư�� ��� Ȱ��ȭ ��Ű�� �޼ҵ�
	{
		panel3.setBetBtn(bl, expt);
	}
	
	public void showResultAlarm(String str)
	{
		JOptionPane.showMessageDialog(this, str, "���", JOptionPane.INFORMATION_MESSAGE);
	}
}

class LoginGui extends JFrame
{
	JLabel label;
	JTextArea idArea;	// ���� �� �г����� �Է��ϴ� �ʵ�
	JButton enterBtn;	// ���� ��ư
	private LoginGui()
	{
		super("����");
		label=new JLabel("�г���");
		idArea=new JTextArea(1, 7);
		enterBtn=new JButton("�����ϱ�");
		enterBtn.addActionListener(new ActionListener() 
		{
			
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				String id=idArea.getText();
				Client.client.myId=id;
				idArea.setText("");
				Client.client.sendToServer("ID#"+id);
				Client.lobbyGui.setVisible(true);
				dispose();
			}
		});
		this.setLayout(new FlowLayout());
		this.setBounds(120, 150, 300, 100);
		this.setVisible(true);
		
		this.add(label);
		this.add(idArea);
		this.add(enterBtn);
	}
	public static LoginGui gui=null;
	public static LoginGui getInst()
	{
		if(gui == null)
			gui=new LoginGui();
		
		return gui;
	}
}

class LobbyGui extends JFrame
{
	public static LobbyGui gui=null;
	// ��� �гε�
	JPanel upLeft;
	JPanel upBtnPanel;
	JButton makeRoomBtn;		//�游��� ��ư
	JButton joinRoomBtn;		//�� �����ϱ� ��ư
	Vector<String> vector=new Vector<String>();	//�÷��̾� ����Ʈ
	JScrollPane leftPane;	// �� List�� ��ũ��
	JList<String> playerList;
	Vector<String> roomVec=new Vector<String>();	//List�� �ֱ�����
	JScrollPane rightPane;	// �÷��̾� List�� ��ũ��
	JList<String> roomList; // *** //
	JPanel upPanel=new JPanel();	// ���� : �� List, ������: ������ List. 1x2
	JPanel downPanel=new JPanel();	// ä��â, ä�� �Է�â, ���� ��ư
	//�ϴ� �гε�
	JTextArea msgArea;	//ä�� �޽����� ������ â
	JScrollPane sc1;	// ä�� �޽��� â�� ��ũ��
	JPanel inputPan;	// �Է�â�� ���۹�ư�� ���� �г�
	JTextArea sendArea;	// ���� ä�� �޽����� �Է��ϴ� â
	JScrollPane sc2;	// ���� ä�� �޽����� â ��ũ��
	JButton sendBtn;	//�޽��� ���� ��ư
	
	Border roomBorder;
	Border playerBorder;
	Border chatBorder;
	Border chatShowBorder;
	Border chatInputBorder;
	
	MakeRoomGui makeRoomGui;
	
	public LobbyGui()
	{
		this.setLayout(new GridLayout(2, 1));		// 2�� 1��
		this.setBounds(180, 0, 800, 700);
		
		roomBorder=BorderFactory.createEtchedBorder();
		roomBorder=BorderFactory.createTitledBorder(roomBorder, "���� ��");
		playerBorder=BorderFactory.createEtchedBorder();
		playerBorder=BorderFactory.createTitledBorder(playerBorder, "�÷��̾�");
		chatBorder=BorderFactory.createEtchedBorder();
		chatShowBorder=BorderFactory.createEtchedBorder();
		chatShowBorder=BorderFactory.createTitledBorder(chatShowBorder, "ä��â");
		chatInputBorder=BorderFactory.createEtchedBorder();
		chatInputBorder=BorderFactory.createTitledBorder(chatInputBorder, "�Է�â");
		
		upPanel.setLayout(new GridLayout(1, 2));	// 1�� 2��
		playerList=new JList<String>(vector);
		rightPane=new JScrollPane(playerList);		// jlist�� ��ũ���� �߰�
		playerList.addMouseListener(new ClickListenerHandler());
		playerList.updateUI();
		playerList.setVisibleRowCount(5);		//�ִ� 5���� �����ش�
		playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	//�ѹ��� �ϳ��� ���ø� ����
		
		roomList=new JList<String>(roomVec);
		leftPane=new JScrollPane(roomList);
		roomList.addMouseListener(new ClickListenerHandler());
		roomList.updateUI();
		roomList.setVisibleRowCount(5);		//�ִ� 5���� �����ش�
		roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	//�ѹ��� �ϳ��� ���ø� ����
		
		rightPane.setBorder(playerBorder);
		leftPane.setBorder(roomBorder);
		upLeft=new JPanel();
		upLeft.setLayout(new BorderLayout());
		upBtnPanel=new JPanel();
		upBtnPanel.setLayout(new GridLayout(1, 2));
		makeRoomBtn=new JButton("�� �����");
		makeRoomBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				makeRoomGui=new MakeRoomGui();
				makeRoomGui.setVisible(true);   //���̰� �Ѵ�
				Client.isLobby=false;
			}
		});
		joinRoomBtn=new JButton("�� ����");
		joinRoomBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				Iterator<Integer> itr=Client.roomList.keySet().iterator();
				String name=roomList.getSelectedValue();
				int roomNum=-1;
				while(itr.hasNext())
				{
					roomNum=itr.next();
					String temp=Client.roomList.get(roomNum);
					if(name.compareTo(temp) == 0)
						break;
				}
				if(roomNum != -1)
				{
				Client.client.sendToServer("JOINROOM#"+roomNum);
				Client.isLobby=false;
				Client.gui=new MainGui(Client.roomList.get(roomNum));
				Client.gui.setVisible(true);
				dispose();
				}
				else
				{
					System.out.println("�ش� �� ��ȣ�� ã�� �� �����ϴ�.");
				}
			}
		});
		upBtnPanel.add(makeRoomBtn);
		upBtnPanel.add(joinRoomBtn);
		upLeft.add(leftPane, BorderLayout.CENTER);
		upLeft.add(upBtnPanel, BorderLayout.SOUTH);
		upPanel.add(upLeft);
		upPanel.add(rightPane);
		
		///////////////////////////////////////////////////////////////////////////////////////////////////
		
		downPanel.setLayout(new GridLayout(2, 1));	//2�� 1��
		downPanel.setBorder(chatBorder);
		
		msgArea=new JTextArea();
		msgArea.setText("");
		sc1=new JScrollPane(msgArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		msgArea.setLineWrap(true);
		msgArea.setWrapStyleWord(true);
		sc1.setBorder(chatShowBorder);
		
		inputPan=new JPanel();
		inputPan.setLayout(new BorderLayout());	// 1�� 2��
		sendArea=new JTextArea();
		sendArea.setText("���� �޽����� �Է��ϴ� â");
		sendArea.setLineWrap(true);
		sendArea.setWrapStyleWord(true);
		sc2=new JScrollPane(sendArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sendBtn=new JButton("����");
		sendBtn.addActionListener(new ActionListener() 
		{
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String msg=sendArea.getText();
				sendArea.setText("");
				msgArea.append(Client.client.myId+" : "+msg+"\n");
				Client.client.sendToServer("CHAT#"+Client.client.myId+" : "+msg+"\n");
			}
		});
		inputPan.add(sc2, BorderLayout.CENTER);
		inputPan.add(sendBtn, BorderLayout.EAST);
		inputPan.setBorder(chatInputBorder);
		
		//downPanel.add(sc1, BorderLayout.CENTER);
		downPanel.add(sc1);
		downPanel.add(inputPan);
		
		this.add(upPanel);
		this.add(upPanel);
		this.add(downPanel);
		super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Client.client.sendToServer("REQUEST#ALLLIST");  //����ڿ� �� ����Ʈ ������ ���� �䱸�ϳ�.
	}
	
	private class ClickListenerHandler extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			System.out.println(roomList.getSelectedValue());
		}
	}
	
	public static LobbyGui getInst()
	{
		if(gui==null)
			gui=new LobbyGui();
		
		return gui;
	}
	
	public void refreshList()		//��� ����Ʈ�� ���ΰ�ħ�Ѵ�.
	{
		roomList.setListData(roomVec);
		roomList.repaint();
		playerList.setListData(vector);
		playerList.repaint();
	}
	
	public void setPlayerList(String msg)
	{
		StringTokenizer st=new StringTokenizer(msg, ":");
		vector=new Vector<String>();
		while(st.hasMoreTokens())
		{
			vector.add(st.nextToken());
		}
	}
	
	public void setRoomList(String msg)
	{
		if(msg.length()==0)
		{
			roomVec.removeAllElements();
			return;
		}
		StringTokenizer st=new StringTokenizer(msg, ":");
		Client.roomList=new HashMap<Integer, String>();
		while(st.hasMoreTokens())
		{
			String roomName=st.nextToken();
			System.out.println("���̸� :" +roomName);
			int roomNum=new Integer(st.nextToken());
			System.out.println("�� ��ȣ : "+roomNum);
			Client.roomList.put(roomNum, roomName);
			roomVec.add(roomName);
		}
	}
	
}

class MakeRoomGui extends JFrame	// �� ����� Gui
{
	JLabel roomName;
	JTextField inPutAreaOfName;	// �� �̸� �Է��ϴ� ��
	JButton makeBtn;	//����� ��ư
	public MakeRoomGui()
	{
		super("�� �����");
		super.setLayout(new FlowLayout());
		super.setBounds(250, 300, 250, 100);
		roomName=new JLabel("�� ����");
		inPutAreaOfName=new JTextField(15);
		makeBtn=new JButton("�����");
		makeBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String name=inPutAreaOfName.getText();		// ���̸�
				Client.client.sendToServer("MAKEROOM#"+name);
				//�������� �޽����� �����Ѵ�.
				Client.gui=new MainGui(name);
				Client.gui.setVisible(true);
				dispose();
				Client.lobbyGui.setVisible(false);
			}
		});
		super.add(roomName);
		super.add(inPutAreaOfName);
		super.add(makeBtn);
		super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	
}
