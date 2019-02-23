package com.team3335.butterfly;

import com.team3335.butterfly.states.DrivetrainState.*;
import com.team3335.butterfly.subsystems.Limelight.Target;

public class Preferences {
	//Drivetrain
	public static DrivetrainWheelState pDefaultDrivetrainWheelState = DrivetrainWheelState.MECANUM;
	public static DriveModeState pMecanumDefaultMode = DriveModeState.MECANUM_ROBOT_RELATIVE;
	public static DriveModeState pSkidSteerDefaultMode = DriveModeState.TANK;
	public static boolean pUseDrivetrainWearPrevention = false; //TODO Feature not currently available - Attempts to decrease wear on drivetrain during extreme using by dynamically adjusting speed and drivetrain state
	public static DriveType pDefaultDriveType = DriveType.CUSTOM;

	//Controller
	public static boolean kJoystickDeadbandEarlyMap = false;
	public static boolean pUseLogisticalGrowthMapping = false;

	//Vision
	public static double pMecanumSidewaysScalar = 1.5;
	public static Target pDefaultTarget = Target.HATCH;

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
