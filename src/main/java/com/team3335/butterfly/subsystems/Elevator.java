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
import com.team3335.butterfly.subsystems.Subsystem;
import com.team3335.lib.util.LatchedBoolean;
import com.team3335.lib.util.Util;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.RobotDriveBase.MotorType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Elevator extends Subsystem {
	private static Elevator mInstance = new Elevator();

	private TalonSRX mWinchMaster;
	private VictorSPX mWinchSlave1;

	private SensorCollection mTalonSensors;

	private boolean mZeroingSensors = false, mSeenAtZeroingStart = false;
	private LatchedBoolean mJustCaughtCarriage = new LatchedBoolean();
	private boolean hasBeenZeroed = false;

	private PeriodicIO mPeriodicIO = new PeriodicIO();


	private ElevatorControlState mElevatorControlState = ElevatorControlState.OPEN_LOOP; //TODO Make private later
	
	private final Loop mLoop = new Loop() {
        @Override
        public void onStart(double timestamp) {
            synchronized (Elevator.this) {
            }
        }

        @Override
        public void onLoop(double timestamp) {
            synchronized (Elevator.this) {

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
		mWinchMaster.setInverted(false);
		mWinchSlave1.setInverted(false);

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
		mWinchMaster.overrideSoftLimitsEnable(false);

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

		mElevatorControlState = ElevatorControlState.OPEN_LOOP;
		
	}

	/* All Motor Controls */

    public synchronized void setOpenLoop(double percentage) {
        if(mElevatorControlState != ElevatorControlState.OPEN_LOOP) {
			mElevatorControlState = ElevatorControlState.OPEN_LOOP;
		}
        mPeriodicIO.percentOutput = percentage>.04 ? percentage : 0;
    }

    public synchronized void setMotionMagicPosition(double positionInchesOffGround) {
        double positionInchesFromZero = positionInchesOffGround - Constants.kElevatorMinHeight;
        int encoderPosition = (int) Math.round(positionInchesFromZero * Constants.kElevatorTicksPerInch);
        if(mElevatorControlState != ElevatorControlState.MOTION_MAGIC) {
			mElevatorControlState = ElevatorControlState.MOTION_MAGIC;
		}
		mPeriodicIO.encoderTarget = encoderPosition;
    }

    public synchronized void setPositionPID(double positionInchesOffGround) {
        double positionInchesFromZero = positionInchesOffGround - Constants.kElevatorMinHeight;
        int encoderPosition = (int) Math.round(positionInchesFromZero * Constants.kElevatorTicksPerInch);
        if (mElevatorControlState != ElevatorControlState.POSITION_PID) {
            mElevatorControlState = ElevatorControlState.POSITION_PID;
        }
        mPeriodicIO.encoderTarget = encoderPosition;
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

	
    public synchronized boolean hasFinishedTrajectory() {
        return mElevatorControlState == ElevatorControlState.MOTION_MAGIC &&
                Util.epsilonEquals(mPeriodicIO.activeTrajectoryPosition, mPeriodicIO.encoderTarget, 5);
    }

	@Override
	public void zeroSensors() {
		if(!mZeroingSensors) {
			mZeroingSensors = true;
			mSeenAtZeroingStart = mPeriodicIO.forwardLimitClosed;
		}
	}


	@Override
	public void outputTelemetry() {
		SmartDashboard.putNumber("Elevator Encoder", mPeriodicIO.encoderPosition);
		SmartDashboard.putString("Elevator Control State", mElevatorControlState.toString());
		SmartDashboard.putNumber("Elevator Target", mPeriodicIO.encoderTarget);
		SmartDashboard.putBoolean("Carriage Seen", mPeriodicIO.forwardLimitClosed);
		SmartDashboard.putNumber("Elevator Percent Output", mPeriodicIO.percentOutput);
	}

	@Override
	public boolean checkSystem() {
		return false;
	}

	@Override
	public void stop() {
		setOpenLoop(0.0);
	}

	@Override
	public void registerEnabledLoops(ILooper enabledLooper) {
		enabledLooper.register(mLoop);
	}
	
	@Override
	public void writePeriodicOutputs() {
		if(mZeroingSensors) {
			if(mSeenAtZeroingStart && Math.abs(Timer.getFPGATimestamp()-mPeriodicIO.lastSeenCarriage)<=.24) {
				mWinchMaster.set(ControlMode.PercentOutput, .1);
			} else if(mJustCaughtCarriage.update(mPeriodicIO.forwardLimitClosed)) {
				mWinchMaster.setSelectedSensorPosition(0, 0, 0);
				mWinchMaster.set(ControlMode.PercentOutput, 0);
				mZeroingSensors = false;
				hasBeenZeroed = true;
				mWinchMaster.configForwardSoftLimitEnable(true);
				mWinchMaster.configReverseSoftLimitEnable(true);
			} else {
				mWinchMaster.set(ControlMode.PercentOutput, -.05);
			} 
		}else if(hasBeenZeroed){
			switch (mElevatorControlState) {
				case MOTION_MAGIC:
					mWinchMaster.set(ControlMode.MotionMagic, mPeriodicIO.encoderTarget);
					break;
				case POSITION_PID:
					mWinchMaster.set(ControlMode.Position, mPeriodicIO.encoderTarget);
					break;
				case OPEN_LOOP:
					mWinchMaster.set(ControlMode.PercentOutput, mPeriodicIO.percentOutput);
					break;
				default:
					mWinchMaster.set(ControlMode.PercentOutput, 0);
			}
		}
	}

	@Override
	public void readPeriodicInputs() {
		mPeriodicIO.encoderPosition = mWinchMaster.getSelectedSensorPosition();
		mPeriodicIO.encoderVelocity = mWinchMaster.getSelectedSensorVelocity();
		if(mElevatorControlState == ElevatorControlState.MOTION_MAGIC) {
			mPeriodicIO.activeTrajectoryPosition = mWinchMaster.getActiveTrajectoryPosition();
			mPeriodicIO.activeTrajectoryVelocity = mWinchMaster.getActiveTrajectoryVelocity();
		}
		mPeriodicIO.forwardLimitClosed = mTalonSensors.isFwdLimitSwitchClosed();
		mPeriodicIO.lastSeenCarriage = mPeriodicIO.forwardLimitClosed ? Timer.getFPGATimestamp() : mPeriodicIO.lastSeenCarriage;
	}


    private enum ElevatorControlState {
        OPEN_LOOP,
        MOTION_MAGIC,
		POSITION_PID;

		private static ElevatorControlState[] vals = values();
		
	    public ElevatorControlState next() {
	        return vals[(this.ordinal()+1) % vals.length];
		}
		
		public ElevatorControlState prev() {
			int p = this.ordinal()-1;
			return p>=0 ? vals[p] : vals[vals.length-1];
		}
		
    }

	public static class PeriodicIO {
		//Inputs
		public int encoderPosition;
		public int encoderVelocity;
		public int activeTrajectoryVelocity;
		public int activeTrajectoryPosition;
		public boolean forwardLimitClosed;
		public double lastSeenCarriage;

		//Outputs
		public int encoderTarget;
		public double percentOutput;
	}
}

