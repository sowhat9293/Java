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
	public static int betMoney=0;	// 입장료 배팅금
	public static int plusMoney=0;	// 추가로 베팅하는 금액
	public static int totalMoney=0;
	public static int myMoney=0;
	public static int yourMoney=0;
	public static Card[] myCard=new Card[2];
	public static Card[] yourCard=new Card[2];
	public static HashMap<Integer, String> roomList;	// 방 번호와 방 제목을 갖고있을 자료구조
	public static boolean isLobby=true;	// 현재 플레이어가 로비면 true, 방이면 false
	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		try
		{
		roomList=new HashMap<Integer,String>();
		loginGui=LoginGui.getInst();
		gui=MainGui.getInst("섯다");
		Socket socket=new Socket("117.17.143.70", 7777);	// 서버로 접속
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
		while(true)	//서버로 부터 끝났다는 메시지가 오면 중단
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
	
	public void filteringMsg(String msg) throws IOException	// 서버로부터 받음 Msg를 인자로 넣으면 그에 해당하는 실행을 한다.
	{
		System.out.println("받음 : " +msg);
		if(msg.startsWith("CARD#") == true)	//게임 초반 내 카드 정보의 전달 일 때
		{
			msg=msg.substring(5);
			StringTokenizer st=new StringTokenizer(msg, ":");
			Client.myCard[0].num=new Integer(st.nextToken());
			Client.myCard[1].num=new Integer(st.nextToken());
			Client.yourCard[0].num=new Integer(st.nextToken());
			Client.yourCard[1].num=new Integer(st.nextToken());
			Client.gui.panel1.changeCardImg(1);
			//	차후 소스 입력
		}
		else if (msg.startsWith("MONEY#") == true)	// 내 보유금액 정보
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
			System.out.println("챗받음"+msg);
			msg=msg.substring(5);
			if(Client.isLobby == true)	//로비이면
			{
				Client.lobbyGui.msgArea.append(msg);
			}
			else		//방이면
			{
				Client.gui.showChatMsg(msg);
			}
		}
		else if(msg.startsWith("ROOM#") == true)	// 전체 방 리스트가 올 때
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
			Client.betMoney=new Integer(msg).intValue();	// 기본 콜의 금액
			Client.gui.showAllMoney();
			if(Client.myMoney - Client.betMoney < 0 && Client.betMoney != 0)
			{
				Client.gui.setBetBtn(true, 1);	//배팅 버튼을 활성화 시키되, 콜만 할 수 있다
				Client.betMoney=Client.myMoney;
				Client.gui.panel3.refreshBtn();
			}
			else
			{
			Client.gui.setBetBtn(true, 0);	// 버튼 활성화
			Client.gui.panel3.refreshBtn();
			}
		}
		else if (msg.startsWith("RESULT#") == true)
		{
			Client.gui.changeCardImg(0);   //상대패를 보여준다.
			msg=msg.substring(7);
			StringTokenizer st=new StringTokenizer(msg, ":");
			String setP1=st.nextToken();
			String setP2=st.nextToken();
			Client.gui.showResultAlarm("결과\n" +"플레이어1 : " +setP1 +"\n" + "플레이어2 : " + setP2 +"\n");
		}
		else if(msg.startsWith("THEEND#") == true)		//콜이 2번되서 게임이 끝났을 때
		{
			Client.gui.panel3.setBetBtn(false, 0);		//배팅 버튼만 무력화 시킨다.
		}
		else if (msg.startsWith("END!") == true)	// 게임 한 판이 끝났을 때 마지막으로 서버가 보내는 메시지
		{
			Client.gui.panel3.setStartBtn(true);
			Client.gui.panel1.resetCard();//재시작 할 수 있게 카드 4장을 뒤집는다.
			Client.betMoney=0;
			Client.gui.panel3.refreshBtn();
		}
		else if(msg.startsWith("ERROR#")== true)
		{
			String errorMsg=msg.substring(6);
			JOptionPane.showMessageDialog(Client.gui, errorMsg, "Error", JOptionPane.INFORMATION_MESSAGE);		// 에러창을 띄워준다.
			Client.gui.dispose();
		}
		else if(msg.startsWith("START#") == true)
		{
			Client.gui.panel3.setStartBtn(false);	//게임이 시작되면 준비 버튼을 비활성화 시킨다.
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
			Client.gui.panel1.resetCard();//재시작 할 수 있게 카드 4장을 뒤집는다.
			Client.betMoney=0;
			Client.gui.panel3.refreshBtn();
		}
	}
	
	public void sendToServer(String str)	// 서버에게 메시지를 전달 한다.
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
	Integer num;	//패의 숫자 1~20
}
 
 
class GamePanel extends JPanel
{
	Border bd1=BorderFactory.createEtchedBorder();
	ImageIcon backgroundImg = new ImageIcon("img/back.jpg");
	ImageIcon cardBackImg = new ImageIcon("img/backcard.jpg");	// 카드 뒷면
	
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
	protected void paintComponent(Graphics g) 	//배경을 찍는 메소드
	{
		// TODO Auto-generated method stub
		g.drawImage(backgroundImg.getImage(), -50, 0, null);
		setOpaque(false);
		super.paintComponent(g);
	}
	
