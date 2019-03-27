package com.team3335.butterfly;

import java.util.Arrays;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.team3335.butterfly.loops.Looper;
import com.team3335.butterfly.states.SuperstructureCommand;
import com.team3335.butterfly.states.SuperstructureState;
import com.team3335.butterfly.states.DrivetrainState.DriveModeState;
import com.team3335.butterfly.states.DrivetrainState.DriveType;
import com.team3335.butterfly.states.DrivetrainState.DrivetrainWheelState;
import com.team3335.butterfly.subsystems.*;
import com.team3335.butterfly.subsystems.Limelight.Target;
import com.team3335.lib.driveassist.*;
import com.team3335.lib.util.LatchedBoolean;

public class Robot extends TimedRobot {
    private Looper mEnabledLooper = new Looper();
    private Looper mDisabledLooper = new Looper();

    private ButterflyDriveHelper mButterflyDriveHelper = new ButterflyDriveHelper();
    private VisionTargetDriver mVisionTargetDriver = new VisionTargetDriver();
    //private DriveAssistant mDriveAssistant = new DriveAssistant();
    private IControlBoard mControlBoard = ControlBoard.getInstance();
    
    private final SubsystemManager mSubsystemManager = new SubsystemManager(
            Arrays.asList(
                    //TODO Add PreferenceHandler once finished
                    Drivetrain.getInstance(),
                    NavX.getInstance(),
                    Carriage.getInstance(),
                    Limelight.getInstance(),
                    Elevator.getInstance(),
                    RearIntake.getInstance(),
                    Superstructure.getInstance()
            )
    );

    private Drivetrain mDrivetrain = Drivetrain.getInstance();
    private NavX mNavX = NavX.getInstance();
    private Carriage mCarriage = Carriage.getInstance();
    private Limelight mLimelight = Limelight.getInstance();
    private Elevator mElevator = Elevator.getInstance();
    private RearIntake mRearIntake = RearIntake.getInstance();
    private Superstructure mSuperstructure = Superstructure.getInstance();
    
    //Buttons
    private LatchedBoolean mToggleDriveType = new LatchedBoolean();
    private LatchedBoolean mDriveButton1 = new LatchedBoolean();
    private LatchedBoolean mDriveButton2 = new LatchedBoolean();
    private LatchedBoolean mToggleTargeting = new LatchedBoolean();
    private LatchedBoolean mStartAction = new LatchedBoolean();
    private LatchedBoolean mTogglePlacing = new LatchedBoolean();

    //Practicing
    private LatchedBoolean mPlaceHatchLow = new LatchedBoolean();
    private LatchedBoolean mPickupHatch = new LatchedBoolean();
    
    /* STORAGE */

    //Drivetrain
    private DriveType type = Preferences.pDefaultDriveType;

    //CUSTOM
    private DrivetrainWheelState wheelState = Preferences.pDefaultDrivetrainWheelState;
    private DriveModeState mecanumModeState = Preferences.pMecanumDefaultMode;
    private DriveModeState skidModeState = Preferences.pSkidSteerDefaultMode;

    //AUTO_SWITCHING
    private DriveModeState asModeState = DriveModeState.MECANUM_FIELD_RELATIVE;
    private Target asTarget = Preferences.pDefaultTarget;
    
    //VISION ASSIST
    private boolean inTargetCycle = false;
    private Target vaTarget = Preferences.pDefaultTarget;

    //FULL_VISION
    private Target fvTarget = Preferences.pDefaultTarget;

    private Targeting mTargeting = Preferences.pDefaultTargeting;
    private Placing mPlacing = Placing.LOW_HATCH;    
    private Action mAction = Action.NONE;
    private double mActionStartTime = 0;
    
    public Robot() {
    	
    }
    
    @Override
    public void robotInit() {
    	SmartDashboard.putString("Match Cycle", "ROBOT INIT");
        mSubsystemManager.registerEnabledLoops(mEnabledLooper);
        mSubsystemManager.registerDisabledLoops(mDisabledLooper);

    }

