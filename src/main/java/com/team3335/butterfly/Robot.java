package com.team3335.butterfly;

import java.util.Arrays;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.team3335.butterfly.loops.Looper;
import com.team3335.butterfly.states.DrivetrainState.DriveModeState;
import com.team3335.butterfly.states.DrivetrainState.DrivetrainWheelState;
import com.team3335.butterfly.subsystems.*;
import com.team3335.butterfly.subsystems.Limelight.Target;
import com.team3335.butterfly.vision.VisionTargetDriver;
import com.team3335.lib.util.ButterflyDriveHelper;
import com.team3335.lib.util.LatchedBoolean;

public class Robot extends TimedRobot {
    private Looper mEnabledLooper = new Looper();
    private Looper mDisabledLooper = new Looper();
    private ButterflyDriveHelper mButterflyDriveHelper = new ButterflyDriveHelper();
    private VisionTargetDriver mVisionTargetDriver = new VisionTargetDriver();
    private IControlBoard mControlBoard = ControlBoard.getInstance();
    
    private final SubsystemManager mSubsystemManager = new SubsystemManager(
            Arrays.asList(
                    Drivetrain.getInstance(),
                    NavX.getInstance(),
                    Carriage.getInstance(),
                    Limelight.getInstance()

            )
    );
 
    private Limelight mLimelight = Limelight.getInstance();
    private Drivetrain mDrivetrain = Drivetrain.getInstance();
    private NavX mNavX = NavX.getInstance();
    private Carriage mCarriage = Carriage.getInstance();
    
    private LatchedBoolean mToggleDriveModeStatePressed = new LatchedBoolean();
    private LatchedBoolean mToggleDrivetrainWheelStatePressed = new LatchedBoolean();
    private LatchedBoolean mToggleBrakePressed = new LatchedBoolean();
    private LatchedBoolean mFireHatch = new LatchedBoolean();
    
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
        mNavX.zeroYaw();
        mDisabledLooper.stop();
        mEnabledLooper.start();
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

        double forward = mControlBoard.getDriveForward();
        double forward2 = mControlBoard.getDriveForward2();
        double sideway = mControlBoard.getDriveSideway();
        double rotation = mControlBoard.getDriveRotation();
        
        SmartDashboard.putNumber("Forward", forward);
        SmartDashboard.putNumber("Sideway", sideway);
        SmartDashboard.putNumber("Rotation", rotation);
        
        boolean tempMode = mToggleDriveModeStatePressed.update(mControlBoard.getToggleDriveMode());
        boolean tempWheel = mToggleDrivetrainWheelStatePressed.update(mControlBoard.getToggleWheelState());
        
        boolean tempBrake = mToggleBrakePressed.update(mControlBoard.getToggleBrake());
        boolean tempHatch = mFireHatch.update(mControlBoard.getHatchPusher());

        
        DriveModeState mode = mDrivetrain.getDriveModeState();
        DrivetrainWheelState wheel = mDrivetrain.getDrivetrainWheelState();
        if(tempHatch) {
            mCarriage.launchHatch();
            SmartDashboard.putNumber("Last Launch Time", timestamp);
        }
        
        if(tempMode) {
        	mode = mode.next();
        }
        if(tempWheel) {
        	wheel = wheel.next();
        	mDrivetrain.setWheelState(wheel);
        }
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
    	}
        
        
    }
    
}
