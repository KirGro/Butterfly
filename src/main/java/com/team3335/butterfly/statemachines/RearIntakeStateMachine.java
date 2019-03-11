package com.team3335.butterfly.statemachines;

import com.team3335.butterfly.states.RearIntakeState;
import com.team3335.butterfly.states.RearIntakeState.*;
import com.team3335.butterfly.subsystems.Carriage;
import com.team3335.butterfly.subsystems.RearIntake;

public class RearIntakeStateMachine {

    public enum WantedAction {
        MANUAL,
        AUTO_CARGO,
        DISABLE
    }
    private RearIntake mRearIntake = RearIntake.getInstance();
    private Carriage mCarriage = Carriage.getInstance();
    
    private RearIntakeState mCommandedState = new RearIntakeState();
    private double mCurrentStateStartTime = 0;

    public RearIntakeState update(double timestamp, WantedAction wantedAction, RearIntakeState currentState) {
        synchronized (RearIntakeStateMachine.this) {

            switch(wantedAction) {
                case MANUAL: 
                    break;
                case AUTO_CARGO:
                    getAutoCargo(currentState, mCommandedState, timestamp);
                    break;
                default: 
                    getStoreAndDisable();
                    break;
            }
            return mCommandedState;
            
        }
    }

    private synchronized void getAutoCargo(RearIntakeState currentState, RearIntakeState newState, double timestamp) {
        //Handle intake automatically if there is alrady cargo or hatches onboard
        if(!mCarriage.getCurrentCarriageState().hasCargo()) {
            if(!mRearIntake.getCurrentRearIntakeState().hasCargo()) {
                newState.mArmAction = ArmAction.GRABBING_CARGO;
            } else {

            }
        } else {

        }

    }

    private synchronized void getStoreAndDisable() {
        mCommandedState = new RearIntakeState();
    }
}