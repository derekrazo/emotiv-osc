package emotiv_osc_examples;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import osc.OSCBundle;
import osc.OSCPortIn;
import osc.OSCPortOut;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import edk.Edk;
import edk.EdkErrorCode;
import edk.EmoState;
import edk.Edk.EE_Event_t;


/***
 * EmotivOscSend by Derek Razo and Russell Butler, partly copied from the emotiv Java SDK.
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
 * misc : the variable edisp was my display that i plot the raw data on, left it out and just printed the data to 
 * the console to keep things simple for now.
 * %%%NOTE edisp has now been added back in. you will need to put the class EELog_arrs_disp in the same package as this one
 * you will definitely have to change the imports to match the configuration on your system, in my eclipse project
 * i have a package named emotiv where i keep all my emotiv imports, hence the import emotiv.Edk; etc...
 * 
 */

public class EmotivOscSend implements Runnable{
	
	static ArrayList<ArrayList<Double>> data2dstat_list ;	    	
    double[][] data2d = new double[19][256] ; // 2d array of double to hold 1 second of EEG time course       
    ArrayList<ArrayList<Double>> alldata2d = new ArrayList<ArrayList<Double>>() ;   // array list to write result of recording to text file       
    ArrayList<Integer> taskarr = new ArrayList<Integer>() ; // boolean array that serves as triggers. 1 = task, 0 = non-task       
    
    
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
	private OSCBundle bundle = new OSCBundle();
	
	//OSC messages without addresses
	osc.OSCMessage excitement_short;
	osc.OSCMessage excitement_long;
	osc.OSCMessage engagement_boredom;
	osc.OSCMessage frustration;
	osc.OSCMessage meditation;
	osc.OSCMessage current_action;



	public EmotivOscSend(){
		
		if (setup()){
			Edk.INSTANCE.EE_DataSetBufferSizeInSec(secs);
			System.out.print("Buffer size in secs: ");
			System.out.println(secs);		
			//edisp = new Emotiv_log_and_display(1500,1000,1200) ; //display to render and paint the EEG time course				
			new Thread(this).start() ;
		}
		
	}	
	
