package com.team3335.butterfly.statemachines;

import com.team3335.butterfly.states.RearIntakeState;

public class RearIntakeStateMachine {
    
    public enum WantedAction {
        OPEN_LOOP,
        PLANNED;
    }

    
    private RearIntakeState mCommandedState = new RearIntakeState();
    private double mCurrentStateStartTime = 0;

    public RearIntakeState update(double time, WantedAction wantedAction, RearIntakeState oldState) {
        synchronized (RearIntakeStateMachine.this) {
            return new RearIntakeState();
        }
    }
}