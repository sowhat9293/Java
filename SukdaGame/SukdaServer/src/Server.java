import java.util.HashSet;
import java.awt.*;
import java.io.*;
import java.math.*;
import java.util.Random;
import java.util.Scanner;
import java.util.HashMap;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

import javax.swing.*;

import java.awt.event.*;

import javax.swing.border.*;

import java.util.StringTokenizer;
import java.net.*;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Iterator;
 
class Card
{
	Integer num;	//���� ���� 1~20
}

class BetMoney
{
	int money;
	public BetMoney()
	{
		money=0;
	}
}

class Room
{
	public String roomName;
	public int roomNum;
	public int numOfPlayer=1;	// �� �濡 ���� ���� �÷��̾��� ���� 1<=x<=2
	
	public void setRoomName(String name)
	{
		roomName=name;
	}
	public void setRoomNumber(int num)
	{
		roomNum=num;
	}
	public boolean addJoinPlayer()
	{
		if(numOfPlayer == 2)
			return false;	// �̹� Ǯ ��
		
		numOfPlayer++;
		return true;
	}
	public void exitPlayer()		//�÷��̾ ������ �ٽ� 1�� �ʱ�ȭ
	{
		numOfPlayer=1;
	}
}
 
class Player
{
	RankName rankName=new RankName();
	Card[] card=new Card[2];
	double rank;	//������ ������ ����
	String rankStr;		//���� ������ ������ ���ڿ�
	public int money;	//���� �ڱ�
	public Player()
	{
		for(int i=0; i<2; i++)
			card[i]=new Card();
		money=0;
	}
	
