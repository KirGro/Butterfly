package com.team3335.butterfly.states;


public class DrivetrainState {
	
	public double drivetrainAngle = 0;
	public double drivetrainX = 0; 
	public double drivetrainY = 0;
	
	public DrivetrainWheelState drivetrainWheelState = DrivetrainWheelState.MECANUM;
	public DriveModeState driveModeState = DriveModeState.MECANUM_ROBOT_RELATIVE;
	
	public double frontRightMotor = 0, 
				  frontLeftMotor = 0, 
				  backRightMotor = 0, 
				  backLeftMotor = 0;
	
	public enum DrivetrainWheelState {
        MECANUM,
        SKID_STEER;
		
		private static DrivetrainWheelState[] vals = values();
	    public DrivetrainWheelState next(){
	        return vals[(this.ordinal()+1) % vals.length];
		}
		public DrivetrainWheelState prev() {
			int p = this.ordinal()-1;
			return p>=0 ? vals[p] : vals[vals.length-1];
		}
    }
	
	public enum DriveModeState{
		MECANUM_ROBOT_RELATIVE,
		MECANUM_FIELD_RELATIVE,
		TANK,
		ARCADE;
		
		private static DriveModeState[] vals = values();
	    public DriveModeState next() {
	        return vals[(this.ordinal()+1) % vals.length];
		}
		
		public DriveModeState prev() {
			int p = this.ordinal()-1;
			return p>=0 ? vals[p] : vals[vals.length-1];
		}
	}
}
