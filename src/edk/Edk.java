package edk;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.*;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;

public interface Edk extends Library  
{
    	Edk INSTANCE = (Edk)
            Native.loadLibrary("edk",
            		Edk.class);
    	
    	public enum EE_ExpressivThreshold_t {
    		EXP_SENSITIVITY
    	} ;

    	//! Expressiv Suite training control enumerator
    	public enum EE_ExpressivTrainingControl_t {
    		EXP_NONE(0), EXP_START(1), EXP_ACCEPT(2), EXP_REJECT(3), EXP_ERASE(4), EXP_RESET(5);
    		private int type;
    		EE_ExpressivTrainingControl_t (int val)
    		{
    			type = val;
    		}
    		public int toInt()
    		{
    			return type;
    		}
    	} 

    	//! Expressiv Suite signature type enumerator
    	public enum EE_ExpressivSignature_t {
    		EXP_SIG_UNIVERSAL, EXP_SIG_TRAINED
    	} 

    	 //! Cognitiv Suite training control enumerator
    	public enum EE_CognitivTrainingControl_t {
    		COG_NONE(0), COG_START(1), COG_ACCEPT(2), COG_REJECT(3), COG_ERASE(4), COG_RESET(5);
    		
    		private int type;
    		EE_CognitivTrainingControl_t(int val)
    		{
    			type = val;
    		}
    		public int getType()
    		{
    			return type;
    		}
    	} 

    //DEPLOYMENT::STABLE_RELEASE::REMOVE_START

    	//! Cognitiv Suite level enumerator
    	//@@ This constant has been obsoleted
    	public enum EE_CognitivLevel_t {
    		COG_LEVEL1(0), COG_LEVEL2(1), COG_LEVEL3(2), COG_LEVEL4(3);
    		private int level;
    		EE_CognitivLevel_t(int val)
    		{
    			level = val;
    		}
    		public int toInt()
    		{
    			return level;
    		}
    	} 

    	//! EmoEngine event types
    	public enum EE_Event_t {
    		EE_UnknownEvent		 (0x0000),
    		EE_EmulatorError	 (0x0001),
    		EE_ReservedEvent	 (0x0002),
    		EE_UserAdded		 (0x0010),
    		EE_UserRemoved		 (0x0020),
    		EE_EmoStateUpdated	 (0x0040),
    		EE_ProfileEvent		 (0x0080),
    		EE_CognitivEvent	 (0x0100),
    		EE_ExpressivEvent	 (0x0200),
    		EE_InternalStateChanged  (0x0400),
    		EE_AllEvent			 (0x07F0);

    		private int bit;
    		EE_Event_t(int bitNumber)
    		{
    			bit = bitNumber;
    		}
    		public int ToInt()
    		{
    			return(bit);
    		}
    	} 

    	//! Expressiv-specific event types
    	public enum EE_ExpressivEvent_t {
    		EE_ExpressivNoEvent, EE_ExpressivTrainingStarted, EE_ExpressivTrainingSucceeded,
    		EE_ExpressivTrainingFailed, EE_ExpressivTrainingCompleted, EE_ExpressivTrainingDataErased,
    		EE_ExpressivTrainingRejected, EE_ExpressivTrainingReset
    	} 
    	
    	//! Cognitiv-specific event types
    	public enum EE_CognitivEvent_t {
    		EE_CognitivNoEvent (0), 
    		EE_CognitivTrainingStarted (1), 
    		EE_CognitivTrainingSucceeded (2),
    		EE_CognitivTrainingFailed(3), 
    		EE_CognitivTrainingCompleted(4),
    		EE_CognitivTrainingDataErased(5),
    		EE_CognitivTrainingRejected(6), 
    		EE_CognitivTrainingReset(7),
    		EE_CognitivAutoSamplingNeutralCompleted(8), 
    		EE_CognitivSignatureUpdated(9);
    		
    		private int cType;
    		EE_CognitivEvent_t(int val)
    		{
    			cType = val;
    		}
    		public int getType()
    		{
    			return cType;
    		}
    	} 

    //DEPLOYMENT::NON_PREMIUM_RELEASE::REMOVE_START
    	public enum EE_DataChannels_t {
    		ED_COUNTER, ED_INTERPOLATED, ED_RAW_CQ,
    		ED_AF3, ED_F7, ED_F3, ED_FC5, ED_T7, 
    		ED_P7, ED_O1, ED_O2, ED_P8, ED_T8, 
    		ED_FC6, ED_F4, ED_F8, ED_AF4, ED_GYROX, 
    		ED_GYROY, ED_TIMESTAMP, ED_ES_TIMESTAMP, ED_FUNC_ID, ED_FUNC_VALUE, ED_MARKER, 
    		ED_SYNC_SIGNAL
    	} 
    //DEPLOYMENT::NON_PREMIUM_RELEASE::REMOVE_END

