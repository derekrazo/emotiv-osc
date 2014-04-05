package edk;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.*;

public interface EmoState extends Library  
{
	EmoState INSTANCE = (EmoState)
            Native.loadLibrary("edk",
            		EmoState.class);
    	
    	public enum EE_EmotivSuite_t {
    		EE_EXPRESSIV, EE_AFFECTIV, EE_COGNITIV
    	} 

    	/**
    	 * Expressiv facial expression type enumerator
    	 */
    	public enum EE_ExpressivAlgo_t {

    		EXP_NEUTRAL			 (0x0001),
    		EXP_BLINK			 (0x0002),
    		EXP_WINK_LEFT		 (0x0004),
    		EXP_WINK_RIGHT		 (0x0008),
            EXP_HORIEYE			 (0x0010),
    		EXP_EYEBROW			 (0x0020),
    		EXP_FURROW			 (0x0040),
            EXP_SMILE			 (0x0080),
    		EXP_CLENCH			 (0x0100),
    		EXP_LAUGH			 (0x0200),
    		EXP_SMIRK_LEFT		 (0x0400),
    		EXP_SMIRK_RIGHT		 (0x0800);

    		private int bit;
    		EE_ExpressivAlgo_t(int bitNumber)
    		{
    			bit = bitNumber;
    		}
    		public int ToInt()
    		{
    			return(bit);
    		}

    	} 
    	
    	/**
    	 * Affectiv emotional type enumerator
    	 */
    	public enum EE_AffectivAlgo_t {

    		AFF_EXCITEMENT			 (0x0001),
    		AFF_MEDITATION			 (0x0002),
    		AFF_FRUSTRATION			 (0x0004),
    		AFF_ENGAGEMENT_BOREDOM	 (0x0008);

    		private int bit;
    		EE_AffectivAlgo_t(int bitNumber)
    		{
    			bit = bitNumber;
    		}
    		public int ToInt()
    		{
    			return(bit);
    		}

    	} 

    	/**
    	 * Cognitiv action type enumerator
    	 */
    	public enum EE_CognitivAction_t {

    		COG_NEUTRAL						 (0x0001),
    		COG_PUSH						 (0x0002),
    		COG_PULL						 (0x0004),
    		COG_LIFT						 (0x0008),
    		COG_DROP						 (0x0010),
    		COG_LEFT						 (0x0020),
    		COG_RIGHT						 (0x0040),
    		COG_ROTATE_LEFT					 (0x0080),
    		COG_ROTATE_RIGHT				 (0x0100),
    		COG_ROTATE_CLOCKWISE			 (0x0200),
    		COG_ROTATE_COUNTER_CLOCKWISE	 (0x0400),
    		COG_ROTATE_FORWARDS				 (0x0800),
    		COG_ROTATE_REVERSE				 (0x1000),
    		COG_DISAPPEAR					 (0x2000);

    		private int bit;
    		EE_CognitivAction_t(int bitNumber)
    		{
    			bit = bitNumber;
    		}
    		public int ToInt()
    		{
    			return(bit);
    		}

    	} 
    	
    	/**
    	 * Wireless Signal Strength enumerator
    	 */
    	public enum EE_SignalStrength_t {

    		NO_SIGNAL, BAD_SIGNAL, GOOD_SIGNAL
    	
    	} 

    	//! Logical input channel identifiers
    	/*! Note: the number of channels may not necessarily match the number of
    	    electrodes on your headset.  Signal quality and input data for some
    		sensors will be identical: CMS = DRL, FP1 = AF3, F2 = AF4.
    	*/
    	public enum EE_InputChannels_t {
    		EE_CHAN_CMS, EE_CHAN_DRL, EE_CHAN_FP1, EE_CHAN_AF3, EE_CHAN_F7, 
    		EE_CHAN_F3, EE_CHAN_FC5, EE_CHAN_T7, EE_CHAN_P7, EE_CHAN_O1,
    		EE_CHAN_O2, EE_CHAN_P8, EE_CHAN_T8, EE_CHAN_FC6, EE_CHAN_F4,
    		EE_CHAN_F8, EE_CHAN_AF4, EE_CHAN_FP2
    	} 

