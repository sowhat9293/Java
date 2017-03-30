import java.util.Scanner;
import java.util.HashSet;
import java.util.Iterator;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;

interface INIT_MENU	//상수들을 인터페이스에 저장해 두었다.
{
	int INPUT=1, EXIT=2;
}

interface INPUT_SELECT
{
	int NORMAL=1, UNIV=2, COMPANY=3;
}

class MenuChoiceException extends Exception		//잘못된 메뉴를 선택한 예외상황을 담당할 예외 클래스
{
	int wrongChoice;
	
	public MenuChoiceException(int choice)
	{
		super("잘못된 선택이 이뤄졌습니다.");
		wrongChoice=choice;
	}
	
	public void showWrongChoice()
	{
		System.out.println(wrongChoice + "에 해당하는 선택은 존재하지 않습니다.");
	}
}

class PhoneInfo implements Serializable
{
	String name;
	String phoneNumber;
	
	public PhoneInfo(String name, String num)
	{
		this.name=name;
		phoneNumber=num;
	}
	
	public String showPhoneInfo()
	{
		return "name : "+name+"\n"+"phone : "+phoneNumber;
	}
	
	public int hashCode()
	{
		return name.hashCode();
	}
	
	public boolean equals(Object obj)
	{
		PhoneInfo cmp=(PhoneInfo)obj;
		if(name.compareTo(cmp.name) == 0)
			return true;
		else
			return false;
	}
}

class PhoneUnivInfo extends PhoneInfo
{
	String major;
	int year;
	
	public PhoneUnivInfo(String name, String num, String major, int year)
	{
		super(name, num);
		this.major=major;
		this.year=year;
	}
	
	public String showPhoneInfo()
	{
		return super.showPhoneInfo() + "\n" + "major : " + major + "\n" + "year : " + year;
	}
}

class PhoneCompanyInfo extends PhoneInfo
{
	String company;
	
	public PhoneCompanyInfo(String name, String num, String company)
	{
		super(name, num);
		this.company=company;
	}
	
	public String showPhoneInfo()
	{
		return super.showPhoneInfo() + "\n" + "company : " + company;
	}
}

class PhoneBookManager
{
	private final File dataFile=new File("PhoneBook.dat");	//클래서가 생성되면 자동 초기화. 차후 변경이 불가능.
	HashSet<PhoneInfo> infoStorage=new HashSet<PhoneInfo>();
	
	static PhoneBookManager inst=null;	//인스턴스를 하나만 생성하게끔하기위한 코드라인. static으로 한 이유는 인스턴스 생성하지 않아도 쓰기위함.
	public static PhoneBookManager createManagerInst()
	{
		if ( inst == null)
			inst=new PhoneBookManager();
		
		return inst;
	}
	
	private PhoneBookManager()
	{
		readFromFile();	//프로그램 시작과 동시에 생성자를 통하여 파일에 있던 데이터를 프로그램으로 갖고온다.
	}
	
	private PhoneInfo readFriendInfo()
	{
		System.out.print("이름 : ");
		String name=MenuViewer.keyboard.nextLine();
		System.out.print("전화번호 : ");
		String phone=MenuViewer.keyboard.nextLine();
		return new PhoneInfo(name, phone);
	}
	
	private PhoneInfo readUnivFriendInfo()
	{
		System.out.print("이름 : ");
		String name=MenuViewer.keyboard.nextLine();
		System.out.print("전화번호 : ");
		String phone=MenuViewer.keyboard.nextLine();
		System.out.print("전공 : ");
		String major=MenuViewer.keyboard.nextLine();
		System.out.print("학년 : ");
		int year=MenuViewer.keyboard.nextInt();
		MenuViewer.keyboard.nextLine();
		return new PhoneUnivInfo(name, phone, major, year);
	}
	
