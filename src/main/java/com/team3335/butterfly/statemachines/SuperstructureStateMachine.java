package com.team3335.butterfly.statemachines;

import com.team3335.butterfly.Constants;
import com.team3335.butterfly.planners.SuperstructureMotionPlanner;
import com.team3335.butterfly.states.SuperstructureCommand;
import com.team3335.butterfly.states.SuperstructureState;
import com.team3335.lib.util.Util;

public class SuperstructureStateMachine {
    public enum WantedAction {
        IDLE,
        TAKE_ACTION,
        WANT_MANUAL
    }

    public enum SystemState {
        HOLDING_POSITION,
        MOVING_TO_POSITION,
        MANUAL
    }

    private SystemState mSystemState = SystemState.HOLDING_POSITION;

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

    public synchronized void jogRearIntake(double relative_degrees) {
        mScoringRearAngle += relative_degrees;
        mScoringRearAngle = Math.min(mScoringRearAngle, Constants.kRearMaxAngle);
        mScoringRearAngle = Math.max(mScoringRearAngle, Constants.kRearMinAngle);
    }

    public synchronized boolean scoringPositionChanged() {
        return !Util.epsilonEquals(mDesiredEndState.rearAngle, mScoringRearAngle) ||
                !Util.epsilonEquals(mDesiredEndState.height, mScoringHeight) ||
                !Util.epsilonEquals(mDesiredEndState.carriageRollerPercent, mScoringCarriageRollerPercent) ||
                !Util.epsilonEquals(mDesiredEndState.rearRollerPercent, mScoringRearRollerPercent) ||
                mDesiredEndState.armsDown != mScoringArmsDown ||
                mDesiredEndState.pushersOut != mScoringPushersOut;
    }

    public synchronized SystemState getSystemState() {
        return mSystemState;
    }

    public synchronized SuperstructureCommand update(double timestamp, WantedAction wantedAction, SuperstructureState currentState) {
        synchronized (SuperstructureStateMachine.this) {
            SystemState newState;

            // Handle state transitions
            switch (mSystemState) {
                case HOLDING_POSITION:
                    newState = handleHoldingPositionTransitions(wantedAction, currentState);
                    break;
                case MOVING_TO_POSITION:
                    newState = handleMovingToPositionTransitions(wantedAction, currentState);
                    break;
                case MANUAL:
                    newState = handleManualTransitions(wantedAction, currentState);
                    break;
                default:
                    System.out.println("Unexpected superstructure system state: " + mSystemState);
                    newState = mSystemState;
                    break;
            }

            if (newState != mSystemState) {
                System.out.println(timestamp + ": Superstructure changed state: " + mSystemState + " -> " + newState);
                mSystemState = newState;
            }

            // Pump elevator planner only if not jogging.
            if (!mCommand.openLoopElevator) {
                mCommandedState = mPlanner.update(currentState);
                mCommand.height = mCommandedState.height;
                mCommand.angle = mCommandedState.rearAngle;
            }

            // Handle state outputs
            switch (mSystemState) {
                case HOLDING_POSITION:
                    getHoldingPositionCommandedState();
                    break;
                case MOVING_TO_POSITION:
                    getMovingToPositionCommandedState();
                    break;
                case MANUAL:
                    getManualCommandedState();
                    break;
                default:
                    System.out.println("Unexpected superstructure state output state: " + mSystemState);
                    break;
            }

            return mCommand;
        }
    }

    private void updateMotionPlannerDesired(SuperstructureState currentState) {
        mDesiredEndState.height = mScoringHeight;
        mDesiredEndState.carriageRollerPercent = mScoringCarriageRollerPercent;
        mDesiredEndState.armsDown = mScoringArmsDown;
        mDesiredEndState.pushersOut = mScoringPushersOut;
        mDesiredEndState.rearAngle = mScoringRearAngle;
        mDesiredEndState.rearRollerPercent = mScoringRearRollerPercent;

        System.out.println("Setting motion planner to height: " + mDesiredEndState.height + " carriage rollers: " + mDesiredEndState.carriageRollerPercent + " carriage arms: " + mDesiredEndState.armsDown + " carriage pushers: " + mDesiredEndState.pushersOut + " rear angle: " + mDesiredEndState.rearAngle + " rear rollers: " + mDesiredEndState.rearRollerPercent);

        // Push into elevator planner.
        mPlanner.setDesiredState(mDesiredEndState, currentState);

        //Redundant but cheesy poofs did it so idek at this point TODO Possibly remove
        mScoringHeight = mDesiredEndState.height;
        mScoringCarriageRollerPercent = mDesiredEndState.carriageRollerPercent;
        mScoringArmsDown = mDesiredEndState.armsDown;
        mScoringPushersOut = mDesiredEndState.pushersOut;
        mScoringRearAngle = mDesiredEndState.rearAngle;
        mScoringRearRollerPercent = mDesiredEndState.rearRollerPercent;
    }

    private SystemState handleDefaultTransitions(WantedAction wantedAction, SuperstructureState currentState) {
        if (wantedAction == WantedAction.TAKE_ACTION) {
            if (scoringPositionChanged()) {
                updateMotionPlannerDesired(currentState);
            } else if (mPlanner.isFinished(currentState)) {
                return SystemState.HOLDING_POSITION;
            }
            return SystemState.MOVING_TO_POSITION;
        } else if (wantedAction == WantedAction.WANT_MANUAL) {
            return SystemState.MANUAL;
        } else {
            if (mSystemState == SystemState.MOVING_TO_POSITION && !mPlanner.isFinished(currentState)) {
                return SystemState.MOVING_TO_POSITION;
            } else {
                return SystemState.HOLDING_POSITION;
            }
        }
    }
    
    // HOLDING_POSITION
    private SystemState handleHoldingPositionTransitions(WantedAction wantedAction, SuperstructureState currentState) {
        return handleDefaultTransitions(wantedAction, currentState);
    }

    private void getHoldingPositionCommandedState() {
        mCommand.openLoopRear = false;
        mCommand.openLoopElevator = false;
    }

    // MOVING_TO_POSITION
    private SystemState handleMovingToPositionTransitions(WantedAction wantedAction, SuperstructureState currentState) {
        return handleDefaultTransitions(wantedAction, currentState);
    }

    private void getMovingToPositionCommandedState() {
        mCommand.openLoopRear = false;
        mCommand.openLoopElevator = false;
    }

    // MANUAL
    private SystemState handleManualTransitions(WantedAction wantedAction, SuperstructureState currentState) {
        if (wantedAction != WantedAction.WANT_MANUAL) {
            // Freeze height.
            mScoringRearAngle = currentState.rearAngle;
            mScoringHeight = currentState.height;
            return handleDefaultTransitions(WantedAction.TAKE_ACTION, currentState);
        }
        return handleDefaultTransitions(wantedAction, currentState);
    }
    
    private void getManualCommandedState() {
        mCommand.openLoopRear = true;
        mCommand.openLoopRearPercent = mOpenLoopRearPercent;
        mCommand.openLoopElevator = true;
        mCommand.openLoopElevatorPercent = mOpenLoopElevatorPercent;

    }



}