	public boolean setup(){
		
		
		////// Setup our OSC port
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
						return false;
					}
					break;
				}
				case 2:{
					System.out.println("Target IP of EmoComposer: [127.0.0.1] ");
			
					if (Edk.INSTANCE.EE_EngineRemoteConnect("127.0.0.1", composerPort, "Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()){
						System.out.println("Cannot connect to EmoComposer on [127.0.0.1]");
						return false;
					}
					System.out.println("Connected to EmoComposer on [127.0.0.1]");
					break;
				}
				default:
					System.out.println("Invalid option...");
					return false;
			   	}
	
		return true;
	}
				
	public void listenForStateChange(){
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
		}
		
		int eventType = Edk.INSTANCE.EE_EmoEngineEventGetType(eEvent);
		Edk.INSTANCE.EE_EmoEngineEventGetUserId(eEvent, userID);

		// Package into OSC the EmoState if it has been updated
		if (eventType == Edk.EE_Event_t.EE_EmoStateUpdated.ToInt() || eventType == EE_Event_t.EE_UnknownEvent.ToInt()) {

			Edk.INSTANCE.EE_EmoEngineEventGetEmoState(eEvent, eState);
			
			float timestamp = EmoState.INSTANCE.ES_GetTimeFromStart(eState);
			
			excitement_short = new osc.OSCMessage("/eeg/excitement/short");
			excitement_short.addArgument(EmoState.INSTANCE.ES_AffectivGetExcitementShortTermScore(eState)*10000);
			
			excitement_long = new osc.OSCMessage("/eeg/excitement/long");
			excitement_long.addArgument(EmoState.INSTANCE.ES_AffectivGetExcitementLongTermScore(eState)*10000);
			
			engagement_boredom = new osc.OSCMessage("/eeg/engagement/boredom");
			engagement_boredom.addArgument(EmoState.INSTANCE.ES_AffectivGetEngagementBoredomScore(eState)*10000);
			
			frustration = new osc.OSCMessage("/eeg/frustration");
			frustration.addArgument(EmoState.INSTANCE.ES_AffectivGetFrustrationScore(eState));
			//System.out.println(EmoState.INSTANCE.ES_AffectivGetFrustrationScore(eState));
			
			meditation = new osc.OSCMessage("/eeg/meditation");
			meditation.addArgument(EmoState.INSTANCE.ES_AffectivGetMeditationScore(eState));
			
			current_action = new osc.OSCMessage("/eeg/action");
			current_action.addArgument(EmoState.INSTANCE.ES_CognitivGetCurrentAction(eState));
			
			//System.out.println(timestamp + " : New EmoState from user " + userID.getValue());
			
			//System.out.print("WirelessSignalStatus: ");
			//System.out.println(EmoState.INSTANCE.ES_GetWirelessSignalStatus(eState));
			
			/*
			if (EmoState.INSTANCE.ES_ExpressivIsBlink(eState) == 1)
				System.out.println("Blink");
			if (EmoState.INSTANCE.ES_ExpressivIsLeftWink(eState) == 1)
				System.out.println("LeftWink");
			if (EmoState.INSTANCE.ES_ExpressivIsRightWink(eState) == 1)
				System.out.println("RightWink");
			if (EmoState.INSTANCE.ES_ExpressivIsLookingLeft(eState) == 1)
				System.out.println("LookingLeft");
			if (EmoState.INSTANCE.ES_ExpressivIsLookingRight(eState) == 1)
				System.out.println("LookingRight");
			
			
		
			System.out.print("ExcitementShortTerm: ");
			System.out.println(EmoState.INSTANCE.ES_AffectivGetExcitementShortTermScore(eState));
			System.out.print("ExcitementLongTerm: ");
			System.out.println(EmoState.INSTANCE.ES_AffectivGetExcitementLongTermScore(eState));
			System.out.print("EngagementBoredom: ");
			System.out.println(EmoState.INSTANCE.ES_AffectivGetEngagementBoredomScore(eState));
			
			*/
			
			//System.out.println ("i: " + i + ", " + data2d[i][index]);
			/*
			@SuppressWarnings("deprecation")
			osc.OSCMessage mesg = new osc.OSCMessage("/eeg/raw/", args);
			
			try {
				
				sender.send(mesg);
			
			} catch (IOException e) {
				e.printStackTrace();
			}
			*/
			
			/*
			System.out.print("CognitivGetCurrentAction: ");
			System.out.println(EmoState.INSTANCE.ES_CognitivGetCurrentAction(eState));
			System.out.print("CurrentActionPower: ");
			System.out.println(EmoState.INSTANCE.ES_CognitivGetCurrentActionPower(eState));
			*/
			
			
		}
		
	}
	
	public void run(){	
		
		//This is the bundle we will put all our data in throughout the app, then send over UDP port 57110
		osc.OSCBundle bundle = new OSCBundle(); 

		while (true){	// loop to acquire, bundle and send data
			
			listenForStateChange();
			
			
			//Collect raw EEG data and send it out the OSC port.
			if (readytocollect) {
				
				//Tell emotiv where to look for the device data in memory
				
				Edk.INSTANCE.EE_DataUpdateHandle(0, hData);
				Edk.INSTANCE.EE_DataGetNumberOfSample(hData, nSamplesTaken);
				
				///////////////////////////////////////
				
				// check to make sure we have samples to query for data
				if (nSamplesTaken != null){
					if (nSamplesTaken.getValue() != 0) { 				
						
						
						double[] data = new double[nSamplesTaken.getValue()]; //set the size of the data array the length of the sample buffer
						int index = 0;					
						
						/**
						 * RAW DATA ACQUISITION LOOP
						 * the outermost loop (sampleIdx) iterates over the samples (usually four sampled / acquisition in my experience)
						 * the innermost loop iterates over each channel's sample value
						 * the raw data array raweeg is just data2d minus the first 3 channels.
						 */
						
						for (int sampleIdx=0 ; sampleIdx<nSamplesTaken.getValue() ; ++ sampleIdx) { // for the number of samples taken
							
							for (int i = 0 ; i < data2d.length ; i++) { //for each EEG channel
								
								Edk.INSTANCE.EE_DataGet(hData,i,data,nSamplesTaken.getValue());
																
								data2d[i][index] = data[sampleIdx] ;	
				
								
							}	
														
							
							double[][]raweeg = new double[data2d.length-3][data2d[0].length] ; // RAW DATA array : EEG channels 1:14
							Object args[] = new Object[16]; //Object where we will put our raw EEG data for transport over OSC
							bundle = new OSCBundle();

							for(int i=0;i<raweeg.length;i++){// Put all the raw EEG data into an object array to be packed into an OSC Bundle

								alldata2d.get(i).add(raweeg[i][index]) ;
								//System.out.println(" channel:" + i + " data:" + data2d[i][index]+" sample:"+scount+sampleIdx) ; // PRINT THE RAW DATA for each sample taken
								
								args[i] = new Float(data2d[i][index]);
								//System.out.println ("i: " + i + ", " + data2d[i][index]);
																
							}
							
							try {
								@SuppressWarnings("deprecation")
								osc.OSCMessage raw = new osc.OSCMessage("/eeg/raw/", args);
								
								bundle.addPacket(raw);
								bundle.addPacket(excitement_short);
								bundle.addPacket(excitement_long);
								bundle.addPacket(engagement_boredom);
								bundle.addPacket(frustration);
								bundle.addPacket(meditation);
								bundle.addPacket(current_action);
								
								sender.send(bundle);
							
							} catch (IOException e) {
								e.printStackTrace();
							}
						}	
					}
				}
			}
		}
	}
			
    public static void main(String[]args){
    	new EmotivOscSend() ;
    }
    
}
