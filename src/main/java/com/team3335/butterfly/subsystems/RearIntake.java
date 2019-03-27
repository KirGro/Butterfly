package com.team3335.butterfly.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.Rev2mDistanceSensor;
import com.revrobotics.Rev2mDistanceSensor.RangeProfile;

import java.nio.ByteBuffer;
import com.team3335.butterfly.Constants;
import com.team3335.butterfly.Preferences;
import com.team3335.butterfly.loops.ILooper;
import com.team3335.butterfly.loops.Loop;
import com.team3335.butterfly.subsystems.Subsystem;
import com.team3335.lib.util.LatchedBoolean;
import com.team3335.lib.util.Util;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RearIntake extends Subsystem {
    private static RearIntake mInstance = new RearIntake();

	private TalonSRX mArmMaster;
    private VictorSPX mArmSlave1;
    private VictorSPX mRollerWheels;

    private SensorCollection mTalonSensors;
	private Rev2mDistanceSensor cargoSensor;

    private PeriodicIO mPeriodicIO = new PeriodicIO();
    
	private boolean mZeroingSensors = false;
	private double mLastSeenArm = Double.NaN;
	private LatchedBoolean mJustCaughtArm = new LatchedBoolean();
	private boolean hasBeenZeroed = false;

    private RearIntakeControlState mRearIntakeControlState = RearIntakeControlState.OPEN_LOOP;
	
	private final Loop mLoop = new Loop() {
        @Override
        public void onStart(double timestamp) {
            synchronized (RearIntake.this) {
            }
        }

        @Override
        public void onLoop(double timestamp) {
            synchronized (RearIntake.this) {
                //RearIntakeState newState = mRearIntakeStateMachine.update(Timer.getFPGATimestamp(), mWantedAction, getCurrentRearIntakeState());
            }
        }

        @Override
        public void onStop(double timestamp) {
            stop();
            //stopLogging();
        }
    };
    
    public static RearIntake getInstance() {
    	if (mInstance == null) {
            mInstance = new RearIntake();
        }
        return mInstance;
    }

	private RearIntake() {
		//Create motors
		mArmMaster = new TalonSRX(Constants.kMasterArmCANId);
        mArmSlave1 = new VictorSPX(Constants.kSlave1ArmCANId);
        mRollerWheels = new VictorSPX(Constants.kRearRollerWheelCANId);

        mTalonSensors = mArmMaster.getSensorCollection();
		cargoSensor = new Rev2mDistanceSensor(Constants.kRearCargoSensorPort);
		cargoSensor.setAutomaticMode(true);
		cargoSensor.setEnabled(true);
		cargoSensor.setRangeProfile(RangeProfile.kHighSpeed);
		//Set robot intial settings
        mArmSlave1.follow(mArmMaster);
        mArmSlave1.setInverted(true);
		
		mArmMaster.config_kP(0, Preferences.kDriveP, Constants.kLongCANTimeoutMs);
		mArmMaster.config_kI(0, Preferences.kDriveI, Constants.kLongCANTimeoutMs);
		mArmMaster.config_kD(0, Preferences.kDriveD, Constants.kLongCANTimeoutMs);
		mArmMaster.config_kF(0, 0, Constants.kLongCANTimeoutMs);
        mArmMaster.config_IntegralZone(0, 0, Constants.kLongCANTimeoutMs);
        
        mArmMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 100);
        mArmMaster.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
        
        mArmMaster.configForwardLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled);

        mArmMaster.configForwardSoftLimitEnable(true);
        mArmMaster.configForwardSoftLimitThreshold((int) (Constants.kSRXEncoderCPR / Constants.kRearEncoderToOutputRatio * Constants.kRearMaxAngle / 360));
        mArmMaster.configReverseSoftLimitEnable(true);
        mArmMaster.configReverseSoftLimitThreshold((int) (Constants.kSRXEncoderCPR / Constants.kRearEncoderToOutputRatio * Constants.kRearMinAngle / 360));
		
        mRearIntakeControlState = RearIntakeControlState.POSITION_PID; //TODO CHANGE

    }

	/* All Motor Controls */
    public synchronized void setOpenLoop(double percentage) {
        if(mRearIntakeControlState != RearIntakeControlState.OPEN_LOOP) {
            mRearIntakeControlState = RearIntakeControlState.OPEN_LOOP;
        }
        mPeriodicIO.armPercentOutput = percentage;
    }

    public synchronized void setMotionMagicPosition(double angle) {
        int encoderPosition = (int) Math.round(angle / Constants.kRearEncoderToOutputRatio);
        if(mRearIntakeControlState != RearIntakeControlState.MOTION_MAGIC) {
			mRearIntakeControlState = RearIntakeControlState.MOTION_MAGIC;
		}
		mPeriodicIO.encoderTarget = encoderPosition;
    }

    public synchronized void setPositionPID(double angle) {
        int encoderPosition = (int) Math.round(angle / Constants.kRearEncoderToOutputRatio);
        if (mRearIntakeControlState != RearIntakeControlState.POSITION_PID) {
            mRearIntakeControlState = RearIntakeControlState.POSITION_PID;
        }
        mPeriodicIO.encoderTarget = encoderPosition;
    }

    
	public void setRollerPower(double power) {
		mPeriodicIO.rollerPercentOutput = power;
	}

    //Sensor returns

    public double getAngle() {
        return mPeriodicIO.encoderPosition * Constants.kRearEncoderToOutputRatio;
    }

    public double getRollerPercent() {
        return mPeriodicIO.rollerPercentOutput;
    }
    
    public synchronized boolean hasFinishedTrajectory() {
        return mRearIntakeControlState == RearIntakeControlState.MOTION_MAGIC &&
                Util.epsilonEquals(mPeriodicIO.activeTrajectoryPosition, mPeriodicIO.encoderTarget, 5);
    }

    public boolean hasCargo() {
        return mPeriodicIO.laserDistance <= 14;
    }

	@Override
	public synchronized void zeroSensors() {
        if(!mZeroingSensors) {
            mZeroingSensors = true;
        }
	}


	@Override
	public void outputTelemetry() {
		SmartDashboard.putNumber("Arm Encoder", mPeriodicIO.encoderPosition);
        SmartDashboard.putNumber("Rear Laser Distance", mPeriodicIO.laserDistance);
        SmartDashboard.putBoolean("Zeroing Elevator", mZeroingSensors);
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
			if(mJustCaughtArm.update(mPeriodicIO.forwardLimitClosed)) {
				mArmMaster.setSelectedSensorPosition(0, 0, 0);
				mArmMaster.set(ControlMode.PercentOutput, 0);
                mZeroingSensors = false;
                hasBeenZeroed = true;
			} else if(Math.abs(Timer.getFPGATimestamp()-mLastSeenArm)<=.12) {
				mArmMaster.set(ControlMode.PercentOutput, .2);
			} else {
				mArmMaster.set(ControlMode.PercentOutput, -.05);
			} 
		}else {
			switch (mRearIntakeControlState) {
				case MOTION_MAGIC:
                    mArmMaster.set(ControlMode.MotionMagic, mPeriodicIO.encoderTarget);
					break;
				case POSITION_PID:
					mArmMaster.set(ControlMode.Position, mPeriodicIO.encoderTarget);
					break;
				case OPEN_LOOP:
					mArmMaster.set(ControlMode.PercentOutput, mPeriodicIO.armPercentOutput);
					break;
				default:
					mArmMaster.set(ControlMode.PercentOutput, 0);
			}
		}
        mRollerWheels.set(ControlMode.PercentOutput, mPeriodicIO.rollerPercentOutput);
	}

	@Override
	public void readPeriodicInputs() {
        mPeriodicIO.encoderPosition = mArmMaster.getSelectedSensorPosition();
        mPeriodicIO.encoderVelocity = mArmMaster.getSelectedSensorVelocity();
		if(mRearIntakeControlState == RearIntakeControlState.MOTION_MAGIC) {
            mPeriodicIO.activeTrajectoryPosition = mArmMaster.getActiveTrajectoryPosition();
            mPeriodicIO.activeTrajectoryVelocity = mArmMaster.getActiveTrajectoryVelocity();
        }
        mPeriodicIO.forwardLimitClosed = mTalonSensors.isFwdLimitSwitchClosed();
        mPeriodicIO.laserDistance = cargoSensor.getRange();
        
    }
    
    private enum RearIntakeControlState {
        OPEN_LOOP,
        MOTION_MAGIC,
		POSITION_PID;

		private static RearIntakeControlState[] vals = values();
	    public RearIntakeControlState next() {
	        return vals[(this.ordinal()+1) % vals.length];
		}
		
		public RearIntakeControlState prev() {
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
        public double laserDistance;

		//Outputs
		public int encoderTarget;
        public double armPercentOutput = 0;
        public double rollerPercentOutput = 0;
	}
}