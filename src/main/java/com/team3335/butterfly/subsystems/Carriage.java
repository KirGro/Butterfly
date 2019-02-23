package com.team3335.butterfly.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.team3335.butterfly.Constants;
import com.team3335.butterfly.loops.ILooper;
import com.team3335.butterfly.loops.Loop;
import com.team3335.butterfly.subsystems.Subsystem;
import com.team3335.lib.util.ChoosableSolenoid;
import com.team3335.lib.util.ChoosableSolenoid.SolenoidState;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Carriage extends Subsystem {
	private static Carriage mInstance = new Carriage();
	
	private ChoosableSolenoid mHatchPusher, mHatchPickup;
	private SolenoidState mHatchPusherState, mHatchPickupState;
	private double mLastLaunchTime;
	private boolean mInLaunchCycle;

	private VictorSPX mRollerWheels;

	private PeriodicIO mPeriodicIO = new PeriodicIO();
	
	private final Loop mLoop = new Loop() {
        @Override
        public void onStart(double timestamp) {
            synchronized (Carriage.this) {
                //startLogging();
            }
        }

        @Override
        public void onLoop(double timestamp) {
            synchronized (Carriage.this) {
				checkPusherCycleStatus(timestamp);
				updateSolenoids();
            }
        }

        @Override
        public void onStop(double timestamp) {
            stop();
            //stopLogging();
        }
    };
    
    public static Carriage getInstance() {
    	if (mInstance == null) {
            mInstance = new Carriage();
        }
        return mInstance;
    }

	private Carriage() {
		//Create solenoids and motors
		mHatchPusher = new ChoosableSolenoid(Constants.kCarriageModule, Constants.kHatchPusher);
		mHatchPickup = new ChoosableSolenoid(Constants.kCarriageModule, Constants.kHatchPickup);

		mRollerWheels = new VictorSPX(Constants.kCarriageRollerWheelCANId);

		//Set robot intial settings
		mHatchPickupState = SolenoidState.FORCED_REVERSE;
		mHatchPusherState = SolenoidState.FORCED_REVERSE;
		pusherIn();
		pickupUp();
		mLastLaunchTime = 0;
		mInLaunchCycle = false;

		mRollerWheels.set(ControlMode.PercentOutput, 0);

	}

	/* All Solenoid Controls */

	public void launchHatch(){
		SmartDashboard.putNumber("launchHatch Last Called", Timer.getFPGATimestamp());
		if(!mInLaunchCycle) {
			mInLaunchCycle = true;
			mLastLaunchTime = Timer.getFPGATimestamp();
			if(mHatchPickupState!=SolenoidState.FORCED_REVERSE) {
				pickupUp();
			}
			pusherOut();
		}
	}

	private void checkPusherCycleStatus(double time) {
		if(mInLaunchCycle) {
			if(time >= (mLastLaunchTime + Constants.kPusherTime)) {
				pusherIn();
			}
			if(Constants.kUsePusherCooldown && time >= (mLastLaunchTime + Constants.kPusherTime + Constants.kCooldownTime)) {
				mInLaunchCycle = false;
			} else if (time >= (mLastLaunchTime + Constants.kPusherTime)){
				mInLaunchCycle = false;
			}
		}
	}

	public void pusherIn() {setPusher(SolenoidState.FORCED_REVERSE);}
	public void pusherOut() {setPusher(SolenoidState.FORCED_FORWARD);}

	private void setPusher(SolenoidState state) {
		if(mHatchPusherState != state) {
			mHatchPusherState = state;
		}	
	}

	public void pickupUp() {setPickup(SolenoidState.FORCED_REVERSE);}
	public void pickupDown() {setPickup(SolenoidState.FORCED_FORWARD);}

	private void setPickup(SolenoidState state) {
		if(mHatchPickupState != state) {
			mHatchPickupState = state;
		}	
	}

	private void updateSolenoids(){
		mHatchPusher.setState(mHatchPusherState);
		mHatchPickup.setState(mHatchPickupState);
		SmartDashboard.putNumber("updateSolenoids Last Call", Timer.getFPGATimestamp());
	}

	/* All Motor Controls */

	


	@Override
	public void outputTelemetry() {
		SmartDashboard.putString("Pusher State", mHatchPusherState.name());
		SmartDashboard.putString("Pickup State", mHatchPickupState.name());
		SmartDashboard.putBoolean("In Launch Cycle", mInLaunchCycle);
	}

	@Override
	public boolean checkSystem() {
		return false;
	}

	@Override
	public void stop() {
		pickupUp();
		pusherIn();
	}

	@Override
	public void registerEnabledLoops(ILooper enabledLooper) {
		enabledLooper.register(mLoop);
    }

	public static class PeriodicIO {
	}
}