	public void changeCardImg(int pin)	// pin 번호르 받아 해당하는 플레이어의 카드 이미지를 바꾼다.
	{ 
		if(pin==1)	// 나 일때
		{
		String num1="img/card/"+Client.myCard[0].num+".jpg";
		String num2="img/card/"+Client.myCard[1].num+".jpg";
		downBtn1.setIcon(new ImageIcon(num1));
		downBtn2.setIcon(new ImageIcon(num2));
		}
		else if (pin == 0)  // 상대방일때
		{
			String num1="img/card/"+Client.yourCard[0].num+".jpg";
			String num2="img/card/"+Client.yourCard[1].num+".jpg";
			upBtn1.setIcon(new ImageIcon(num1));
			upBtn2.setIcon(new ImageIcon(num2));
		}
	}
	
	public void resetCard()	// 게임이 다시 시작됬을 때 카드 4장을 Back img로 만드는 메소드
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
	JTextArea chatShow;	//채팅 목록을 보여줄 아리아
	JScrollPane chatShowScroll;
	
	JPanel alarmPanel;	//알림창을 싫을 패널
	JPanel chatPanel;	//채팅 창과 inputchatpanel을 넣을 패널
	JButton sendBtn;
	JPanel inputChatPanel;	// 전송할 메시지와 버튼을 넣을 패널
	JTextArea sendText;	// 채팅 메시지를 입력할 텍스트 아리아
	JScrollPane chatSend;
	
	public ChatPanel()
	{
		this.setLayout(new GridLayout(2, 1));
		
		alarmBorder=BorderFactory.createEtchedBorder();
		alarmBorder=BorderFactory.createTitledBorder(alarmBorder, "알림창");
		chatBorder=BorderFactory.createEtchedBorder();
		chatBorder=BorderFactory.createTitledBorder(chatBorder, "채팅창");
			
		alarm=new JTextArea(8,14);		//서버로 부터의 알림 표시 창
		alarmScroll=new JScrollPane(alarm, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		chatShow=new JTextArea(8,14);		// 상대 플레이어와의 채팅 창
		chatShowScroll=new JScrollPane(chatShow, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		alarm.setLineWrap(true);
		alarm.setWrapStyleWord(true);
		chatShow.setLineWrap(true);
		chatShow.setWrapStyleWord(true);
		
		alarmPanel=new JPanel();	//알림창을 싫을 패널
		alarmPanel.setLayout(new BorderLayout());
		alarmPanel.add(alarmScroll, BorderLayout.CENTER);
		alarmPanel.setBorder(alarmBorder);
		chatPanel=new JPanel();	//채팅 창과 inputchatpanel을 넣을 패널
		chatPanel.setLayout(new BorderLayout());;
		chatPanel.setBorder(chatBorder);
		sendBtn=new JButton("전송");
		sendBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String str=sendText.getText();	//입력한 메시지를 받아 온다.
				sendText.setText("");	// 입력한 메시지를 모두 지운다.
				chatShow.append(Client.client.myId+" : "+str+"\n");	// 내가 입력한 메시지를 바로 채팅창에 보여준다.
				Client.client.sendToServer("CHAT#"+Client.client.myId+" : "+str+"\n");	//서버에게 내가 입력한 메시지를 보내준다.
			}
		});
		inputChatPanel=new JPanel();	// 전송할 메시지와 버튼을 넣을 패널
		inputChatPanel.setLayout(new BorderLayout());
		sendText=new JTextArea(3,5);	// 메시지를 입력할 텍스트 아리아
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
	
