package com.team3335.butterfly.subsystems;

import com.team3335.butterfly.loops.*;
import com.team3335.butterfly.statemachines.SuperstructureStateMachine;
import com.team3335.butterfly.states.SuperstructureCommand;
import com.team3335.butterfly.states.SuperstructureState;

public class Superstructure extends Subsystem{
    static Superstructure mInstance = null;
    private SuperstructureState mState = new SuperstructureState();
    private Elevator mElevator = Elevator.getInstance();
    private Carriage mCarriage = Carriage.getInstance();
    private RearIntake mRearIntake = RearIntake.getInstance();
    private SuperstructureStateMachine mStateMachine = new SuperstructureStateMachine();
    private SuperstructureStateMachine.WantedAction mWantedAction =
            SuperstructureStateMachine.WantedAction.IDLE;
    
    
    public synchronized static Superstructure getInstance() {
        if (mInstance == null) {
            mInstance = new Superstructure();
        }
        return mInstance;
    }

    @Override
    public boolean checkSystem() {
        return false;
    }

    @Override
    public void outputTelemetry() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void zeroSensors() {

    }

    public synchronized SuperstructureStateMachine.SystemState getSuperStructureState() {
        return mStateMachine.getSystemState();
    }

    public synchronized SuperstructureState getObservedState() {
        return mState;
    }

    private synchronized void updateObservedState(SuperstructureState state) {
        state.height = mElevator.getInchesOffGround();
        state.angle = mWrist.getAngle();
        state.jawClamped = mIntake.getJawState() == IntakeState.JawState.CLAMPED;

        state.elevatorSentLastTrajectory = mElevator.hasFinishedTrajectory();
        state.wristSentLastTrajectory = mWrist.hasFinishedTrajectory();
    }

    synchronized void setFromCommandState(SuperstructureCommand commandState) {
    }

    @Override
    public void registerEnabledLoops(ILooper enabledLooper) {
        enabledLooper.register(new Loop() {
            private SuperstructureCommand mCommand;

            @Override
            public void onStart(double timestamp) {
                mStateMachine.resetManual();
            }

            @Override
            public void onLoop(double timestamp) {
                synchronized (Superstructure.this) {
                    updateObservedState(mState);

                    if (!isKickStandEngaged()) {
                        // Kickstand is fired, so not engaged.
                        mStateMachine.setMaxHeight(SuperstructureConstants.kElevatorMaxHeight);
                    } else {
                        mStateMachine.setMaxHeight(SuperstructureConstants.kElevatorMaxHeightKickEngaged);
                    }

                    mIntake.setKickStand(isKickStandEngaged());

                    mCommand = mStateMachine.update(timestamp, mWantedAction, mState);
                    setFromCommandState(mCommand);
                }
            }

            @Override
            public void onStop(double timestamp) {

            }
        });
    }

}