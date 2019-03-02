package com.team3335.butterfly.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.team3335.butterfly.Constants;
import com.team3335.butterfly.Preferences;
import com.team3335.butterfly.loops.ILooper;
import com.team3335.butterfly.loops.Loop;
import com.team3335.butterfly.subsystems.Subsystem;
import com.team3335.lib.util.ChoosableSolenoid;
import com.team3335.lib.util.ChoosableSolenoid.SolenoidState;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Elevator extends Subsystem {
	private static Elevator mInstance = new Elevator();

	private TalonSRX mWinchMaster;
	private VictorSPX mWinchSlave1;

	private PeriodicIO mPeriodicIO = new PeriodicIO();

	private boolean mClosedLoop;
	
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
		//Set robot intial settings
		mWinchSlave1.follow(mWinchMaster);
		mWinchMaster.setInverted(true);
		mWinchSlave1.setInverted(true);
		
		mWinchMaster.config_kP(0, Preferences.kDriveP, Constants.kLongCANTimeoutMs);
		mWinchMaster.config_kI(0, Preferences.kDriveI, Constants.kLongCANTimeoutMs);
		mWinchMaster.config_kD(0, Preferences.kDriveD, Constants.kLongCANTimeoutMs);
		mWinchMaster.config_kF(0, 0, Constants.kLongCANTimeoutMs);
		mWinchMaster.config_IntegralZone(0, 0, Constants.kLongCANTimeoutMs);
		mWinchMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 100);
		
	}

	/* All Motor Controls */

	public void driveRaw(double percent) {
		mWinchMaster.set(ControlMode.PercentOutput, percent/2);
	}


	public void setEncoderTarget(int position) {
		mPeriodicIO.encoderTarget = position;
	} 

	private int getEncoderPosition() {
		return mPeriodicIO.encoderPosition;
	}

	@Override
	public void zeroSensors() {
		mWinchMaster.setSelectedSensorPosition(0, 0, 0);
	}


	@Override
	public void outputTelemetry() {
		SmartDashboard.putNumber("Elevator Encoder", getEncoderPosition());
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
			mWinchMaster.set(ControlMode.Position, mPeriodicIO.encoderTarget);
		} else {
			mWinchMaster.set(ControlMode.PercentOutput, mPeriodicIO.percentOutput);
		}
	}

	@Override
	public void readPeriodicInputs() {
		mPeriodicIO.encoderPosition = mWinchMaster.getSelectedSensorPosition();
	}

	public static class PeriodicIO {
		//Inputs
		public int encoderPosition;

		//Outputs
		public int encoderTarget;
		public double percentOutput;
	}
}

