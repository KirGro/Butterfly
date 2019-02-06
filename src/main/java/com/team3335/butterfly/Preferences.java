package com.team3335.butterfly;

import com.team3335.butterfly.states.DrivetrainState.*;

public class Preferences {
	
	/* DRIVETRAIN */
	public static final DriveModeState pMecanumDefaultMode = DriveModeState.MECANUM_ROBOT_RELATIVE;
	public static final DriveModeState pSkidSteerDefaultMode = DriveModeState.TANK;
	public static final boolean pUseDrivetrainWearPrevention = false; //TODO Feature not currently available - Attempts to decrease wear on drivetrain during extreme using by dynamically adjusting speed and drivetrain state
	
	
}
