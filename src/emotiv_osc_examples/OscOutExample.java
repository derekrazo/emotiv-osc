package emotiv_osc_examples;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

import osc.OSCMessage;
import osc.OSCPortOut;



public class OscOutExample{
	
	private static OSCPortOut sender;	
	public static Object args[] = new Object[16];
	static Random randomGenerator = new Random();
	 
	 public static void main(String[]args){
	    	while (true){
	    		oscSendRandom();
	    		//System.out.println(sender.getAddress());
	    		//System.out.println(sender.getHost());
	    	}	  
	    	
	    }

	 public static void oscSendRandom(){
	 		
	 		try {
	 			sender = new OSCPortOut();		
	 		} catch (UnknownHostException e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		} catch (SocketException e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		} catch (IOException e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		}
		    	 
	    	 for (int i=0; i <args.length;i++){
	 			args[i] = randomGenerator.nextInt(1500)+ 3500;
	 		}

		    osc.OSCMessage mesg = new osc.OSCMessage("/random/", args);
		    
		    try {
				sender.send(mesg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
}

