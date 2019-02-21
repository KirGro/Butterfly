package com.team3335.lib.driveassist;

import com.team3335.butterfly.Preferences;
import com.team3335.butterfly.states.DrivetrainState.DriveModeState;
import com.team3335.butterfly.states.DrivetrainState.DriveType;
import com.team3335.butterfly.states.DrivetrainState.DrivetrainWheelState;
import com.team3335.butterfly.subsystems.Drivetrain;
import com.team3335.butterfly.subsystems.Limelight;
import com.team3335.butterfly.subsystems.NavX;
import com.team3335.butterfly.subsystems.Limelight.Target;

@Deprecated
/*
 *Migrated to live entirely inside the Robot.java teleopPeriodic() method
 */
public class DriveAssistant{
    /* STORAGE */

    //CUSTOM
    private DrivetrainWheelState wheelState = DrivetrainWheelState.MECANUM;
    private DriveModeState mecanumModeState = Preferences.pMecanumDefaultMode;
    private DriveModeState skidModeState = Preferences.pSkidSteerDefaultMode;

    //AUTO_SWITCHING
    private DriveModeState asModeState = DriveModeState.MECANUM_FIELD_RELATIVE;
    private Target asTarget = Preferences.pDefaultTarget;
    
    //FULL_VISION
    private Target fvTarget = Preferences.pDefaultTarget;


    private ButterflyDriveHelper mButterflyDriveHelper = new ButterflyDriveHelper();
    private VisionTargetDriver mVisionTargetDriver = new VisionTargetDriver();

    private Limelight mLimelight = Limelight.getInstance();
    private NavX mNavX = NavX.getInstance();
    private Drivetrain mDrivetrain = Drivetrain.getInstance();

    public DriveAssistant() {
    }


    public void driveChooser(DriveType t, double f1, double f2, double s, double r, boolean db1, boolean db2) {
        //Update values based on drivetype
        switch(t) {
            case CUSTOM:
                if(db1) {
                    if(wheelState == DrivetrainWheelState.MECANUM) mecanumModeState = mecanumModeState.next();
                    else skidModeState = skidModeState.next();
                    while(skidModeState == DriveModeState.MECANUM_FIELD_RELATIVE || skidModeState == DriveModeState.MECANUM_ROBOT_RELATIVE) skidModeState = skidModeState.next();
                }
                if(db2) wheelState = wheelState.next();
                break;
            case AUTO_SWITCHING:
                if(db1) asTarget.prev();
                if(db2) asTarget.next();
                mLimelight.setPipeline(Target.HATCH);
                if(mLimelight.hasTarget() && mLimelight.getDistance()<24) asModeState = DriveModeState.MECANUM_ROBOT_RELATIVE;
                else asModeState = DriveModeState.MECANUM_FIELD_RELATIVE;
                break;
            case FULL_VISION:
                if(db1) asTarget.prev();
                if(db2) asTarget.next();
                break;

        }
        //Send stuff to specific drivetrain helpers to run calculations and then those to drivetrain
        DriveModeState usingMode = (wheelState==DrivetrainWheelState.SKID_STEER ? skidModeState : mecanumModeState);
        switch(t) {
            case CUSTOM:
                switch(usingMode) {
    			    case TANK:
    			    	mDrivetrain.setOpenLoop(mButterflyDriveHelper.butterflyDrive(f1, f2, r, 0, usingMode, wheelState, true));
    	            	break;
    			    case ARCADE:
    	            	mDrivetrain.setOpenLoop(mButterflyDriveHelper.butterflyDrive(f1, s, r, 0, usingMode, wheelState, true));
    	            	break;
                    case MECANUM_FIELD_RELATIVE:
                        mDrivetrain.setOpenLoop(mButterflyDriveHelper.butterflyDrive(f1, s, r, mNavX.getYaw(), usingMode, wheelState, true));
                        break;
    			    case MECANUM_ROBOT_RELATIVE:
    	            	mDrivetrain.setOpenLoop(mButterflyDriveHelper.butterflyDrive(f1, s, r, 0, usingMode, wheelState, true));
                        break;    
                }
                break;
            case AUTO_SWITCHING:
                mButterflyDriveHelper.butterflyDrive(f1, s, r, mNavX.getYaw(), asModeState, DrivetrainWheelState.MECANUM, true);
                break;
            case FULL_VISION:
                mVisionTargetDriver.pureVisionDriveControl(fvTarget);
                break;
        }
    }

}