        //! EEG Electrode Contact Quality enumeration
        /*! Used to characterize the EEG signal reception or electrode contact
            for a sensor on the headset.  Note that this differs from the wireless
            signal strength, which refers to the radio communication between the 
            headset transmitter and USB dongle receiver.
         */
        public enum EE_EEG_ContactQuality_t {
            EEG_CQ_NO_SIGNAL, EEG_CQ_VERY_BAD, EEG_CQ_POOR, 
            EEG_CQ_FAIR, EEG_CQ_GOOD } 

    	//! Create EmoState handle.
    	/*!
            NOTE: THIS FUNCTION HAS BEEN DEPRECATED - please use EE_EmoStateCreate instead.

    		ES_Init is called automatically after the creation of the EmoState handle.
    		ES_Free must be called to free up resources during the creation of the EmoState handle.

    		\return the Pointer if succeeded

    		\sa EE_EmoStateCreate, ES_Free, ES_Init
    	*/
    	Pointer	ES_Create();

    	//! Free EmoState handle
    	/*!
            NOTE: THIS FUNCTION HAS BEEN DEPRECATED - please use EE_EmoStateFree instead.

    		\param state - Pointer that was created by ES_Create function call

    		\sa EE_EmoStateFree, ES_Create
    	*/
    	void ES_Free(Pointer state);

    	//! Initialize the EmoState into neutral state
    	/*!
    		\param state - Pointer
    		
    		\sa ES_Create, ES_Free
    	*/
    	void ES_Init(Pointer state);

    	//! Return the time since EmoEngine has been successfully connected to the headset
    	/*!
    		If the headset is disconnected from EmoEngine due to low battery or weak
    		wireless signal, the time will be reset to zero.

    		\param state - Pointer

    		\return float - time in second

    	*/
    	float ES_GetTimeFromStart(Pointer state);

    	//! Return whether the headset has been put on correctly or not
    	/*!
    		If the headset cannot not be detected on the head, then signal quality will not report
    		any results for all the channels

    		\param state - EmoStatehandle

    		\return int - (1: On, 0: Off)
    	*/
    	int	ES_GetHeadsetOn(Pointer state);

    	//! Query the number of channels of available sensor contact quality data
    	/*!
    		\param state - Pointer
    		\return number of channels for which contact quality data is available (int)

    		\sa ES_GetNumContactQualityChannels
    	*/
    	int ES_GetNumContactQualityChannels(Pointer state);

    	//! Query the contact quality of a specific EEG electrode
    	/*!
    		\param state - Pointer
    		\param electroIdx - The index of the electrode for query

    		\return int - Enumerated value that characterizes the EEG electrode contact for the specified input channel

    		\sa ES_GetContactQuality
    	*/
    	int ES_GetContactQuality(Pointer state, int electroIdx);

    	//! Query the contact quality of all the electrodes in one single call
    	/*!
    		The contact quality will be stored in the array, contactQuality, passed to the function.
    		The value stored in contactQuality[0] is identical to the result returned by
    		ES_GetContactQuality(state, 0)
    		The value stored in contactQuality[1] is identical to the result returned by
    		ES_GetContactQuality(state, 1). etc.
    		The ordering of the array is consistent with the ordering of the logical input
    		channels in EE_InputChannels_enum.

    		\param state - Pointer
    		\param contactQuality - array of 32-bit float of size numChannels
    		\param numChannels - size (number of floats) of the input array

    		\return Number of signal quality values copied to the contactQuality array.

    		\sa ES_GetContactQualityFromAllChannels
    	*/
    	int ES_GetContactQualityFromAllChannels(Pointer state, IntByReference contactQuality, int numChannels);

