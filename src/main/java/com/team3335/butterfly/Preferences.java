package com.team3335.butterfly;

import com.team3335.butterfly.planners.SuperstructureMotionPlanner.Targeting;
import com.team3335.butterfly.states.DrivetrainState.*;
import com.team3335.butterfly.subsystems.Limelight.Target;

public class Preferences {
	//Planning
	public static Targeting pDefaultTargeting = Targeting.HATCH;
	public static boolean pDefaultPlacingLow = true;
	
	//Drivetrain
	public static DrivetrainWheelState pDefaultDrivetrainWheelState = DrivetrainWheelState.MECANUM;
	public static DriveModeState pMecanumDefaultMode = DriveModeState.MECANUM_ROBOT_RELATIVE;
	public static DriveModeState pSkidSteerDefaultMode = DriveModeState.TANK;
	public static boolean pUseDrivetrainWearPrevention = false; //TODO Feature not currently available - Attempts to decrease wear on drivetrain during extreme using by dynamically adjusting speed and drivetrain state
	public static DriveType pDefaultDriveType = DriveType.CUSTOM;

	public static double kDriveP = .6;
	public static double kDriveI = .00001;
	public static double kDriveD = 50;


	//Carriage
	public static double pControlPower = .1;

	public static double kPusherTime = 1.5;
	public static boolean kUsePusherCooldown = true;
	public static double kCooldownTime = 1.;

	public static double kHabPickupDelay = 1;

	//Rear Intake
	public static double kCargoPickupAngle = 70; //TODO test cargo pickup angle
	public static double kClimbingAngle = 80; //TODO figure out climbing angle

	// Weird but rollers
	public static double kRollerIntakePercent = .3;
	

	//Controller
	public static boolean kJoystickDeadbandEarlyMap = false;
	public static boolean pUseLogisticalGrowthMapping = false;

	//Vision
	public static double pMecanumSidewaysScalar = 1.5;
	public static Target pDefaultTarget = Target.HATCH_TARGET;

}


/*	OLD STUFF

	private static HashMap<String, Object> preferences = new HashMap<String, Object>();
	public static void addEnum(String key, Enum value) {
		preferences.put("e"+key, value.ordinal());
	}

	public static void addInt(String key, int i) {
		preferences.put("i"+key, i);
	}

	public static void putDouble(String key, double d) {
		preferences.put("d"+key, d);
	}

	public static void addBoolean(String key, boolean b) {
		preferences.put("b"+key, b);
	}

	public static int getEnum(String key) {
		return (int)preferences.get("e"+key);
	}

	public static int getInt(String key) {
		return (int)preferences.get("i"+key);
	}

	public static double getDouble(String key) {
		return (double)preferences.get("d"+key);
	}

	public static boolean getBoolean(String key) {
		return (boolean) preferences.get("b"+key);
	}

}
*/
