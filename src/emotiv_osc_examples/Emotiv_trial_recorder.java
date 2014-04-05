package emotiv_osc_examples;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import osc.OSCPortIn;
import osc.OSCPortOut;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import edk.Edk;
import edk.EdkErrorCode;
import edk.Edk.EE_Event_t;

/***
 * Emotiv_trial_recorder by Russell Butler, partly copied from the emotiv Java SDK.
 * gets and displays raw data from emotiv headset (display currently disabled)
 * can break raw data up into task and non-task blocks, preceded by a beep from the speaker.
 * saves recording along with a boolean array of task and non-task time points. 
 * the boolean array is the ArrayList<Integer> taskarr, and is the same size as the time series.
 * 
 * For real time access to raw signal, use the variable double[][] data2d which is a 2d array of 
 * double holding the real time signal values, constantly refreshed with each new sample.
 * data2d holds 128 sample points in the current configuration.
 * 
 * for the total history of the current experiment, use ArrayList<ArrayList<Double>> alldata2d
 * this is an nx14 array of channel data, using the channel indices in the emotiv SDK 
 * this arrayList is saved upon the end of the recording
 * 
 * if you want the gyroscope and other misc channels, you need to change the size (rows) of data2d
 * and the acquisition loop will automatically collect the additional channels. 
 * %%%NOTE this updated version now plots and acquires the gyroscope already, they're in the last two rows of data2d
 * 
 * SOME NOTES ON USE WITH THE MATLAB SCRIPT
 * the BEEP you hear signals the onset of a task period. the task period proceeds for tasktime seconds
 * and then the rest period begins which is not signalled by anything. the next beep will be the onset
 * of the next task period.
 * the matlab script analyze_erp creates an array taskblocks which should be ntrials x (sample points collected)
 * in size. each row of the matlab array taskblocks holds the data collected after each beep output by this 
 * program, each row of the array taskblocks should be tasktime*128 in size, so the matlab array tasblocks should
 * be of size ntrials x 128*tasktime
 * 
 * misc : the variable edisp was my display that i plot the raw data on, left it out and just printed the data to 
 * the console to keep things simple for now.
 * %%%NOTE edisp has now been added back in. you will need to put the class EELog_arrs_disp in the same package as this one
 * you will definitely have to change the imports to match the configuration on your system, in my eclipse project
 * i have a package named emotiv where i keep all my emotiv imports, hence the import emotiv.Edk; etc...
 * 
 */

public class Emotiv_trial_recorder implements Runnable{
	
	static ArrayList<ArrayList<Double>> data2dstat_list ;
	static ArrayList<Integer> taskarrstat ;
	boolean task = false ; //task variable task is on/off true/false		    	
    double[][] data2d = new double[19][256] ; // 2d array of double to hold 1 second of EEG time course       
    ArrayList<ArrayList<Double>> alldata2d = new ArrayList<ArrayList<Double>>() ;   // array list to write result of recording to text file       
    ArrayList<Integer> taskarr = new ArrayList<Integer>() ; // boolean array that serves as triggers. 1 = task, 0 = non-task       
    String recording_name = "eegdata1" ; // name of the file to be saved. logical array is saved under this name with "_block" appended to end        
    double tasktime = 2 ; //task time in seconds       
    double resttime = 2 ; //rest time in seconds       
    int ntrials = 10 ; // number of stimulus trials
    double startTime = 0 ;        
 	int scount= 0 ; // counter for how many samples have been taken so far  	
    boolean spike = false ; // there was a spike in the sample counter, and it messed with the data   
    Emotiv_log_and_display  edisp ; //display
    
   	Pointer eEvent				= Edk.INSTANCE.EE_EmoEngineEventCreate();
   	Pointer eState				= Edk.INSTANCE.EE_EmoStateCreate();
    IntByReference userID 		= new IntByReference(0);
	IntByReference nSamplesTaken= new IntByReference(0);
   	short composerPort			= 1726;
   	int option 					= 1;
    int state  					= 0;
   	float secs 					= 1;
   	boolean readytocollect 		= false;    	
	Pointer hData = Edk.INSTANCE.EE_DataCreate();
	
