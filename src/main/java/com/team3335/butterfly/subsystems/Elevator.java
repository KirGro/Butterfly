package com.team3335.butterfly.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.team3335.butterfly.Constants;
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
	
	private final Loop mLoop = new Loop() {
        @Override
        public void onStart(double timestamp) {
            synchronized (Elevator.this) {
                //startLogging();
            }
        }

        @Override
        public void onLoop(double timestamp) {
            synchronized (Elevator.this) {
				double time = Timer.getFPGATimestamp();
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

		

	}

	/* All Motor Controls */

	


	@Override
	public void outputTelemetry() {
		
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

	public static class PeriodicIO {
	}
}

