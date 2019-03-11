package com.team3335.butterfly.states;

import com.team3335.butterfly.Constants;

public class SuperstructureCommand {
    //Elevator
    public double height = Constants.kElevatorMinHeight;
    public boolean openLoopElevator = false;
    public double openLoopElevatorPercent = 0.0;

    //Carriage
    public double carriageRollerPercent = 0.0;
    public boolean armsDown = false;
    public boolean pushersOut = false;

    //Rear Intake
    public double angle = Constants.kRearMinAngle;
    public boolean openLoopRear = false;
    public double openLoopRearPercent = 0.0;
    public double rearRollerPercent = 0.0;
    
}