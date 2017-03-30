package TermProject;

public class Control {
	public Control(){
		 arrNum=new int[3];
		 arrNum[0]=0;		arrNum[1] = 0;		arrNum[2]=0;
		 input= new InputView(this);
	}
	int [] arrNum;
	InputView input;

public void gameStart() 
{
	arrNum=input.getNumArr();

	Model model = new Model(arrNum[0],arrNum[1],arrNum[2]);
	NumSet numset = NumSet.getInst();
	numset.setModel(model);
	Mine mine = new Mine(model);
	mine.setMine();
	numset.setNumber();
	View view = new View(model, this);
	for(int i=0;i<arrNum[0];i++){
		for(int j=0;j<arrNum[1];j++){
			System.out.print(model.ImageIndex[i][j]+"\t");
		}
		System.out.println();
	}
	System.out.println("===========================");

}
}
