package com.team3335.butterfly;

import com.team3335.butterfly.ControlBoard.*;
import com.team3335.butterfly.states.DrivetrainState.DriveModeState;

public class Constants {

    public static final double kLooperDt = 0.01;	//Honestly no clue what this is, used by team3335.butterfly.loops.Looper, I just copied from Cheesy Poofs. Potentially deals with loop timing?
	public static final int kLongCANTimeoutMs = 100;
	
	/* FIELD CONSTATNS */

	//Cargo
    
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
	
	//Wheel power ratios
	public static final double kGearRatio1 = -(14/56);	//From motor output to encoder shaft
	public static final double kGearRatio2 = -(40/50);	//From encoder shaft to mecanum
	public static final double kChainRatio1 = (12/24);	//From mecanum to high track
	
	public static final double kMotorToMecanumRatio = kGearRatio1*kGearRatio2;
	public static final double kMecanumToSkidSteerRatio = kChainRatio1;
	public static final double kMotorToSkidSteerRatio = kMotorToMecanumRatio*kMecanumToSkidSteerRatio;
	
	//Vision
	public static final double kCameraHeight = 36; //TODO Correct
	public static final double kCameraAngle = -18; //TODO Correct
	public static final double kCameraDistanceFromFront = 6; 
	
	/* SOFTWARE CONSTANTS */
	
	//Drive Controls
	public static final double kJoystickDeadband = .05;
	public static final double kGamepadDeadband = .1;
	public static final double kSkidSteerMaxPower = 1; 
	public static final double kMecanumMaxPower = .5; 
	public static final boolean kUseSinMapping = false; //TODO Maybe add this later...
	
	//Preferences
	//public static final boolean kUseDriveModeCorrection = false; //TODO Maybe add this later... 
	public static final boolean kFixIncompatableDriveMode = true;
	public static final DriveModeState kDefaultMecanumDriveState = DriveModeState.MECANUM_ROBOT_RELATIVE;
	public static final DriveModeState kDefaultSkidSteerDriveState = DriveModeState.TANK;
	
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
	
	public static final boolean kUsingSpringSolenoids = true;
	public static final int kShifterModule = 1;
	public static final int kShifterRightMecId = 2;
	public static final int kShifterRightSkidId = 0;
	public static final int kShifterLeftMecId = 3;
	public static final int kShifterLeftSkidId = 1;
	public static final int kCoolantId = 7;

	public static final int kCarriageModule = 1;
	public static final int kHatchPusher = 5;
	
	//Controllers - Use -1 for unused controllers
	public static final GamepadControlBoardType kGamepadType = GamepadControlBoardType.XBOX;
	public static final JoystickControlBoardType kJoystickType = JoystickControlBoardType.PRO_3D;
	public static final int kDriveGamepadPort = 0;
	public static final int kDriveJoystickPort = -1;
	public static final int kAssisstantGamepadPort = -1;
	public static final int kAssisstantJoystickPort = -1;
	
	public static final boolean kUseGamepadForDriving = true;
	public static final boolean kUseGamepadForButtons = true;
	
	
	
	
	
	
}
