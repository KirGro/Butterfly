package com.team3335.butterfly.states;

public class CarriageState {
    public boolean mPushersOut = false;
    public boolean mPickupDown = false;
    public RollerWheelState mRollerState = RollerWheelState.OFF;
    public RollerAction mRollerAction = RollerAction.NONE;
    public double mRollerSpeed = 0.0;;

    //Planner doesn't touch
    public boolean mHasCargo = false;
    

    /*
    public CarriageState(boolean pushersOut, boolean pickupDown, RollerWheelState rollerState, RollerAction rollerAction) {
        mPushersOut = pushersOut;
        mPickupDown = pickupDown;
        mRollerState = rollerState;
        mRollerAction = rollerAction;
    }
    */

    public enum RollerWheelState {
        OFF,
        INTAKE_FORWARD,
        INTAKE_REVERSE,
        FULL_FORWARD,
        FULL_REVERSE;

		private static RollerWheelState[] vals = values();
    }

    public enum RollerAction {
        NONE,
        CUSTOM,
        WAITING_FOR_CARGO,
        HOLDING_CARGO,
        SHOOTING;

		private static RollerAction[] vals = values();
    }

    public enum ArmAction {
        NONE,
        CUSTOM,
        HAB_PICKUP,
        FLOOR_PICKUP,
        CARGOSHIP_PLACING,
        ROCKET_PLACING;

		private static ArmAction[] vals = values();
    }

}