    	//! Input sensor description
    	public static class InputSensorDescriptor_t extends Structure {
    		EmoState.EE_InputChannels_t channelId;  // logical channel id
    		int                fExists;    // does this sensor exist on this headset model
    		String        pszLabel;   // text label identifying this sensor
    		double             xLoc;       // x coordinate from center of head towards nose
    		double             yLoc;       // y coordinate from center of head towards ears
    		double             zLoc;       // z coordinate from center of head toward top of skull
    	}


    	//! Initializes the connection to EmoEngine. This function should be called at the beginning of programs that make use of EmoEngine, most probably in initialization routine or constructor.
    	/*!	
    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if a connection is established

    		\sa edkErrorCode.h
    	*/
    	int
    		EE_EngineConnect(String strDevID);

    	
    	//! Initializes the connection to a remote instance of EmoEngine.
    	/*!
    		Blocking call

    		\param szHost - A null-terminated string identifying the hostname or IP address of the remote EmoEngine server
    		\param port - The port number of the remote EmoEngine server
    					- If connecting to the Emotiv Control Panel, use port 3008
    					- If connecting to the EmoComposer, use port 1726
    	
    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if a connection is established

    		\sa edkErrorCode.h
    	*/
    	int
    		EE_EngineRemoteConnect(String szHost, short port, String strDevID);

    	
    	//! Terminates the connection to EmoEngine. This function should be called at the end of programs which make use of EmoEngine, most probably in clean up routine or destructor.
    	/*!
    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if disconnection is achieved

    		\sa edkErrorCode.h
    	*/
    	int
    		EE_EngineDisconnect();


    	//! Controls the output of logging information from EmoEngine (which is off by default). This should only be enabled if instructed to do so by Emotiv developer support for the purposes of collecting diagnostic information.
    	/*!
    	    \param szFilename - The path of the logfile
    		\param fEnable - If non-zero, then diagnostic information will be written to logfile.
    		               - If zero, then all diagnostic information is suppressed (default).
    	    \param nReserved - Reserved for future use.

    	    \return EDK_ERROR_CODE
    		        - EDK_ERROR_CODEEDK_OK if the command succeeded
    	*/
    	int
    		EE_EnableDiagnostics(String szFilename, int fEnable, int nReserved);

    	
    	//! Returns a handle to memory that can hold an EmoEngine event. This handle can be reused by the caller to retrieve subsequent events.
    	/*!
    		\return Pointer
    	*/
    	Pointer
    		EE_EmoEngineEventCreate();


    	//! Returns a handle to memory that can hold a profile byte stream. This handle can be reused by the caller to retrieve subsequent profile bytes.
    	/*!
    		\return Pointer
    	*/
    	Pointer
    		EE_ProfileEventCreate();

    	
    	//! Frees memory referenced by an event handle.
    	/*!
    		\param hEvent - a handle returned by EE_EmoEngineEventCreate() or EE_ProfileEventCreate()
    	*/
    	void
    		EE_EmoEngineEventFree(Pointer hEvent);

    	
    	//! Returns a handle to memory that can store an EmoState. This handle can be reused by the caller to retrieve subsequent EmoStates.
    	/*!
    		\return Pointer
    	*/
    	Pointer
    		EE_EmoStateCreate();

    	
    	//! Frees memory referenced by an EmoState handle.
    	/*!
    		\param hState - a handle returned by EE_EmoStateCreate()
    	*/
    	void
    		EE_EmoStateFree(Pointer hState);


    	//! Returns the event type for an event already retrieved using EE_EngineGetNextEvent.
    	/*!
    		\param hEvent - a handle returned by EE_EmoEngineEventCreate()
    	
    		\return int
    	*/
    	int
    		EE_EmoEngineEventGetType(Pointer hEvent);

    	
    	//! Returns the Cognitiv-specific event type for an EE_CognitivEvent event already retrieved using EE_EngineGetNextEvent.
    	/*!
    		\param hEvent - a handle returned by EE_EmoEngineEventCreate()
    	
    		\return int
    	*/
    	int
    		EE_CognitivEventGetType(Pointer hEvent);


    	//! Returns the Expressiv-specific event type for an EE_ExpressivEvent event already retrieved using EE_EngineGetNextEvent.
    	/*!
    		\param hEvent - a handle returned by EE_EmoEngineEventCreate()
    	
    		\return int
    	*/
    	int
    		EE_ExpressivEventGetType(Pointer hEvent);
    	

