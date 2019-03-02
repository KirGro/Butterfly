package com.team3335.butterfly;

import java.util.HashMap;

import com.team3335.butterfly.ControlBoard.*;
import com.team3335.butterfly.states.DrivetrainState.DriveModeState;

public class Constants {

    public static final double kLooperDt = 0.01;	//Honestly no clue what this is, used by team3335.butterfly.loops.Looper, I just copied from Cheesy Poofs. Potentially deals with loop timing?
	public static final int kLongCANTimeoutMs = 100;

	public static final int kRobot = 2;

	/* FIELD CONSTATNS */

	//Cargo
	public static final double kFloorToLowCargo = 27.5;
	public static final double kFloorToMiddleCargo = 55.5;
	public static final double kFloorToHighCargo = 83.5;
	
	public static final double kFloorToBottomCargoShip = 31.5;
	public static final double kFloorToTopCargoShip = 31.5;

    //Hatch
    public static final double kHatchTargetWidth = 14.5;
    public static final double kHatchTargetHeight = 5.75;
	public static final double kHatchTargetBottomToHatchCenter = 6.5;
	public static final double kFloorToLowHatchCenter = 19;
	public static final double kFloorToLMiddleHatchCenter = 47;
	public static final double kFloorToHighHatchCenter = 75;
    
    
	/* ROBOT PHYSICAL CONSTANTS*/
	
	//Drivetrain
	public static final double kDrivetrainWheelDiameterInches = 4.0;
	public static final double kMecanumWheelWidth = 2.25;
	public static final double kSkidSteerWidth = 1.347;
	public static final double kMecanumWheelbaseWidthInches = 21.596300;	//Sidenote: length runs parallel to the Inner and Outer Side Chassis pieces
	public static final double kMecanumWheelbaseLengthInches = 14.000000;	//Note: All dimension taken from CAD model, when that drivetrain is deployed
	public static final double kMecanumWheelbaseDiameter = Math.sqrt(Math.pow(kMecanumWheelbaseWidthInches, 2) + Math.pow(kMecanumWheelbaseLengthInches, 2));
	public static final double kSkidSteerWheelbaseWidthInches = 20.203800;
	public static final double kSkidSteerWheelbaseLengthInches = 22.869944; 
	public static final double kSkidSteerWheelbaseDiameter = Math.sqrt(Math.pow(kSkidSteerWheelbaseWidthInches, 2) + Math.pow(kSkidSteerWheelbaseLengthInches, 2));
	public static final boolean kUsingTalonSRXs = true;
	public static final int kSRXEncoderCPR = 4096;

	public static final double kGearRatio1 = -(14/56);	//From motor output to encoder shaft
	public static final double kGearRatio2 = -(40/50);	//From encoder shaft to mecanum
	public static final double kChainRatio1 = (12/24);	//From mecanum to high track
	
	public static final double kMotorToMecanumRatio = kGearRatio1*kGearRatio2;
	public static final double kMecanumToSkidSteerRatio = kChainRatio1;
	public static final double kMotorToSkidSteerRatio = kMotorToMecanumRatio*kMecanumToSkidSteerRatio;

	//Carriage

	//Elevator
	public static final double kElevatorMaxTravel = (kRobot == 2 ? 41 : 43); //In inches
	
	
	//Vision
	public static final double kCameraHeight = (kRobot == 2 ? 37 + 1/16:36); //TODO Correct
	public static final double kCameraAngle = (kRobot == 2 ? -13 : -18); 	//TODO Correct
	public static final double kCameraDistanceFromFront = (kRobot == 2 ? 15 + 5/8 : 12); //TODO Correct

	
	/* SOFTWARE CONSTANTS / PREFERENCES */
	
	//Drive Controls
	public static final double kJoystickDeadband = .05;
	public static final double kGamepadDeadband = .1;
	//public static final double kSkidSteerMaxPower = 1; 
	//public static final double kMecanumMaxPower = .5;
	
		//public static final boolean kUseDriveModeCorrection = false; //TODO Maybe add this later... 
	public static final boolean kFixIncompatableDriveMode = true;
	public static final DriveModeState kDefaultMecanumDriveState = DriveModeState.MECANUM_ROBOT_RELATIVE;
	public static final DriveModeState kDefaultSkidSteerDriveState = DriveModeState.TANK;

	//Carriage
	
	//Vision
	public static final int kPixelHeight = 240;
	public static final int kPixelWidth = 320;
	public static final double kVerticalFOV = 41;
	public static final double kHorizontalFOV = 54;
	




	
	/* I/O */
	
	//Drivetrain
	public static final int kFrontRightCANId = 11; 
	public static final int kFrontLeftCANId = 14; 
	public static final int kBackRightCANId = 12; 
	public static final int kBackLeftCANId = 13; 
	
	public static final int kFrontRightPDPId = 0;
	public static final int kFrontLeftPDPId = 1;
	public static final int kBackRightPDPId = 2;
	public static final int kBackLeftPDPId = 3;
	
	public static final boolean kDrivetrainUsingSpringSolenoids = true;
	public static final int kShifterModule = 1;
	public static final int kShifterRightMecId = 2;
	public static final int kShifterRightSkidId = 0;
	public static final int kShifterLeftMecId = 3;
	public static final int kShifterLeftSkidId = 1;

	//Elevator
	public static final int kMasterWinchCANId = -1; //TODO
	public static final int kSlave1WinchCANId = -1; //TODO

	//Carriage
	public static final int kCarriageRollerWheelCANId = 23;

	public static final int kCarriageModule = 1;
	public static final int kHatchPusher = 2;
	public static final int kHatchPickup = 3;
	
		//TODO Add dio for laser distance sensor from REV here

	//Rear Pickup 
	public static final int kMasterArmCANId = -1; //TODO
	public static final int kSlave1ArmCANd = -1; //TODO
	public static final int kRearRollerWheelCANId = -1; //TODO

		//TODO Add dio for laser distance sensor from REV here
	
	//Controllers - Use -1 for unused controllers
	public static final GamepadControlBoardType kGamepadType = GamepadControlBoardType.XBOX;
	public static final JoystickControlBoardType kJoystickType = JoystickControlBoardType.PRO_3D;
	public static final int kDriveGamepadPort = 0;
	public static final int kDriveJoystickPort = -1;
	public static final int kAssistantGamepadPort = 1;
	public static final int kAssistantJoystickPort = -1;
	
	public static final boolean kUseGamepadForDriving = true;
	public static final boolean kUseGamepadForButtons = true;
	
	public static final HashMap<String, Integer> buttonNameToId = new HashMap<String, Integer>() {
		private static final long serialVersionUID = 1L;

		{
			//XBox
			put("A", 1);
			put("B", 2);
			put("X", 3);
			put("Y", 4);
			put("Back", 7);
			put("Start", 8);

			//Playstation
			put("Cross", 1);
			put("Circle", 2);
			put("Square", 3);
			put("Triangle", 4);
			put("Share", 7);
			put("Options", 8);

			//Both
			put("Left Shoulder", 5);
			put("Right Shoulder", 6);
		}
	};
	
	public static final HashMap<String, Integer> axisNameToId = new HashMap<String, Integer>() {
		private static final long serialVersionUID = 1L;

		{
			//Both
			put("Left X", 0);			//-1 to 1
			put("Left Y", 1);			//-1 to 1
			put("Left Trigger", 2);		//0 to 1
			put("Right Trigger", 3);	//0 to 1
			put("Right X", 4);			//-1 to 1
			put("Right Y", 5);			//-1 to 1
		
		}
	};

	
	
	
	
	
}
