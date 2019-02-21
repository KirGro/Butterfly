package com.team3335.butterfly.states;

public class CarriageState {
    public boolean mPushersOut = false;
    public boolean mPickupDown = false;
    public RollerWheelState mRollerState = RollerWheelState.OFF;
    

    public enum RollerWheelState {
        OFF,
        INTAKE_FORWARD,
        INTAKE_REVERSE,
        FULL_FORWARD,
        FULL_REVERSE;

		private static RollerWheelState[] vals = values();
    }

}