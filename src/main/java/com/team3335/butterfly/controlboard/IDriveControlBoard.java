package com.team3335.butterfly.controlboard;

public interface IDriveControlBoard {
	/* Things the driver controls */
	
	
	public double getDriveForward();
	public double getDriveForward2();
	public double getDriveSideway();
	public double getDriveRotation();

	public boolean getToggleDriveType();
	public boolean getDriveButton1();
	public boolean getDriveButton2();
	
	public boolean getUseAssist();	//TODO IDK, obselete at this point?

	public boolean getHatchPusher();
	public boolean getHatchGrabber();

	public boolean getElevatorStateToggle();
	public double getElevator();
	public boolean getHatchLowHeight();
	public boolean getHatchMiddleHeight();
	public boolean getHatchHighHeight();
}
