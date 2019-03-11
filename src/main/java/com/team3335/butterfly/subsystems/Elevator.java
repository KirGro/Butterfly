package com.team3335.butterfly.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.FollowerType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.team3335.butterfly.Constants;
import com.team3335.butterfly.Preferences;
import com.team3335.butterfly.loops.ILooper;
import com.team3335.butterfly.loops.Loop;
import com.team3335.butterfly.states.ElevatorState.ElevatorAction;
import com.team3335.butterfly.subsystems.Subsystem;
import com.team3335.lib.util.LatchedBoolean;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Elevator extends Subsystem {
	private static Elevator mInstance = new Elevator();

	private TalonSRX mWinchMaster;
	private VictorSPX mWinchSlave1;

	private SensorCollection mTalonSensors;

	private boolean mZeroingSensors = false;
	private double mLastSeenCarriage = Double.NaN;
	private LatchedBoolean mJustCaughtCarriage = new LatchedBoolean();

	private PeriodicIO mPeriodicIO = new PeriodicIO();

	public boolean mClosedLoop; //TODO Make private later
	
	private final Loop mLoop = new Loop() {
        @Override
        public void onStart(double timestamp) {
            synchronized (Elevator.this) {
            }
        }

        @Override
        public void onLoop(double timestamp) {
            synchronized (Elevator.this) {
				if(mPeriodicIO.forwardLimitClosed) {
					mLastSeenCarriage = Timer.getFPGATimestamp();
				}

            }
        }

        @Override
        public void onStop(double timestamp) {
            stop();
            //stopLogging();
        }
    };
    
    public static Elevator getInstance() {
    	if (mInstance == null) {
            mInstance = new Elevator();
        }
        return mInstance;
    }

	private Elevator() {
		//Create motors
		mWinchMaster = new TalonSRX(Constants.kMasterWinchCANId);
		mWinchSlave1 = new VictorSPX(Constants.kSlave1WinchCANId);

		mTalonSensors = mWinchMaster.getSensorCollection();

		//Set robot intial settings
		mWinchSlave1.follow(mWinchMaster);
		mWinchMaster.setInverted(true);
		mWinchSlave1.setInverted(true);

		mWinchMaster.setNeutralMode(NeutralMode.Brake);
		mWinchSlave1.setNeutralMode(NeutralMode.Brake);

		mWinchMaster.configNeutralDeadband(.001);
		mWinchSlave1.configNeutralDeadband(.001);
		
		mWinchMaster.config_kP(0, .05, Constants.kLongCANTimeoutMs);
		mWinchMaster.config_kI(0, 0, Constants.kLongCANTimeoutMs);
		mWinchMaster.config_kD(0, 30, Constants.kLongCANTimeoutMs);
		mWinchMaster.config_kF(0, 0, Constants.kLongCANTimeoutMs);
		mWinchMaster.config_IntegralZone(0, 0, Constants.kLongCANTimeoutMs);
		mWinchMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 100);

		mWinchMaster.configForwardSoftLimitEnable(true);
		mWinchMaster.configForwardSoftLimitThreshold(convertHeightToEncoderTarget(Constants.kElevatorRelativeMaxHeight));
		mWinchMaster.configReverseSoftLimitEnable(true);
		mWinchMaster.configReverseSoftLimitThreshold(convertHeightToEncoderTarget(Constants.kElevatorRelativeMinHeight));

		//MotionMagic junk
		mWinchMaster.configMaxIntegralAccumulator(0, 100000, Constants.kLongCANTimeoutMs);
		mWinchMaster.config_IntegralZone(0, 0, Constants.kLongCANTimeoutMs);
		mWinchMaster.configAllowableClosedloopError(0, 0, Constants.kLongCANTimeoutMs);
		mWinchMaster.configMotionAcceleration(15000, Constants.kLongCANTimeoutMs); //254 had 27000 
		mWinchMaster.configMotionCruiseVelocity(12500, Constants.kLongCANTimeoutMs); //254 had 12500
		mWinchMaster.configClosedloopRamp(.3, Constants.kLongCANTimeoutMs); //254 had .1
		mWinchMaster.configContinuousCurrentLimit(20, Constants.kLongCANTimeoutMs); //254 had 20
		mWinchMaster.configPeakCurrentLimit(35, Constants.kLongCANTimeoutMs); //254 had 35
		mWinchMaster.configPeakCurrentDuration(200, Constants.kLongCANTimeoutMs); //254 had 200
		mWinchMaster.enableCurrentLimit(true);

		mClosedLoop = true;
		
	}

	/* All Motor Controls */

	public void driveRaw(double percent) {
		mPeriodicIO.percentOutput = percent;
	}


	public void setEncoderTarget(int position) {
		mPeriodicIO.encoderTarget = position;
	} 

	public void setHeightRobot(double height) {
		setEncoderTarget(convertHeightToEncoderTarget(height + Constants.kElevatorRelativeMinHeight));
	}

	public void setHeightFloor(double height) {
		setHeightRobot(height - Constants.kElevatorMinHeight);
	}

	public int convertHeightToEncoderTarget(double height) {
		return (int) Math.round(height * Constants.kElevatorTicksPerInch);
		//System.out.println("Im trying really hard, h: "+height+ ", CPR: "+Constants.kSRXEncoderCPR+", Drum: "+Constants.kElevatorDrumDiameter+", Ratio: "+Constants.kEGearRatio2);
	}

	public int getEncoderPosition() {
		return mPeriodicIO.encoderPosition;
	}

	public double getInchesOffGround() {
		return (mPeriodicIO.encoderPosition/Constants.kElevatorTicksPerInch) - Constants.kElevatorRelativeMinHeight + Constants.kElevatorMinHeight; 
	}

	@Override
	public void zeroSensors() {
		mZeroingSensors = true;
	}


	@Override
	public void outputTelemetry() {
		SmartDashboard.putNumber("Elevator Encoder", mPeriodicIO.encoderPosition);
		SmartDashboard.putBoolean("Elevator Closed Loop", mClosedLoop);
		SmartDashboard.putNumber("Rere target ", mPeriodicIO.encoderTarget);
	}

	@Override
	public boolean checkSystem() {
		return false;
	}

	@Override
	public void stop() {
	}

	@Override
	public void registerEnabledLoops(ILooper enabledLooper) {
		enabledLooper.register(mLoop);
	}
	
	@Override
	public void writePeriodicOutputs() {
		if(mZeroingSensors) {
			if(mJustCaughtCarriage.update(mPeriodicIO.forwardLimitClosed)) {
				mWinchMaster.setSelectedSensorPosition(0, 0, 0);
				mWinchMaster.set(ControlMode.PercentOutput, 0);
				mZeroingSensors = false;
			} else if(Math.abs(Timer.getFPGATimestamp()-mLastSeenCarriage)<=.12) {
				mWinchMaster.set(ControlMode.PercentOutput, .2);
			} else {
				mWinchMaster.set(ControlMode.PercentOutput, .05);
			} 
		}else if(mClosedLoop) {
			mWinchMaster.set(ControlMode.MotionMagic, mPeriodicIO.encoderTarget);
		} else {
			mWinchMaster.set(ControlMode.PercentOutput, mPeriodicIO.percentOutput);
		}
	}

	@Override
	public void readPeriodicInputs() {
		mPeriodicIO.encoderPosition = mWinchMaster.getSelectedSensorPosition();
		mPeriodicIO.forwardLimitClosed = mTalonSensors.isFwdLimitSwitchClosed();
	}

	public static class PeriodicIO {
		//Inputs
		public int encoderPosition = 0;
		public boolean forwardLimitClosed = false;

		//Outputs
		public int encoderTarget;
		public double percentOutput;
	}
}

