package TermProject;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

public class ImagePanel extends JPanel{
	int count = 0;
   Model _model;
   View _view;
   JPanel _panel;
   NumSet _numset;
   ImagePanel(Model model,View view){
      _model = model;
      _view = view;
      _panel = this;
      _numset=NumSet.getInst();
      this.addMouseListener(new MouseListener() {
		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
	    	  if(count ==0){
	        	  _view.timer.cancel();
	        	  _view.timer = new Timer();
	        	  _view.timer.scheduleAtFixedRate(new TimerTask(){
	    				 public void run(){
	    					 _view.Timer_L.setText("���� �ð� : "+_view.Sec+" ��");
	    					 _view.Sec +=1;
	    				 }
	    			 },500,1000);
	        	  count++;
	        	  }
	         int button;
	         int x = e.getX();
	         int y = e.getY();
	         int row = y/(getHeight()/_model.getRowNum());
	         int col = x/(getWidth()/_model.getColNum());
	         button = e.getButton();//���콺 Ŭ�� ���� �����´� 1 = ����, 2 = ��ũ��, 3 = ������
	         
	         if(button ==3 && _model.getCellNum(row, col) < 10){
	            _model.setCellNum(row, col, transferNum(_model.getCellNum(row, col)));
	            _panel.repaint();
	            return;
	         }
	         if(_model.getCellNum(row, col)%10==9)      // �ش� ���� �����̸� ������ �����Ѵ�.
	            {
	               _model.setCellNum(row, col, _model.getCellNum(row, col)+10);
	               _panel.repaint();
	              _view.callDialog(false);
	            }
	         if(_model.getCellNum(row, col)>=10 || _model.getCellNum(row, col) < 0)      
	        	 // �ش� ���� �̹� ���µǾ� ������ �Լ��� ����������.
	            return;
	            
	         _model.setCellNum(row, col,_model.getCellNum(row, col)+10);
	         openNearImg(row, col);
	         _panel.repaint();
	         
	         if(checkEndGame())   // ���ڸ� �����ϰ� ��� ���� �����ߴٸ� ������ �����Ų��.
	         {
	            _view.callDialog(true);
	         }
		}
	});
   }
   public void paint(Graphics g){
      int width = getWidth();
      int height = getHeight();
      
      g.clearRect(0, 0, width, height);
      for(int row=0;row<_model.getRowNum();row++){
         for(int col=0;col<_model.getColNum();col++){
            if(_model.getCellNum(row, col) < 0)               //  ��� ������ Ŀ���� ����ؾ� �ϴ°��
            {
               g.drawImage(_view.ImageStore[11], col*(width/_model.getColNum()), row*(height/_model.getRowNum()),
            		   width/_model.getColNum(),height/_model.getRowNum(),this);
            }
            else if(_model.getCellNum(row, col)/10==0){         // Ŀ���� ������ �ϴ� ���
               g.drawImage(_view.ImageStore[10], col*(width/_model.getColNum()), row*(height/_model.getRowNum()),
            		   width/_model.getColNum(),height/_model.getRowNum(),this);
            }
            else{
               Image image = _view.getImage(_model.getCellNum(row, col)%10);
               g.drawImage(image, col*(width/_model.getColNum()), row*(height/_model.getRowNum()),
            		   width/_model.getColNum(),height/_model.getRowNum(),this);
            }
            g.setColor(Color.BLACK);
            g.drawRect(col*(width/_model.getColNum()), row*(height/_model.getRowNum()),
            		width/_model.getColNum(),height/_model.getRowNum());
         }
      }
   }
   
   public void openNearImg(int x, int y){
	      if(_model.getCellNum(x, y)%10==0){         // ���� ���� 0�̸� �ֺ��� ����.
	         for(int i=x-1; i<=x+1; i++)
	         {
	            for(int j=y-1; j<=y+1; j++)
	            {
	            	 if(_numset.checkBoundary(i, j) && (_model.getCellNum(i,j) < 10 && _model.getCellNum(i, j) >= 0))
	               {
	                  _model.setCellNum(i, j,_model.getCellNum(i, j)+10);
	                  	if(_model.getCellNum(i, j) % 10 == 0)
	                     openNearImg(i, j);
	               }
	            }
	         }
	      }
	   }
   private int transferNum(int num) //��� ������ ����, ��� ���ָ� ���
   {
      num = -1*num;         
     if(num % 10 == 0)
        num -= 10;
      return num;
   }
   private boolean checkEndGame()
   {
      int count=0;
      for(int i=0; i<_model.getRowNum(); i++)
      {
         for(int j=0; j< _model.getColNum(); j++)
         {
            if(_model.getCellNum(i, j) >= 10)
               count++;
         }
      }
      if(count == (_model.getNotMineNum()))
            return true;
      
      return false;
   }
}
