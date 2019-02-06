package com.team3335.lib.drivers;

public interface IBasicMotor {	
	
	public abstract void driveRaw(double speed);
	public abstract void driveRaw(double speed, boolean reverseInput);
	public abstract boolean getBrakeMode();
	public abstract void setBrakeMode(boolean on);
	public abstract void invertMotor(boolean inverted);
}