    	//! Retrieves the user ID for EE_UserAdded and EE_UserRemoved events.
    	/*!
    		\param hEvent - a handle returned by EE_EmoEngineEventCreate()
    		\param pUserIdOut - receives the user ID associated with the current event
    	
    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful.

    		\sa edkErrorCode.h
    	*/
    	int
    		EE_EmoEngineEventGetUserId(Pointer hEvent, IntByReference pUserIdOut);

    	
    	//! Copies an EmoState returned with a EE_EmoStateUpdate event to memory referenced by an Pointer.
    	/*!
    		\param hEvent - a handle returned by EE_EmoEngineEventCreate() and populated with EE_EmoEngineGetNextEvent()
    		\param hEmoState - a handle returned by EE_EmoStateCreate
    	
    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful.

    		\sa edkErrorCode.h
    	*/
    	int
    		EE_EmoEngineEventGetEmoState(Pointer hEvent, Pointer hEmoState);
    	

    	//! Retrieves the next EmoEngine event
    	/*!
    		Non-blocking call

    		\param hEvent - a handle returned by EE_EmoEngineEventCreate()

    		\return EDK_ERROR_CODE
                    <ul>
    		        <li> EDK_ERROR_CODEEDK_OK if a new event has been retrieved
    				<li> EDK_ERROR_CODEEDK_NO_EVENT if no new events have been generated by EmoEngine
    				</ul>
    		
    		\sa edkErrorCode.h
    	*/
    	int
    		EE_EngineGetNextEvent(Pointer hEvent);

    	
    	//! Clear a specific EmoEngine event type or all events currently inside the event queue. Event flags can be combined together as one argument except EE_UnknownEvent and EE_EmulatorError.
    	/*!
    		\param eventTypes - EmoEngine event type (int), multiple events can be combined such as (EE_UserAdded | EE_UserRemoved)

    		\return EDK_ERROR_CODE
                    <ul>
    		        <li> EDK_ERROR_CODEEDK_OK if the events have been cleared from the queue
    				<li> EDK_ERROR_CODEEDK_INVALID_PARAMETER if input event types are invalid
    				</ul>
    		
    		\sa int, edkErrorCode.h
    	*/
    	int
    		EE_EngineClearEventQueue(int eventTypes);

    	
    	//! Retrieves number of active users connected to the EmoEngine.
    	/*!
    		\param pNumUserOut - receives number of users

    		\return EDK_ERROR_CODE
    		        - EDK_ERROR_CODEEDK_OK if successful.

    		\sa edkErrorCode.h
    	*/
    	int
    		EE_EngineGetNumUser(IntByReference pNumUserOut);


    	//! Sets the player number displayed on the physical input device (currently the USB Dongle) that corresponds to the specified user
    	/*!
    		\param userId - EmoEngine user ID
    		\param playerNum - application assigned player number displayed on input device hardware (must be in the range 1-4)
    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h
    	*/
    	int
    		EE_SetHardwarePlayerDisplay(int userId, int playerNum);


    	//! Loads an EmoEngine profile for the specified user.  
    	/*!
    		\param userId - user ID
    		\param profileBuffer - pointer to buffer containing a serialized user profile previously returned from EmoEngine.
    		\param length - buffer size (number of byte[]s)

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODE if the function succeeds in loading this profile

    		\sa edkErrorCode.h
    	*/
    	int
    		EE_SetUserProfile(int userId, byte[] profileBuffer, int length);


    	//! Returns user profile data in a synchronous manner.
    	/*!
    	     Fills in the event referred to by hEvent with an EE_ProfileEvent event
    		 that contains the profile data for the specified user.

    		 \param userId - user ID
    		 \param hEvent - a handle returned by EE_EmoEngineEventCreate()

    		 \return EDK_ERROR_CODE
    		         - EDK_ERROR_CODEEDK_OK if successful

    		 \sa edkErrorCode.h
    	*/
    	int
    		EE_GetUserProfile(int userId, Pointer hEvent);


    	//! Returns a serialized user profile for a default user in a synchronous manner.
    	/*!
    	    Fills in the event referred to by hEvent with an EE_ProfileEvent event
    		that contains the profile data for the default user

    		 \param hEvent - a handle returned by EE_EmoEngineEventCreate()

    		 \return EDK_ERROR_CODE
    		         - EDK_ERROR_CODEEDK_OK if successful

    		 \sa edkErrorCode.h
    	*/
    	int
    		EE_GetBaseProfile(Pointer hEvent);
    	

    	//! Returns the number of bytes required to store a serialized version of the requested user profile.
    	/*!	
    		\param hEvt - an Pointer of type EE_ProfileEvent
    		\param pProfileSizeOut - receives number of bytes required by the profile

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h
    	*/
    	int
    		EE_GetUserProfileSize(Pointer hEvt, IntByReference pProfileSizeOut);

    	
    	//! Copies a serialized version of the requested user profile into the caller's buffer.
    	/*!		
    		\param hEvt - an Pointer returned in a EE_ProfileEvent event
    		\param destBuffer - pointer to a destination buffer
    		\param length - the size of the destination buffer in byte[]s

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h
    	*/
    	int
    		EE_GetUserProfileBytes(Pointer hEvt, byte[] destBuffer, int length);

