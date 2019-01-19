package main.java.com.team3335.lib.util;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import main.java.com.team3335.butterfly.Constants;
import main.java.com.team3335.butterfly.states.DrivetrainState.*;

public class ButterflyDriveHelper {
	
	private DriveIntent oldDriveIntent = DriveIntent.MECANUM_BRAKE;
	
	/**
	 * 
	 * @param f					X (or forwards/backwards motion) for all drivetrain styles, except tank where it is the right side of the drivetrain
	 * @param s					Y (or sideways motion) for mecanum, left side of drivetrain for tank, and not used for arcade
	 * @param r					Z rotation for mecanum and arcade, and not used for tank
	 * @param h					Angle of robot relative to where it started, only for field relative motion
	 * @param driveMode			Duh
	 * @param driveWheelState	Duh
	 * @param brake				Use break mode
	 * @return
	 */
	public DriveIntent butterflyDrive(double f, double s, double r, double h, DriveModeState driveMode, DrivetrainWheelState driveWheelState, boolean brake) {
		return butterflyDrive(f, s, r, h, driveMode, driveWheelState, brake, false);
	}
	
	public DriveIntent butterflyDrive(double f, double s, double r, double h, DriveModeState driveMode, DrivetrainWheelState driveWheelState, boolean brake, boolean visionDriving) {
		switch(driveMode) {
			case MECANUM_FIELD_RELATIVE:
				return fieldRelative(f, s, r, h, driveMode, driveWheelState, brake, visionDriving);
			case MECANUM_ROBOT_RELATIVE: 
				return robotRelative(f, s, r, driveMode, driveWheelState, brake, visionDriving);
			case TANK:
				return tank(f, s, driveMode, driveWheelState, brake, visionDriving);
			case ARCADE:
				return arcade(f, r, driveMode, driveWheelState, brake, visionDriving);
			default:
				DriverStation.reportError("Drive helper recieved unknown drive mode: "+driveMode+". Stopping all drivetrain movement.", false);
				return DriveIntent.MECANUM_BRAKE;
		}
	}

	public double map(double value) {
		if(Constants.kUseSinMapping) {
			return value; //TODO
		} else {
			return value==0 ? 0 : value*value*(Math.abs(value)/value);
		}
	}
	
	private DriveIntent fieldRelative(double f, double s, double r, double h, DriveModeState driveMode, DrivetrainWheelState driveWheelState, boolean brake) {
		return fieldRelative(f, s, r, h, driveMode, driveWheelState, brake, false);
	}
	
	private DriveIntent fieldRelative(double f, double s, double r, double h, DriveModeState driveMode, DrivetrainWheelState driveWheelState, boolean brake, boolean visionDriving) {
		//TODO MAKE FEILD RELATIVE
		return robotRelative(f, s, r, driveMode, driveWheelState, brake, visionDriving);
	}
	
	private DriveIntent robotRelative(double f, double s, double r, DriveModeState driveMode, DrivetrainWheelState driveWheelState, boolean brake) {
		return robotRelative(f, s, r, driveMode, driveWheelState, brake, false);
	}
	
	private DriveIntent robotRelative(double f, double s, double r, DriveModeState driveMode, DrivetrainWheelState driveWheelState, boolean brake, boolean visionDriving) {
		double mFR, mFL, mBR, mBL;
		
		//Calculate scalar, and smoothing
		double forward = map(f), sideway = map(s), rotation = map(r);
		
		//Calculate all wheel speeds
		mFR = (forward + sideway + rotation);
		mFL = (forward - sideway - rotation);
		mBR = (forward - sideway + rotation);
		mBL = (forward + sideway - rotation);
		
		/* Old
		mFR = (forward - sideway + rotation);
		mFL = (forward + sideway + rotation);
		mBR = (-forward - sideway + rotation);
		mBL = (-forward + sideway + rotation);
		*/
		
		//Fix overpowered wheels
		double largest = Math.abs(mFR)>Math.abs(mFL) ? Math.abs(mFR) : Math.abs(mFL);
		largest = largest>Math.abs(mBR) ? largest : Math.abs(mBR);
		largest = largest>Math.abs(mBL) ? largest : Math.abs(mBL);
		SmartDashboard.putString("Drive Intent Calculating: ", "Robot Relative, largest - "+largest);
		if(largest>1) {
			mFR/=largest;
			mFL/=largest;
			mBR/=largest;
			mBL/=largest;
		}
		if(visionDriving) {
			mFR*=.5;
			mFL*=.5;
			mBR*=.5;
			mBL*=.5;
		}
		oldDriveIntent = new DriveIntent(mFR, mFL, mBR, mBL, driveMode, true);
		return oldDriveIntent;
	}

	private DriveIntent tank(double f, double s, DriveModeState driveMode, DrivetrainWheelState driveWheelState, boolean brake) {
		return tank(f, s, driveMode, driveWheelState, brake, false);
	}
	
	private DriveIntent tank(double f, double s, DriveModeState driveMode, DrivetrainWheelState driveWheelState, boolean brake, boolean visionDriving) {
		double forwardRight = map(f), forwardLeft = map(s);

		if(visionDriving) {
			forwardLeft*=.5;
			forwardLeft*=.5;
		}
		
		if(driveWheelState == DrivetrainWheelState.MECANUM) {
			//TODO Test
			oldDriveIntent = new DriveIntent(forwardRight, forwardLeft, forwardRight, forwardLeft, driveMode, brake);
		} else {
			oldDriveIntent = new DriveIntent(forwardRight, forwardLeft, forwardRight, forwardLeft, driveMode, brake);
		}
		SmartDashboard.putString("Drive Intent Calculating: ", "Tank Drive");
		return oldDriveIntent;
	}
	
	private DriveIntent arcade(double f, double r, DriveModeState driveMode, DrivetrainWheelState driveWheelState, boolean brake) {
		return arcade(f, r, driveMode, driveWheelState, brake, false);
	}

	private DriveIntent arcade(double f, double r, DriveModeState driveMode, DrivetrainWheelState driveWheelState, boolean brake, boolean visionDriving) {
		double forward = map(f), turn = map(r);
		if(driveWheelState == DrivetrainWheelState.MECANUM) {
			//TODO Test
			double fr = forward+turn;
			double fl = forward-turn;
			double largest = Math.abs(fl)>Math.abs(fr) ? Math.abs(fl): Math.abs(fr);
			SmartDashboard.putString("Drive Intent Calculating: ", "Arcade Drive, largest - "+largest);
			if(largest>1) {
				fr/=largest;
				fl/=largest;
			}
			if(visionDriving) {
				fr*=.5;
				fl*=.5;
			}
			oldDriveIntent = new DriveIntent(fr, fl, fr, fl, driveMode, brake);
		} else {
			double fr = forward+turn;
			double fl = forward-turn;
			double largest = Math.abs(fl)>Math.abs(fr) ? Math.abs(fl): Math.abs(fr);
			if(largest>1) {
				fr/=largest;
				fl/=largest;
			}
			if(visionDriving) {
				fr*=.5;
				fl*=.5;
			}
			oldDriveIntent = new DriveIntent(fr, fl, fr, fl, driveMode, brake);
		}
		return oldDriveIntent;
	}


}
