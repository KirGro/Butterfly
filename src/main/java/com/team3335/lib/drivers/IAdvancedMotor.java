package com.team3335.lib.drivers;

import com.ctre.phoenix.motorcontrol.can.TalonSRXPIDSetConfiguration;

public interface IAdvancedMotor extends IBasicMotor {
	
	public void setFeedbackType(FeedbackType type);
	public int getEncoderPosition();
	public int getEncoderRotation();
	public int getEncoderVelocity();
	public void zeroEncoder();
	public void configForPathFollowing(TalonSRXPIDSetConfiguration configPID);
	public void configForInput(TalonSRXPIDSetConfiguration configPID);
	public void setControlPointFromCurrent(double rotations);
	public void setEncoderToOutputRatio(double ratio);

	
	public enum EncoderType {
		SRX,
		OUTSIDE,
		NONE
	}
	
	public enum FeedbackType{
		RELATIVE,
		ABSOLUTE
	}
}

/*
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
//import edu.

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class AdvancedMotor {
	
	private int portNumberPDP;
	private int talonCANNumber;
	private TalonSRX talon;
	
	private int gearRatioToEncoder;
	
	//default constructor
	public AdvancedMotor(int talonCANNumber) {
		this.talonCANNumber=talonCANNumber;
		talon = new TalonSRX(talonCANNumber);
		setDefaults();
	}
	
	public void rotateByDegrees() {
		
	}
	
	private void setDefaults() {
		gearRatioToEncoder = 1;
		//portNumberPDP = talonCANNumber+256;
		portNumberPDP = -1;
	}
	
	public TalonSRX getTalon() {
		return talon;
	}
	
	public int getTalonCANNumber() {
		return talonCANNumber;
	}
	
	public int getPortNumberPDP() {
		return portNumberPDP;
	}
	
	
	
}
*/