    @Override
    public void disabledInit() {
        SmartDashboard.putString("Match Cycle", "DISABLED");
        mEnabledLooper.stop();
        mDisabledLooper.start();
    }
    
    @Override
    public void autonomousInit() {
    	SmartDashboard.putString("Match Cycle", "AUTONOMOUS");
    }
    
    @Override
    public void teleopInit() {
        SmartDashboard.putString("Match Cycle", "TELEOP");
        mDrivetrain.zeroSensors();
        mElevator.zeroSensors();
        mDisabledLooper.stop();
        mEnabledLooper.start();
        //mElevator.setEncoderTargetHeight(0);
    }

    @Override
    public void testInit() {
        SmartDashboard.putString("Match Cycle", "TEST");
        mDisabledLooper.stop();
        mEnabledLooper.stop();
    }

    @Override
    public void disabledPeriodic() {
        SmartDashboard.putString("Match Cycle", "DISABLED");
    }   

    @Override
    public void autonomousPeriodic() {
        SmartDashboard.putString("Match Cycle", "AUTONOMOUS");
    }
    
    
    @Override
    public void teleopPeriodic() {
        SmartDashboard.putString("Match Cycle", "TELEOP");
        double timestamp = Timer.getFPGATimestamp();        

        /* HANDLE CONTROLLER INPUTS*/
        double f1 = mControlBoard.getDriveForward();
        double f2 = mControlBoard.getDriveForward2();
        double s = mControlBoard.getDriveSideway();
        double r = mControlBoard.getDriveRotation();
        boolean toggleDriveType = mToggleDriveType.update(mControlBoard.getToggleDriveType());
        boolean db1 = mDriveButton1.update(mControlBoard.getDriveButton1());
        boolean db2 = mDriveButton2.update(mControlBoard.getDriveButton2());
        SmartDashboard.putBoolean("db1", db1);
        SmartDashboard.putBoolean("db2", db2);
        SmartDashboard.putString("SkidMode", skidModeState.toString());
        SmartDashboard.putString("MecanumMode", mecanumModeState.toString());

        if(toggleDriveType) type = type.next();
        SmartDashboard.putString("Type", type.toString());

        /* HANDLE DRIVETRAIN STUFF */
        switch(type) {
            case CUSTOM:
                if(db1) {
                    if(wheelState == DrivetrainWheelState.MECANUM) {
                        mecanumModeState = mecanumModeState.next();
                    } else {
                        skidModeState = skidModeState.next();
                        while (skidModeState == DriveModeState.MECANUM_FIELD_RELATIVE || skidModeState == DriveModeState.MECANUM_ROBOT_RELATIVE) {
                            skidModeState = skidModeState.next();
                        }
                    }

                }
                if(db2) {
                    wheelState = wheelState.next();
                }
                break;
            case AUTO_SWITCHING:
                if(db1) asTarget.prev();
                if(db2) asTarget.next();
                mLimelight.setPipeline(Target.HATCH_TARGET);
                if(mLimelight.hasTarget() && mLimelight.getDistance()<24) asModeState = DriveModeState.MECANUM_ROBOT_RELATIVE;
                else asModeState = DriveModeState.MECANUM_FIELD_RELATIVE;
                break;
            case VISION_ASSIST:
                if(db1) {

                }
                break;
            case FULL_VISION:
                if(db1) asTarget.prev();
                if(db2) asTarget.next();
                break;

        }
        //Send stuff to specific drivetrain helpers to run calculations and then those to drivetrain
        DriveModeState usingMode = (wheelState==DrivetrainWheelState.SKID_STEER ? skidModeState : mecanumModeState);
        mDrivetrain.setWheelState(wheelState);
        switch(type) {
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
                mDrivetrain.setOpenLoop(mButterflyDriveHelper.butterflyDrive(f1, s, r, mNavX.getYaw(), asModeState, DrivetrainWheelState.MECANUM, true));
                break;
            case VISION_ASSIST:
                if(mControlBoard.getDriveButton1()){
                    mDrivetrain.setPositionFollowing(mVisionTargetDriver.pureVisionDriveControl(fvTarget));
                } else {
                    mDrivetrain.setOpenLoop(mButterflyDriveHelper.butterflyDrive(f1, s, r, 0, DriveModeState.MECANUM_ROBOT_RELATIVE, DrivetrainWheelState.SKID_STEER, true));
                }
                break;
            case FULL_VISION:
                mDrivetrain.setPositionFollowing(mVisionTargetDriver.pureVisionDriveControl(fvTarget));
                break;
        }

        /* CARRIAGE STUFF */
        /* OLD
        if(fireHatch) {
            mCarriage.placeHatchCargoship();
        } else if(habPickup) {
            mCarriage.habPickup();
            mElevator.setEncoderTargetHeight(4);
        }

        if(mControlBoard.getUseAssist()) {
            mCarriage.setShootForward();
        } else {
            mCarriage.stopRollers();
        } 
        boolean switchEM = mSwitchElevatorMode.update(mControlBoard.getSwitchElevatorMode());

        
        
        if(mControlBoard.getHabPickupHeight()) {
            mElevator.setHeightRobot(2);
        } else if(mControlBoard.getHatchLowHeight()) {
            mElevator.setEncoderTargetHeight(0);
        } else if(mControlBoard.getHatchMiddleHeight()) {
            mElevator.setEncoderTargetHeight(5);
        }*/

        /*
        SuperstructureState currentState = mSuperstructure.getObservedState();
        if(mToggleTargeting.update(mControlBoard.getToggleTargeting())) {
            mTargeting = mTargeting.next();
        }
        if(mAction == Action.NONE && mTogglePlacing.update(mControlBoard.getTogglePlacing())) {
            if(currentState.hasCargo()) {
                mPlacing = mPlacing.next();
                if(mPlacing != Placing.LOW_CARGO || mPlacing != Placing.SHIP_CARGO || mPlacing != Placing.MID_CARGO) {
                    mPlacing = Placing.LOW_CARGO;
                }
            } else if(currentState.hasHatch) {
                mPlacing = mPlacing.next();
                if(mPlacing != Placing.LOW_HATCH || mPlacing != Placing.MID_HATCH) {
                    mPlacing = Placing.LOW_HATCH;
                }

            } else if(mTargeting == Targeting.HATCH){
                mPlacing = mPlacing.next();
                if(mPlacing != Placing.LOW_HATCH || mPlacing != Placing.MID_HATCH) {
                    mPlacing = Placing.LOW_HATCH;
                }
            } else if(mTargeting == Targeting.CARGO) {
                mPlacing = mPlacing.next();
                if(mPlacing != Placing.LOW_CARGO || mPlacing != Placing.SHIP_CARGO || mPlacing != Placing.MID_CARGO) {
                    mPlacing = Placing.LOW_CARGO;
                }
            }
        }

        boolean inPractice = true;
        SuperstructureCommand command = new SuperstructureCommand();
        if(!currentState.hasGamePiece() && !inPractice) {
            if(mTargeting == Targeting.HATCH) {
                command.height = Constants.kFloorToLowHatchCenter + Constants.kElevatorReachOffset;
            } else if(mTargeting == Targeting.CARGO) {
                command.height = Constants.kFloorToHabCargoCenter;
                command.angle = 90;
                command.carriageRollerPercent = -.1;
                command.rearRollerPercent = -.1;
            } else {
                command.height = Constants.kElevatorMinHeight+6;
            }
        } else if(mAction == Action.NONE && mStartAction.update(mControlBoard.getStartAction())){
            if(!currentState.hasGamePiece()) {
                if(!currentState.hasHatch) {
                    mAction = Action.HATCH_HAB_PICKUP;
                    mActionStartTime = timestamp;
                    command.armsDown = true;
                    command.height = Constants.kFloorToLowHatchCenter + Constants.kElevatorReachOffset;
                }
            } else {
                
            }
        } 

        switch(mAction) {
            case HATCH_FLOOR_PICKUP:
            case HATCH_HAB_PICKUP:
                if(timestamp-mActionStartTime > .125) {

                }
        }


        mSuperstructure.setFromCommandState(command);
        */

        //Practice code
        SuperstructureCommand command = new SuperstructureCommand();
        SuperstructureState currentState = mSuperstructure.getObservedState();
        if(mAction == Action.NONE) {
            if(mPlaceHatchLow.update(mControlBoard.getHatchPusher())) {
                mAction = Action.PLACING_HATCH;
                mActionStartTime = timestamp;
                command.pushersOut = false;
                command.armsDown = true;
            } else if(mPickupHatch.update(mControlBoard.getHabPickup())) {
                mAction = Action.HATCH_HAB_PICKUP;
                mActionStartTime = timestamp;
                command.pushersOut = false;
                command.armsDown = true;
            }
        } else {
            if(mAction == Action.HATCH_HAB_PICKUP) {
                if(timestamp-mActionStartTime < .6) {
                    
                } else if(timestamp-mActionStartTime < .12){

                }

            } else if(mAction == Action.PLACING_HATCH) {

            }
        }
        //mSuperstructure.setFromCommandState(command);
    }
    
    
    