    	//!  Loads a user profile from disk and assigns it to the specified user
    	/*!		
    		\param userID - a valid user ID
    		\param szInputFilename - platform-dependent filesystem path of saved user profile

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h
    	*/
    	int
    		EE_LoadUserProfile(int userID, String szInputFilename);

    	//!  Saves a user profile for specified user to disk
    	/*!		
    		\param userID - a valid user ID
    		\param szOutputFilename - platform-dependent filesystem path for output file

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h
    	*/
    	int
    		EE_SaveUserProfile(int userID, String szOutputFilename);

    	//! Set threshold for Expressiv algorithms
    	/*!
    		\param userId - user ID
    		\param algoName - Expressiv algorithm type
    		\param thresholdName - Expressiv threshold type
    		\param value - threshold value (min: 0 max: 1000)

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h, int, int
    	*/
    	int
    		EE_ExpressivSetThreshold(int userId, int algoName, int thresholdName, int value);


    	//! Get threshold from Expressiv algorithms
    	/*!
    		\param userId - user ID
    		\param algoName - Expressiv algorithm type
    		\param thresholdName - Expressiv threshold type
    		\param pValueOut - receives threshold value

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h, int, int
    	*/
    	int
    		EE_ExpressivGetThreshold(int userId, int algoName, int thresholdName, IntByReference pValueOut);


    	//! Set the current facial expression for Expressiv training
    	/*!
    		Blocking call

    		\param userId - user ID
    		\param action - which facial expression would like to be trained

    		\return EDK_ERROR_CODE - current status of EmoEngine. If the query is successful, EDK_ERROR_CODEOK.

    		\sa edkErrorCode.h, int
    	*/
    	int 
    		EE_ExpressivSetTrainingAction(int userId, int action);


    	//! Set the control flag for Expressiv training
    	/*!
    		Blocking call

    		\param userId - user ID
    		\param control - pre-defined control command

    		\return EDK_ERROR_CODE - current status of EmoEngine. If the query is successful, EDK_ERROR_CODEOK.

    		\sa edkErrorCode.h, int
    	*/
    	int 
    		EE_ExpressivSetTrainingControl(int userId, int control);


    	//! Gets the facial expression currently selected for Expressiv training
    	/*!
    		Blocking call

    		\param userId - user ID
    		\param pActionOut - receives facial expression currently selected for training

    		\return EDK_ERROR_CODE - current status of EmoEngine. If the query is successful, EDK_ERROR_CODEOK.

    		\sa EDK_ERROR_CODE, int
    	*/
    	int 
    		EE_ExpressivGetTrainingAction(int userId, IntByReference pActionOut);

    	
    	//! Return the duration of a Expressiv training session
    	/*!
    		\param userId - user ID
    		\param pTrainingTimeOut - receive the training time in ms

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h
    	*/
    	int 
    		EE_ExpressivGetTrainingTime(int userId, IntByReference pTrainingTimeOut);


    	//! Gets a list of the actions that have been trained by the user
    	/*!
    		Blocking call

    		\param userId - user ID
    		\param pTrainedActionsOut - receives a bit vector composed of int contants

    		\return EDK_ERROR_CODE - current status of EmoEngine. If the query is successful, EDK_ERROR_CODEOK.

    		\sa EDK_ERROR_CODE, int
    	*/
        int
            EE_ExpressivGetTrainedSignatureActions(int userId, NativeLongByReference pTrainedActionsOut);


    	//! Gets a flag indicating if the user has trained sufficient actions to activate a trained signature
    	/*!
            *pfAvailableOut will be set to 1 if the user has trained EXP_NEUTRAL and at least
            one other Expressiv action.  Otherwise, *pfAvailableOut == 0.

    		Blocking call

    		\param userId - user ID
    		\param pfAvailableOut - receives an int that is non-zero if a trained signature can be activated

    		\return EDK_ERROR_CODE - current status of EmoEngine. If the query is successful, EDK_ERROR_CODEOK.

    		\sa EDK_ERROR_CODE
    	*/
        int
    		EE_ExpressivGetTrainedSignatureAvailable(int userId, IntByReference pfAvailableOut);

    	//! Configures the Expressiv suite to use either the built-in, universal signature or a personal, trained signature
    	/*!
            Note: Expressiv defaults to use its universal signature.  This function will fail if EE_ExpressivGetTrainedSignatureAvailable returns false.

    		Blocking call

    		\param userId - user ID
    		\param sigType - signature type to use

    		\return EDK_ERROR_CODE - current status of EmoEngine. If the query is successful, EDK_ERROR_CODEOK.

    		\sa EDK_ERROR_CODE, int
    	*/
    	int 
    		EE_ExpressivSetSignatureType(int userId, int sigType);

