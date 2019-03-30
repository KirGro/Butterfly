package com.team3335.butterfly;

import java.util.Arrays;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.team3335.butterfly.loops.Looper;
import com.team3335.butterfly.states.DrivetrainState.DriveModeState;
import com.team3335.butterfly.states.DrivetrainState.DriveType;
import com.team3335.butterfly.states.DrivetrainState.DrivetrainWheelState;
import com.team3335.butterfly.subsystems.*;
import com.team3335.butterfly.subsystems.Limelight.Target;
import com.team3335.lib.driveassist.*;
import com.team3335.lib.util.LatchedBoolean;
import com.team3335.butterfly.subsystems.Elevator.ElevatorControlState;

public class Robot extends TimedRobot {
    private Looper mEnabledLooper = new Looper();
    private Looper mDisabledLooper = new Looper();

    private ButterflyDriveHelper mButterflyDriveHelper = new ButterflyDriveHelper();
    private VisionTargetDriver mVisionTargetDriver = new VisionTargetDriver();
    private IControlBoard mControlBoard = ControlBoard.getInstance();
    
    private final SubsystemManager mSubsystemManager = new SubsystemManager(
            Arrays.asList(
                    //TODO Add PreferenceHandler once finished
                    Drivetrain.getInstance(),
                    NavX.getInstance(),
                    Carriage.getInstance(),
                    Limelight.getInstance(),
                    Elevator.getInstance(),
                    USBCamera.getInstance()
            )
    );

    private Drivetrain mDrivetrain = Drivetrain.getInstance();
    private NavX mNavX = NavX.getInstance();
    private Carriage mCarriage = Carriage.getInstance();
    private Limelight mLimelight = Limelight.getInstance();
    private Elevator mElevator = Elevator.getInstance();
    private USBCamera mUsbCamera = USBCamera.getInstance();
    
    //Buttons
    private LatchedBoolean mToggleDriveType = new LatchedBoolean();
    private LatchedBoolean mDriveButton1 = new LatchedBoolean();
    private LatchedBoolean mDriveButton2 = new LatchedBoolean();
    private LatchedBoolean pusherToggle = new LatchedBoolean();
    private LatchedBoolean hatchGrabberToggle = new LatchedBoolean();
    private LatchedBoolean elevatorStateToggle = new LatchedBoolean();
    private LatchedBoolean elevatorLowPositionToggle = new LatchedBoolean();
    private LatchedBoolean elevatorMidPositionToggle = new LatchedBoolean();
    private LatchedBoolean elevatorHighPositionToggle = new LatchedBoolean();
    
    
    
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
    
    //DUNCAN
    private DrivetrainWheelState duncanState = Preferences.pDefaultDrivetrainWheelState;

    //Elevator
    private ElevatorControlState elevatorState = ElevatorControlState.MOTION_MAGIC;
    private double distanceOffGround = 19;
    
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
        mDrivetrain.zeroSensors();
        mDisabledLooper.stop();
        mEnabledLooper.start();
        if(!mElevator.hasBeenZeroed()) {
            mElevator.zeroSensors();
        }
    }
    
    @Override
    public void teleopInit() {
        SmartDashboard.putString("Match Cycle", "TELEOP");
        mDisabledLooper.stop();
        mEnabledLooper.start();
        if(!mElevator.hasBeenZeroed()) {
            mElevator.zeroSensors();
        }
    }

    @Override
    public void testInit() {
        SmartDashboard.putString("Match Cycle", "TEST");
        mDisabledLooper.stop();
        mEnabledLooper.stop();
        mElevator.zeroSensors();
    }

    @Override
    public void disabledPeriodic() {
        SmartDashboard.putString("Match Cycle", "DISABLED");
    }   

    @Override
    public void autonomousPeriodic() {
        SmartDashboard.putString("Match Cycle", "AUTONOMOUS");
        teleopPeriodic();
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
                if(db1) asTarget = asTarget.prev();
                if(db2) asTarget = asTarget.next();
                mLimelight.setPipeline(Target.HATCH_TARGET);
                if(mLimelight.hasTarget() && mLimelight.getDistance()<24) asModeState = DriveModeState.MECANUM_ROBOT_RELATIVE;
                else asModeState = DriveModeState.MECANUM_FIELD_RELATIVE;
                break;
            case VISION_ASSIST:
                if(db1) {

                }
                break;
            case FULL_VISION:
                if(db1) asTarget = asTarget.prev();
                if(db2) asTarget = asTarget.next();
                break;
            case DUNCAN:
                if(db2) duncanState = duncanState.next();
                break;

        }

        //Send stuff to specific drivetrain helpers to run calculations and then those to drivetrain
        DriveModeState usingMode = (wheelState==DrivetrainWheelState.SKID_STEER ? skidModeState : mecanumModeState);
        if(type != DriveType.DUNCAN) {
            mDrivetrain.setWheelState(wheelState);
        } else {
            mDrivetrain.setWheelState(duncanState);
        }
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
            case DUNCAN:
                switch(duncanState) {
                    case SKID_STEER:

                        mDrivetrain.setOpenLoop(mButterflyDriveHelper.butterflyDrive(f1, s, r, 0, DriveModeState.ARCADE, DrivetrainWheelState.SKID_STEER, true));
                        break;
                    case MECANUM:
                        mDrivetrain.setOpenLoop(mButterflyDriveHelper.butterflyDrive(f1, s, r, 0, DriveModeState.MECANUM_ROBOT_RELATIVE, DrivetrainWheelState.MECANUM, true));
                        break;

                }
                break;
        }
        //Carriage stuff
        if(pusherToggle.update(mControlBoard.getHatchPusher())) {
            mCarriage.togglePusherState();
        }
        if(hatchGrabberToggle.update(mControlBoard.getHatchGrabber())) {
            mCarriage.toggleGrabberState();
        }

        //Elevator stuff
        if(elevatorStateToggle.update(mControlBoard.getElevatorStateToggle())) {
            elevatorState = elevatorState.next();
        }

        if(elevatorLowPositionToggle.update(mControlBoard.getHatchLowHeight())) {
            distanceOffGround = Constants.kFloorToLowHatchCenter-1;
        } else if(elevatorMidPositionToggle.update(mControlBoard.getHatchMiddleHeight())) {
            distanceOffGround = Constants.kFloorToMiddleHatchCenter;
        } else if(elevatorHighPositionToggle.update(mControlBoard.getHatchHighHeight())) {
            distanceOffGround = Constants.kFloorToHighHatchCenter+2;
        }

        switch(elevatorState) {
            case OPEN_LOOP:
                mElevator.setOpenLoop(mControlBoard.getElevator());
                break;
            case MOTION_MAGIC:
                mElevator.setMotionMagicPosition(distanceOffGround);
                break;
            case POSITION_PID:
                mElevator.setPositionPID(distanceOffGround);
                break;
        }

    }
    
}