    public enum Targeting {
        HATCH,
        CARGO,
        CLIMB;

        
		private static Targeting[] vals = values();
	    public Targeting next() {
	        return vals[(this.ordinal()+1) % vals.length];
		}
		
		public Targeting prev() {
			int p = this.ordinal()-1;
			return p>=0 ? vals[p] : vals[vals.length-1];
		}
    }

    public enum Action {
        NONE,
        HATCH_FLOOR_PICKUP,
        HATCH_HAB_PICKUP,
        PLACING_HATCH,
        PLACING_CARGO
    }

    public enum Placing {
        LOW_HATCH,
        MID_HATCH,
        LOW_CARGO,
        SHIP_CARGO,
        MID_CARGO;
        
		private static Placing[] vals = values();
	    public Placing next() {
	        return vals[(this.ordinal()+1) % vals.length];
		}
		
		public Placing prev() {
			int p = this.ordinal()-1;
			return p>=0 ? vals[p] : vals[vals.length-1];
		}
    }
}


        /*
    	if(mLimelight.hasTarget() && mControlBoard.getUseAssist()) {
            //mDrivetrain.setOpenLoop(mVisionTargetDriver.pureVisionDriveRaw(0)); //Old way using raw velocities
            mDrivetrain.setPositionFollowing(mVisionTargetDriver.pureVisionDriveControl(Target.HATCH)); //New way using actual distances and encoders
    		
    	}else {
    		switch(mode) {
    			case TANK:
    				mDrivetrain.setOpenLoop(mButterflyDriveHelper.butterflyDrive(forward, forward2, rotation, 0, mode, wheel, tempBrake));
    	        	break;
    			case ARCADE:
    	        	mDrivetrain.setOpenLoop(mButterflyDriveHelper.butterflyDrive(forward, sideway, rotation, 0, mode, wheel, tempBrake));
    	        	break;
                case MECANUM_FIELD_RELATIVE:
                    mDrivetrain.setOpenLoop(mButterflyDriveHelper.butterflyDrive(forward, sideway, rotation, mNavX.getYaw(), mode, wheel, tempBrake));
                    break;
    			case MECANUM_ROBOT_RELATIVE:
    	        	mDrivetrain.setOpenLoop(mButterflyDriveHelper.butterflyDrive(forward, sideway, rotation, 0, mode, wheel, tempBrake));
    	        	break;    			
    		}
    	}*/