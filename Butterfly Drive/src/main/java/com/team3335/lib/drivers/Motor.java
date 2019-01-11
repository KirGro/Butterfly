package main.java.com.team3335.lib.drivers;

public abstract class Motor implements IBasicMotor{
	
	public int mCANId, mPDPId;
	protected boolean mBrakeMode;
	
	public Motor(int CANId) {
		this(CANId, -1);
	}
	
	public Motor(int CANId, int PDPId) {
		mCANId = CANId;
		mPDPId = PDPId;
	}
	
	public int getCANId() {
		return mCANId;
	}
	
	@Override
	public boolean getBrakeMode() {
		return mBrakeMode;
	}
}
