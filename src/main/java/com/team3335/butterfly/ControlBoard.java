package com.team3335.butterfly;

import com.team3335.butterfly.controlboard.*;

public class ControlBoard implements IControlBoard{
	 private static ControlBoard mInstance = null;

	    public static ControlBoard getInstance() {
	        if (mInstance == null) {
	            mInstance = new ControlBoard();
	        }
	        return mInstance;
	    }

	    private IDriveControlBoard mDriveControlBoard;
	    private IAssistantControlBoard mAssistantControlBoard;

	    private ControlBoard() {
	        if (Constants.kUseGamepadForDriving) {
	            mDriveControlBoard = GamepadDriveControlBoard.getInstance();
	        } else {
	            //mDriveControlBoard = MainDriveControlBoard.getInstance();
	        }

	        if (Constants.kUseGamepadForButtons) {
	            mAssistantControlBoard = GamepadAssistantControlBoard.getInstance();
	        } else {
	            //mAssisstantControlBoard = MainAssistantBoard.getInstance();
	        }
	    }

	@Override
	public double getDriveForward() {
		if(Preferences.kJoystickDeadbandEarlyMap)return mapDeadband(mDriveControlBoard.getDriveForward());
		else return mDriveControlBoard.getDriveForward();
	}
	

	@Override
	public double getDriveForward2() {
		if(Preferences.kJoystickDeadbandEarlyMap)return mapDeadband(mDriveControlBoard.getDriveForward2());
		else return mDriveControlBoard.getDriveForward2();
	}

	@Override
	public double getDriveSideway() {
		if(Preferences.kJoystickDeadbandEarlyMap)return mapDeadband(mDriveControlBoard.getDriveSideway());
		else return mDriveControlBoard.getDriveSideway();
	}

	@Override
	public double getDriveRotation() {
		if(Preferences.kJoystickDeadbandEarlyMap)return mapDeadband(mDriveControlBoard.getDriveRotation());
		else return mDriveControlBoard.getDriveRotation();
	}

	@Override
	public boolean getToggleDriveMode() {
		return mAssistantControlBoard.getToggleDriveMode();
	}

	@Override
	public boolean getToggleWheelState() {
		return mAssistantControlBoard.getToggleWheelState();
	}

	@Override
	public boolean getForceSkidSteer() {
		return mAssistantControlBoard.getForceSkidSteer();
	}

	@Override
	public boolean getForceMecanum() {
		return mAssistantControlBoard.getForceMecanum();
	}

	@Override
	public boolean getToggleBrake() {
		return mAssistantControlBoard.getToggleBrake();
	}
	
	@Override 
	public boolean getUseAssist() {
		return mDriveControlBoard.getUseAssist();
	}

	@Override 
	public boolean getHatchPusher() {
		return mDriveControlBoard.getHatchPusher();
	}

	@Override
	public boolean getHatchGrabber() {
		return mDriveControlBoard.getHatchGrabber();
	}

	@Override
	public boolean getHabPickup() {
		return mDriveControlBoard.getHabPickup();
	}

	@Override
	public boolean getToggleDriveType() {
		return mDriveControlBoard.getToggleDriveType();
	}

	@Override
	public boolean getDriveButton1() {
		return mDriveControlBoard.getDriveButton1();
	}

	@Override
	public boolean getDriveButton2() {
		return mDriveControlBoard.getDriveButton2();
	}

	@Override
	public double getElevator() {
		return mDriveControlBoard.getElevator();
	}
	
	private double mapDeadband(double value) {
		double deadband = (Constants.kUseGamepadForDriving ? Constants.kGamepadDeadband : Constants.kJoystickDeadband);
		double absvalue = Math.abs(value);
		if(absvalue <= deadband) {
			return 0;
		} else {
			return ((absvalue-deadband)*(absvalue/value)) / (1-deadband);
		}
	}
	
	public enum GamepadControlBoardType{
		XBOX,
		PLAYSTATION
	}

	public enum JoystickControlBoardType{
		ATTACK3,
		PRO_3D
	}

	@Override
	public boolean getHabPickupHeight() {
		return mDriveControlBoard.getHabPickupHeight();
	}

	@Override
	public boolean getHatchLowHeight() {
		return mDriveControlBoard.getHatchLowHeight();
	}

	@Override
	public boolean getHatchMiddleHeight() {
		return mDriveControlBoard.getHatchMiddleHeight();
	}

	@Override
	public boolean getSwitchElevatorMode() {
		return mDriveControlBoard.getSwitchElevatorMode();
	}

	@Override
	public boolean getElevatorStateToggle() {
		return mDriveControlBoard.getElevatorStateToggle();
	}
}
