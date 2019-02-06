package com.team3335.butterfly.controlboard;

import com.team3335.butterfly.Constants;
import com.team3335.butterfly.ControlBoard.GamepadControlBoardType;

import java.util.HashMap;

import edu.wpi.first.wpilibj.Joystick;


//Image of button mappings for an XBox controller: http://team358.org/files/programming/ControlSystem2009-/Logitech-F310_ControlMapping.png
public class GamepadDriveControlBoard implements IDriveControlBoard{
	private static GamepadDriveControlBoard mInstance = null;
	@SuppressWarnings("serial")
	private static final HashMap<String, Integer> xboxButtonNameToId = new HashMap<String, Integer>() {{
		//Mapping for XBox buttons
		put("A", 1);
		put("B", 2);
		put("X", 3);
		put("Y", 4);
		put("Left Shoulder", 5);
		put("Right Shoulder", 6);
		put("Back", 7);
		put("Start", 8);
	}};
	
	private static final HashMap<String, Integer> xboxAxisNameToId = new HashMap<String, Integer>() {{
		//Mapping for XBox axis
		put("Left X", 0);			//-1 to 1
		put("Left Y", 1);			//-1 to 1
		put("Left Trigger", 2);		//0 to 1
		put("Right Trigger", 3);	//0 to 1
		put("Right X", 4);			//-1 to 1
		put("Right Y", 5);			//-1 to 1
		
	}};
	

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
		return mJoystick.getRawAxis(5);
	}

    @Override
	public double getDriveForward2() {
		return mJoystick.getRawAxis(1);
	}

    @Override
	public double getDriveSideway() {
		return mJoystick.getRawAxis(4);
	}

    @Override
	public double getDriveRotation() {
		return mJoystick.getRawAxis(0);
	}

    @Override
	public boolean getToggleDriveMode() {
		return mJoystick.getRawButton(5);
	}

    @Override
	public boolean getForceSkidSteer() {
		return mJoystick.getRawButton(1); //TODO rando button
	}

    @Override
	public boolean getForceMecanum() {
		return mJoystick.getRawButton(2); //TODO rando button
	}

	@Override
	public boolean getToggleWheelState() {
		return mJoystick.getRawButton(6);
	}

	@Override
	public boolean getToggleBrake() {
		return mJoystick.getRawButton(2);
	}
	
	@Override
	public boolean getUseAssist() {
		return mJoystick.getRawButton(4);
	}

	@Override
	public boolean getHatchPusher(){
		return mJoystick.getRawButton(1);
	}
}