	public void setAllMoney()	// GUi 클래스로 부터 날아온 String을 채팅창 화면에 뿌려준다.
	{
		alarm.setText("");
		alarm.append("나의 돈 : "+Client.myMoney+"\n");
		alarm.append("상대편 돈 : "+Client.yourMoney+"\n");
		alarm.append("판 돈 : "+Client.totalMoney+"\n");
	}
	
	public void setChatMsg(String str)	// GUi클래스로부터 날아온 string을 채팅창에 출력한다.
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
		
		btn1 = new JButton("준비");
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
		btn2=new JButton("콜 +\n +("+new Integer(Client.betMoney).toString()+")");
		btn2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try
				{
					if(Client.myMoney == Client.betMoney)	// 내 보유머니와 bet머니 금액이 같을 때
						throw new Exception();					// bet머니로 들어온 금액이 내 보유금액보다 많으면
				Client.client.sendToServer("CALL#" + Client.betMoney);	// bet머니를 mymoney금액으로 바꾼다.
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
				Client.gui.setBetBtn(false, 0);	// 버튼 비활성화
			}
		});
		btn3=new JButton("레이스");
		btn3.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String money=callText.getText();
				callText.setText("");
				if(money.length() == 0)	// call + 에 아무숫자도 입력하지 않고 눌렀을 땐 그냥 콜로 인식한다.
				{
					Client.client.sendToServer("CALL#"+Client.betMoney);
					Client.myMoney -= Client.betMoney;
					Client.totalMoney += Client.betMoney;
					Client.gui.showAllMoney();
					Client.gui.setBetBtn(false, 0);	// 버튼 비활성화
				}
				else
				{
					if(new Integer(money).intValue() + Client.betMoney > Client.myMoney )
					{
						JOptionPane.showMessageDialog(Client.gui, "금액이 부족하므로 추가 배팅이 불가합니다.", "Error", JOptionPane.INFORMATION_MESSAGE);
					}
					else
					{
						Client.client.sendToServer("RAISE#"+money);
						Client.betMoney += new Integer(money).intValue();
						Client.myMoney -= Client.betMoney;
						Client.totalMoney += Client.betMoney;
						Client.gui.showAllMoney();
						Client.gui.setBetBtn(false, 0);	// 버튼 비활성화
					}
				}
			}
		});
		btn4=new JButton("다이");
		btn4.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Client.client.sendToServer("DIE#");
			}
		});
		btn5=new JButton("나가기");
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
	
	public void setBetBtn(boolean bl, int expt)		// 게임 시작 후 배팅관련 버튼을 모두 활성화 시키는 메소드
	{
		if(expt != 1)
		{
		btn2.setEnabled(bl);
		btn3.setEnabled(bl);
		btn4.setEnabled(bl);
		}
		else if(expt == 1)	// 내돈이 배팅금액 보다 적을 때 레이스 버튼을 닫는다.
		{
			btn2.setEnabled(bl);
			btn3.setEnabled(false);
			btn4.setEnabled(bl);
		}
	}
	
	public void refreshBtn()
	{
		btn2.setText("콜 +\n +("+new Integer(Client.betMoney).toString()+")");
	}
}
 
