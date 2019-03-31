package com.team3335.butterfly.vision;

import com.team3335.butterfly.Constants;
import com.team3335.butterfly.subsystems.Limelight;

public class HatchTarget implements IVisionTarget{
	
	public HatchTarget() {
		
	}

	@Override
	public double getDistance() {
		//return (Constants.kHatchTargetHeight*Constants.kPixelHeight) / (2*(Limelight.getInstance().getTa()/.15)*Math.tan(Constants.kVerticalFOV)); //Old an innaccurate way
		return -12 - Constants.kCameraDistanceFromFront - //Distance offset to make distance relative to robot front
			  (Constants.kCameraHeight + 4.5 - (Constants.kHatchTargetBottomToHatchCenter + Constants.kFloorToLowHatchCenter)) / //Y component of the triangle
			  (Math.tan(Math.toRadians(Constants.kCameraAngle + getHeightAngle()))); //Angle of the triangle
	}

	@Override
	public double getHeightAngle() {
		return Limelight.getInstance().getTy();
	}

	@Override
	public double getOffsetAngle() {
		return Limelight.getInstance().getTs();
	}

	@Override
	public double getSidewaysAngle() {
		//OLD HATCH return Limelight.getInstance().getTx();
		return Limelight.getInstance().getTx();
	}

	@Override
	public IVisionTarget getTargetType() {
		return this;
	}

}