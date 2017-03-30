package TermProject;

public class NumSet {
	Model _model;
	public static NumSet inst=null;
	 
	
	private NumSet(){
	}
	
	
	public void setNumber(){
		int[][] arr=_model.getImageIndexArray();
		for(int i=0;i<_model.getRowNum();i++){
			for(int j =0;j<_model.getColNum();j++){
				if(arr[i][j] == 9)	// Áö·ÚÀÏ¶§
					continue;
				else
				{
					arr[i][j]=getMineNumber(i, j);
					
					
				}
			}
		}
	}
	
	public int getMineNumber(int x, int y)
	{
		int count=0;	// Áö·ÚÀÇ °¹¼ö
		for(int i=x-1; i<=x+1; i++)
		{
			for(int j=y-1; j<=y+1; j++)
			{
				if(checkBoundary(i, j) && _model.ImageIndex[i][j] == 9)
				{
					count++;
				}
			}
		}
		return count;
	}
	
	public boolean checkBoundary(int x, int y)
	{
		return ((x>=0 && x<_model.getRowNum()) && (y>=0 && y <_model.getColNum()));
	}
	
	public void setModel(Model model)
	{
		this._model=model;
	}
	
	public static NumSet getInst()
	{
		if(inst == null)
			inst=new NumSet();
		
		return inst;
	}
	
}
