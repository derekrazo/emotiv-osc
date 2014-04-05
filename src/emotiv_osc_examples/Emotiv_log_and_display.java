package emotiv_osc_examples;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Emotiv_log_and_display extends JPanel implements Runnable{

	
	BufferedImage bim ;
	double[][]chanvals ;
	Graphics2D g2 ;
	JFrame jf ;
	int rendercount = 0 ;
	int interval  = 128 ;
	int current  = 0 ;
	int arrindex = 0 ;
	int colorcount = 0 ;
	double[]prevals = new double[19] ;
	double[] meanarr ;
	boolean linedraw = false ;
	
	public Emotiv_log_and_display (){
		
	}
	public Emotiv_log_and_display (int w,int h,int interval){
		this.interval = interval ;
		this.setDoubleBuffered(true);
		jf = new JFrame("EDK display") ;
		bim = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB) ;
		g2 = bim.createGraphics() ;
		jf.add(this) ;
		this.setPreferredSize(new Dimension(w,h));
		jf.pack(); 
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true) ;
		setBackground(Color.LIGHT_GRAY) ;
		new Thread(this).start() ;
		
	}
	public void update(double[][]input,int arrindex){
		chanvals = input ;
		this.arrindex = arrindex ;
		renderChannels() ;
		current ++ ;
	}
	public void clear(Color background){
		g2.setColor(background);
		g2.fill(new Rectangle2D.Double(0,0,getWidth(),getHeight())) ;
	}
	public void renderChannels(){
	
		if(chanvals != null){	
			double xstep = (double)getWidth()/(double)interval ;
			double xpos = xstep*(double)(current%interval) ;	
			double ystep = (double)getHeight()/(double)chanvals.length ;
			double w = (double)getWidth() ;
			g2.setColor(Color.BLACK);
				double xstep2 = w/interval ;	
				if(!linedraw)
				g2.setColor(Color.LIGHT_GRAY);
				else 				g2.setColor(Color.RED);

				g2.fill(new Rectangle2D.Double(xpos+1,0,xstep2,getHeight()));
				for(int i=0;i<chanvals.length;i++){
					double cmax = getcmax_13(i) ;
					double cmin = getcmin_13(i) ;
					double ystep2 = ystep/(cmax-cmin) ;
					double offset = (double)i*ystep ;
					double val1 = 0 ;
					if(arrindex > 0)
						val1 = (cmax-chanvals[i][arrindex-1])*(ystep2) + offset ;
					else val1 = (cmax-chanvals[i][chanvals[i].length-1])*(ystep2) + offset ;
					//System.out.println("val1 = " + val1 + " preval = " + prevals[i]) ;
				//	if(val1 != prevals[i]){
				//		System.out.println("inequality detected at arrindex " + arrindex +  " at channel " + i) ;
				//		g2.setColor(Color.RED);
				//		g2.draw(new Ellipse2D.Double(xpos-7,val1-7,14,14));
				//	}
					double val2 = (cmax-chanvals[i][arrindex])*ystep2 + offset ;
				
					g2.setColor(Color.BLACK);
					g2.draw(new Line2D.Double(xpos,val1,xpos+xstep2,val2));
					g2.drawString(Integer.toString(i),0,(int)offset+10);
					prevals[i] = val2 ;
				}		
			repaint() ;
		} // the problem is its taking sometimes the y value of index-2 instead of index-1
	}
	public void drawvline(int x){ //where x is an array index within size(chanvals,2) 
		//double xstep = (double)getWidth()/(double)interval ;
		linedraw = true ;

		/*
		double xpos = xstep*(double)(current%interval) ;
		g2.setColor(Color.RED);
		g2.fill(new Rectangle2D.Double(xpos,0,xstep,getHeight()));
		renderChannels() ;*/
	}
	public double getcmax(int channel){
		switch(channel){
			case 0 : return 128 ;
			case 1 : return 0 ;
			case 2 : return 0 ;
			case 3 : return 4100 ;
			case 4 : return 4150 ;
			case 5 : return 4300 ;
			case 6 : return 4050 ;
			case 7 : return 4170 ;
			case 8 : return 4774 ;
			case 9 : return 4635 ;
			case 10 : return 4550 ;
			case 11 : return 4326 ;
			case 12 : return 4129 ;
			case 13 : return 4374 ;
			case 14 : return 4552 ;
			case 15 : return 4547 ;
			case 16 : return 4328 ;
			case 17 : return 2000 ;
			case 18 : return 2000 ;		
		}
		return 0 ;
	}
	public double getcmin(int channel){
		switch(channel){
			case 0 : return 0 ;
			case 1 : return 0 ;
			case 2 : return 0 ;
			case 3 : return 4000 ;
			case 4 : return 4050 ;
			case 5 : return 4200 ;
			case 6 : return 3940 ;
			case 7 : return 4070 ;
			case 8 : return 4674 ;
			case 9 : return 4535 ;
			case 10 : return 4450 ;
			case 11 : return 4226 ;
			case 12 : return 4029 ;
			case 13 : return 4274 ;
			case 14 : return 4452 ;
			case 15 : return 4447 ;
			case 16 : return 4228 ;
			case 17 : return 1000 ;
			case 18 : return 1000 ;		
		}
		return 0 ;
	}
	
	public double getcmax_13(int channel){
		switch(channel){
			case 0 : return 4110 ;
			case 1 : return 4162 ;
			case 2 : return 4316 ;
			case 3 : return 4052 ;
			case 4 : return 4169 ;
			case 5 : return 4766 ;
			case 6 : return 4634 ;
			case 7 : return 4549 ;
			case 8 : return 4338 ;
			case 9 : return 4141 ;
			case 10 : return 4375 ;
			case 11 : return 4550 ;
			case 12 : return 4549 ;
			case 13 : return 4339 ;
			case 14 : return 1000 ;
			case 15 : return 1000 ;
		}
		return 0 ;
	}
	public double getcmin_13(int channel){
		switch(channel){
			case 0 : return 4010 ;
			case 1 : return 4062 ;
			case 2 : return 4216 ;
			case 3 : return 3952 ;
			case 4 : return 4069 ;
			case 5 : return 4666 ;
			case 6 : return 4534 ;
			case 7 : return 4449 ;
			case 8 : return 4238 ;
			case 9 : return 4041 ;
			case 10 : return 4275 ;
			case 11 : return 4450 ;
			case 12 : return 4449 ;
			case 13 : return 4239 ;
			case 14 : return 2000 ;
			case 15 : return 2000 ;
		}
		return 0 ;
	}
	
	public void updatemean(double[]chanvals){
		
	}
	
	public void run(){
		renderChannels() ;
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g) ;
		Graphics2D g2 = (Graphics2D)g ;
		g2.drawImage(bim, 0, 0, null) ;
	}
	
	 public static void main(String[]args){
		 
		 Emotiv_log_and_display edisp = new Emotiv_log_and_display(1000,500,64) ;
		 double[][]data2d = new double[19][128] ;
		 int counter = 0 ;
		 
		 while(true){
			 try{Thread.sleep(100);}catch(Exception e){}
			 for(int i=0;i<data2d.length;i++){
				 data2d[i][counter%data2d[i].length] = Math.random() ;			 
			 }
			 edisp.update(data2d, counter%data2d[0].length);
			 System.out.println("index = " + counter%data2d[0].length) ;
			 counter++ ;
		 }
		 
		 
	 }
}
