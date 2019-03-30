package com.team3335.butterfly;

import java.util.HashMap;

import com.revrobotics.Rev2mDistanceSensor.Port;
import com.team3335.butterfly.ControlBoard.*;
import com.team3335.butterfly.states.DrivetrainState.DriveModeState;


//NOTE: ALL GEAR RATIOS ALREADY ACCOUNT FOR ANGULAR DIRECTION CHANGES WITH THE SIGN!!!!
public class Constants {

    public static final double kLooperDt = 0.01;	//Honestly no clue what this is, used by team3335.butterfly.loops.Looper, I just copied from Cheesy Poofs. Potentially deals with loop timing?
	public static final int kLongCANTimeoutMs = 100;

	public static final int kRobot = 2;

	/* FIELD CONSTATNS */

	//Cargo
	public static final double kFloorToLowCargo = 27.5;
	public static final double kFloorToMiddleCargo = 55.5;
	public static final double kFloorToHighCargo = 83.5;
	
	public static final double kFloorToBottomShipCargo = 31.5;
	public static final double kFloorToTopShipCargo = 48;
	public static final double kFloorToShipCargo = (kFloorToTopShipCargo + kFloorToBottomShipCargo) / 2;

	public static final double kFloorToHabCargoCenter = 45;


    //Hatch
    public static final double kHatchTargetWidth = 14.5;
    public static final double kHatchTargetHeight = 5.75;
	public static final double kHatchTargetBottomToHatchCenter = 6.5;
	public static final double kFloorToLowHatchCenter = 19;
	public static final double kFloorToMiddleHatchCenter = 47;
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

	public static final double kDGearRatio1 = -(14/56);	//From motor output to encoder shaft
	public static final double kDGearRatio2 = -(40/50);	//From encoder shaft to mecanum
	public static final double kDChainRatio1 = (12/24);	//From mecanum to high track
	
	public static final double kMotorToMecanumRatio = kDGearRatio1*kDGearRatio2;
	public static final double kMecanumToSkidSteerRatio = kDChainRatio1;
	public static final double kMotorToSkidSteerRatio = kMotorToMecanumRatio*kMecanumToSkidSteerRatio;

	//Carriage
	public static final double kCarriageMotorToOutputRatio = .1;

	//Elevator
	public static final double kElevatorRelativeMaxHeight = (kRobot == 2 ? 28 : 43); //In inches, Must be >0, relative to talon tach
	public static final double kElevatorRelativeMinHeight = (kRobot == 2 ? 0 : 0); //In inches, Must be <=0, relative to talon tach
	public static final double kElevatorMinHeight = 19;
	public static final double kElevatorMaxHeight = kElevatorMinHeight + kElevatorRelativeMaxHeight - kElevatorMinHeight;

	public static final double kElevatorDrumDiameter = 1.2;
	public static final double kElevatorScalarFactor = 1.1342376464;

	public static final double kEGearRatio1 = 1/5;
	public static final double kEGearRatio2 = -0.3;

	public static final double kElevatorMotorToEncoderRatio = kEGearRatio1;
	public static final double kElevatorEncoderToOutput = kEGearRatio2 * kEGearRatio2; //Cannot init this way appearently?
	
	public static final double kElevatorReachOffset = 1.5;
	public static final double kElevatorCargoPassHeight = 1;

	public static final int kElevatorTicksPerInch = (int) Math.round(Constants.kSRXEncoderCPR / (2 * Constants.kElevatorScalarFactor * Constants.kElevatorDrumDiameter * Math.PI * Constants.kEGearRatio2 * Constants.kEGearRatio2));

	
	//Vision
	public static final double kCameraHeight = (kRobot == 2 ? 36 + 10/16 : 36);
	public static final double kCameraAngle = (kRobot == 2 ? -15 : -18);
	public static final double kCameraDistanceFromFront = (kRobot == 2 ? 17 + 6/8 : 12);

	
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
	public static final int kMasterWinchCANId = 21;
	public static final int kSlave1WinchCANId = 22;

	//Carriage
	public static final int kCarriageRollerWheelCANId = 23;

	public static final int kCarriageModule = 1;
	public static final int kHatchPusher = 2;
	public static final int kHatchGrabber = 3;
	public static final Port kCarriageCargoSensorPort = Port.kOnboard;
	
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
