package com.team3335.butterfly.states;

import com.team3335.butterfly.Constants;
import com.team3335.lib.util.Util;

public class SuperstructureState {
    //For planner
    public Goal goal = Goal.GAME_PEICE_OR_CLIMB;
    //Elevator
    public double height = Constants.kElevatorMinHeight;

    //Carriage
    public double carriageRollerPercent = 0.0;
    public boolean armsDown = false;
    public boolean pushersOut = false;

    //Rear Intake
    public double rearAngle = Constants.kRearMinAngle;
    public double rearRollerPercent = 0.0;

    //Not touched by planner
    public boolean elevatorSentLastTrajectory = false;
    public boolean rearIntakeSentLastTrajectory = false;
    public boolean hasRearCargo = false;
    public boolean hasCarriageCargo = false;
    public boolean hasHatch = false;

    public SuperstructureState(double height, double carriageRollerPercent, boolean armsDown, boolean pushersOut, double rearAngle, double rearRollerPercent) {
        this.height = height;
        this.carriageRollerPercent = carriageRollerPercent;
        this.armsDown = armsDown;
        this.pushersOut = pushersOut;
        this.rearAngle = rearAngle;
        this.rearRollerPercent = rearRollerPercent;
    }

    public SuperstructureState(SuperstructureState other) {
        this.height = other.height;
        this.carriageRollerPercent = other.carriageRollerPercent;
        this.armsDown = other.armsDown;
        this.pushersOut = other.pushersOut;
        this.rearAngle = other.rearAngle;
        this.rearRollerPercent = other.rearRollerPercent;
    }
    
    public SuperstructureState() {
        this(Constants.kElevatorMinHeight, 0, false, false, Constants.kRearMinAngle, 0);
    }

    public boolean isInRange(SuperstructureState otherState, double heightThreshold, double armThreshold) {
        return Util.epsilonEquals(otherState.height, height, heightThreshold) &&
                Util.epsilonEquals(otherState.rearAngle, rearAngle, armThreshold);

    }

    public boolean hasCargo() {
        return hasCarriageCargo || hasRearCargo;
    }

    public boolean hasGamePiece() {
        return hasCargo() || hasHatch;
    }

    public enum Goal {
        GAME_PEICE_OR_CLIMB,
        PLACING
    }

}