    	//! Indicates whether the Expressiv suite is currently using either the built-in, universal signature or a trained signature
    	/*!
    		Blocking call

    		\param userId - user ID
    		\param pSigTypeOut - receives the signature type currently in use

    		\return EDK_ERROR_CODE - current status of EmoEngine. If the query is successful, EDK_ERROR_CODEOK.

    		\sa EDK_ERROR_CODE, int
    	*/
    	int 
    		EE_ExpressivGetSignatureType(int userId, IntByReference pSigTypeOut);


    //DEPLOYMENT::STABLE_RELEASE::REMOVE_START

    	//@@ These APIs have been obsoleted
    	//@@ Use EE_CognitivSetActiveActions and EE_CognitivGetActiveActions instead

    	//! Set the current Cognitiv level and corresponding action types
    	/*!
    		\param userId - user ID
    		\param level - current level (min: 1, max: 4)
    		\param level1Action - action type in level 1
    		\param level2Action - action type in level 2
    		\param level3Action - action type in level 3
    		\param level4Action - action type in level 4

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h, int, int
    	*/
    	int 
    		EE_CognitivSetCurrentLevel(int userId, int level,
    									int level1Action, int level2Action,
    									int level3Action, int level4Action);

    	
    	//! Query the current Cognitiv level and corresponding action types
    	/*!
    		\param userId - user ID
    		\param pLevelOut - current level (min: 1, max: 4)
    		\param pLevel1ActionOut - action type in level 1
    		\param pLevel2ActionOut - action type in level 2
    		\param pLevel3ActionOut - action type in level 3
    		\param pLevel4ActionOut - action type in level 4

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h, int, int
    	*/
    	int 
    		EE_CognitivGetCurrentLevel(int userId, IntByReference pLevelOut,
    									IntByReference pLevel1ActionOut, IntByReference pLevel2ActionOut,
    									IntByReference pLevel3ActionOut, IntByReference pLevel4ActionOut);

    //DEPLOYMENT::STABLE_RELEASE::REMOVE_END


    	//! Set the current Cognitiv active action types
    	/*!
    		\param userId - user ID
    		\param activeActions - a bit vector composed of int contants

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h, int
    	*/
    	//int
    	//	EE_CognitivSetActiveActions(int userId, NativeLong activeActions);

    	int
			EE_CognitivSetActiveActions(int userId, long activeActions);
    	//! Get the current Cognitiv active action types
    	/*!
    		\param userId - user ID
    		\param pActiveActionsOut - receive a bit vector composed of int contants

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h, int
    	*/
    	int
    		EE_CognitivGetActiveActions(int userId, NativeLongByReference pActiveActionsOut);

    	
    	//! Return the duration of a Cognitiv training session
    	/*!
    		\param userId - user ID
    		\param pTrainingTimeOut - receive the training time in ms

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h
    	*/
    	int 
    		EE_CognitivGetTrainingTime(int userId, IntByReference pTrainingTimeOut);

    	
    	//! Set the training control flag for Cognitiv training
    	/*!
    		\param userId - user ID
    		\param control - pre-defined Cognitiv training control

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h, int
    	*/
    	int 
    		EE_CognitivSetTrainingControl(int userId, int control);

    	
    	//! Set the type of Cognitiv action to be trained
    	/*!
    		\param userId - user ID
    		\param action - which action would like to be trained

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h, int
    	*/
    	int 
    		EE_CognitivSetTrainingAction(int userId, int action);


    	//! Get the type of Cognitiv action currently selected for training
    	/*!
    		\param userId - user ID
    		\param pActionOut - action that is currently selected for training

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h, int
    	*/
    	int 
    		EE_CognitivGetTrainingAction(int userId, IntByReference pActionOut);


    	//! Gets a list of the Cognitiv actions that have been trained by the user
    	/*!
    		Blocking call

    		\param userId - user ID
    		\param pTrainedActionsOut - receives a bit vector composed of int contants

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h, int
    	*/
        int
            EE_CognitivGetTrainedSignatureActions(int userId, NativeLongByReference pTrainedActionsOut);
    	
    	
    	//! Gets the current overall skill rating of the user in Cognitiv
    	/*!
    		Blocking call

    		\param userId - user ID
    		\param pOverallSkillRatingOut - receives the overall skill rating [from 0.0 to 1.0]

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h
    	*/
        int
            EE_CognitivGetOverallSkillRating(int userId, FloatByReference pOverallSkillRatingOut);


