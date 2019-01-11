package main.java.com.team3335.butterfly.vision;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import main.java.com.team3335.butterfly.Constants;

/**
 * Wrapper class for getting and setting Limelight NetworkTable values.
 * 
 */

public class Limelight {
	private NetworkTableInstance table = null;
	
	public Limelight() {
		table = NetworkTableInstance.getDefault();
	}

	public enum LightMode {
		eOn, eOff, eBlink
	}

	public enum CameraMode {
		eVision, eDriver
	}
	
	public double getTargetDistance() {
		return (Constants.kHatchTargetHeight*Constants.kPixelHeight) / (2*(getTa()/.15)*Math.tan(Constants.kVerticalFOV));
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
	}

	private NetworkTableEntry getValue(String key) {
		return table.getTable("limelight").getEntry(key);
	}
}