package main.java.com.team3335.lib.util;

import main.java.com.team3335.butterfly.states.DrivetrainState.DriveModeState;

public class DriveIntent {
	private double mFrontRightWheel, mFrontLeftWheel, mBackRightWheel, mBackLeftWheel;
	
	private boolean mBrakeMode;
	private DriveModeState mDMS;
	
	public static DriveIntent MECANUM_NEUTRAL = new DriveIntent(0, 0, 0, 0, DriveModeState.MECANUM_ROBOT_RELATIVE, false);
	public static DriveIntent MECANUM_BRAKE = new DriveIntent(0, 0, 0, 0, DriveModeState.MECANUM_ROBOT_RELATIVE, true);

	public static DriveIntent SKID_STEER_NEUTRAL = new DriveIntent(0, 0, 0, 0, DriveModeState.TANK, false);
	public static DriveIntent SKID_STEER_BRAKE = new DriveIntent(0, 0, 0, 0, DriveModeState.TANK, true);
	
	
	
	public DriveIntent(double frontRightWheel, double frontLeftWheel, double backRightWheel, double backLeftWheel, DriveModeState ds, boolean brakeMode) {
		mFrontRightWheel = frontRightWheel;
		mFrontLeftWheel = frontLeftWheel;
		mBackRightWheel = backRightWheel;
		mBackLeftWheel = backLeftWheel;
		mDMS = ds;
		mBrakeMode = brakeMode;
	}
	
	public void setDriveModeState(DriveModeState mode) {
		mDMS = mode;
	}
	
	public double getFrontRight() {
		return mFrontRightWheel;
	}
	
	public double getFrontLeft() {
		return mFrontLeftWheel;
	}
	
	public double getBackRight() {
		return mBackRightWheel;
	}
	
	public double getBackLeft() {
		return mBackLeftWheel;
	}
	
	public boolean getBrakeMode() {
        return mBrakeMode;
    }
	
	public DriveModeState getDriveModeState() {
		return mDMS;
	}
	
	@Override
    public String toString() {
        return "FR: "+mFrontRightWheel+", FL: "+mFrontLeftWheel+", BR: "+mBackRightWheel+", BL: "+mBackLeftWheel+", Brake Enabled: "+mBrakeMode+", DriveState: "+mDMS;
    }
	
}