class MainGui extends JFrame	// 메인 GUI , 모든 패널을 담고 있음.
{
	public static MainGui gui=null;
	
	public DataOutputStream out;
	
	GamePanel panel1; // 게임 판
	ChatPanel panel2;	// 채팅방
	ButtonPanel panel3;	// 버튼들
	
	public MainGui(String str)
	{
		super(str);
		this.setBounds(360, 360, 600, 600);
		this.setLayout(new BorderLayout());
		
		panel1 = new GamePanel(); // 게임 판
		panel2 = new ChatPanel();	// 채팅방
		panel3 =new ButtonPanel();
		//panel3 초기화
		
		
		//초기화 끝
		
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
	
	public void showAllMoney()		//나의 자금, 상대편 자금, 총 배팅 금액을 다시 출력한다.
	{
		panel2.setAllMoney();
	}
	
	public void showChatMsg(String str)	// 채팅 메시지를 출력한다.
	{
		panel2.setChatMsg(str);
	}
	
	public void sendString(String str)	// 	GUI로 부터 서버에게 String을 전달한다.(버튼이 눌렸을 때, 채팅 메시지를 입력 후 전달할 때)
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
	
	public void setBetBtn(boolean bl, int expt)		// 게임 시작 후 배팅관련 버튼을 모두 활성화 시키는 메소드
	{
		panel3.setBetBtn(bl, expt);
	}
	
	public void showResultAlarm(String str)
	{
		JOptionPane.showMessageDialog(this, str, "결과", JOptionPane.INFORMATION_MESSAGE);
	}
}

class LoginGui extends JFrame
{
	JLabel label;
	JTextArea idArea;	// 입장 할 닉네임을 입력하는 필드
	JButton enterBtn;	// 입장 버튼
	private LoginGui()
	{
		super("섯다");
		label=new JLabel("닉네임");
		idArea=new JTextArea(1, 7);
		enterBtn=new JButton("입장하기");
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
	// 상단 패널들
	JPanel upLeft;
	JPanel upBtnPanel;
	JButton makeRoomBtn;		//방만들기 버튼
	JButton joinRoomBtn;		//방 접속하기 버튼
	Vector<String> vector=new Vector<String>();	//플레이어 리스트
	JScrollPane leftPane;	// 방 List의 스크롤
	JList<String> playerList;
	Vector<String> roomVec=new Vector<String>();	//List에 넣기위함
	JScrollPane rightPane;	// 플레이어 List의 스크롤
	JList<String> roomList; // *** //
	JPanel upPanel=new JPanel();	// 왼쪽 : 방 List, 오른쪽: 접속자 List. 1x2
	JPanel downPanel=new JPanel();	// 채팅창, 채팅 입력창, 전송 버튼
	//하단 패널들
	JTextArea msgArea;	//채팅 메시지를 보여줄 창
	JScrollPane sc1;	// 채팅 메시지 창의 스크롤
	JPanel inputPan;	// 입력창과 전송버튼을 담을 패널
	JTextArea sendArea;	// 보낼 채팅 메시지를 입력하는 창
	JScrollPane sc2;	// 보낼 채팅 메시지의 창 스크롤
	JButton sendBtn;	//메시지 전송 버튼
	
	Border roomBorder;
	Border playerBorder;
	Border chatBorder;
	Border chatShowBorder;
	Border chatInputBorder;
	
	MakeRoomGui makeRoomGui;
	
	public LobbyGui()
	{
		this.setLayout(new GridLayout(2, 1));		// 2행 1열
		this.setBounds(180, 0, 800, 700);
		
		roomBorder=BorderFactory.createEtchedBorder();
		roomBorder=BorderFactory.createTitledBorder(roomBorder, "게임 방");
		playerBorder=BorderFactory.createEtchedBorder();
		playerBorder=BorderFactory.createTitledBorder(playerBorder, "플레이어");
		chatBorder=BorderFactory.createEtchedBorder();
		chatShowBorder=BorderFactory.createEtchedBorder();
		chatShowBorder=BorderFactory.createTitledBorder(chatShowBorder, "채팅창");
		chatInputBorder=BorderFactory.createEtchedBorder();
		chatInputBorder=BorderFactory.createTitledBorder(chatInputBorder, "입력창");
		
		upPanel.setLayout(new GridLayout(1, 2));	// 1행 2열
		playerList=new JList<String>(vector);
		rightPane=new JScrollPane(playerList);		// jlist에 스크롤을 추가
		playerList.addMouseListener(new ClickListenerHandler());
		playerList.updateUI();
		playerList.setVisibleRowCount(5);		//최대 5개만 보여준다
		playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	//한번에 하나의 선택만 가능
		
		roomList=new JList<String>(roomVec);
		leftPane=new JScrollPane(roomList);
		roomList.addMouseListener(new ClickListenerHandler());
		roomList.updateUI();
		roomList.setVisibleRowCount(5);		//최대 5개만 보여준다
		roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	//한번에 하나의 선택만 가능
		
		rightPane.setBorder(playerBorder);
		leftPane.setBorder(roomBorder);
		upLeft=new JPanel();
		upLeft.setLayout(new BorderLayout());
		upBtnPanel=new JPanel();
		upBtnPanel.setLayout(new GridLayout(1, 2));
		makeRoomBtn=new JButton("방 만들기");
		makeRoomBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				makeRoomGui=new MakeRoomGui();
				makeRoomGui.setVisible(true);   //보이게 한다
				Client.isLobby=false;
			}
		});
		joinRoomBtn=new JButton("방 들어가기");
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
					System.out.println("해당 방 번호를 찾을 수 없습니다.");
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
		
