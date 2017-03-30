import java.util.Scanner;
import java.util.HashSet;
import java.util.Iterator;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;

interface INIT_MENU	//������� �������̽��� ������ �ξ���.
{
	int INPUT=1, EXIT=2;
}

interface INPUT_SELECT
{
	int NORMAL=1, UNIV=2, COMPANY=3;
}

class MenuChoiceException extends Exception		//�߸��� �޴��� ������ ���ܻ�Ȳ�� ����� ���� Ŭ����
{
	int wrongChoice;
	
	public MenuChoiceException(int choice)
	{
		super("�߸��� ������ �̷������ϴ�.");
		wrongChoice=choice;
	}
	
	public void showWrongChoice()
	{
		System.out.println(wrongChoice + "�� �ش��ϴ� ������ �������� �ʽ��ϴ�.");
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
	private final File dataFile=new File("PhoneBook.dat");	//Ŭ������ �����Ǹ� �ڵ� �ʱ�ȭ. ���� ������ �Ұ���.
	HashSet<PhoneInfo> infoStorage=new HashSet<PhoneInfo>();
	
	static PhoneBookManager inst=null;	//�ν��Ͻ��� �ϳ��� �����ϰԲ��ϱ����� �ڵ����. static���� �� ������ �ν��Ͻ� �������� �ʾƵ� ��������.
	public static PhoneBookManager createManagerInst()
	{
		if ( inst == null)
			inst=new PhoneBookManager();
		
		return inst;
	}
	
	private PhoneBookManager()
	{
		readFromFile();	//���α׷� ���۰� ���ÿ� �����ڸ� ���Ͽ� ���Ͽ� �ִ� �����͸� ���α׷����� ����´�.
	}
	
	private PhoneInfo readFriendInfo()
	{
		System.out.print("�̸� : ");
		String name=MenuViewer.keyboard.nextLine();
		System.out.print("��ȭ��ȣ : ");
		String phone=MenuViewer.keyboard.nextLine();
		return new PhoneInfo(name, phone);
	}
	
	private PhoneInfo readUnivFriendInfo()
	{
		System.out.print("�̸� : ");
		String name=MenuViewer.keyboard.nextLine();
		System.out.print("��ȭ��ȣ : ");
		String phone=MenuViewer.keyboard.nextLine();
		System.out.print("���� : ");
		String major=MenuViewer.keyboard.nextLine();
		System.out.print("�г� : ");
		int year=MenuViewer.keyboard.nextInt();
		MenuViewer.keyboard.nextLine();
		return new PhoneUnivInfo(name, phone, major, year);
	}
	
	private PhoneInfo readCompanyFriendInfo()
	{
		System.out.print("�̸� : ");
		String name=MenuViewer.keyboard.nextLine();
		System.out.print("��ȭ��ȣ : ");
		String phone=MenuViewer.keyboard.nextLine();
		System.out.print("ȸ�� : ");
		String company=MenuViewer.keyboard.nextLine();
		return new PhoneCompanyInfo(name, phone, company);
	}
	
	public void inputData() throws MenuChoiceException
	{
		System.out.println("������ �Է��� �����մϴ�..");
		System.out.println("1. �Ϲ�, 2. ����, 3. ȸ��");
		System.out.print("����>> ");
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
		
		boolean isAdded=infoStorage.add(info);	// HashSet<PhoneInfo>�� ���� �� ����Ȯ�ο��θ� ��ȯ���� �����ϴ� �ڵ����
		if ( isAdded == true)
			System.out.println("������ �Է��� �Ϸ�Ǿ����ϴ�. \n");
		else
			System.out.println("�̹� ����� �������Դϴ�. \n");
	}
	
	public String searchData(String nm)	//GUI�� ���� ��. Panel1
	{
		PhoneInfo info=search(nm);
		if ( info == null)
			return null;
		else
		{
			return info.showPhoneInfo();
		}
	}
	
	public boolean deleteData(String name)	//GUI�� ���ߵ�. Panel3
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
		System.out.println("�����ϼ���...");
		System.out.println("1. ������ �Է�");
		System.out.println("2. ���α׷� ����");
		System.out.print("���� : ");
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
					manager.storeToFile();		//���� ���� �޸𸮿��ִ� �����͸� File�� �����Ѵ�.
					System.out.println("���α׷��� �����մϴ�.");
					return;
				}
			}
			catch ( MenuChoiceException e)
			{
				e.showWrongChoice();
				System.out.println("�޴� ������ ó������ �ٽ� �����մϴ�. \n");
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
		String name=text.getText();	// �˻�â���� �Է� ���� �̸��� ����
		String out=manager.searchData(name);
		area.setText("");
		if ( out == null)
			area.append("�ش� ������ �������� �ʽ��ϴ�.");
		else
			area.append("ã���ô� ������ �˷��帳�ϴ�.\n" + out);
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
			area.append("�ش� �����Ͱ� ���� �Ϸ�Ǿ����ϴ�.");
		else
			area.append("�ش� �����Ͱ� �������� �ʽ��ϴ�.");
	}
}

class SearchDelFrame extends JFrame
{
	JTextField searchText=new JTextField(15);	//�˻� �Է� â
	JButton searchBtn=new JButton("SEARCH");	//�˻� ��ư
	
	JTextField delText=new JTextField(15);		//���� �Է� â
	JButton delBtn=new JButton("DEL");			//���� ��ư
	
	JTextArea textArea=new JTextArea(20, 25);	//��� â
	
	PhoneBookManager manager=PhoneBookManager.createManagerInst();
	
	public SearchDelFrame(String title)
	{
		super(title);
		setBounds(120, 120, 300, 400);
		setLayout(new BorderLayout());	// ���δ� 1, ���δ� �����Ӱ�

//		 	�г� 1
		Border border1=BorderFactory.createEtchedBorder();
		border1=BorderFactory.createTitledBorder(border1, "Search");

		JPanel panel1=new JPanel();
		panel1.setLayout(new FlowLayout());
		panel1.setBorder(border1);
		panel1.add(searchText);
		panel1.add(searchBtn);
		//

		//�г� 2
		JPanel panel2=new JPanel();
		panel2.setLayout(new FlowLayout());
		Border border2=BorderFactory.createEtchedBorder();
		border2=BorderFactory.createTitledBorder(border2, "Information Board");
		panel2.setBorder(border2);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);

		JScrollPane simpleScroll=new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		panel2.add(simpleScroll);
		searchBtn.addActionListener(new SearchAndShowActionHandler(searchText, textArea, manager));	//�˻����� ����Ұ��� ������Ʈ ����
		//

		// �г� 3
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