package com.team3335.butterfly.statemachines;

import com.team3335.butterfly.Constants;
import com.team3335.butterfly.planners.SuperstructureMotionPlanner;
import com.team3335.butterfly.states.SuperstructureCommand;
import com.team3335.butterfly.states.SuperstructureState;

public class SuperstructureStateMachine {
    public enum WantedAction {
        IDLE,
        GET_GAME_PEICE,
        PLACE_GAME_PEICE,
        WANT_MANUAL;
    }

    public enum SystemState {
        HOLDING_PLACE,
        WAITING_FOR_GAME_PEICE,
        PLACING_GAME_PEICE,
        MANUAL;
    }

    private SystemState mSystemState = SystemState.HOLDING_PLACE;

    private SuperstructureCommand mCommand = new SuperstructureCommand();
    private SuperstructureState mCommandedState = new SuperstructureState();
    private SuperstructureState mDesiredEndState = new SuperstructureState();

    private SuperstructureMotionPlanner mPlanner = new SuperstructureMotionPlanner();

    private double mScoringHeight = Constants.kElevatorMinHeight;
    private double mScoringCarriageRollerPercent = 0;
    private boolean mScoringArmsDown = false;
    private boolean mScoringPushersOut = false;
    private double mScoringRearAngle = Constants.kRearMinAngle;
    private double mScoringRearRollerPercent = 0;

    private double mOpenLoopElevatorPercent = 0.0;
    private double mOpenLoopRearPercent = 0.0;



    public synchronized void resetManual() {
        mOpenLoopElevatorPercent = 0.0;
        mOpenLoopRearPercent = 0.0;
        mScoringCarriageRollerPercent = 0.0;
        mScoringRearRollerPercent = 0.0;
    }

    public synchronized void setOpenLoopElevatorPercent(double power) {
        mOpenLoopElevatorPercent = power;
    }
    
    public synchronized void setOpenLoopRearPercent(double power) {
        mOpenLoopRearPercent = power;
    }

    public synchronized void setScoringHeight(double inches) {
        mScoringHeight = inches;
    }

    public synchronized double getScoringHeight() {
        return mScoringHeight;
    }

    public synchronized void setScoringRearAngle(double angle) {
        mScoringRearAngle = angle;
    }

    public synchronized double getScoringRearAngle() {
        return mScoringRearAngle;
    }
    
    public synchronized void setScoringCarriageRollerPercent(double percent) {
        mScoringCarriageRollerPercent = percent;
    }

    public synchronized double getScoringCarriageRollerPercent() {
        return mScoringCarriageRollerPercent;
    }
    
    public synchronized void setScoringRearRollerPercent(double percent) {
        mScoringRearRollerPercent = percent;
    }

    public synchronized double getScoringRearRollerPercent() {
        return mScoringRearRollerPercent;
    }
    
    public synchronized void setScoringArmsDown(boolean armsDown) {
        mScoringArmsDown = armsDown;
    }

    public synchronized boolean getScoringArmsDown() {
        return mScoringArmsDown;
    }
    
    public synchronized void setScoringPushersOut(boolean pushersOut) {
        mScoringPushersOut = pushersOut;
    }

    public synchronized boolean getScoringPushersOut() {
        return mScoringPushersOut;
    }

    public synchronized void jogElevator(double relative_inches) {
        mScoringHeight += relative_inches;
        mScoringHeight = Math.min(mScoringHeight, Constants.kElevatorMaxHeight);
        mScoringHeight = Math.max(mScoringHeight, Constants.kElevatorMinHeight);
    }
}