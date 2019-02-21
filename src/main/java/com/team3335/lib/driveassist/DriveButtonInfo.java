package com.team3335.lib.driveassist;

import com.team3335.butterfly.states.DrivetrainState.DriveModeState;
import com.team3335.butterfly.states.DrivetrainState.DrivetrainWheelState;
import com.team3335.butterfly.subsystems.Limelight.Target;


@Deprecated
public class DriveButtonInfo {
    private DriveType type;

    //Modes
    private boolean brake;

    //Controller Inputs
    private double forward, forward2, sideway, rotation;
    private boolean firstButton, secondButton;

    //Vision
    private boolean vision;

    public DriveButtonInfo(DriveType t) {
        type = t;
        switch(type) {
            case CUSTOM:
                vision = false;
                break;
            case FULL_VISION:
            case AUTO_SWITCHING:
                vision = true;
                break;
        }
    }

    public enum DriveType {
        CUSTOM,
        AUTO_SWITCHING,
        FULL_VISION
    }

}

/*

    public boolean setupCustom(double f, double f2, double s, double r, boolean b, boolean mb, boolean wb) {
        if(type == DriveType.CUSTOM) {
            forward = f;
            forward2 = f2;
            sideway = s;
            rotation = r;
            brake = b;
            firstButton = mb;
            firstButton = mb;
            return true;
        } else return false;
    }

    public boolean setupAutoSwitching(double f, double s, double r, boolean b, Target t) {
        if(type == DriveType.AUTO_SWITCHING) {
            forward = f;
            sideway = s;
            rotation = r;
            brake = b;
            target = t;
            return true;
        } else return false;
    }

    public boolean setupFullVision(Target t) {
        if(type == DriveType.FULL_VISION) {
            target = t;
            return true;
        } else return false;
    }

    public boolean isSetupComplete() {return setupComplete;}
    public DriveType getDriveType() {return type;}
    public boolean getBrakeEnabled() {return brake;}
    public DriveModeState getDriveModeState() {return modeState;}
    public DrivetrainWheelState getDrivetrainWheelState() {return wheelState;}
    public double getForward() {return forward;}
    public double getForward2() {return forward2;}
    public double getSideway() {return sideway;}
    public double getRotation() {return rotation;}
    public boolean getVisionEnabled() {return vision;}
    public Target getSelectedTarget() {return target;}

    
    public enum DriveType {
        CUSTOM,
        AUTO_SWITCHING,
        FULL_VISION
    }
}*/