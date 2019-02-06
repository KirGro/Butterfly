package com.team3335.butterfly.subsystems;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.team3335.butterfly.vision.*;

/**
 * Wrapper class for getting and setting Limelight NetworkTable values.
 * 
 */

public class Limelight extends Subsystem implements IVisionTarget{
	private NetworkTableInstance table = null;
	private static Limelight mInstance;
	private IVisionTarget[] mTargets = new IVisionTarget[10];
	private int mTargetSelected;
	
	public static Limelight getInstance() {
		if (mInstance == null) {
            mInstance = new Limelight();
        }
        return mInstance;
    }
	
	private Limelight() {
		table = NetworkTableInstance.getDefault();
		mTargets[0] = new HatchTarget();
		mTargetSelected = 0;
	}

	public enum LightMode {
		DEFAULT,
		ON,
		BLINK,
		OFF
	}

	public enum CameraMode {
		VISION, 
		DRIVER
	}
	
	public enum StreamMode {
		SIDE_BY_SIDE,
		MAIN,
		SECONDARY
	}

	public boolean isTarget() {
		return getValue("tv").getDouble(0) == 1;
	}

	public double getTx() {
		return getValue("tx").getDouble(0.00);
	}

	public double getTy() {
		return getValue("ty").getDouble(0.00);
	}

	public double getTa() {
		return getValue("ta").getDouble(0.00);
	}

	public double getTs() {
		return getValue("ts").getDouble(0.00);
	}

	public double getTl() {
		return getValue("tl").getDouble(0.00);
	}

	public void setLedMode(LightMode mode) {
		getValue("ledMode").setNumber(mode.ordinal());
	}

	public void setCameraMode(CameraMode mode) {
		getValue("camMode").setNumber(mode.ordinal());
	}

	public void setPipeline(int number) {
		getValue("pipeline").setNumber(number);
		mTargetSelected = number;
	}

	public int getTargetSelected() {
		return mTargetSelected;
	}
	
	public void setSteam(StreamMode mode) {
		getValue("stream").setNumber(mode.ordinal());
	}

	private NetworkTableEntry getValue(String key) {
		return table.getTable("limelight").getEntry(key);
	}

	@Override
	public IVisionTarget getTargetType() {
		return mTargets[mTargetSelected];
	}

	@Override
	public double getDistance() {
		return mTargets[mTargetSelected].getDistance();
	}

	@Override
	public double getHeightAngle() {
		return mTargets[mTargetSelected].getHeightAngle();
	}

	@Override
	public double getOffsetAngle() {
		return mTargets[mTargetSelected].getOffsetAngle();
	}

	@Override
	public double getSidewaysAngle() {
		return mTargets[mTargetSelected].getSidewaysAngle();
	}

	@Override
	public boolean checkSystem() {
		return false;
	}

	@Override
	public void outputTelemetry() {
		//post to smart dashboard periodically
        SmartDashboard.putBoolean("Limeligh Target Aquired", isTarget());
        SmartDashboard.putNumber("Limelight X", getTx());
        SmartDashboard.putNumber("Limelight Y", getTy());
        SmartDashboard.putNumber("Limelight S", getTs());
		SmartDashboard.putNumber("Limelight Area", getTa());
	}

	@Override
	public void stop() {

	}

	
}