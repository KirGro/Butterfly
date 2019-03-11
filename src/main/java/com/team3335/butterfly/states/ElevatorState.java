package com.team3335.butterfly.states;

import com.team3335.butterfly.Constants;

public class ElevatorState {
    public ElevatorAction mElevatorAction;
    

    public enum ElevatorAction {
        ZERO,
        CUSTOM,
        HAB_PICKUP_HATCH,
        FLOOR_PICKUP_HATCH,
        RECEIVING_CARGO,
        CARGOSHIP_PLACING_HATCH,
        CARGOSHIP_PLACING_CARGO,
        ROCKET_PLACING_LOW_HATCH,
        ROCKET_PLACING_LOW_CARGO,
        ROCKET_PLACING_MID_HATCH,
        ROCKET_PLACING_MID_CARGO;

        private static ElevatorAction[] vals = values();
        
        public double getHeightFromFloor() {
            switch(this) {
                case ZERO:
                case FLOOR_PICKUP_HATCH:
                    return Constants.kElevatorMinHeight;
                case CUSTOM:
                    return Double.NaN;
                case HAB_PICKUP_HATCH:
                case CARGOSHIP_PLACING_HATCH:
                case ROCKET_PLACING_LOW_HATCH:
                    return Constants.kFloorToLowHatchCenter + Constants.kElevatorReachOffset;
                case ROCKET_PLACING_MID_HATCH:
                    return Constants.kFloorToMiddleHatchCenter + Constants.kElevatorReachOffset;
                case CARGOSHIP_PLACING_CARGO:
                    return Constants.kFloorToShipCargo;
                case ROCKET_PLACING_LOW_CARGO: 
                    return Constants.kFloorToLowCargo;
                case ROCKET_PLACING_MID_CARGO: 
                    return Constants.kFloorToMiddleCargo;
                case RECEIVING_CARGO:
                    return Constants.kElevatorCargoPassHeight;
                default:
                    return Double.NaN;
            }
        }
    }
	
	
}