    	//! Query whether the user is blinking at the time the EmoState is captured.
    	/*!
    		\param state - Pointer

    		\return blink status (1: blink, 0: not blink)

    	*/
    	int ES_ExpressivIsBlink(Pointer state);

    	//! Query whether the user is winking left at the time the EmoState is captured.
    	/*!
    		\param state - Pointer

    		\return left wink status (1: wink, 0: not wink)

    		\sa ES_ExpressivIsRightWink
    	*/
    	int ES_ExpressivIsLeftWink(Pointer state);

    	//! Query whether the user is winking right at the time the EmoState is captured.
    	/*!
    		\param state - Pointer

    		\return right wink status (1: wink, 0: not wink)

    		\sa ES_ExpressivIsLeftWink
    	*/
    	int ES_ExpressivIsRightWink(Pointer state);

    	//! Query whether the eyes of the user are opened at the time the EmoState is captured.
    	/*!
    		\param state - Pointer

    		\return eye open status (1: eyes open, 0: eyes closed)

    	*/
    	int ES_ExpressivIsEyesOpen(Pointer state);

    	//! Query whether the user is looking up at the time the EmoState is captured.
    	/*!
    		\param state - Pointer

    		\return eyes position (1: looking up, 0: not looking up)

    		\sa ES_ExpressivIsLookingDown
    	*/
    	int ES_ExpressivIsLookingUp(Pointer state);

    	//! Query whether the user is looking down at the time the EmoState is captured.
    	/*!
    		\param state - Pointer

    		\return eyes position (1: looking down, 0: not looking down)

    		\sa ES_ExpressivIsLookingUp
    	*/
    	int ES_ExpressivIsLookingDown(Pointer state);

    	//! Query whether the user is looking left at the time the EmoState is captured.
    	/*!
    		\param state - EmoStatehandle

    		\return eye position (1: looking left, 0: not looking left)

    		\sa ES_ExpressivIsLookingRight
    	*/
    	int ES_ExpressivIsLookingLeft(Pointer state);

    	//! Query whether the user is looking right at the time the EmoState is captured.
    	/*!
    		\param state - EmoStatehandle

    		\return eye position (1: looking right, 0: not looking right)

    		\sa ES_ExpressivIsLookingLeft
    	*/
    	int ES_ExpressivIsLookingRight(Pointer state);

    	//! Query the eyelids state of the user
    	/*!
    		The left and right eyelid state are stored in the parameter leftEye and rightEye
    		respectively. They are floating point values ranging from 0.0 to 1.0.
    		0.0 indicates that the eyelid is fully opened while 1.0 indicates that the
    		eyelid is fully closed.

    		\param state - EmoStatehandle
    		\param leftEye - the left eyelid state (0.0 to 1.0)
    		\param rightEye - the right eyelid state (0.0 to 1.0)

    	*/
    	void ES_ExpressivGetEyelidState(Pointer state, FloatByReference leftEye, FloatByReference rightEye);

    	//! Query the eyes position of the user
    	/*!
    		The horizontal and vertical position of the eyes are stored in the parameter x and y
    		respectively. They are floating point values ranging from -1.0 to 1.0.
    		
    		For horizontal position, -1.0 indicates that the user is looking left while
    		1.0 indicates that the user is looking right.
    		
    		For vertical position, -1.0 indicates that the user is looking down while
    		1.0 indicatest that the user is looking up.

    		This function assumes that both eyes have the same horizontal or vertical positions.
    		(i.e. no cross eyes)

    		\param state - Pointer
    		\param x - the horizontal position of the eyes
    		\param y - the veritcal position of the eyes

    	*/
    	void ES_ExpressivGetEyeLocation(Pointer state, FloatByReference x, FloatByReference y);

