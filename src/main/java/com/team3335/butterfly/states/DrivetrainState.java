package com.team3335.butterfly.states;


public class DrivetrainState {
	
	public double drivetrainAngle = 0;
	public double drivetrainX = 0; 
	public double drivetrainY = 0;
	
	public DrivetrainWheelState drivetrainWheelState = DrivetrainWheelState.MECANUM;
	public DriveModeState driveModeState = DriveModeState.MECANUM_ROBOT_RELATIVE;
	
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
	
    
    public enum DriveType {
        CUSTOM,
        AUTO_SWITCHING,
        FULL_VISION;
        
        protected static DriveType[] types = DriveType.values();
		public DriveType next() {
	        return types[(this.ordinal()+1) % types.length];
		}
		
		public DriveType prev() {
			int p = this.ordinal()-1;
			return p>=0 ? types[p] : types[types.length-1];
		}
    }
}