	private PhoneInfo readCompanyFriendInfo()
	{
		System.out.print("이름 : ");
		String name=MenuViewer.keyboard.nextLine();
		System.out.print("전화번호 : ");
		String phone=MenuViewer.keyboard.nextLine();
		System.out.print("회사 : ");
		String company=MenuViewer.keyboard.nextLine();
		return new PhoneCompanyInfo(name, phone, company);
	}
	
	public void inputData() throws MenuChoiceException
	{
		System.out.println("데이터 입력을 시작합니다..");
		System.out.println("1. 일반, 2. 대학, 3. 회사");
		System.out.print("선택>> ");
		int choice=MenuViewer.keyboard.nextInt();
		MenuViewer.keyboard.nextLine();
		PhoneInfo info=null;
		
		if(choice<INPUT_SELECT.NORMAL || choice>INPUT_SELECT.COMPANY)
			throw new MenuChoiceException(choice);
		
		switch(choice)
		{
		case INPUT_SELECT.NORMAL :
			info=readFriendInfo();
			break;
		case INPUT_SELECT.UNIV :
			info=readUnivFriendInfo();
			break;
		case INPUT_SELECT.COMPANY :
			info=readCompanyFriendInfo();
			break;
		}
		
		boolean isAdded=infoStorage.add(info);	// HashSet<PhoneInfo>에 저장 및 저장확인여부를 반환시켜 저장하는 코드라인
		if ( isAdded == true)
			System.out.println("데이터 입력이 완료되었습니다. \n");
		else
			System.out.println("이미 저장된 데이터입니다. \n");
	}
	
	public String searchData(String nm)	//GUI로 가야 됨. Panel1
	{
		PhoneInfo info=search(nm);
		if ( info == null)
			return null;
		else
		{
			return info.showPhoneInfo();
		}
	}
	
	public boolean deleteData(String name)	//GUI로 가야됨. Panel3
	{
		Iterator<PhoneInfo> itr=infoStorage.iterator();
		while(itr.hasNext())
		{
			PhoneInfo curInfo=itr.next();
			if ( name.compareTo(curInfo.name) == 0)
			{
				itr.remove();
				return true;
			}
		}
		return false;
	}
	
	private PhoneInfo search(String name)
	{
		Iterator<PhoneInfo> itr=infoStorage.iterator();
		
		while(itr.hasNext())
		{
			PhoneInfo curInfo=itr.next();
			if ( name.compareTo(curInfo.name) == 0)
				return curInfo;
		}
		return null;
	}
	