    	//! Gets the current skill rating for particular Cognitiv actions of the user
    	/*!
    		Blocking call

    		\param userId - user ID
    		\param action - a particular action of int contant
    		\param pActionSkillRatingOut - receives the action skill rating [from 0.0 to 1.0]

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h, int
    	*/
        int
            EE_CognitivGetActionSkillRating(int userId, int action, FloatByReference pActionSkillRatingOut);

    	
    	//! Set the overall sensitivity for all Cognitiv actions
    	/*!
    		\param userId - user ID
    		\param level - sensitivity level of all actions (lowest: 1, highest: 7)

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h
    	*/
    	int 
    		EE_CognitivSetActivationLevel(int userId, int level);

    	
    	//! Set the sensitivity of Cognitiv actions
    	/*!
    		\param userId - user ID
    		\param action1Sensitivity - sensitivity of action 1 (min: 1, max: 10)
    		\param action2Sensitivity - sensitivity of action 2 (min: 1, max: 10)
    		\param action3Sensitivity - sensitivity of action 3 (min: 1, max: 10)
    		\param action4Sensitivity - sensitivity of action 4 (min: 1, max: 10)

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h
    	*/
    	int 
    		EE_CognitivSetActionSensitivity(int userId,
    										int action1Sensitivity, int action2Sensitivity,
    										int action3Sensitivity, int action4Sensitivity);

    	
    	//! Get the overall sensitivity for all Cognitiv actions
    	/*!
    		\param userId - user ID
    		\param pLevelOut - sensitivity level of all actions (min: 1, max: 10)

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h
    	*/
    	int 
    		EE_CognitivGetActivationLevel(int userId, IntByReference pLevelOut);

    	
    	//! Query the sensitivity of Cognitiv actions
    	/*!
    		\param userId - user ID
    		\param pAction1SensitivityOut - sensitivity of action 1
    		\param pAction2SensitivityOut - sensitivity of action 2
    		\param pAction3SensitivityOut - sensitivity of action 3
    		\param pAction4SensitivityOut - sensitivity of action 4

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h
    	*/
    	int 
    		EE_CognitivGetActionSensitivity(int userId,
    										IntByReference pAction1SensitivityOut, IntByReference pAction2SensitivityOut,
    										IntByReference pAction3SensitivityOut, IntByReference pAction4SensitivityOut);

    	
    	//! Start the sampling of Neutral state in Cognitiv
    	/*!
    		\param userId - user ID

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h
    	*/
    	int
    		EE_CognitivStartSamplingNeutral(int userId);

    	
    	//! Stop the sampling of Neutral state in Cognitiv
    	/*!
    		\param userId - user ID

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h
    	*/
    	int
    		EE_CognitivStopSamplingNeutral(int userId);

    	
    	//! Enable or disable signature caching in Cognitiv
    	/*!
    		\param userId  - user ID
    		\param enabled - flag to set status of caching (1: enable, 0: disable)

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h
    	*/
    	int
    		EE_CognitivSetSignatureCaching(int userId, int enabled);


    	//! Query the status of signature caching in Cognitiv
    	/*!
    		\param userId  - user ID
    		\param pEnabledOut - flag to get status of caching (1: enable, 0: disable)

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h
    	*/
    	int
    		EE_CognitivGetSignatureCaching(int userId, IntByReference pEnabledOut);


    	//! Set the cache size for the signature caching in Cognitiv
    	/*!
    		\param userId - user ID
    		\param size   - number of signatures to be kept in the cache (0: unlimited)

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h
    	*/
    	int
    		EE_CognitivSetSignatureCacheSize(int userId, int size);


    	//! Get the current cache size for the signature caching in Cognitiv
    	/*!
    		\param userId - user ID
    		\param pSizeOut - number of signatures to be kept in the cache (0: unlimited)

    		\return EDK_ERROR_CODE
    				- EDK_ERROR_CODEEDK_OK if successful

    		\sa edkErrorCode.h
    	*/
    	int
    		EE_CognitivGetSignatureCacheSize(int userId, IntByReference pSizeOut);


    	//! Returns a struct containing details about the specified EEG channel's headset 
        /*!
            \param channelId - channel identifier (see EmoStateDll.h)
            \param pDescriptorOut - provides detailed sensor location and other info

            \return EDK_ERROR_CODE
                    - EDK_ERROR_CODEEDK_OK if successful

            \sa EmoStateDll.h, edkErrorCode.h
    	*/
    	int
    		EE_HeadsetGetSensorDetails(int channelId, InputSensorDescriptor_t pDescriptorOut);


    	//! Returns the current hardware version of the headset and dongle for a particular user
        /*!
            \param userId - user ID for query
    		\param pHwVersionOut - hardware version for the user headset/dongle pair. hiword is headset version, loword is dongle version.

            \return EDK_ERROR_CODE
                    - EDK_ERROR_CODEEDK_OK if successful

            \sa EmoStateDll.h, edkErrorCode.h
    	*/
    	int
    		EE_HardwareGetVersion(int userId, NativeLongByReference pHwVersionOut);

