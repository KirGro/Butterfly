package com.team3335.butterfly.subsystems;

import com.team3335.butterfly.loops.ILooper;
import com.team3335.butterfly.loops.Loop;
import com.team3335.lib.util.ChoosableSolenoid;
import com.team3335.lib.util.ChoosableSolenoid.SolenoidState;

public class Carriage extends Subsystem {
    private static Carriage mInstance;
    
    private ChoosableSolenoid mHatchPusher, mHatchGrabber;
    
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

    public Carriage(){
        mHatchPusher = new ChoosableSolenoid(1, 2);
        mHatchGrabber = new ChoosableSolenoid(1, 3);

        setGrabberState(SolenoidState.FORCED_REVERSE);
        setPusherState(SolenoidState.FORCED_REVERSE);
    }

    public void togglePusherState() {
        mPeriodicIO.hatchPusherState.toggle();
    }

    public void toggleGrabberState() {
        mPeriodicIO.hatchGrabberState.toggle();
    }

    public void setGrabberState(SolenoidState state) {
        mPeriodicIO.hatchGrabberState = state;
    }

    public void setPusherState(SolenoidState state) {
        mPeriodicIO.hatchPusherState = state;
    }

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

    @Override
    public void readPeriodicInputs() {
        //mPeriodicIO.laserDistance = mDistanceSensor.getValue();
        //TODO DISTANCE AND TRIGGER STUFF
    }

    @Override
    public void writePeriodicOutputs() {
        mHatchGrabber.setState(mPeriodicIO.hatchGrabberState);
        mHatchPusher.setState(mPeriodicIO.hatchPusherState);
    }

    public static class PeriodicIO {
        //inputs
        public double distance;
        public boolean hatchPusherExtended;

        //outputs
        public SolenoidState hatchPusherState;
        public SolenoidState hatchGrabberState;
    }
}
