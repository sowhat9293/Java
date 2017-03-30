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
	Integer num;	//패의 숫자 1~20
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
	public int numOfPlayer=1;	// 이 방에 접속 중인 플레이어의 숫자 1<=x<=2
	
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
			return false;	// 이미 풀 방
		
		numOfPlayer++;
		return true;
	}
	public void exitPlayer()		//플레이어가 나가면 다시 1로 초기화
	{
		numOfPlayer=1;
	}
}
 
class Player
{
	RankName rankName=new RankName();
	Card[] card=new Card[2];
	double rank;	//순위를 저장할 변수
	String rankStr;		//최종 조합을 저장할 문자열
	public int money;	//보유 자금
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
		String rm=rankName.hMap.get((int)rank);	// ex) 떙, 끗 같은걸 저장
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
			case 4 :	// 땡일 때
				rankStr=cardOne+rm;
				break;
			case 11 :	// 끗 일 때
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
	public HashMap<Integer, Player> playerMap;	//플레이어들을 저장할 HashMap, int값으론 playernumber
	Random dealer;
	public Room room=new Room();	// 방 이름과, 방 번호를 저장할 인스턴스
	HashSet<Integer> cardSet;	// 플레이어 두명에게 나누어준 카드 4장을 기록할 변수
	public int p1Num;	//방장의 플레이어 번호
	public int p2Num;	// 게스트의 플레이어 번호
	public int totalMoney=0;
	public int wantDiePlayerNumber=0;
	public HashMap<Integer, BetMoney> betMoneySet;	// 플레이어가 배팅한 금액이 있는 셋
	public int count=0;	// 콜 카운트, 2가 되면 게임종료
	public int ready=0;	//2가되면 모두 레디한것이다.

	public Dealer(int p1Num)	// 방장 번호
	{
		this.p1Num=p1Num;
		Player owner=new Player();
		playerMap=new HashMap<Integer, Player>();
		betMoneySet=new HashMap<Integer, BetMoney>();
		playerMap.put(p1Num, owner);	// 플레이어 1
		owner.setMoney(Server.vector.get(p1Num).money);
		betMoneySet.put(p1Num, new BetMoney());
		dealer=new Random();
		dealer.setSeed(System.currentTimeMillis());	// 시간에 따른 시드를 주어서 계속 다른 수가  뽑힘
		cardSet=new HashSet<Integer>();
	}
	
	public boolean settingGuest(int p2Num)		//상대방 셋팅
	{
		if(room.addJoinPlayer())
		{
		Player guest=new Player();
		playerMap.put(p2Num, guest);	// 플레이어 2
		guest.setMoney(Server.vector.get(p2Num).money);
		betMoneySet.put(p2Num, new BetMoney());
		this.p2Num=p2Num;
		return true;
		}
		else
			return false;		// 이미 풀방이면 false를 반환
	}
	
	public void setRoomInst(String name, int num)
	{
		room.setRoomName(name);
		room.setRoomNumber(num);
	}
 
	public void deal()	// 카드를 나누어 주고 랭크를 셋팅하는 메소드, 방이 만들어지고 게임이 시작되면 맨처음 실행되야하는 메소드, 
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
		Integer num=null;	// 랜덤 함수에서 나온 하나의 정수 (1~20)
		while(true)
		{
			num=( dealer.nextInt() % 20 ) + 1 ;	//1~20까지의 수 , Auto-Boxing
			if(num <= 0)
				continue;
			bl=cardSet.add(num);
			if(bl == true)
				break;
		}
		
