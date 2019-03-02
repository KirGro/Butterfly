package com.team3335.butterfly.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.team3335.butterfly.Constants;
import com.team3335.butterfly.Preferences;
import com.team3335.butterfly.loops.ILooper;
import com.team3335.butterfly.loops.Loop;
import com.team3335.butterfly.states.CarriageState;
import com.team3335.butterfly.states.CarriageState.ArmAction;
import com.team3335.butterfly.states.CarriageState.RollerAction;
import com.team3335.butterfly.states.CarriageState.RollerWheelState;
import com.team3335.butterfly.subsystems.Subsystem;
import com.team3335.lib.util.ChoosableSolenoid;
import com.team3335.lib.util.ChoosableSolenoid.SolenoidState;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Carriage extends Subsystem {
	private static Carriage mInstance = new Carriage();
	
	private ChoosableSolenoid mHatchPusher, mHatchPickup;
	private SolenoidState mHatchPusherState, mHatchPickupState;
	//private double mLastLaunchTime;
	//private boolean mInLaunchCycle;

	private VictorSPX mRollerWheels;
	private RollerWheelState mRollerWheelState;
	private RollerAction mRollerAction;
	private ArmAction mArmAction;
	private boolean mInAction;
	private double mActionStartTime;

	private PeriodicIO mPeriodicIO = new PeriodicIO();

	private CarriageState mCurrentState;
	
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

		mRollerWheels.setNeutralMode(NeutralMode.Brake);
		mRollerWheels.setInverted(true);
		stopRollers();
		mRollerAction = RollerAction.NONE;
		mRollerWheelState = RollerWheelState.OFF;
		mArmAction = ArmAction.NONE;
		mActionStartTime = -1;
		mInAction = false;


	}

	/* All Solenoid Controls */

	public void placeHatchCargoship(){
		if(mArmAction == ArmAction.NONE) {
			mInAction = true;
			mArmAction = ArmAction.CARGOSHIP_PLACING;
			mActionStartTime = Timer.getFPGATimestamp();
			pickupUp();
			pusherOut();
		}
	}

	public void habPickup() {
		if(mArmAction == ArmAction.NONE) {
			mInAction = true;
			mArmAction = ArmAction.HAB_PICKUP;
			mActionStartTime = Timer.getFPGATimestamp();
			pusherIn();
			pickupDown();
		}
	}

	private void checkPusherCycleStatus(double time) {
		if(mArmAction == ArmAction.CARGOSHIP_PLACING) {
			if(time >= (mActionStartTime + Preferences.kPusherTime)) {
				pusherIn();
			}
			if(Preferences.kUsePusherCooldown && time >= (mActionStartTime + Preferences.kPusherTime + Preferences.kCooldownTime)) {
				mInAction = false;
				mArmAction = ArmAction.NONE;
			} else if (time >= (mActionStartTime + Preferences.kPusherTime)){
				mInAction = false;
				mArmAction = ArmAction.NONE;
			}
		} else if(mArmAction == ArmAction.HAB_PICKUP) {
			if(time >= (mActionStartTime+Preferences.kHabPickupDelay)) {
				pickupUp();
				mInAction = false;
				mArmAction = ArmAction.NONE;
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

	public void grabCargo() {
		if(!mInAction) {
			mInAction = true;
			mActionStartTime = Timer.getFPGATimestamp();

		}
	}

	public void setShootForward() {setRollerPower(1);}
	public void setShootReverse() {setRollerPower(-1);}

	public void setIntakeForward() {setRollerPower(Preferences.pControlPower);}
	public void setIntakeReverse() {setRollerPower(-Preferences.pControlPower);}

	public void stopRollers() {setRollerPower(0);}

	private void setRollerPower(double power) {
		mRollerWheels.set(ControlMode.PercentOutput, power);
	}


	@Override
	public void outputTelemetry() {
		SmartDashboard.putString("Pusher State", mHatchPusherState.name());
		SmartDashboard.putString("Pickup State", mHatchPickupState.name());
		//SmartDashboard.putBoolean("In Launch Cycle", mInLaunchCycle);
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

