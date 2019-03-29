package com.team3335.butterfly.controlboard;

import com.team3335.butterfly.Constants;
import com.team3335.butterfly.ControlBoard.GamepadControlBoardType;

import java.util.HashMap;

import edu.wpi.first.wpilibj.Joystick;


//Image of button mappings for an XBox controller: http://team358.org/files/programming/ControlSystem2009-/Logitech-F310_ControlMapping.png
public class GamepadDriveControlBoard implements IDriveControlBoard{
	private static GamepadDriveControlBoard mInstance;

    public static GamepadDriveControlBoard getInstance() {
        if (mInstance == null) {
            mInstance = new GamepadDriveControlBoard(Constants.kGamepadType);
        }
        return mInstance;
    }

    private Joystick mJoystick;
    
    private GamepadDriveControlBoard(GamepadControlBoardType type) {
        mJoystick = new Joystick(Constants.kDriveGamepadPort);
    }
	
    @Override
	public double getDriveForward() {
		return mJoystick.getRawAxis(Constants.axisNameToId.get("Right Y"));
	}

    @Override
	public double getDriveForward2() {
		return mJoystick.getRawAxis(Constants.axisNameToId.get("Left Y"));
	}

    @Override
	public double getDriveSideway() {
		return mJoystick.getRawAxis(Constants.axisNameToId.get("Right X"));
	}

    @Override
	public double getDriveRotation() {
		return mJoystick.getRawAxis(Constants.axisNameToId.get("Left X"));
	}
	
	@Override
	public boolean getUseAssist() {
		return mJoystick.getRawButton(Constants.buttonNameToId.get("Start"));
	}

	@Override
	public boolean getHatchPusher(){
		return mJoystick.getRawButton(Constants.buttonNameToId.get("Y"));
	}

	@Override
	public boolean getHatchGrabber() {
		return mJoystick.getRawButton(Constants.buttonNameToId.get("B"));
	}

	@Override
	public boolean getToggleDriveType() {
		return mJoystick.getRawButton(Constants.buttonNameToId.get("Back"));
	}

	@Override
	public boolean getDriveButton1() {
		return mJoystick.getRawButton(Constants.buttonNameToId.get("Left Shoulder"));
	}

	@Override
	public boolean getDriveButton2() {
		return mJoystick.getRawButton(Constants.buttonNameToId.get("Right Shoulder"));
	}

	@Override
	public boolean getHabPickup() {
		return false;//mJoystick.getRawButton(Constants.buttonNameToId.get("B"));
	}

	@Override
	public double getElevator() {
		return mJoystick.getRawAxis(Constants.axisNameToId.get("Right Trigger")) - mJoystick.getRawAxis(Constants.axisNameToId.get("Left Trigger"));
	}

	@Override
	public boolean getSwitchElevatorMode() {
		return false; //mJoystick.getRawButton(Constants.buttonNameToId.get("Back"));
	}

	@Override
	public boolean getHabPickupHeight() {
		return mJoystick.getPOV(0)==6*45;
	}

	@Override
	public boolean getHatchLowHeight() {
		return mJoystick.getPOV(0)==4*45;
	}

	@Override
	public boolean getHatchMiddleHeight() {
		return mJoystick.getPOV(0)==0*45;
	}

	
}
