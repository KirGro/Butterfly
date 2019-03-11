package com.team3335.butterfly.states;

import com.team3335.butterfly.Constants;

public class SuperstructureState {
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
    public boolean hasRearCargo =false;
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
        this(Constants.kElevatorMinHeight + Constants.kCenterHeightFromFloorAtMin, 0, false, false, Constants.kRearMinAngle, 0);
    }

}