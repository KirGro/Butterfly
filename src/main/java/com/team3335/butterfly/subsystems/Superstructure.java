package com.team3335.butterfly.subsystems;

import com.team3335.butterfly.loops.*;
import com.team3335.butterfly.statemachines.SuperstructureStateMachine;
import com.team3335.butterfly.states.SuperstructureCommand;
import com.team3335.butterfly.states.SuperstructureState;
import com.team3335.butterfly.states.SuperstructureState.Goal;

public class Superstructure extends Subsystem{
    private Goal mGoal = Goal.GAME_PEICE_OR_CLIMB;
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

    public synchronized void setOpenLoopElevatorPercent(double power) {
        mStateMachine.setOpenLoopElevatorPercent(power);
    }
    
    public synchronized void setOpenLoopRearPercent(double power) {
        mStateMachine.setOpenLoopRearPercent(power);
    }

    public synchronized void setScoringHeight(double inches) {
        mStateMachine.setScoringHeight(inches);
    }

    public synchronized double getScoringHeight() {
        return mStateMachine.getScoringHeight();
    }

    public synchronized void setScoringRearAngle(double angle) {
        mStateMachine.setScoringRearAngle(angle);
    }

    public synchronized double getScoringRearAngle() {
        return mStateMachine.getScoringRearAngle();
    }
    
    public synchronized void setScoringCarriageRollerPercent(double percent) {
        mStateMachine.setScoringRearRollerPercent(percent);
    }

    public synchronized double getScoringCarriageRollerPercent() {
        return mStateMachine.getScoringCarriageRollerPercent();
    }
    
    public synchronized void setScoringRearRollerPercent(double percent) {
        mStateMachine.setScoringRearRollerPercent(percent);
    }

    public synchronized double getScoringRearRollerPercent() {
        return mStateMachine.getScoringRearRollerPercent();
    }
    
    public synchronized void setScoringArmsDown(boolean armsDown) {
        mStateMachine.setScoringArmsDown(armsDown);
    }

    public synchronized boolean getScoringArmsDown() {
        return mStateMachine.getScoringArmsDown();
    }
    
    public synchronized void setScoringPushersOut(boolean pushersOut) {
        mStateMachine.setScoringPushersOut(pushersOut);
    }

    public synchronized boolean getScoringPushersOut() {
        return mStateMachine.getScoringPushersOut();
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

    public synchronized void setGoal(Goal goal) {
        mGoal = goal;
    }

    private synchronized void updateObservedState(SuperstructureState state) {
        state.goal = mGoal;
        state.height = mElevator.getInchesOffGround();

        state.carriageRollerPercent = mCarriage.getRollerPercent();
        state.armsDown = mCarriage.getArmsDown();
        state.pushersOut = mCarriage.getPushersOut();

        state.rearAngle = mRearIntake.getAngle();
        state.rearRollerPercent = mRearIntake.getRollerPercent();

        state.elevatorSentLastTrajectory = mElevator.hasFinishedTrajectory();
        state.rearIntakeSentLastTrajectory = mRearIntake.hasFinishedTrajectory();

        state.hasRearCargo = mRearIntake.hasCargo();
        state.hasCarriageCargo = mCarriage.hasCargo();
        state.hasHatch = mCarriage.hasHatch();
    }

    public synchronized void setFromCommandState(SuperstructureCommand commandState) {
        mElevator.setMotionMagicPosition(commandState.height);
        
        mCarriage.setRollerPower(commandState.carriageRollerPercent);
        mCarriage.setArms(commandState.armsDown);
        mCarriage.setPusher(commandState.pushersOut);

        mRearIntake.setMotionMagicPosition(commandState.angle);
        mRearIntake.setRollerPower(commandState.rearRollerPercent);
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

                    //mIntake.setKickStand(isKickStandEngaged());

                    //mCommand = mStateMachine.update(timestamp, mWantedAction, mState);
                    //setFromCommandState(mCommand);
                }
            }

            @Override
            public void onStop(double timestamp) {

            }
        });
    }

}