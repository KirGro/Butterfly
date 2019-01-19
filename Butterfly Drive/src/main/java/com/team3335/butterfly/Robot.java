package main.java.com.team3335.butterfly;

import java.util.Arrays;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import main.java.com.team3335.butterfly.loops.Looper;
import main.java.com.team3335.butterfly.states.DrivetrainState.DriveModeState;
import main.java.com.team3335.butterfly.states.DrivetrainState.DrivetrainWheelState;
import main.java.com.team3335.butterfly.subsystems.Drivetrain;
import main.java.com.team3335.butterfly.vision.Limelight;
import main.java.com.team3335.lib.util.ButterflyDriveHelper;
import main.java.com.team3335.lib.util.LatchedBoolean;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Robot extends IterativeRobot {
    private Looper mEnabledLooper = new Looper();
    private Looper mDisabledLooper = new Looper();
    private ButterflyDriveHelper mButterflyDriveHelper = new ButterflyDriveHelper();
    private IControlBoard mControlBoard = ControlBoard.getInstance();
 
    private Limelight mLimelight = Limelight.getInstance();
    
    private final SubsystemManager mSubsystemManager = new SubsystemManager(
            Arrays.asList(
                    Drivetrain.getInstance()
            )
    );

    private Drivetrain mDrivetrain = Drivetrain.getInstance();
    
    private LatchedBoolean mToggleDriveModeStatePressed = new LatchedBoolean();
    private LatchedBoolean mToggleDrivetrainWheelStatePressed = new LatchedBoolean();
    private LatchedBoolean mToggleBrakePressed = new LatchedBoolean();
    
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
    
    private double lastGoodS = 0;
    
    @Override
    public void teleopPeriodic() {
        SmartDashboard.putString("Match Cycle", "TELEOP");
        double timestamp = Timer.getFPGATimestamp();

        //read values periodically
        boolean v = mLimelight.isTarget();
        double x = mLimelight.getTx();
        double y = mLimelight.getTy();
        double s = mLimelight.getTs();
        double area = mLimelight.getTa();
        lastGoodS = s!=0d && s!=-90d ? s : lastGoodS; 

        //post to smart dashboard periodically
        SmartDashboard.putBoolean("Limeligh Target Aquired", v);
        SmartDashboard.putNumber("Limelight X", x);
        SmartDashboard.putNumber("Limelight Y", y);
        SmartDashboard.putNumber("Limelight S", s);
        SmartDashboard.putNumber("Limelight Area", area);
        

        double forward = mControlBoard.getDriveForward();
        double forward2 = mControlBoard.getDriveForward2();
        double sideway = mControlBoard.getDriveSideway();
        double rotation = mControlBoard.getDriveRotation();
        
        SmartDashboard.putNumber("Forward", forward);
        SmartDashboard.putNumber("Sideway", sideway);
        SmartDashboard.putNumber("Rotation", rotation);
        
        boolean tempMode = mToggleDriveModeStatePressed.update(mControlBoard.getToggleDriveMode());
        boolean tempWheel = mToggleDrivetrainWheelStatePressed.update(mControlBoard.getToggleWheelState());
        SmartDashboard.putBoolean("Drive Mode State Pressed", tempMode);
        SmartDashboard.putBoolean("Drive Wheel State Pressed", tempWheel);
        
        boolean tempBrake = mToggleBrakePressed.update(mControlBoard.getToggleBrake());
        
        DriveModeState mode = mDrivetrain.getDriveModeState();
        DrivetrainWheelState wheel = mDrivetrain.getDrivetrainWheelState();
        if(tempMode) {
        	mode = mode.next();
        }
        if(tempWheel) {
        	wheel = wheel.next();
        	mDrivetrain.setWheelState(wheel);
        }
    	SmartDashboard.putString("Temp mode", ""+mode);
    	SmartDashboard.putString("Temp wheel", ""+wheel);
    	if(v && mControlBoard.getUseAssist()) {
    		//Rotate to head on
    		//if(Math.abs(x)>.5) {
    			double horizontalScalar = Math.abs(x)>7 ? x * .05: x * .1;
    			//mDrivetrain.setOpenLoop(mButterflyDriveHelper.butterflyDrive(forward, sideway, rotation+horizontalScalar, 0, DriveModeState.MECANUM_ROBOT_RELATIVE, DrivetrainWheelState.MECANUM, true));
    		//} else {
    			double distance = mLimelight.getDistance();
    			double angle = lastGoodS<-50 ? 90-lastGoodS : -lastGoodS;
    			double sidewaysComp = distance * Math.tan(angle) * (lastGoodS<-50 ? 1 : -1);
    			double distanceComp =distance/12 - 1;
    			if(distanceComp<0) distanceComp = 0;
    			distanceComp/=100;
    			SmartDashboard.putNumber("Distance", distance);
    			SmartDashboard.putNumber("Distance Comp", distanceComp);
    			mDrivetrain.setOpenLoop(mButterflyDriveHelper.butterflyDrive(forward-distanceComp, sideway+(distance<300 ? sidewaysComp*.1 : 0), rotation+horizontalScalar, 0, DriveModeState.MECANUM_ROBOT_RELATIVE, DrivetrainWheelState.MECANUM, true, true));
    		//}
    		
    		
    	}else {
    		switch(mode) {
    			case TANK:
    				mDrivetrain.setOpenLoop(mButterflyDriveHelper.butterflyDrive(forward, forward2, rotation, 0, mode, wheel, tempBrake));
    	        	break;
    			case ARCADE:
    	        	mDrivetrain.setOpenLoop(mButterflyDriveHelper.butterflyDrive(forward, sideway, rotation, 0, mode, wheel, tempBrake));
    	        	break;
    			case MECANUM_FIELD_RELATIVE:
    			case MECANUM_ROBOT_RELATIVE:
    	        	mDrivetrain.setOpenLoop(mButterflyDriveHelper.butterflyDrive(forward, sideway, rotation, 0, mode, wheel, tempBrake));
    	        	break;    			
    		}
    	}
        
        
    }
    
    

    public void outputToSmartDashboard() {
    	mDrivetrain.outputTelemetry();
    }
    
}