	public void storeToFile()
	{
		try
		{
			FileOutputStream file=new FileOutputStream(dataFile);
			ObjectOutputStream out=new ObjectOutputStream(file);
			
			Iterator<PhoneInfo> itr=infoStorage.iterator();
			while(itr.hasNext())
			{
				out.writeObject(itr.next());
			}
			
			out.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void readFromFile()
	{
		if(dataFile.exists() ==false)
			return;
		
		try
		{
			FileInputStream file=new FileInputStream(dataFile);
			ObjectInputStream in=new ObjectInputStream(file);
			
			while(true)
			{
				PhoneInfo info=(PhoneInfo)in.readObject();
				if(info == null)
					break;
				infoStorage.add(info);
			}
			
			in.close();
		}
		catch ( IOException e)
		{
			return;
		}
		catch ( ClassNotFoundException e)
		{
			return;
		}
	}
}

class MenuViewer
{
	public static Scanner keyboard=new Scanner(System.in);
	
	public static void showMenu()
	{
		System.out.println("선택하세요...");
		System.out.println("1. 데이터 입력");
		System.out.println("2. 프로그램 종료");
		System.out.print("선택 : ");
	}
}

class PhoneBk
{
	public static void main(String[] args)
	{
		PhoneBookManager manager=PhoneBookManager.createManagerInst();
		int choice;
		SearchDelFrame frm=new SearchDelFrame("PhoneBook");
		{
			try
			{
				MenuViewer.showMenu();
				choice=MenuViewer.keyboard.nextInt();
				MenuViewer.keyboard.nextLine();
				
				if ( choice < INIT_MENU.INPUT || choice>INIT_MENU.EXIT)
					throw new MenuChoiceException(choice);
				
				switch(choice)
				{
				case INIT_MENU.INPUT :
					manager.inputData();
					break;
				case INIT_MENU.EXIT :
					manager.storeToFile();		//종료 직전 메모리에있는 데이터를 File에 저장한다.
					System.out.println("프로그램을 종료합니다.");
					return;
				}
			}
			catch ( MenuChoiceException e)
			{
				e.showWrongChoice();
				System.out.println("메뉴 선택을 처음부터 다시 진행합니다. \n");
			}
		}
	}
}




class SearchAndShowActionHandler implements ActionListener
{
	PhoneBookManager manager;
	JTextArea area;
	JTextField text;
	
	public SearchAndShowActionHandler(JTextField text, JTextArea area, PhoneBookManager manager)
	{
		this.text=text;
		this.area=area;
		this.manager=manager;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		String name=text.getText();	// 검색창에서 입력 받은 이름을 저장
		String out=manager.searchData(name);
		area.setText("");
		if ( out == null)
			area.append("해당 정보가 존재하지 않습니다.");
		else
			area.append("찾으시는 정보를 알려드립니다.\n" + out);
	}
}

class DeleteActionHandler implements ActionListener
{
	PhoneBookManager manager;
	JTextField text;
	JTextArea area;
	
	public DeleteActionHandler(JTextField text, JTextArea area, PhoneBookManager manager)
	{
		this.text=text;
		this.area=area;
		this.manager=manager;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		String name=text.getText();
		area.setText("");
		if (manager.deleteData(name))
			area.append("해당 데이터가 삭제 완료되었습니다.");
		else
			area.append("해당 데이터가 존재하지 않습니다.");
	}
}

class SearchDelFrame extends JFrame
{
	JTextField searchText=new JTextField(15);	//검색 입력 창
	JButton searchBtn=new JButton("SEARCH");	//검색 버튼
	
	JTextField delText=new JTextField(15);		//삭제 입력 창
	JButton delBtn=new JButton("DEL");			//삭제 버튼
	
	JTextArea textArea=new JTextArea(20, 25);	//출력 창
	
	PhoneBookManager manager=PhoneBookManager.createManagerInst();
	
	public SearchDelFrame(String title)
	{
		super(title);
		setBounds(120, 120, 300, 400);
		setLayout(new BorderLayout());	// 가로는 1, 세로는 자유롭게

//		 	패널 1
		Border border1=BorderFactory.createEtchedBorder();
		border1=BorderFactory.createTitledBorder(border1, "Search");

		JPanel panel1=new JPanel();
		panel1.setLayout(new FlowLayout());
		panel1.setBorder(border1);
		panel1.add(searchText);
		panel1.add(searchBtn);
		//

		//패널 2
		JPanel panel2=new JPanel();
		panel2.setLayout(new FlowLayout());
		Border border2=BorderFactory.createEtchedBorder();
		border2=BorderFactory.createTitledBorder(border2, "Information Board");
		panel2.setBorder(border2);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);

		JScrollPane simpleScroll=new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		panel2.add(simpleScroll);
		searchBtn.addActionListener(new SearchAndShowActionHandler(searchText, textArea, manager));	//검색값과 출력할곳의 컴포넌트 전달
		//

		// 패널 3
		JPanel panel3=new JPanel();
		Border border3=BorderFactory.createEmptyBorder();
		border3=BorderFactory.createTitledBorder(border3, "Delete");
		panel3.setLayout(new FlowLayout());
		panel3.setBorder(border3);
		delBtn.addActionListener(new DeleteActionHandler(delText, textArea, manager));
		panel3.add(delText);
		panel3.add(delBtn);
		//

		add(panel1, BorderLayout.NORTH);
		add(panel2, BorderLayout.CENTER);
		add(panel3, BorderLayout.SOUTH);

		setVisible(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
}