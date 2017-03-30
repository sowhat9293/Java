package TermProject;

public class Model {
  int [][] ImageIndex;
  int row;   //ÇàÀÇ °¹¼ö
  int col;   // ¿­ÀÇ °¹¼ö
  int totalCellNum;      // ÃÑ Ä­ÀÇ °¹¼ö
  int numOfMine;      // ¸¶ÀÎÀÇ °¹¼ö
  public Model(int row, int col, int numOfMine)
  {
     this.row=row;      this.col=col;
     ImageIndex=new int[this.row][this.col];
     for(int i=0; i<this.row; i++)
     {
        for(int j=0; j<this.col; j++)
           ImageIndex[i][j]=0;
     }
     totalCellNum=this.row*this.col;
     this.numOfMine=numOfMine;
  }
  int getCellNum(int row,int col){
     return ImageIndex[row][col];
  }
  int setCellNum(int row,int col,int k){
     return ImageIndex[row][col]=k;
  }
  int getRowNum()
  {
     return this.row;
  }
  int getColNum()
  {
     return this.col;
  }
  int getMineNum()
  {
	  return this.numOfMine;
  }
  int getNotMineNum()         // ÀüÃ¼ Áß¿¡ ¸¶ÀÎÀÌ ¾Æ´Ñ ¼¿ÀÇ °¹¼ö
  {
     return totalCellNum - numOfMine;
  }
  int[][] getImageIndexArray()
  {
	  return ImageIndex;
  }
}