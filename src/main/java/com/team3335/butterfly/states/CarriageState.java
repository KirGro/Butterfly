package com.team3335.butterfly.states;

import com.team3335.butterfly.states.RearIntakeState.RollerAction;

public class CarriageState {
    //Planner controls
    public boolean mPushersOut = false;
    public boolean mPickupDown = false;
    public RollerAction mRollerAction = RollerAction.NONE;

    //Only used for manual control
    public double mRollerSpeed = 0.0;

    //Planner doesn't touch
    public boolean mLaserBroken = false;
    public boolean mHatchTriggered = false;

    
    public boolean hasCargo() {
        return mLaserBroken;
    }

    public boolean hasHatch() {
        return mHatchTriggered;
    }
    

    /*
    public CarriageState(boolean pushersOut, boolean pickupDown, RollerWheelState rollerState, RollerAction rollerAction) {
        mPushersOut = pushersOut;
        mPickupDown = pickupDown;
        mRollerState = rollerState;
        mRollerAction = rollerAction;
    }
    */

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