		downPanel.setLayout(new GridLayout(2, 1));	//2행 1열
		downPanel.setBorder(chatBorder);
		
		msgArea=new JTextArea();
		msgArea.setText("");
		sc1=new JScrollPane(msgArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		msgArea.setLineWrap(true);
		msgArea.setWrapStyleWord(true);
		sc1.setBorder(chatShowBorder);
		
		inputPan=new JPanel();
		inputPan.setLayout(new BorderLayout());	// 1행 2열
		sendArea=new JTextArea();
		sendArea.setText("보낼 메시지를 입력하는 창");
		sendArea.setLineWrap(true);
		sendArea.setWrapStyleWord(true);
		sc2=new JScrollPane(sendArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sendBtn=new JButton("전송");
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
		Client.client.sendToServer("REQUEST#ALLLIST");  //사용자와 방 리스트 보내줄 것을 요구하낟.
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
	
	public void refreshList()		//모든 리스트를 새로고침한다.
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
			System.out.println("방이름 :" +roomName);
			int roomNum=new Integer(st.nextToken());
			System.out.println("방 번호 : "+roomNum);
			Client.roomList.put(roomNum, roomName);
			roomVec.add(roomName);
		}
	}
	
}

class MakeRoomGui extends JFrame	// 방 만들기 Gui
{
	JLabel roomName;
	JTextField inPutAreaOfName;	// 방 이름 입력하는 곳
	JButton makeBtn;	//만들기 버튼
	public MakeRoomGui()
	{
		super("방 만들기");
		super.setLayout(new FlowLayout());
		super.setBounds(250, 300, 250, 100);
		roomName=new JLabel("방 제목");
		inPutAreaOfName=new JTextField(15);
		makeBtn=new JButton("만들기");
		makeBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String name=inPutAreaOfName.getText();		// 방이름
				Client.client.sendToServer("MAKEROOM#"+name);
				//서버에게 메시지를 전달한다.
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
