package main.java.com.team3335.butterfly.controlboard;

public interface IDriveControlBoard {
	/* Things the driver controls */
	
	
	public double getDriveForward();

	public double getDriveForward2();
	
	public double getDriveSideway();
	
	public double getDriveRotation();
	
	public boolean getToggleDriveMode();
	
	public boolean getToggleWheelState();
	
	public boolean getForceSkidSteer();
	
	public boolean getForceMecanum();
	
	public boolean getToggleBrake();
	
	public boolean getUseAssist();
}
