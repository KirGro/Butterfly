package com.team3335.butterfly.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import java.nio.ByteBuffer;
import com.team3335.butterfly.Constants;
import com.team3335.butterfly.Preferences;
import com.team3335.butterfly.loops.ILooper;
import com.team3335.butterfly.loops.Loop;
import com.team3335.butterfly.statemachines.RearIntakeStateMachine;
import com.team3335.butterfly.statemachines.RearIntakeStateMachine.WantedAction;
import com.team3335.butterfly.states.RearIntakeState;
import com.team3335.butterfly.subsystems.Subsystem;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RearIntake extends Subsystem {
    private static RearIntake mInstance = new RearIntake();
    
    private RearIntakeStateMachine mRearIntakeStateMachine = new RearIntakeStateMachine();
    private RearIntakeState mCurrentState = new RearIntakeState();
    private WantedAction mWantedAction = WantedAction.PLANNED;

	private TalonSRX mArmMaster;
    private VictorSPX mArmSlave1;
    private VictorSPX mRollerWheels;

    private SensorCollection mTalonSensors;
    private I2C mRearCargoSensor;

	private PeriodicIO mPeriodicIO = new PeriodicIO();

	private boolean mClosedLoop;
	
	private final Loop mLoop = new Loop() {
        @Override
        public void onStart(double timestamp) {
            synchronized (RearIntake.this) {
            }
        }

        @Override
        public void onLoop(double timestamp) {
            synchronized (RearIntake.this) {
                RearIntakeState newState = mRearIntakeStateMachine.update(Timer.getFPGATimestamp(), mWantedAction, getCurrentRearIntakeState());
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
        mRearCargoSensor = new I2C(Constants.kRearCargoSensorPort, 0x52);
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
		
		mClosedLoop = true;
    }
    
    public RearIntakeState getCurrentRearIntakeState() {
        mCurrentState.mArmEncoderPosition = mPeriodicIO.encoderPosition;
        mCurrentState.mArmEncoderSetpoint = mPeriodicIO.encoderTarget;
        mCurrentState.mArmPercent = mPeriodicIO.armPercentOutput;
        mCurrentState.mRollerPercent = mPeriodicIO.rollerPercentOutput;
        mCurrentState.mLaserBroken = mPeriodicIO.laserBroken;
        return mCurrentState;
    }

    public void updateComponentsFromState(RearIntakeState newState) {
        if(mWantedAction == WantedAction.PLANNED) {

        }
    }

	/* All Motor Controls */

	public void driveRaw(double percent) {
		mPeriodicIO.armPercentOutput = percent/5;
	}


	public void setEncoderTarget(int position) {
		mPeriodicIO.encoderTarget = position;
	} 

	@Override
	public synchronized void zeroSensors() {
        while(mTalonSensors.isFwdLimitSwitchClosed()) {
            mArmMaster.set(ControlMode.PercentOutput, .2);
        }
        try {
            Thread.sleep(125);
        } catch (InterruptedException e) { }
        
        while(!mTalonSensors.isFwdLimitSwitchClosed()) {
            mArmMaster.set(ControlMode.PercentOutput, .08);
        }
        mArmMaster.set(ControlMode.PercentOutput, 0);
		mArmMaster.setSelectedSensorPosition(0, 0, 0);
	}


	@Override
	public void outputTelemetry() {
		SmartDashboard.putNumber("Arm Encoder", mPeriodicIO.encoderPosition);
        SmartDashboard.putNumber("Rear Laser Distance", mPeriodicIO.laserDistance);

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
		if(mClosedLoop) {
			mArmMaster.set(ControlMode.Position, mPeriodicIO.encoderTarget);
		} else {
			mArmMaster.set(ControlMode.PercentOutput, mPeriodicIO.armPercentOutput);
        }
        mRollerWheels.set(ControlMode.PercentOutput, mPeriodicIO.rollerPercentOutput);
	}

	@Override
	public void readPeriodicInputs() {
        mPeriodicIO.encoderPosition = mArmMaster.getSelectedSensorPosition();
        byte[] temp = new byte[1];
        mRearCargoSensor.read(0x52, 1, temp);
        ByteBuffer buffer = ByteBuffer.wrap(temp); //turns the byte value into a double
        mPeriodicIO.laserDistance = buffer.getDouble();
        mPeriodicIO.laserBroken = mPeriodicIO.laserDistance < 13;
	}

	public static class PeriodicIO {
		//Inputs
        public int encoderPosition;
        public double laserDistance;
        public boolean laserBroken;

		//Outputs
		public int encoderTarget;
        public double armPercentOutput;
        public double rollerPercentOutput;
	}
}