		return num;
	}
	
	public double decode(Card[] card)	// 순위를 판독해서 반환해주는 메소드, setRank()에서 사용하기 위한 메소드
	{
		int cardOne=card[0].num;	// Auto-UnBoxing
		int cardTwo=card[1].num;	// 두번째 가드
		return RankArray.rankArr[cardOne-1][cardTwo-1];
	}
	
	public void setRank()
	{
			playerMap.get(p1Num).setRank(decode(playerMap.get(p1Num).card));
			playerMap.get(p2Num).setRank(decode(playerMap.get(p2Num).card));
	}
	public String judgeMent()		// 승자와 패자를 가려주는 메소드, 승자를 반환, 배팅이 끝나고 마지막으로 실행해야하는 메소드
	{			
				if(playerMap.get(p1Num).rank == playerMap.get(p2Num).rank)	// 둘이 똑같을 때 재경기
				{
					return "WIN#DRAW";
				}
				if(playerMap.get(p1Num).rank == 30.0)	// 플레이어1이 사구파토 패 일 때
				{
					if(playerMap.get(p2Num).rank >= 5.0)
					{
						return "WIN#DRAW";
					}
				}
				else if (playerMap.get(p2Num).rank == 30.0)	//플레이어2가 사구파토 패 일 때
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
				
				if ( playerMap.get(p1Num).rank == 20.0)	// 땡잡이
				{
					if(playerMap.get(p2Num).rank >= 4.1 && playerMap.get(p2Num).rank <= 4.9)
					{
						this.setPlayerMoney(p1Num, playerMap.get(p1Num).money + this.totalMoney);
						this.totalMoney=0;
						return "WIN#"+p1Num;
					}
					else if (playerMap.get(p2Num).rank == 12)	// 상대가 망통일때
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
				else if ( playerMap.get(p2Num).rank == 20.0)	// 땡잡이
				{
					if(playerMap.get(p1Num).rank >= 4.1 && playerMap.get(p1Num).rank <= 4.9)
					{
						this.setPlayerMoney(p2Num, playerMap.get(p2Num).money + this.totalMoney);
						this.totalMoney=0;
						return "WIN#"+p2Num;
					}
					else if (playerMap.get(p1Num).rank == 12)	// 상대가 망통일때
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
	
	public void refresh()	// 재경기를 할 수 있게 끔 HashSet을 모두 지우는 메소드, 배팅금액도 0으로 초기화 해준다.
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
	
/*	public static void checkAllReady()	// 두 플레이어가 Ready 했는지 확인 해 주는 메소드
	{												// static 선언 이유는 main 메소드가 static 이기 때문이다. static은 개별 인스턴스의 메소드에 접근 불가
		while(true)
		{
			if(Server.ready >= 2)
				break;
		}
	}*/
	
	public boolean call(int playerNum, int money)	// count 값이 2가되면 false를 반환, 아니면 true를 반환
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
	
	public void sendMoneyMsg()	// 모든 플레이어에게 금액 관련 메시지를 보낸다
	{
		Server.vector.get(p1Num).sendMsg("MONEY#"+playerMap.get(p1Num).money+":"+playerMap.get(p2Num).money+":"+totalMoney);
		Server.vector.get(p2Num).sendMsg("MONEY#"+playerMap.get(p2Num).money+":"+playerMap.get(p1Num).money+":"+totalMoney);
	}
	
	public void sendCardNumMsg()	//카드 숫자 정보를 전송
	{
		Server.vector.get(p1Num).sendMsg("CARD#"+getCard(p1Num)+":"+getCard(p2Num));
		Server.vector.get(p2Num).sendMsg("CARD#"+getCard(p2Num)+":"+getCard(p1Num));
	}
	
	public void StartGame()	// 카드 정보를 보내준다.
	{
		this.sendMoneyMsg();
		this.deal();
		Server.vector.get(p1Num).sendMsg("CARD#"+getCard(p1Num)+":"+getCard(p2Num));
		Server.vector.get(p2Num).sendMsg("CARD#"+getCard(p2Num)+":"+getCard(p1Num));
		Server.vector.get(p1Num).sendMsg("SET#BTNON");
	}
	
	public void endingGame()	// 카드를 나누어주고, 배팅을한 뒤 마지막으로 실행하는 메소드
	{
		Server.vector.get(p1Num).sendMsg("THEEND#");
		Server.vector.get(p2Num).sendMsg("THEEND#");		// 배팅 버튼 무력화 시키기 위함
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
		refresh();// 카드팩 초기화
	}
 
	public void setPlayerMoney(int playerNum, int money)	// 해당 플레이어의 금액 정보를 일괄적으로 바꾼다
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
	public static HashMap<Integer, Dealer> room=new HashMap<Integer, Dealer>();	// 방들을 저장할 해쉬
	public static boolean requestExit=false;	// 플레이어가 게임 도중 나가기 버튼을 누르면 false가 true로 된다.
	public static int totalPlayerNum=0;	// 총 접속자 수
	public static int totalRoomNum=1;	// 총 방 갯수
	public static ClientAcceptThread acceptThread;


	
	public static void main(String[] args) 
	{
		socket=null;
		acceptThread=new ClientAcceptThread();
		try
		{
		server=new ServerSocket(7777);	//서버를 만든다
		System.out.println("서버 준비 완료");
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
	
	public static void makeRoom(int playerNum, String roomName)		//방장인 플레이어 번호를 받는다.
	{
		Dealer temp=new Dealer(playerNum);
		temp.setRoomInst(roomName, Server.totalRoomNum);		// 방이름은 같으면 안된다.
		room.put(Server.totalRoomNum, temp);	// 새 방을 만들고 룸 넘버를 부여한다. 룸 넘버는 1부터 시작.
		vector.get(playerNum).room=temp;	// 만든 방 인스턴스를 방장 플레이어의 room인스턴스가 참조하게끔 한다.
		vector.get(playerNum).roomNum=Server.totalRoomNum;	// 방장 플레이어의 룸 넘버를 해당 룸 넘버로 바꿔주고 룸넘버를 증가시킨다.
		Server.totalRoomNum++;
		showAllRoomList();  //방이 만들어졌으므로, 다시 방정보를 보내준다.
	}
	
	public static void enterTheRoom(int roomNum, int playerNum)	// 만들어져 있는 방에 접속하는 메소드
	{
		Dealer inst=room.get(roomNum);
		boolean bl=inst.settingGuest(playerNum);
		if(bl)
		{
		vector.get(playerNum).room=inst;
		vector.get(playerNum).roomNum=roomNum;
		}
		else
			vector.get(playerNum).sendMsg("ERROR#풀방입니다.");
	}
	
	public static void exitRoom(int roomNum, int playerNum)	// 플레이어가 방을 나가면 서버에게 방을 나갔다는 메시지를 보내고, 그 메시지를 받은 서버는 exitRoom을 통하여 해당 플레이어의 쓰레드 인스턴스를 수정한다.
	{
		Dealer inst=room.get(roomNum);

		if(inst==null)
		{
			vector.get(playerNum).room=null;
			vector.get(playerNum).roomNum=0;		// 대기실 번호로 바꾼다.
			showAllRoomList();
			return;
		}
		ClientThread owner=vector.get(inst.p1Num);	//방장
		ClientThread guest=vector.get(inst.p2Num);	//게스트
		if(inst.p1Num == playerNum)	//나가고자 하는 플레이어가 방장 일 때, 방폭
		{
			owner.room=null;
			owner.roomNum=0;
			owner.setMoney(inst.playerMap.get(inst.p1Num).money);
			guest.sendMsg("BOOM#");		//방폭을 알리는 메시지를 게스트에게 전달한다.
			room.remove(roomNum);	//방을 제거한다.
		}
		else if(inst.p2Num == playerNum)	//나가고자 하는 플레이어가 게스트 일 땐 방은 그대로 유지
		{
			guest.setMoney(inst.playerMap.get(inst.p2Num).money);
			owner.sendMsg("GUESTOUT#");		//게스트가 방을 나갔음을 알린다.
			inst.refresh();
			inst.betMoneySet.remove(playerNum);	//나가는 플레이어의 인스턴스를 지운다.
			inst.playerMap.remove(playerNum);
			inst.room.exitPlayer();		//나갔으므로 방의 인원 수를 1로 만든다.
		}
		sendListData();
		guest.room=null;
		guest.roomNum=0;		// 대기실 번호로 바꾼다.
	}
	
	public static void showAllRoomList()		// 플레이어들에게 방 리스트를 전송해준다.
	{
		Iterator<Integer> itr=room.keySet().iterator();
		while(itr.hasNext())
		{
			Dealer temp=room.get(itr.next());
			sendBroadCast("ROOMLIST#"+temp.room.roomName+":"+temp.room.roomNum);
		}
	}

	
	public static void sendBroadCast(String msg)		// 모든 사용자에게 MSG를 전달
	{
		for(int i=0; i<Server.totalPlayerNum; i++)
			vector.get(i).sendMsg(msg);
	}
	
	public static void sendListData()	// 브로드캐스트로 접속자 ID와 방List를 보내준다.
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
		System.out.println("플레이어 입장");
		Server.vector.add(temp);		// 0부터 들어간다
		temp.playerNum=Server.totalPlayerNum++;		//후위증가
		System.out.println("입장한 번호 : "+Server.vector.get(Server.totalPlayerNum-1).playerNum);
		temp.start();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
 
class ClientThread extends Thread	//서버가 갖고있어야할 클래스
{
	Socket socket=null;
	DataInputStream in;
	DataOutputStream out;
	String id;
	public int money=50000;
	public int playerNum;		// 자신의 플레이어 번호, vector의 Index 값이다.
	public int roomNum=0;			// 플레이어가 소속되어 있는 ROOM 넘버, 0은 로비방
	boolean bl=true;
	
	public Dealer room=null;	// 기본 null이며 방에 들어 갈 경우 해당 Dealer 인스턴스가 들어간다.
	
	public void run()
	{
		while(bl)
		{
			try
			{
			String msg=in.readUTF();
			System.out.println("받음 : "+msg);
			if(msg.startsWith("CHAT#") == true)	//채팅 메시지 일 때
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
			else if(msg.startsWith("ID#") == true)	// 처음 접속 시 닉네임 설정
			{
					id=msg.substring(3);
					Server.sendListData();  //새로운 플레이어가 접속했으므로 List를 다시 보내준다.
			}
			else if(msg.startsWith("ALLIN#") == true)
			{
				int money=new Integer(msg.substring(6)).intValue();
				room.allIn(this.playerNum);
			}
			else if (msg.startsWith("RAISE#")==true)	//콜에 돈을 추가할때
			{
					int money=new Integer(msg.substring(6)).intValue();
					room.raise(playerNum, money);
					sendToRoom("BET#"+money);
			}
			else if (msg.startsWith("THEEND") == true)	// 정말 게임 끝일때만 수신 스레드를 종료 시킨다.
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
			else if(msg.startsWith("MAKEROOM#") == true)		//방을 만들고자 할 때, #다음 방 이름
			{
				String roomName=msg.substring(9);
				Server.makeRoom(this.playerNum, roomName);
			}
			else if(msg.startsWith("JOINROOM#") == true)	// 방에 접속하고자 할 때, #다음 룸 넘버
			{
				int roomNum=new Integer(msg.substring(9)).intValue();
				Server.enterTheRoom(roomNum, playerNum);
			}
			else if(msg.startsWith("EXITROOM#") == true)// 방을 나가고자 할 때, #다음 룸 넘버
			{
				int roomNum=new Integer(msg.substring(9)).intValue();
				Server.exitRoom(roomNum, playerNum);
			}
			else if (msg.startsWith("DIE#")==true)
			{
				if(this.playerNum == room.p1Num)		//플레이어 1이 die를 했을 때
				{
					room.wantDiePlayerNumber=room.p2Num;
				}
				else	// 2가 die를 했을 때
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
	
	public void setTurn()	// 순서를 정한다 true를 넘겨주면 처음 시작이 된다.
	{
		sendMsg("SET#BTNON");
	}
	
	public void setRoom(Dealer inst)
	{
		this.room=inst;
	}
	
	public void sendMsg(String str)	// 해당 쓰레드의 플레이어에게 메시지 전송
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
	
	
	public void sendToRoom(String str)	//나를 제외한 같은 방의 사람들에게 메시지 전송
	{
		for(int i=0; i<Server.totalPlayerNum; i++)
		{
			System.out.println(i+"번 인덱스 플레이어 확인");
			if((this.roomNum == Server.vector.get(i).roomNum) && (this.playerNum != Server.vector.get(i).playerNum))
			{
				System.out.println(i+"번 인덱스에게 보냈음");
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
		hMap.put(1, "삼팔광땡");
		hMap.put(2, "일팔광땡");
		hMap.put(3, "일삼광땡");
		hMap.put(4, "땡");
		hMap.put(5, "알리");
		hMap.put(6, "독사");
		hMap.put(7, "구삥");
		hMap.put(8, "장삥");
		hMap.put(9, "장사");
		hMap.put(10, "세륙");
		hMap.put(11, "끗");
		hMap.put(12, "망통");
		hMap.put(20, "땡잡이");
		hMap.put(30, "사구");
		hMap.put(40, "멍텅구리 사구");
	}
}
 
class RankArray
{
	public static double[][] rankArr=new double[][] {	// 접근 할 시 행과 열의 Index에 각각 +1씩 해야한다.
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
