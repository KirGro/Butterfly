package com.team3335.butterfly.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.Rev2mDistanceSensor;
import com.revrobotics.Rev2mDistanceSensor.RangeProfile;
import com.team3335.butterfly.Constants;
import com.team3335.butterfly.Preferences;
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
	private VictorSPX mRollerWheels;

	private PeriodicIO mPeriodicIO = new PeriodicIO();

	private Rev2mDistanceSensor cargoSensor;
	
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

		cargoSensor = new Rev2mDistanceSensor(Constants.kCarriageCargoSensorPort);
		cargoSensor.setAutomaticMode(true);
		cargoSensor.setEnabled(true);
		cargoSensor.setRangeProfile(RangeProfile.kHighSpeed);

		//Set robot intial settings
		setPushersIn();
		setArmsUp();

		mRollerWheels.setNeutralMode(NeutralMode.Brake);
		mRollerWheels.setInverted(true);
		stopRollers();



	}

	/* All Solenoid Controls */

	public void setPushersIn() {setPusher(SolenoidState.FORCED_REVERSE);}
	public void setPushersOut() {setPusher(SolenoidState.FORCED_FORWARD);}

	public void setPusher(SolenoidState state) {
		if(mPeriodicIO.pusherState != state) {
			mPeriodicIO.pusherState = state;
		}	
	}

	public void setPusher(boolean out) {
		if(out) {
			setPushersOut();
		} else {
			setPushersIn();
		}
	}
	

	public void setArmsUp() {setArms(SolenoidState.FORCED_REVERSE);}
	public void setArmsDown() {setArms(SolenoidState.FORCED_FORWARD);}

	public void setArms(SolenoidState state) {
		if(mPeriodicIO.armState != state) {
			mPeriodicIO.armState = state;
		}	
	}

	public void setArms(boolean down) {
		if(down) {
			setArmsDown();
		} else {
			setArmsUp();
		}
	}

	/* All Motor Controls */

	public void setShootForward() {setRollerPower(1);}
	public void setShootReverse() {setRollerPower(-1);}

	public void setIntakeForward() {setRollerPower(Preferences.pControlPower);}
	public void setIntakeReverse() {setRollerPower(-Preferences.pControlPower);}

	public void stopRollers() {setRollerPower(0);}

	public void setRollerPower(double power) {
		mPeriodicIO.rollerPercent = power;
	}

	public boolean getArmsDown() {
		return mPeriodicIO.armState == SolenoidState.FORCED_FORWARD;
	}

	public boolean getPushersOut() {
		return mPeriodicIO.pusherState == SolenoidState.FORCED_FORWARD;
	}

	public double getRollerPercent() {
		return mPeriodicIO.rollerPercent;
	}

    public boolean hasCargo() {
		return mPeriodicIO.laserDistance <= 12;
    }

    public boolean hasHatch() {
		return mPeriodicIO.hatchLimitClosed;
    }


	@Override
	public void outputTelemetry() {
		SmartDashboard.putString("Pusher State", mPeriodicIO.pusherState.name());
		SmartDashboard.putString("Arm State", mPeriodicIO.armState.name());
		SmartDashboard.putNumber("Roller Percent", mPeriodicIO.rollerPercent);
		SmartDashboard.putNumber("Cargo Laser Distance", mPeriodicIO.laserDistance);
	}

	@Override
	public boolean checkSystem() {
		return false;
	}

	@Override
	public void stop() {
		setArmsUp();
		setPushersIn();
		setRollerPower(0.0);
	}

	@Override
	public void registerEnabledLoops(ILooper enabledLooper) {
		enabledLooper.register(mLoop);
	}

	@Override
	public void readPeriodicInputs() {
		mPeriodicIO.laserDistance = cargoSensor.getRange();
	}
	
	@Override
	public void writePeriodicOutputs() {
		mRollerWheels.set(ControlMode.PercentOutput, mPeriodicIO.rollerPercent);
		mHatchPusher.setState(mPeriodicIO.pusherState);
		mHatchPickup.setState(mPeriodicIO.armState);
	}

	private enum CarriageControlState {
		OPEN_LOOP,
		PLANNED;

		private static CarriageControlState[] vals = values();
		
	    public CarriageControlState next() {
	        return vals[(this.ordinal()+1) % vals.length];
		}
		
		public CarriageControlState prev() {
			int p = this.ordinal()-1;
			return p>=0 ? vals[p] : vals[vals.length-1];
		}
		
    }

	public static class PeriodicIO {
		//inputs (currently not used)
		public double armsLastChanged;
		public double pushersLastChanged;
		public double laserDistance;
		public boolean hatchLimitClosed;

		//Outputs
		public double rollerPercent;
		public SolenoidState armState;
		public SolenoidState pusherState;
	}
}

