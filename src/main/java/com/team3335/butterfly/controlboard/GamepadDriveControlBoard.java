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
		return mJoystick.getRawButton(4); //TODO Random, possibly obselete
	}

	@Override
	public boolean getHatchPusher(){
		return mJoystick.getRawButton(Constants.buttonNameToId.get("A"));
	}

	@Override
	public boolean getToggleDriveType() {
		return mJoystick.getRawButton(Constants.buttonNameToId.get("Y"));
	}

	@Override
	public boolean getDriveButton1() {
		return mJoystick.getRawButton(Constants.buttonNameToId.get("Left Shoulder"));
	}

	@Override
	public boolean getDriveButton2() {
		return mJoystick.getRawButton(Constants.buttonNameToId.get("Right Shoulder"));
	}
}
