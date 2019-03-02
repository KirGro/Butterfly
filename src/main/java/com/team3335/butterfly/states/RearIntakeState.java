package com.team3335.butterfly.states;
import com.team3335.butterfly.statemachines.RearIntakeStateMachine.*;

public class RearIntakeState {
    public WantedAction mSystemState = WantedAction.PLANNED;
    public RollerWheelState mRollerState = RollerWheelState.OFF;
    public ArmState mArmState = ArmState.STORED;

    //Not touched by planner
    public double mRollerPercent = 0.0;
    public double mArmPercent = 0.0;
    public double mArmEncoderPosition = 0;
    public double mArmEncoderSetpoint = 0;
    public boolean mLaserBroken = false;


    public enum RollerWheelState {
        OFF,
        INTAKE_FORWARD,
        INTAKE_REVERSE,
        FULL_FORWARD,
        FULL_REVERSE,
        CUSTOM;

		private static RollerWheelState[] vals = values();
    }

    public enum ArmState {
        STORED,
        GRABBING_CARGO,
        CLIMBING,
        CUSTOM;

		private static ArmState[] vals = values();
    }

}