    	//! Returns the eyebrow extent of the user (Obsolete function)
    	/*!
    		\param state - Pointer
    		
    		\return eyebrow extent value (0.0 to 1.0)

    		\sa ES_ExpressivGetUpperFaceAction, ES_ExpressivGetUpperFaceActionPower
    	*/
    	float ES_ExpressivGetEyebrowExtent(Pointer state);

    	//! Returns the smile extent of the user (Obsolete function)
    	/*!
    		\param state - EmoStatehandle
    		
    		\return smile extent value (0.0 to 1.0)

    		\sa ES_ExpressivGetLowerFaceAction, ES_ExpressivGetLowerFaceActionPower
    	*/
    	float ES_ExpressivGetSmileExtent(Pointer state);

    	//! Returns the clench extent of the user (Obsolete function)
    	/*!
    		\param state - EmoStatehandle

    		\return clench extent value (0.0 to 1.0)

    		\sa ES_ExpressivGetLowerFaceAction, ES_ExpressivGetLowerFaceActionPower
    	*/
    	float ES_ExpressivGetClenchExtent(Pointer state);


    	//! Returns the detected upper face Expressiv action of the user
    	/*!
    		\param state - EmoStatehandle

    		\return pre-defined Expressiv action types

    		\sa ES_ExpressivGetUpperFaceActionPower
    	*/
    	int ES_ExpressivGetUpperFaceAction(Pointer state);

    	//! Returns the detected upper face Expressiv action power of the user
    	/*!
    		\param state - EmoStatehandle

    		\return power value (0.0 to 1.0)

    		\sa ES_ExpressivGetUpperFaceAction
    	*/
    	float ES_ExpressivGetUpperFaceActionPower(Pointer state);

    	//! Returns the detected lower face Expressiv action of the user
    	/*!
    		\param state - EmoStatehandle

    		\return pre-defined Expressiv action types

    		\sa ES_ExpressivGetLowerFaceActionPower
    	*/
    	int ES_ExpressivGetLowerFaceAction(Pointer state);

    	//! Returns the detected lower face Expressiv action power of the user
    	/*!
    		\param state - EmoStatehandle

    		\return power value (0.0 to 1.0)

    		\sa ES_ExpressivGetLowerFaceAction
    	*/
    	float ES_ExpressivGetLowerFaceActionPower(Pointer state);
    	
    	//! Query whether the signal is too noisy for Expressiv detection to be active
    	/*!
    		\param state - Pointer
    		\param type  - Expressiv detection type

    		\return detection state (0: Not Active, 1: Active)

    		\sa int
    	*/
    	int ES_ExpressivIsActive(Pointer state, int type);

    	//! Returns the NativeLong term excitement level of the user
    	/*!
    		\param state - Pointer

    		\return excitement level (0.0 to 1.0)

    		\sa ES_AffectivGetExcitementShortTermScore
    	*/
    	float ES_AffectivGetExcitementLongTermScore(Pointer state);

    	//! Returns short term excitement level of the user
    	/*!
    		\param state - Pointer

    		\return excitement level (0.0 to 1.0)

    		\sa ES_AffectivGetExcitementLongTermScore
    	*/
    	float ES_AffectivGetExcitementShortTermScore(Pointer state);
    	
    	//! Query whether the signal is too noisy for Affectiv detection to be active
    	/*!
    		\param state - Pointer
    		\param type  - Affectiv detection type

    		\return detection state (0: Not Active, 1: Active)

    		\sa int
    	*/
    	int ES_AffectivIsActive(Pointer state, int type);

    	//! Returns meditation level of the user
    	/*!
    		\param state - Pointer

    		\return meditation level (0.0 to 1.0)
    	*/
    	float ES_AffectivGetMeditationScore(Pointer state);

    	//! Returns frustration level of the user
    	/*!
    		\param state - Pointer

    		\return frustration level (0.0 to 1.0)
    	*/
    	float ES_AffectivGetFrustrationScore(Pointer state);