	public void setMoney(int money)
	{
		this.money=money;
	}
	public void setRank(double rank)
	{
		this.rank=rank;
		String rm=rankName.hMap.get((int)rank);	// ex) ��, �� ������ ����
		try
		{
		int cardOne=card[0].num;
		if(cardOne > 10)
			cardOne -= 10;
		int cardTwo=card[1].num;
		
		if(cardTwo > 10)
			cardTwo -= 10;
		
		switch((int)rank)
		{
			case 4 :	// ���� ��
				rankStr=cardOne+rm;
				break;
			case 11 :	// �� �� ��
				int num=(cardOne+cardTwo);
				if(num>10)
					num -= 10;
					rankStr=num+rm;
				break;
			default :
				rankStr=rm;
		}
		}	// try
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
 
class Dealer
{
	RankName rankName=new RankName();
	public HashMap<Integer, Player> playerMap;	//�÷��̾���� ������ HashMap, int������ playernumber
	Random dealer;
	public Room room=new Room();	// �� �̸���, �� ��ȣ�� ������ �ν��Ͻ�
	HashSet<Integer> cardSet;	// �÷��̾� �θ��� �������� ī�� 4���� ����� ����
	public int p1Num;	//������ �÷��̾� ��ȣ
	public int p2Num;	// �Խ�Ʈ�� �÷��̾� ��ȣ
	public int totalMoney=0;
	public int wantDiePlayerNumber=0;
	public HashMap<Integer, BetMoney> betMoneySet;	// �÷��̾ ������ �ݾ��� �ִ� ��
	public int count=0;	// �� ī��Ʈ, 2�� �Ǹ� ��������
	public int ready=0;	//2���Ǹ� ��� �����Ѱ��̴�.

	public Dealer(int p1Num)	// ���� ��ȣ
	{
		this.p1Num=p1Num;
		Player owner=new Player();
		playerMap=new HashMap<Integer, Player>();
		betMoneySet=new HashMap<Integer, BetMoney>();
		playerMap.put(p1Num, owner);	// �÷��̾� 1
		owner.setMoney(Server.vector.get(p1Num).money);
		betMoneySet.put(p1Num, new BetMoney());
		dealer=new Random();
		dealer.setSeed(System.currentTimeMillis());	// �ð��� ���� �õ带 �־ ��� �ٸ� ����  ����
		cardSet=new HashSet<Integer>();
	}
	
	public boolean settingGuest(int p2Num)		//���� ����
	{
		if(room.addJoinPlayer())
		{
		Player guest=new Player();
		playerMap.put(p2Num, guest);	// �÷��̾� 2
		guest.setMoney(Server.vector.get(p2Num).money);
		betMoneySet.put(p2Num, new BetMoney());
		this.p2Num=p2Num;
		return true;
		}
		else
			return false;		// �̹� Ǯ���̸� false�� ��ȯ
	}
	
	public void setRoomInst(String name, int num)
	{
		room.setRoomName(name);
		room.setRoomNumber(num);
	}
 
	public void deal()	// ī�带 ������ �ְ� ��ũ�� �����ϴ� �޼ҵ�, ���� ��������� ������ ���۵Ǹ� ��ó�� ����Ǿ��ϴ� �޼ҵ�, 
	{
				for(int j=0; j<2; j++)
				{
				playerMap.get(p1Num).card[j].num=getCard();
				playerMap.get(p2Num).card[j].num=getCard();
				}
			setRank();
	}
	
	public Integer getCard()
	{
		boolean bl;
		Integer num=null;	// ���� �Լ����� ���� �ϳ��� ���� (1~20)
		while(true)
		{
			num=( dealer.nextInt() % 20 ) + 1 ;	//1~20������ �� , Auto-Boxing
			if(num <= 0)
				continue;
			bl=cardSet.add(num);
			if(bl == true)
				break;
		}
		
		return num;
	}
	
	public double decode(Card[] card)	// ������ �ǵ��ؼ� ��ȯ���ִ� �޼ҵ�, setRank()���� ����ϱ� ���� �޼ҵ�
	{
		int cardOne=card[0].num;	// Auto-UnBoxing
		int cardTwo=card[1].num;	// �ι�° ����
		return RankArray.rankArr[cardOne-1][cardTwo-1];
	}
	
	public void setRank()
	{
			playerMap.get(p1Num).setRank(decode(playerMap.get(p1Num).card));
			playerMap.get(p2Num).setRank(decode(playerMap.get(p2Num).card));
	}
	public String judgeMent()		// ���ڿ� ���ڸ� �����ִ� �޼ҵ�, ���ڸ� ��ȯ, ������ ������ ���������� �����ؾ��ϴ� �޼ҵ�
	{			
				if(playerMap.get(p1Num).rank == playerMap.get(p2Num).rank)	// ���� �Ȱ��� �� ����
				{
					return "WIN#DRAW";
				}
				if(playerMap.get(p1Num).rank == 30.0)	// �÷��̾�1�� �籸���� �� �� ��
				{
					if(playerMap.get(p2Num).rank >= 5.0)
					{
						return "WIN#DRAW";
					}
				}
				else if (playerMap.get(p2Num).rank == 30.0)	//�÷��̾�2�� �籸���� �� �� ��
				{
					if(playerMap.get(p1Num).rank >= 5.0)
					{
						return "WIN#DRAW";
					}
				}
				
				if(playerMap.get(p1Num).rank == 40.0)
				{
					if(playerMap.get(p2Num).rank >= 2.0)
					{
						return "WIN#DRAW";
					}
				}
				else if ( playerMap.get(p2Num).rank == 40.0)
				{
					if(playerMap.get(p1Num).rank >= 2.0)
					{
						return "WIN#DRAW";
					}
				}
				
				if ( playerMap.get(p1Num).rank == 20.0)	// ������
				{
					if(playerMap.get(p2Num).rank >= 4.1 && playerMap.get(p2Num).rank <= 4.9)
					{
						this.setPlayerMoney(p1Num, playerMap.get(p1Num).money + this.totalMoney);
						this.totalMoney=0;
						return "WIN#"+p1Num;
					}
					else if (playerMap.get(p2Num).rank == 12)	// ��밡 �����϶�
					{
						return "WIN#DRAW";
					}
					else
					{
						this.setPlayerMoney(p2Num, playerMap.get(p2Num).money + this.totalMoney);
						this.totalMoney=0;
						return "WIN#"+p2Num;
					}
				}
				else if ( playerMap.get(p2Num).rank == 20.0)	// ������
				{
					if(playerMap.get(p1Num).rank >= 4.1 && playerMap.get(p1Num).rank <= 4.9)
					{
						this.setPlayerMoney(p2Num, playerMap.get(p2Num).money + this.totalMoney);
						this.totalMoney=0;
						return "WIN#"+p2Num;
					}
					else if (playerMap.get(p1Num).rank == 12)	// ��밡 �����϶�
					{
						return "WIN#DRAW";
					}
					else
					{
						this.setPlayerMoney(p1Num, playerMap.get(p1Num).money + this.totalMoney);
						this.totalMoney=0;
						return "WIN#"+p1Num;
					}
				}
				
				if(playerMap.get(p1Num).rank < playerMap.get(p2Num).rank)
				{
					this.setPlayerMoney(p1Num, playerMap.get(p1Num).money + this.totalMoney);
					this.totalMoney=0;
					return "WIN#"+p1Num;
				}
				else if ( playerMap.get(p1Num).rank > playerMap.get(p2Num).rank)
				{
					this.setPlayerMoney(p2Num, playerMap.get(p2Num).money + this.totalMoney);
					this.totalMoney=0;
					return "WIN#"+p2Num;
				}
				else
				{
					return "WIN#DRAW";
				}
 
 
	}
	
	public String getCard(int playerNum)
	{
		return playerMap.get(playerNum).card[0].num.toString() + ":" +
				playerMap.get(playerNum).card[1].num.toString() ;
	}
	
	public void refresh()	// ���⸦ �� �� �ְ� �� HashSet�� ��� ����� �޼ҵ�, ���ñݾ׵� 0���� �ʱ�ȭ ���ش�.
	{
		for(int i=0; i<2; i++)
		{
		cardSet.remove(playerMap.get(p1Num).card[i].num);
		cardSet.remove(playerMap.get(p2Num).card[i].num);
		}
		betMoneySet.get(p1Num).money=0;
		betMoneySet.get(p2Num).money=0;
		this.count=0;
		this.ready=0;
	}
	
/*	public static void checkAllReady()	// �� �÷��̾ Ready �ߴ��� Ȯ�� �� �ִ� �޼ҵ�
	{												// static ���� ������ main �޼ҵ尡 static �̱� �����̴�. static�� ���� �ν��Ͻ��� �޼ҵ忡 ���� �Ұ�
		while(true)
		{
			if(Server.ready >= 2)
				break;
		}
	}*/
	
	public boolean call(int playerNum, int money)	// count ���� 2���Ǹ� false�� ��ȯ, �ƴϸ� true�� ��ȯ
	{
		count++;

		totalMoney += money;
		playerMap.get(playerNum).money -= money;
		if(playerNum == p1Num)
			betMoneySet.get(p1Num).money =	betMoneySet.get(p2Num).money - betMoneySet.get(p1Num).money;
		else
			betMoneySet.get(p2Num).money =	betMoneySet.get(p1Num).money - betMoneySet.get(p2Num).money;	
		sendMoneyMsg();
		if(count == 2)
		{
			return false;
		}
		return true;
	}
	
	public void raise(int playerNum, int money)
	{
		if(playerNum == p1Num)
		{
			totalMoney += (betMoneySet.get(p2Num).money - betMoneySet.get(p1Num).money) + money;
			playerMap.get(p1Num).money -= (betMoneySet.get(p2Num).money - betMoneySet.get(p1Num).money)+money;
			betMoneySet.get(p1Num).money = (betMoneySet.get(p2Num).money - betMoneySet.get(p1Num).money) + money;
		}
		else
		{
			totalMoney += (betMoneySet.get(p1Num).money - betMoneySet.get(p2Num).money) + money;
			playerMap.get(p2Num).money -= (betMoneySet.get(p1Num).money - betMoneySet.get(p2Num).money)+money;
			betMoneySet.get(p2Num).money = (betMoneySet.get(p1Num).money - betMoneySet.get(p2Num).money) + money;
		}
		sendMoneyMsg();
		count = 0;
	}
	
	public void allIn(int playerNum)
	{
		this.totalMoney += betMoneySet.get(playerNum).money;
		betMoneySet.get(playerNum).money=0;
		this.count=2;
		this.endingGame();
	}
	
	public void sendMoneyMsg()	// ��� �÷��̾�� �ݾ� ���� �޽����� ������
	{
		Server.vector.get(p1Num).sendMsg("MONEY#"+playerMap.get(p1Num).money+":"+playerMap.get(p2Num).money+":"+totalMoney);
		Server.vector.get(p2Num).sendMsg("MONEY#"+playerMap.get(p2Num).money+":"+playerMap.get(p1Num).money+":"+totalMoney);
	}
	
	public void sendCardNumMsg()	//ī�� ���� ������ ����
	{
		Server.vector.get(p1Num).sendMsg("CARD#"+getCard(p1Num)+":"+getCard(p2Num));
		Server.vector.get(p2Num).sendMsg("CARD#"+getCard(p2Num)+":"+getCard(p1Num));
	}
	
	public void StartGame()	// ī�� ������ �����ش�.
	{
		this.sendMoneyMsg();
		this.deal();
		Server.vector.get(p1Num).sendMsg("CARD#"+getCard(p1Num)+":"+getCard(p2Num));
		Server.vector.get(p2Num).sendMsg("CARD#"+getCard(p2Num)+":"+getCard(p1Num));
		Server.vector.get(p1Num).sendMsg("SET#BTNON");
	}
	
	public void endingGame()	// ī�带 �������ְ�, �������� �� ���������� �����ϴ� �޼ҵ�
	{
		Server.vector.get(p1Num).sendMsg("THEEND#");
		Server.vector.get(p2Num).sendMsg("THEEND#");		// ���� ��ư ����ȭ ��Ű�� ����
		String result=judgeMent();
		String setP1=playerMap.get(p1Num).rankStr;
		String setP2=playerMap.get(p2Num).rankStr;
		Server.vector.get(p1Num).sendMsg(result);
		Server.vector.get(p1Num).sendMsg("RESULT#"+setP1+":"+setP2);
		Server.vector.get(p2Num).sendMsg(result);
		Server.vector.get(p2Num).sendMsg("RESULT#"+setP1+":"+setP2);

		sendMoneyMsg();
		Server.vector.get(p1Num).sendMsg("END!");
		Server.vector.get(p2Num).sendMsg("END!");
		refresh();// ī���� �ʱ�ȭ
	}
 
	public void setPlayerMoney(int playerNum, int money)	// �ش� �÷��̾��� �ݾ� ������ �ϰ������� �ٲ۴�
	{
		Server.vector.get(playerNum).setMoney(money);
		playerMap.get(playerNum).setMoney(money);
	}
	
}
 
public class Server
{
	public static Socket socket;
	public static ServerSocket server;
	public static Vector<ClientThread> vector=new Vector<ClientThread>();
	public static HashMap<Integer, Dealer> room=new HashMap<Integer, Dealer>();	// ����� ������ �ؽ�
	public static boolean requestExit=false;	// �÷��̾ ���� ���� ������ ��ư�� ������ false�� true�� �ȴ�.
	public static int totalPlayerNum=0;	// �� ������ ��
	public static int totalRoomNum=1;	// �� �� ����
	public static ClientAcceptThread acceptThread;


	
	public static void main(String[] args) 
	{
		socket=null;
		acceptThread=new ClientAcceptThread();
		try
		{
		server=new ServerSocket(7777);	//������ �����
		System.out.println("���� �غ� �Ϸ�");
		while(true)
		{
			ClientAcceptThread temp=new ClientAcceptThread();
			temp.start();
			temp.join();
		}
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
			for(int i=0; i<totalPlayerNum; i++)
				vector.get(i).socket.close();
			server.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void makeRoom(int playerNum, String roomName)		//������ �÷��̾� ��ȣ�� �޴´�.
	{
		Dealer temp=new Dealer(playerNum);
		temp.setRoomInst(roomName, Server.totalRoomNum);		// ���̸��� ������ �ȵȴ�.
		room.put(Server.totalRoomNum, temp);	// �� ���� ����� �� �ѹ��� �ο��Ѵ�. �� �ѹ��� 1���� ����.
		vector.get(playerNum).room=temp;	// ���� �� �ν��Ͻ��� ���� �÷��̾��� room�ν��Ͻ��� �����ϰԲ� �Ѵ�.
		vector.get(playerNum).roomNum=Server.totalRoomNum;	// ���� �÷��̾��� �� �ѹ��� �ش� �� �ѹ��� �ٲ��ְ� ��ѹ��� ������Ų��.
		Server.totalRoomNum++;
		showAllRoomList();  //���� ����������Ƿ�, �ٽ� �������� �����ش�.
	}
	
	public static void enterTheRoom(int roomNum, int playerNum)	// ������� �ִ� �濡 �����ϴ� �޼ҵ�
	{
		Dealer inst=room.get(roomNum);
		boolean bl=inst.settingGuest(playerNum);
		if(bl)
		{
		vector.get(playerNum).room=inst;
		vector.get(playerNum).roomNum=roomNum;
		}
		else
			vector.get(playerNum).sendMsg("ERROR#Ǯ���Դϴ�.");
	}
	
	public static void exitRoom(int roomNum, int playerNum)	// �÷��̾ ���� ������ �������� ���� �����ٴ� �޽����� ������, �� �޽����� ���� ������ exitRoom�� ���Ͽ� �ش� �÷��̾��� ������ �ν��Ͻ��� �����Ѵ�.
	{
		Dealer inst=room.get(roomNum);

		if(inst==null)
		{
			vector.get(playerNum).room=null;
			vector.get(playerNum).roomNum=0;		// ���� ��ȣ�� �ٲ۴�.
			showAllRoomList();
			return;
		}
		ClientThread owner=vector.get(inst.p1Num);	//����
		ClientThread guest=vector.get(inst.p2Num);	//�Խ�Ʈ
		if(inst.p1Num == playerNum)	//�������� �ϴ� �÷��̾ ���� �� ��, ����
		{
			owner.room=null;
			owner.roomNum=0;
			owner.setMoney(inst.playerMap.get(inst.p1Num).money);
			guest.sendMsg("BOOM#");		//������ �˸��� �޽����� �Խ�Ʈ���� �����Ѵ�.
			room.remove(roomNum);	//���� �����Ѵ�.
		}
		else if(inst.p2Num == playerNum)	//�������� �ϴ� �÷��̾ �Խ�Ʈ �� �� ���� �״�� ����
		{
			guest.setMoney(inst.playerMap.get(inst.p2Num).money);
			owner.sendMsg("GUESTOUT#");		//�Խ�Ʈ�� ���� �������� �˸���.
			inst.refresh();
			inst.betMoneySet.remove(playerNum);	//������ �÷��̾��� �ν��Ͻ��� �����.
			inst.playerMap.remove(playerNum);
			inst.room.exitPlayer();		//�������Ƿ� ���� �ο� ���� 1�� �����.
		}
		sendListData();
		guest.room=null;
		guest.roomNum=0;		// ���� ��ȣ�� �ٲ۴�.
	}
	
	public static void showAllRoomList()		// �÷��̾�鿡�� �� ����Ʈ�� �������ش�.
	{
		Iterator<Integer> itr=room.keySet().iterator();
		while(itr.hasNext())
		{
			Dealer temp=room.get(itr.next());
			sendBroadCast("ROOMLIST#"+temp.room.roomName+":"+temp.room.roomNum);
		}
	}

	
	public static void sendBroadCast(String msg)		// ��� ����ڿ��� MSG�� ����
	{
		for(int i=0; i<Server.totalPlayerNum; i++)
			vector.get(i).sendMsg(msg);
	}
	
	public static void sendListData()	// ��ε�ĳ��Ʈ�� ������ ID�� ��List�� �����ش�.
	{
		String idData="IDLIST#";
		for(int i=0; i<(totalPlayerNum-1); i++)
			idData += (vector.get(i).id+":");
		idData += vector.get(totalPlayerNum-1).id;
		
		String roomList="ROOMLIST#";
		Iterator<Integer> itr=room.keySet().iterator();
		while(itr.hasNext())
		{
			int num=itr.next();
			roomList += (room.get(num).room.roomName+":"+room.get(num).room.roomNum+":");
		}
		roomList=roomList.substring(0,(roomList.length()-1));
		if(roomList.length()==8)
			roomList +="#";
		
		sendBroadCast(idData);
		sendBroadCast(roomList);
	}
}

class ClientAcceptThread extends Thread
{
	public void run()
	{
		ClientThread temp=new ClientThread();
		try
		{
		temp.setSocket(Server.server.accept());
		System.out.println("�÷��̾� ����");
		Server.vector.add(temp);		// 0���� ����
		temp.playerNum=Server.totalPlayerNum++;		//��������
		System.out.println("������ ��ȣ : "+Server.vector.get(Server.totalPlayerNum-1).playerNum);
		temp.start();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
 
class ClientThread extends Thread	//������ �����־���� Ŭ����
{
	Socket socket=null;
	DataInputStream in;
	DataOutputStream out;
	String id;
	public int money=50000;
	public int playerNum;		// �ڽ��� �÷��̾� ��ȣ, vector�� Index ���̴�.
	public int roomNum=0;			// �÷��̾ �ҼӵǾ� �ִ� ROOM �ѹ�, 0�� �κ��
	boolean bl=true;
	
	public Dealer room=null;	// �⺻ null�̸� �濡 ��� �� ��� �ش� Dealer �ν��Ͻ��� ����.
	
	public void run()
	{
		while(bl)
		{
			try
			{
			String msg=in.readUTF();
			System.out.println("���� : "+msg);
			if(msg.startsWith("CHAT#") == true)	//ä�� �޽��� �� ��
			{
				sendToRoom(msg);
			}
			else if (msg.startsWith("READY#") == true)
			{
				room.ready++;
				if(room.ready==2)
					room.StartGame();
			}
			else if (msg.startsWith("CALL#") == true)
			{
				int money=new Integer(msg.substring(5)).intValue();
				boolean callIsTwo=room.call(playerNum, money);
				if(callIsTwo == true)	
				{
					sendToRoom("BET#0");
				}
				else if(callIsTwo == false)
				{
					room.endingGame();
				}
			}
			else if(msg.startsWith("ID#") == true)	// ó�� ���� �� �г��� ����
			{
					id=msg.substring(3);
					Server.sendListData();  //���ο� �÷��̾ ���������Ƿ� List�� �ٽ� �����ش�.
			}
			else if(msg.startsWith("ALLIN#") == true)
			{
				int money=new Integer(msg.substring(6)).intValue();
				room.allIn(this.playerNum);
			}
			else if (msg.startsWith("RAISE#")==true)	//�ݿ� ���� �߰��Ҷ�
			{
					int money=new Integer(msg.substring(6)).intValue();
					room.raise(playerNum, money);
					sendToRoom("BET#"+money);
			}
			else if (msg.startsWith("THEEND") == true)	// ���� ���� ���϶��� ���� �����带 ���� ��Ų��.
			{
/*				for(int i=0; i<=1; i++)
					Server.vector.get(i).bl=false;*/
			}
			else if (msg.startsWith("EXIT#") == true)
			{
				//sendToRoom("END!");
				Server.exitRoom(this.roomNum, this.playerNum);
				Server.sendListData();
			}
			else if(msg.startsWith("MAKEROOM#") == true)		//���� ������� �� ��, #���� �� �̸�
			{
				String roomName=msg.substring(9);
				Server.makeRoom(this.playerNum, roomName);
			}
			else if(msg.startsWith("JOINROOM#") == true)	// �濡 �����ϰ��� �� ��, #���� �� �ѹ�
			{
				int roomNum=new Integer(msg.substring(9)).intValue();
				Server.enterTheRoom(roomNum, playerNum);
			}
			else if(msg.startsWith("EXITROOM#") == true)// ���� �������� �� ��, #���� �� �ѹ�
			{
				int roomNum=new Integer(msg.substring(9)).intValue();
				Server.exitRoom(roomNum, playerNum);
			}
			else if (msg.startsWith("DIE#")==true)
			{
				if(this.playerNum == room.p1Num)		//�÷��̾� 1�� die�� ���� ��
				{
					room.wantDiePlayerNumber=room.p2Num;
				}
				else	// 2�� die�� ���� ��
				{
					room.wantDiePlayerNumber=room.p1Num;
				}
				Server.requestExit=true;
				sendMsg("WIN#P"+room.wantDiePlayerNumber);
				sendToRoom("WIN#P"+room.wantDiePlayerNumber);
				sendMsg("THEEND#");
				sendToRoom("THEEND#");
			}
			else if(msg.startsWith("REQUEST#ALLLIST")== true)
			{
				Server.sendListData();
			}

			}
			catch(IOException e)
			{
				e.printStackTrace();
			}

		}
	}
	
	
	public void setSocket(Socket socket)
	{
		this.socket=socket;
		try
		{
		this.in=new DataInputStream(this.socket.getInputStream());
		this.out=new DataOutputStream(this.socket.getOutputStream());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setMoney(int money)
	{
		this.money=money;
	}
	
	public void setTurn()	// ������ ���Ѵ� true�� �Ѱ��ָ� ó�� ������ �ȴ�.
	{
		sendMsg("SET#BTNON");
	}
	
	public void setRoom(Dealer inst)
	{
		this.room=inst;
	}
	
	public void sendMsg(String str)	// �ش� �������� �÷��̾�� �޽��� ����
	{
		try
		{
		out.writeUTF(str);
		out.flush();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void sendToRoom(String str)	//���� ������ ���� ���� ����鿡�� �޽��� ����
	{
		for(int i=0; i<Server.totalPlayerNum; i++)
		{
			System.out.println(i+"�� �ε��� �÷��̾� Ȯ��");
			if((this.roomNum == Server.vector.get(i).roomNum) && (this.playerNum != Server.vector.get(i).playerNum))
			{
				System.out.println(i+"�� �ε������� ������");
				Server.vector.get(i).sendMsg(str);
			}
		}
	}
	
	public void setPlayerNum(int num)
	{
		this.playerNum=num;
	}
}
 
class RankName
{
	public HashMap<Integer, String> hMap=new HashMap<Integer, String>();
	public RankName()
	{
		hMap.put(1, "���ȱ���");
		hMap.put(2, "���ȱ���");
		hMap.put(3, "�ϻﱤ��");
		hMap.put(4, "��");
		hMap.put(5, "�˸�");
		hMap.put(6, "����");
		hMap.put(7, "����");
		hMap.put(8, "���");
		hMap.put(9, "���");
		hMap.put(10, "����");
		hMap.put(11, "��");
		hMap.put(12, "����");
		hMap.put(20, "������");
		hMap.put(30, "�籸");
		hMap.put(40, "���ֱ��� �籸");
	}
}
 
class RankArray
{
	public static double[][] rankArr=new double[][] {	// ���� �� �� ��� ���� Index�� ���� +1�� �ؾ��Ѵ�.
		{-1 ,5 ,3 ,6 ,11.4 ,11.3 ,11.2 ,2, 7, 8, 4.9, 5, 11.6, 6, 11.4, 11.3, 11.2, 11.1, 7, 8},
		{5, -1, 11.5, 11.4, 11.3, 11.2, 11.1, 12, 11.9, 11.8, 5, 4.8, 11.5, 11.4, 11.3, 11.2, 11.1, 12, 11.9, 11.8},
		{3, 11.5, -1, 11.3, 11.2, 11.1, 20, 1, 11.8, 11.7, 11.6, 11.5, 4.7, 11.3, 11.2, 11.1, 20, 11.9, 11.8, 11.7},
		{6, 11.4, 11.3, -1, 11.1, 10, 11.9, 11.8, 40, 9, 6, 11.4, 11.3, 4.6, 11, 10, 11.9, 11.8, 30, 9},
		{11.4,11.3,11.2,11.1,-1,11.9,11.8,11.7,11.6,11.5,11.4,11.3,11.2,11.1,4.5,11.9,11.8,11.7,11.6,11.5},
		{11.3,11.2,11.1,10,11.9,-1,11.7,11.6,11.5,11.4,11.3,11.2,11.1,10,11.9,4.4,11.7,11.6,11.5,11.4},
		{11.2,11.1,20,11.9,11.8,11.7,-1,11.5,11.4,11.3,11.2,11.1,20,11.9,11.8,11.7,4.3,11.5,11.4,11.3},
		{2,12,1,11.8,11.7,11.6,11.5,-1,11.3,11.2,11.1,12,11.9,11.8,11.7,11.6,11.5,4.2,11.3,11.2},
		{7,11.9,11.8,40,11.6,11.5,11.4,11.3,-1,11.1,7,11.9,11.8,30,11.6,11.5,11.4,11.3,4.1,11.1},
		{8,11.8,11.7,9,11.5,11.4,11.3,11.2,11.1,-1,8,11.8,11.7,9,11.5,11.4,11.3,11.2,11.1,4},
		{4.9,5,11.6,6,11.4,11.3,11.2,11.1,7,8,-1,5,11.6,6,11.4,11.3,11.2,11.1,7,8},
		{5,4.8,11.5,11.4,11.3,11.2,11.1,12,11.9,11.8,5,-1,11.5,11.4,11.3,11.2,11.1,12,11.9,11.8},
		{11.6, 11.5, 4.7, 11.3, 11.2, 11.1, 20, 11.9, 11.8, 11.7, 11.6, 11.5, -1, 11.3, 11.2, 11.1, 20, 11.9,11.8,11.7},
		{6,11.4,11.3,4.6,11.1,10,11.9,11.8,30,9,6,11.4,11.3,-1,11.1,10,11.9,11.8,30,9},
		{11.4,11.3,11.2,11,4.5,11.9,11.8,11.7,11.6,11.5,11.4,11.3,11.2,11.1,-1,11.9,11.8,11.7,11.6,11.5},
		{11.3,11.2,11.1,10,11.9,4.4,11.7,11.6,11.5,11.4,11.3,11.2,11.1,10,11.9,-1,11.7,11.6,11.5,11.4},
		{11.2,11.1,20,11.9,11.8,11.7,4.3,11.5,11.4,11.3,11.2,11.1,20,11.9,11.8,11.7,-1,11.5,11.4,11.3},
		{11.1,12,11.9,11.8,11.7,11.6,11.5,4.2,11.3,11.2,11.1,12,11.9,11.8,11.7,11.6,11.5,-1,11.3,11.2},
		{7,11.9,11.8,30,11.6,11.5,11.4,11.3,4.1,11.1,7,11.9,11.8,30,11.6,11.5,11.4,11.3,-1,11.1},
		{8,11.8,11.7,9,11.5,11.4,11.3,11.2,11.1,4,8,11.8,11.7,9,11.5,11.4,11.3,11.2,11.1,-1}
	};
 
}