	private OSCPortOut sender;
	private OSCPortIn  receiver;	
	
		
	public Emotiv_trial_recorder(){
		
		
		
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
		
	    for(int i=0;i<data2d.length-3;i++) // don't use channels 0-3 of raw data (they are not EEG)
	    	alldata2d.add(new ArrayList<Double>()) ;
	    	    
	    switch (option){
		case 1:{
			if (Edk.INSTANCE.EE_EngineConnect("Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()){
				System.out.println("Emotiv Engine start up failed.");
				return;
			}
			break;
		}
		case 2:{
			System.out.println("Target IP of EmoComposer: [127.0.0.1] ");
	
			if (Edk.INSTANCE.EE_EngineRemoteConnect("127.0.0.1", composerPort, "Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()){
				System.out.println("Cannot connect to EmoComposer on [127.0.0.1]");
				return;
			}
			System.out.println("Connected to EmoComposer on [127.0.0.1]");
			break;
		}
		default:
			System.out.println("Invalid option...");
			return;
	   	}
	    		
		Edk.INSTANCE.EE_DataSetBufferSizeInSec(secs);
		System.out.print("Buffer size in secs: ");
		System.out.println(secs);		
		edisp = new Emotiv_log_and_display(1500,1000,1200) ; //display to render and paint the EEG time course				
		 startTime = System.nanoTime() ; 
		 new Thread(this).start() ;
	}	
				
	public void run(){	

		while (true){	// loop to acquire data
			state = Edk.INSTANCE.EE_EngineGetNextEvent(eEvent);

			// New event needs to be handled
			if (state == EdkErrorCode.EDK_OK.ToInt()){
				int eventType = Edk.INSTANCE.EE_EmoEngineEventGetType(eEvent);
				Edk.INSTANCE.EE_EmoEngineEventGetUserId(eEvent, userID);

				// Log the EmoState if it has been updated
				if (eventType == Edk.EE_Event_t.EE_UserAdded.ToInt()) 
				if (userID != null){
						System.out.println("User added");
						Edk.INSTANCE.EE_DataAcquisitionEnable(userID.getValue(),true);
						readytocollect = true;
					}
			}
			else if (state != EdkErrorCode.EDK_NO_EVENT.ToInt()) {
				System.out.println("Internal error in Emotiv Engine!");
				break;
			}
			if (readytocollect) {
				Edk.INSTANCE.EE_DataUpdateHandle(0, hData);

				Edk.INSTANCE.EE_DataGetNumberOfSample(hData, nSamplesTaken);
				//System.out.println(nSamplesTaken.getValue()) ;
				if (nSamplesTaken != null){
					if (nSamplesTaken.getValue() != 0) {						
						double[] data = new double[nSamplesTaken.getValue()];
						int index = 0 ;
						
						/**
						 * RAW DATA ACQUISITION LOOP
						 * the outermost loop (sampleIdx) iterates over the samples (usually four sampled / acquisition in my experience)
						 * the innermost loop iterates over each channel's sample value
						 * the raw data array raweeg is just data2d minus the first 3 channels.
						 */
						for (int sampleIdx=0 ; sampleIdx<nSamplesTaken.getValue() ; ++ sampleIdx) { // for the number of samples taken
							for (int i = 0 ; i < data2d.length ; i++) { //for each EEG channel
								Edk.INSTANCE.EE_DataGet(hData,i,data,nSamplesTaken.getValue());
								index = (scount+sampleIdx)%data2d[0].length ; // index in our array data2d (for display update)
								if(i==0){ // check for spike
									if(index > 0){
										if(data[sampleIdx] != data2d[0][index-1]+1 && data[sampleIdx] != 0){
											System.out.println("spike detected") ;
											spike = true ;
										}
										else spike = false ;
									}
								}								
								if(!spike || i==0)
									data2d[i][index] = data[sampleIdx] ;									
								else {
									if(index > 0)
										data2d[i][index] = data2d[i][index-1] ;
									else 
										data2d[i][index] = data2d[i][data2d[i].length-1] ;
								}		
								if(i==0){
									if(task)
										taskarr.add(1) ;
									else taskarr.add(0) ;
								}
							}													
							double[][]raweeg = new double[data2d.length-3][data2d[0].length] ; // RAW DATA array : EEG channels 1:14
							int count = 0 ;
							for(int e=0;e<data2d.length;e++)
								if(e!=1 && e!=2 && e!= 0){ // remove three non-eeg channels
									for(int lol=0;lol<raweeg[count].length;lol++)
										raweeg[count][lol] = data2d[e][lol] ; //copy over the data
									count++ ;				
								}
							
							edisp.update(raweeg,index) ; //draw the new channel data
							Object args[] = new Object[16];
							
							//System.out.println(raweeg.length);
							
							for(int i=0;i<raweeg.length;i++){
								alldata2d.get(i).add(raweeg[i][index]) ;
								//System.out.println(" channel:" + i + " data:" + data2d[i][index]+" sample:"+scount+sampleIdx) ; // PRINT THE RAW DATA for each sample taken
								
								args[i] = new Float(data2d[i][index]);
								
								//System.out.println (data2d[i][index]);
								
								@SuppressWarnings("deprecation")
								osc.OSCMessage mesg = new osc.OSCMessage("/eeg/raw", args);
								
								try {
									
									sender.send(mesg);
								
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
										
							double currentTime = System.nanoTime() ;
							double tlapse = (currentTime - startTime)/1000000000 ; // elapsed time in seconds 
							if(tlapse > resttime && !task){ // start task
								startTime = System.nanoTime() ;
								if(!task){	
									System.out.println("start trial") ;
									java.awt.Toolkit.getDefaultToolkit().beep();
									ntrials -- ;								
								}
									task = true ;
									edisp.drawvline(index);
								}
							else if (tlapse > tasktime && task) { // end task
								System.out.println("end trial") ;
								edisp.linedraw = false ;
								java.awt.Toolkit.getDefaultToolkit().beep();
								task = false ;
								startTime = System.nanoTime() ;
							}
						}	
					}
				}
			}
			
			//update static variables for save data			
			data2dstat_list = alldata2d ;
			taskarrstat = taskarr ;
			scount += nSamplesTaken.getValue() ;
			
			boolean exit = false ;
			if(ntrials ==-1){
				saveRecordingtxt(recording_name) ;
				exit = true ;
			}
			if(exit)
				break ;
		} 	
		System.out.println("exiting") ;
    	Edk.INSTANCE.EE_EngineDisconnect();
    	Edk.INSTANCE.EE_EmoStateFree(eState);
    	Edk.INSTANCE.EE_EmoEngineEventFree(eEvent);
    	System.out.println("Disconnected!");
    	System.exit(0);
    }
    

    //save eeg recording as text file, rows are channels, columns are time points
	// the printwriter pr2 saves a logical array which has an index for every sample
	//point in the experiment, the index is 0 or 1 depending on whether that sample point
	//belonged to a task or rest block
    public static void saveRecordingtxt(String filename){

    	try { 		
    	    PrintWriter pr = new PrintWriter(filename);
    	        for (int i=0; i<data2dstat_list.size() ; i++){
    	           for(int j=0;j<data2dstat_list.get(i).size();j++){
    	        	   pr.print(data2dstat_list.get(i).get(j) + " ");
    	           }
    	           pr.println();
    	        }
    	        pr.close();
    	        
    		    PrintWriter pr2 = new PrintWriter(filename+"_block");
    	        for (int i=0; i<taskarrstat.size() ; i++){
    	        	pr2.print(taskarrstat.get(i) + " ");
    	        }
    	        pr2.println();
    	        pr2.close();  	      
    	    }
    	    catch (Exception e)
    	    {
    	        e.printStackTrace();
    	        System.out.println("No such file exists.");
    	    }
    	} 
    
    public static void main(String[]args){
    	System.out.println("starting Emotiv_trial_recorder") ;
    	new Emotiv_trial_recorder() ;
    }
}

//if (variable) => read the the following comment 
