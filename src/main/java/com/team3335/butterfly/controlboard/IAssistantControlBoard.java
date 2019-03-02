package com.team3335.butterfly.controlboard;

public interface IAssistantControlBoard {
	/* Just sit there and look pretty, will ya? */
	
	public default UnknownTruth getAmIAlive() {return null;}
	
	public boolean getToggleDriveMode();
	
	public boolean getToggleWheelState();
	
	public boolean getForceSkidSteer();
	
	public boolean getForceMecanum();
	
	public boolean getToggleBrake();

	//public double getArm();
	

	
	
	public enum UnknownTruth {
		//YES,
		NO,
		MAYBE,
		WHO_EVEN_KNOWS_AT_THIS_POINT,
		IS_THIS_THE_REAL_LIFE_IS_THIS_JUST_FANTASY,
		UNDEFINED,
		NO_SOLUTION
	}
}
