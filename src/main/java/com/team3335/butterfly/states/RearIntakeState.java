package com.team3335.butterfly.states;

import com.team3335.butterfly.Constants;
import com.team3335.butterfly.Preferences;

public class RearIntakeState {
    public boolean mClosedLoop = true;
    public RollerAction mRollerAction = RollerAction.NONE;
    public RearArmAction mRearArmAction = RearArmAction.STORED;

    //Not touched by planner
    public double mRearRollerPercent = 0.0;
    public double mRearArmPercent = 0.0;
    public double mRearArmEncoderPosition = 0;
    public double mRearArmEncoderSetpoint = 0;
    public boolean mRearLaserBroken = false;

    public boolean closedLoop() {
        return mClosedLoop;
    }
    public boolean hasCargo() {
        return mRearLaserBroken;
    }

    public static int convertArmStateToEncoder(RearArmAction armAction) {
        switch(armAction) {
            case STORED:
                return (int) (Constants.kSRXEncoderCPR / Constants.kRearEncoderToOutputRatio * Constants.kRearMinAngle / 360);
            case GRABBING_CARGO:
                return (int) (Constants.kSRXEncoderCPR / Constants.kRearEncoderToOutputRatio * Preferences.kCargoPickupAngle / 360);
            case CLIMBING:
                return (int) (Constants.kSRXEncoderCPR / Constants.kRearEncoderToOutputRatio * Preferences.kClimbingAngle / 360);
            default: 
                return Integer.MIN_VALUE;
        }
    }

    public static double convertRollerStateToPercent(RollerAction rollerAction) {
        switch(rollerAction) {
            case NONE:
            case HOLDING_CARGO:
                return 0;
            case WAITING_FOR_CARGO:
                return Preferences.kRollerIntakePercent;
            case SHOOTING:
                return 1;
            default: 
                return Double.NaN;
        }
    }

    public enum RollerAction {
        NONE,
        CUSTOM,
        WAITING_FOR_CARGO,
        HOLDING_CARGO,
        SHOOTING;

		private static RollerAction[] vals = values();
    }

    public enum RearArmAction {
        STORED,
        GRABBING_CARGO,
        CLIMBING,
        CUSTOM;

		private static RearArmAction[] vals = values();
    }

}