    	//! Returns engagement/boredom level of the user
    	/*!
    		\param state - Pointer

    		\return engagement/boredom level (0.0 to 1.0)
    	*/
    	float ES_AffectivGetEngagementBoredomScore(Pointer state);

    	//! Returns the detected Cognitiv action of the user
    	/*!
    		\param state - Pointer

    		\return Cognitiv action type

    		\sa int, ES_CognitivGetCurrentActionPower
    	*/
    	int ES_CognitivGetCurrentAction(Pointer state);

    	//! Returns the detected Cognitiv action power of the user
    	/*!
    		\param state - Pointer

    		\return Cognitiv action power (0.0 to 1.0)

    		\sa ES_CognitivGetCurrentAction
    	*/
    	float ES_CognitivGetCurrentActionPower(Pointer state);
    	
    	//! Query whether the signal is too noisy for Cognitiv detection to be active
    	/*!
    		\param state - Pointer

    		\return detection state (0: Not Active, 1: Active)
    	*/
    	int ES_CognitivIsActive(Pointer state);


    	//! Query of the current wireless signal strength
    	/*!
    		\param state - Pointer

    		\return wireless signal strength [No Signal, Bad, Fair, Good, Excellent].

    		\sa int
    	*/
    	int ES_GetWirelessSignalStatus(Pointer state);

    	//! Clone Pointer
    	/*!
    		\param a - Destination of Pointer
    		\param b - Source of Pointer

    		\sa ES_Create
    	*/
    	void ES_Copy(Pointer a, Pointer b);

    	//! Check whether two states are with identical 'emotiv' state
    	/*!
    		\param a - Pointer
    		\param b - Pointer

    		\return 1: Equal, 0: Different

    		\sa ES_ExpressivEqual, ES_CognitivEqual, ES_EmoEngineEqual, ES_Equal
    	*/
    	int ES_AffectivEqual(Pointer a, Pointer b);

    	//! Check whether two states are with identical Expressiv state, i.e. are both state representing the same facial expression
    	/*!
    		\param a - Pointer
    		\param b - Pointer

    		\return 1: Equal, 0: Different

    		\sa ES_AffectivEqual, ES_CognitivEqual, ES_EmoEngineEqual, ES_Equal
    	*/
    	int ES_ExpressivEqual(Pointer a, Pointer b);

    	//! Check whether two states are with identical Cognitiv state
    	/*!
    		\param a - Pointer
    		\param b - Pointer

    		\return 1: Equal, 0: Different

    		\sa ES_AffectivEqual, ES_ExpressivEqual, ES_EmoEngineEqual, ES_Equal
    	*/
    	int ES_CognitivEqual(Pointer a, Pointer b);

    	//! Check whether two states are with identical EmoEngine state.
    	/*!
    		This function is comparing the time since EmoEngine start,
    		the wireless signal strength and the signal quality of different channels

    		\param a - Pointer
    		\param b - Pointer

    		\return 1: Equal, 0: Different

    		\sa ES_AffectivEqual, ES_ExpressivEqual, ES_CognitivEqual, ES_Equal
    	*/
    	int ES_EmoEngineEqual(Pointer a, Pointer b);

    	//! Check whether two Pointers are identical
    	/*!
    		\param a - Pointer
    		\param b - Pointer

    		\return 1: Equal, 0: Different

    		\sa ES_AffectivEqual, ES_ExpressivEqual, ES_EmoEngineEqual
    	*/
    	int ES_Equal(Pointer a, Pointer b);

    	//! Get the level of charge remaining in the headset battery
    	/*!
    		\param state			- Pointer
    		\param chargeLevel		- the current level of charge in the headset battery
    		\param maxChargeLevel	- the maximum level of charge in the battery

    	*/
    	void ES_GetBatteryChargeLevel(Pointer state, IntByReference chargeLevel, IntByReference maxChargeLevel);
}