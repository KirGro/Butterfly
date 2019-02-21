package com.team3335.butterfly.controlboard;

import com.team3335.butterfly.Constants;

import edu.wpi.first.wpilibj.Joystick;

public class GamepadAssistantControlBoard implements IAssistantControlBoard {
	private static GamepadAssistantControlBoard mInstance;

    public static GamepadAssistantControlBoard getInstance() {
        if (mInstance == null) {
            mInstance = new GamepadAssistantControlBoard();
        }
        return mInstance;
    }

    private Joystick mJoystick;
    
    private GamepadAssistantControlBoard() {
        mJoystick = new Joystick(Constants.kAssistantGamepadPort);
    }
    

    @Override
	public boolean getToggleDriveMode() {
		return false;
	}

    @Override
	public boolean getForceSkidSteer() {
		return false; 
	}

    @Override
	public boolean getForceMecanum() {
		return false; 
	}

	@Override
	public boolean getToggleWheelState() {
		return false;
	}

	@Override
	public boolean getToggleBrake() {
		return false;
	}
}