    	//! Returns the current version of the Emotiv SDK software
        /*!
    		\param pszVersionOut - SDK software version in X.X.X.X format. Note: current beta releases have a major version of 0.
    		\param nVersionChars - Length of byte buffer pointed to by pszVersion argument.
    		\param pBuildNumOut  - Build number.  Unique for each release.

            \return EDK_ERROR_CODE
                    - EDK_ERROR_CODEEDK_OK if successful

            \sa edkErrorCode.h
    	*/
    	int
    		EE_SoftwareGetVersion(String pszVersionOut, int nVersionChars, NativeLongByReference pBuildNumOut);

    	//! Returns the delta of the movement of the gyro since the previous call for a particular user
    	/*!
    		\param userId - user ID for query
    		\param pXOut  - horizontal displacement
    		\param pYOut  - vertical displacment

    		\return EDK_ERROR_CODE
                    - EDK_ERROR_CODEEDK_OK if successful

            \sa EmoStateDll.h
            \sa edkErrorCode.h
    	*/
    	int
    		EE_HeadsetGetGyroDelta(int userId, IntByReference pXOut, IntByReference pYOut);

    	//! Re-zero the gyro for a particular user
    	/*!
    		\param userId - user ID for query

    		\return EDK_ERROR_CODE
                    - EDK_ERROR_CODEEDK_OK if successful

            \sa EmoStateDll.h
            \sa edkErrorCode.h
    	*/
    	int
    		EE_HeadsetGyroRezero(int userId);

    	//! Returns a handle to memory that can hold an optimization paramaeter which is used to configure the behaviour of optimization
    	/*!
    		\return Pointer
    	*/
    	Pointer
    		EE_OptimizationParamCreate();

    	//! Frees memory referenced by an optimization parameter handle
    	/*!
    		\param hParam - a handle returned by EE_OptimizationParamCreate()
    	*/
    	void
    		EE_OptimizationParamFree(Pointer hParam);

    	//! Enable optimization. EmoEngine will try to optimize its performance according to the information passed in with optimization parameter. EmoEngine guarantees the correctness of the results of vital algorithms. For algorithms that are not vital, results are undefined.
    	/*!
    		\param hParam - a handle returned by EE_OptimizationParamCreate()
    		\return EDK_ERROR_CODE
                    - EDK_ERROR_CODEEDK_OK if successful
    	*/
    	int
    		EE_OptimizationEnable(Pointer hParam);

    	//! Determine whether optimization is on
    	/*!
    		\param pEnabledOut - receives information about whether optimization is on
    		\return EDK_ERROR_CODE
                    - EDK_ERROR_CODEEDK_OK if successful
    	*/
    	int
    		EE_OptimizationIsEnabled(IntByReference pEnabledOut);

    	//! Disable optimization
    	/*!
    		\return EDK_ERROR_CODE
                    - EDK_ERROR_CODEEDK_OK if successful
    	*/
    	int
    		EE_OptimizationDisable();

    	//! Get optimization parameter.  If optimization is not enabled (this can be checked with EE_OptimmizationIsEnabled) then the results attached to the hParam parameter are undefined.
    	/*!
    		\param hParam - a handle returned by EE_OptimizationParamCreate()
    		\return EDK_ERROR_CODE
                    - EDK_ERROR_CODEEDK_OK if successful
    	*/
    	int
    		EE_OptimizationGetParam(Pointer hParam);

    	//! Get a list of vital algorithms of specific suite from optimization parameter
    	/*!
    		\param hParam - a handle returned by EE_OptimizationParamCreate()
    		\param suite - suite that you are interested in
    		\param pVitalAlgorithmBitVectorOut - receives a list of vital algorithm composed of int, int or int depending on the suite parameter
    		\return EDK_ERROR_CODE
                    - EDK_ERROR_CODEEDK_OK if successful
    	*/
    	int
    		EE_OptimizationGetVitalAlgorithm(Pointer hParam, int suite, IntByReference pVitalAlgorithmBitVectorOut);

    	//! Set a list of vital algorithms of specific suite to optimization parameter
    	/*!
    		\param hParam - a handle returned by EE_OptimizationParamCreate()
    		\param suite - suite that you are interested in
    		\param vitalAlgorithmBitVector - a list of vital algorithm composed of int, int or int depended on the suite parameter passed in
    		\return EDK_ERROR_CODE
                    - EDK_ERROR_CODEEDK_OK if successful
    	*/
    	int
    		EE_OptimizationSetVitalAlgorithm(Pointer hParam, int suite, int vitalAlgorithmBitVector);

