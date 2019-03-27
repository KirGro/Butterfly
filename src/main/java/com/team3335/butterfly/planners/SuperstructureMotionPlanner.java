package com.team3335.butterfly.planners;

import java.util.LinkedList;
import java.util.Optional;

import com.team3335.butterfly.Constants;
import com.team3335.butterfly.Preferences;
import com.team3335.butterfly.states.SuperstructureState;
import com.team3335.lib.util.Util;

import edu.wpi.first.wpilibj.Timer;

public class SuperstructureMotionPlanner {
    
    class SubCommand {
        public SubCommand(SuperstructureState endState) {
            mEndState = endState;
            mStartTime = Timer.getFPGATimestamp();
        }

        public double mStartTime;
        public SuperstructureState mEndState;
        public double mHeightThreshold = 1.0;
        public double mArmThreshold = 5.0;

        public boolean isFinished(SuperstructureState currentState) {
            return mEndState.isInRange(currentState, mHeightThreshold, mArmThreshold);
        }
    }

    /*
    class WaitForSafeToPassSubcommand extends SubCommand {
        public WaitForSafeToPassSubcommand(SuperstructureState endState, double startTime) {
            super(endState, startTime);
            mArmThreshold = mArmThreshold + Math.max(0.0, mEndState.angle - SuperstructureConstants
                    .kClearFirstStageMinWristAngle);
        }

        @Override
        public boolean isFinished(SuperstructureState currentState) {
            return mEndState.isInRange(currentState, Double.POSITIVE_INFINITY, mWristThreshold);
        }
    }

    class WaitForElevatorSafeSubcommand extends SubCommand {
        public WaitForElevatorSafeSubcommand(SuperstructureState endState, SuperstructureState currentState, double startTime) {
            super(endState, startTime);
            if (endState.height >= currentState.height) {
                mHeightThreshold = mHeightThreshold + Math.max(0.0, mEndState.height - SuperstructureConstants
                        .kClearFirstStageMaxHeight);
            } else {
                mHeightThreshold = mHeightThreshold + Math.max(0.0, SuperstructureConstants.kClearFirstStageMaxHeight
                        - mEndState.height);
            }
        }

        @Override
        public boolean isFinished(SuperstructureState currentState) {
            return mEndState.isInRange(currentState, mHeightThreshold, Double.POSITIVE_INFINITY);
        }
    }
    

    class WaitForElevatorApproachingSubcommand extends SubCommand {
        public WaitForElevatorApproachingSubcommand(SuperstructureState endState) {
            super(endState);
            mHeightThreshold = SuperstructureConstants.kElevatorApproachingThreshold;
        }

        @Override
        public boolean isFinished(SuperstructureState currentState) {
            return mEndState.isInRange(currentState, mHeightThreshold, Double.POSITIVE_INFINITY);
        }
    }
    */

    class WaitForFinalSetpointSubcommand extends SubCommand {
        public WaitForFinalSetpointSubcommand(SuperstructureState endState) {
            super(endState);
        }

        @Override
        public boolean isFinished(SuperstructureState currentState) {
            return currentState.elevatorSentLastTrajectory && currentState.rearIntakeSentLastTrajectory;
        }
    }

    class WaitForCargoSubCommand extends SubCommand {
        public WaitForCargoSubCommand(SuperstructureState endState) {
            super(endState);
        }
        @Override
        public boolean isFinished(SuperstructureState currentState) {
            return currentState.hasCargo();
        }
    }

    class WaitForHatchSubCommand extends SubCommand {
        public WaitForHatchSubCommand(SuperstructureState endState) {
            super(endState);
        }
        @Override
        public boolean isFinished(SuperstructureState currentState) {
            return currentState.hasHatch;
        }
    }

    class WaitForGamePeiceSubCommand extends SubCommand {
        public WaitForGamePeiceSubCommand(SuperstructureState endState) {
            super(endState);
        }

        @Override
        public boolean isFinished(SuperstructureState currentState) {
            return currentState.hasGamePiece();
        }
    }

    
    

    protected SuperstructureState mCommandedState = new SuperstructureState();
    protected SuperstructureState mIntermediateCommandState = new SuperstructureState();
    protected LinkedList<SubCommand> mCommandQueue = new LinkedList<>();
    protected Optional<SubCommand> mCurrentCommand = Optional.empty();

    public synchronized boolean setDesiredState(SuperstructureState desiredStateIn, SuperstructureState currentState) {
        SuperstructureState desiredState = new SuperstructureState(desiredStateIn);

        // Limit illegal inputs.
        desiredState.rearAngle = Util.limit(desiredState.rearAngle, Constants.kRearMinAngle, Constants.kRearMaxAngle);
        desiredState.height = Util.limit(desiredState.height, Constants.kElevatorMinHeight, Constants.kElevatorMaxHeight);

        mCommandQueue.clear();
        
        return false;

    }
    
    void reset(SuperstructureState currentState) {
        mIntermediateCommandState = currentState;
        mCommandQueue.clear();
        mCurrentCommand = Optional.empty();
    }

    
    public SuperstructureState update(SuperstructureState currentState) {
        if (!mCurrentCommand.isPresent() && !mCommandQueue.isEmpty()) {
            mCurrentCommand = Optional.of(mCommandQueue.remove());
        }

        if (mCurrentCommand.isPresent()) {
            SubCommand subCommand = mCurrentCommand.get();
            mIntermediateCommandState = subCommand.mEndState;
            if (subCommand.isFinished(currentState) && !mCommandQueue.isEmpty()) {
                // Let the current command persist until there is something in the queue. or not. desired outcome
                // unclear.
                mCurrentCommand = Optional.empty();
            }
        } else {
            mIntermediateCommandState = currentState;
        }

        mCommandedState.rearAngle = Util.limit(mIntermediateCommandState.rearAngle, Constants.kRearMinAngle, Constants.kRearMaxAngle);
        mCommandedState.height = Util.limit(mIntermediateCommandState.height, Constants.kElevatorMinHeight, Constants.kElevatorMaxHeight);

        return mCommandedState;
    }

    public boolean isFinished(SuperstructureState currentState) {
        return mCurrentCommand.isPresent() && mCommandQueue.isEmpty() && currentState.rearIntakeSentLastTrajectory &&
                currentState.elevatorSentLastTrajectory;
    }
}