package com.team3335.butterfly.subsystems;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.team3335.butterfly.loops.ILooper;
import com.team3335.butterfly.loops.Loop;
import com.team3335.butterfly.vision.*;

/**
 * Wrapper class for getting and setting Limelight NetworkTable values.
 * 
 */

public class Limelight extends Subsystem implements IVisionTarget{
	private NetworkTableInstance table = null;
	private static Limelight mInstance;

	private IVisionTarget[] mTargets = new IVisionTarget[10];

	private PeriodicIO mPeriodicIO = new PeriodicIO();

	private LightMode mLightMode;
	private CameraMode mCameraMode;
	private StreamMode mStreamMode;
	private Target mTargetSelected;
	
	private final Loop mLoop = new Loop() {
        @Override
        public void onStart(double timestamp) {
            synchronized (Limelight.this) {
				//startLogging();
            }
        }

        @Override
        public void onLoop(double timestamp) {
            synchronized (Limelight.this) {
            }
        }

        @Override
        public void onStop(double timestamp) {
            stop();
            //stopLogging();
        }
    };
	
	public static Limelight getInstance() {
		if (mInstance == null) {
            mInstance = new Limelight();
        }
        return mInstance;
    }
	
	private Limelight() {
		//Set up table and game targets
		table = NetworkTableInstance.getDefault();
		mTargets[Target.HATCH.ordinal()] = new HatchTarget();

		//Set default values
		mTargetSelected = Target.HATCH;
		mLightMode = LightMode.DEFAULT;
		mCameraMode = CameraMode.VISION;
		mStreamMode = StreamMode.SECONDARY;
	}

	public enum Target {
		HATCH,
		BALL;
		
		protected static Target[] targets = Target.values();
		public Target next() {
	        return targets[(this.ordinal()+1) % targets.length];
		}
		
		public Target prev() {
			int p = this.ordinal()-1;
			return p>=0 ? targets[p] : targets[targets.length-1];
		}
	}

	public enum LightMode {
		DEFAULT,
		OFF,
		BLINK,
		ON;

		protected static LightMode[] lightModes = LightMode.values();
	}

	public enum CameraMode {
		VISION, 
		DRIVER;

		protected static CameraMode[] cameraModes = CameraMode.values();
	}
	
	public enum StreamMode {
		SIDE_BY_SIDE,
		MAIN,
		SECONDARY;

		protected static StreamMode[] streamModes = StreamMode.values();
	}

	public boolean hasTarget() {return mPeriodicIO.hasTarget;}

	public double getTx() {return mPeriodicIO.tx;}

	public double getTy() {return mPeriodicIO.ty;}

	public double getTa() {return mPeriodicIO.ta;}

	public double getTs() {return mPeriodicIO.ts;}

	public void setLedMode(LightMode mode) {mLightMode = mode;}

	public void setCameraMode(CameraMode mode) {mCameraMode = mode;}

	public void setPipeline(Target target) {mTargetSelected = target;}

	public Target getTargetSelected() {return mTargetSelected;}
	
	public void setSteam(StreamMode mode) {mStreamMode = mode;}

	private NetworkTableEntry getValue(String key) {return table.getTable("limelight").getEntry(key);}

	@Override
	public IVisionTarget getTargetType() {return mTargets[mTargetSelected.ordinal()];}
	@Override
	public double getDistance() {return mTargets[mTargetSelected.ordinal()].getDistance();}
	@Override
	public double getHeightAngle() {return mTargets[mTargetSelected.ordinal()].getHeightAngle();}
	@Override
	public double getOffsetAngle() {return mTargets[mTargetSelected.ordinal()].getOffsetAngle();}
	@Override
	public double getSidewaysAngle() {return mTargets[mTargetSelected.ordinal()].getSidewaysAngle();}

	@Override
	public boolean checkSystem() {
		return false;
	}

	@Override
	public void outputTelemetry() {
		//post to smart dashboard periodically
        SmartDashboard.putBoolean("Limeligh Target Aquired", hasTarget());
        SmartDashboard.putNumber("Limelight X", getTx());
        SmartDashboard.putNumber("Limelight Y", getTy());
        SmartDashboard.putNumber("Limelight S", getTs());
		SmartDashboard.putNumber("Limelight Area", getTa());
	}

	@Override
	public void stop() {

	}
	
	@Override
	public void registerEnabledLoops(ILooper enabledLooper) {
		enabledLooper.register(mLoop);
    }
	
	@Override
    public synchronized void readPeriodicInputs() {
		mPeriodicIO.hasTarget = getValue("tv").getDouble(0) == 1;
		mPeriodicIO.tx = getValue("tx").getDouble(0.00);
		mPeriodicIO.ty = getValue("ty").getDouble(0.00);
		mPeriodicIO.ta = getValue("ta").getDouble(0.00);
		mPeriodicIO.ts = getValue("ts").getDouble(0.00);
		mPeriodicIO.targetSelected = Target.targets[(int) getValue("pipeline").getDouble(0)];
		mPeriodicIO.cameraMode = CameraMode.cameraModes[(int) getValue("camMode").getDouble(0)];
		mPeriodicIO.streamMode = StreamMode.streamModes[(int) getValue("stream").getDouble(0)];
		mPeriodicIO.lightMode = LightMode.lightModes[(int) getValue("ledMode").getDouble(0)];
	}

	@Override
	public synchronized void writePeriodicOutputs() {
		if(mPeriodicIO.targetSelected != mTargetSelected) getValue("pipeline").setNumber(mTargetSelected.ordinal());
		if(mPeriodicIO.lightMode != mLightMode) getValue("ledMode").setNumber(mLightMode.ordinal());
		if(mPeriodicIO.streamMode != mStreamMode) getValue("stream").setNumber(mStreamMode.ordinal());
		if(mPeriodicIO.cameraMode!= mCameraMode) getValue("camMode").setNumber(mCameraMode.ordinal());
		
	}

	public static class PeriodicIO {
		//Inputs
		public boolean hasTarget;
		public double tx;
		public double ty;
		public double ta;
		public double ts;

		//Outputs
		public Target targetSelected;
		public StreamMode streamMode;
		public LightMode lightMode;
		public CameraMode cameraMode;
	}

	
}