    	//! Resets all settings and user-specific profile data for the specified detection suite
    	/*!
    		\param userId - user ID
    		\param suite - detection suite (Expressiv, Affectiv, or Cognitiv)
    		\param detectionBitVector - identifies specific detections.  Set to zero for all detections.
    		\return EDK_ERROR_CODE
                    - EDK_ERROR_CODEEDK_OK if successful
    	*/
        int EE_ResetDetection(int userId, int suite, int detectionBitVector);

    //DEPLOYMENT::NON_PREMIUM_RELEASE::REMOVE_START
    	//! Returns a handle to memory that can hold data. This handle can be reused by the caller to retrieve subsequent data.
    	/*!
    		\return Pointer
    	*/
    	Pointer EE_DataCreate();

    	//! Frees memory referenced by a data handle.
    	/*!
    		\param hData - a handle returned by EE_DataCreate()
    	*/
    	void EE_DataFree(Pointer hData);

    	//! Updates the content of the data handle to point to new data since the last call
    	/*!
    		\param userId - user ID
    		\param hData - a handle returned by EE_DataCreate()
    		\return EDK_ERROR_CODE
                    - EDK_ERROR_CODEEDK_OK if successful
    	*/
    	int EE_DataUpdateHandle(int userId, Pointer hData);

    	//! Extracts data from the data handle
    	/*!
    		\param hData - a handle returned by EE_DataCreate()
    		\param channel - channel that you are interested in
    		\param buffer - pre-allocated buffer
    		\param bufferSizeInSample - size of the pre-allocated buffer
    		\return EDK_ERROR_CODE
                    - EDK_ERROR_CODEEDK_OK if successful
    	*/
    	int EE_DataGet(Pointer hData, int channel, double[] buffer, int bufferSizeInSample);

    	//! Returns number of sample of data stored in the data handle
    	/*!
    		\param hData - a handle returned by EE_DataCreate()
    		\param nSampleOut - receives the number of sample of data stored in teh data handle
    		\return EDK_ERROR_CODE
                    - EDK_ERROR_CODEEDK_OK if successful
    	*/
    	int EE_DataGetNumberOfSample(Pointer hData, IntByReference nSampleOut);

    	//! Sets the size of the data buffer. The size of the buffer affects how frequent EE_DataUpdateHandle() needs to be called to prevent data loss.
    	/*!
    		\param bufferSizeInSec - buffer size in second
    		\return EDK_ERROR_CODE
                    - EDK_ERROR_CODEEDK_OK if successful
    	*/
    	int EE_DataSetBufferSizeInSec(float bufferSizeInSec);

    	//! Returns the size of the data buffer
    	/*! 
    		\param pBufferSizeInSecOut - receives the size of the data buffer
    		\return EDK_ERROR_CODE
                    - EDK_ERROR_CODEEDK_OK if successful
    	*/
    	int EE_DataGetBufferSizeInSec(FloatByReference pBufferSizeInSecOut);

    	//! Controls acquisition of data from EmoEngine (which is off by default).
    	/*!
    	    \param userId - user ID
    		\param enable - If true, then enables data acquisition
    		              - If false, then disables data acquisition
    		\return EDK_ERROR_CODE
    		        - EDK_ERROR_CODEEDK_OK if the command succeeded
    	*/
    	int EE_DataAcquisitionEnable(int userId, Boolean enable);

    	//! Returns whether data acquisition is enabled
    	/*!
    		\param userId - user ID
    		\param pEnableOut - receives whether data acquisition is enabled
    		\return EDK_ERROR_CODE
    		        - EDK_ERROR_CODEEDK_OK if the command succeeded
    	*/
    	int EE_DataAcquisitionIsEnabled(int userId, IntByReference pEnableOut);

    	//! Sets sychronization signal
    	/*!
    		\param userId - user ID
    		\param signal - value of the sychronization signal
    		\return EDK_ERROR_CODE
    		        - EDK_ERROR_CODEEDK_OK if the command succeeded
    	*/
    	int EE_DataSetSychronizationSignal(int userId, int signal);

    	//! Sets marker
    	/*!
    		\param userId - user ID
    		\param marker - value of the marker
    		\return EDK_ERROR_CODE
    		        - EDK_ERROR_CODEEDK_OK if the command succeeded
    	*/
    	int EE_DataSetMarker(int userId, int marker);

    	//! Gets sampling rate
    	/*!
    		\param userId - user ID
    		\param samplingRateOut - receives the sampling rate
    		\return EDK_ERROR_CODE
    		        - EDK_ERROR_CODEEDK_OK if the command succeeded
    	*/
    	int EE_DataGetSamplingRate(int userId, IntByReference samplingRateOut);
    	
    	
    //DEPLOYMENT::NON_PREMIUM_RELEASE::REMOVE_END
}
    