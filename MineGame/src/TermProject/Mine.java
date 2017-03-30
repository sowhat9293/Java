package TermProject;
import java.util.Random;
class Mine {
	   Random random;
	   Model _model;
	   public Mine(Model model)
	   {
	      random=new Random();
	      random.setSeed(System.currentTimeMillis());         // Set Seed
	      this._model=model;
	   }
	   
	   void setMine()
	   {
		  int[][] arr=_model.getImageIndexArray();
	      int random_row;   // row Number of Random
	      int random_col;   // col Number of Random
	      
	      for(int i=0; i<_model.getMineNum(); i++)   // Áö·ÚÀÇ °¹¼ö¸¸Å­ ½ÇÇà
	      {
	      random_row=random.nextInt(_model.getRowNum());
	      random_col=random.nextInt(_model.getColNum());
	      if(isMine(random_row, random_col, arr))   // ÀÌ¹Ì Áö·Ú ÀÏ ¶§
	      {
	         i--;
	         continue;
	      }
	      arr[random_row][random_col]=9;
	      }
	      
	      
	   }
	   
	   boolean isMine(int row, int col, int[][] arr)
	   {
	      if(arr[row][col] == 9)      // Áö·Ú ÀÏ ¶§
	         return true;
	      else
	         return false;
	   }

	}