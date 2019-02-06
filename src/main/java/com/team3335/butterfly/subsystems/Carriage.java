package com.team3335.butterfly.subsystems;

import com.team3335.butterfly.Constants;
import com.team3335.butterfly.subsystems.Subsystem;
import com.team3335.lib.util.ChoosableSolenoid;
import com.team3335.lib.util.ChoosableSolenoid.SolenoidState;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Carriage extends Subsystem {
	private static Carriage mInstance = new Carriage();
	private ChoosableSolenoid mHatchPusher;

	private Carriage() {
		mHatchPusher = new ChoosableSolenoid(Constants.kCarriageModule, Constants.kHatchPusher);
	}

	public void launchHatch(){
		mHatchPusher.setState(SolenoidState.FORCED_FORWARD);
		waitTimeMillis(1000);
		mHatchPusher.setState(SolenoidState.FORCED_REVERSE);
	}
	
	private void waitTimeMillis(int millis) {
		double startTime = Timer.getFPGATimestamp();
		double endTime = startTime + (millis/1000); 

		while(Timer.getFPGATimestamp()<endTime){
		}
		return;
	}

	
    public static Carriage getInstance() {
    	if (mInstance == null) {
            mInstance = new Carriage();
        }
        return mInstance;
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

}



/*
import edu.wpi.first.wpilibj.DriverStation;
//import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class NavX extends Subsystem implements LoggableSubsystem {
	private AHRS ahrs = null;

	public NavX() {
		try {
			
			ahrs = new AHRS(SPI.Port.kMXP); // Use SPI!!!
			//ahrs = new AHRS(I2C.Port.kMXP);
		} catch (RuntimeException ex ) {
			DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
		}
	}

	@Override
	protected void initDefaultCommand() {
	}

	public AHRS getAHRS() {
		return ahrs;
	}

	public double getYaw() {
		return ahrs.getYaw();
	}

	public void zeroYaw() {
		ahrs.zeroYaw();
	}

	@Override
	public void log() {
		/* Display 6-axis Processed Angle Data                                      */
		//SmartDashboard.putBoolean(  "NavX: IMU_Connected",        ahrs.isConnected());
		//SmartDashboard.putBoolean(  "NavX: IMU_IsCalibrating",    ahrs.isCalibrating());
//		SmartDashboard.putNumber(   "NavX: IMU_Yaw",              ahrs.getYaw());
//		SmartDashboard.putNumber(   "NavX: IMU_Pitch",            ahrs.getPitch());
//		SmartDashboard.putNumber(   "NavX: IMU_Roll",             ahrs.getRoll());

		/* Display tilt-corrected, Magnetometer-based heading (requires             */
		/* magnetometer calibration to be useful)                                   */

//		SmartDashboard.putNumber(   "NavX: IMU_CompassHeading",   ahrs.getCompassHeading());

		/* Display 9-axis Heading (requires magnetometer calibration to be useful)  */
		//SmartDashboard.putNumber(   "NavX: IMU_FusedHeading",     ahrs.getFusedHeading());

		/* These functions are compatible w/the WPI Gyro Class, providing a simple  */
		/* path for upgrading from the Kit-of-Parts gyro to the navx MXP            */

//		SmartDashboard.putNumber(   "NavX: IMU_TotalYaw",         ahrs.getAngle());
		//SmartDashboard.putNumber(   "NavX: IMU_YawRateDPS",       ahrs.getRate());

		/* Display Processed Acceleration Data (Linear Acceleration, Motion Detect) */

		//SmartDashboard.putNumber(   "NavX: IMU_Accel_X",          ahrs.getWorldLinearAccelX());
		//SmartDashboard.putNumber(   "NavX: IMU_Accel_Y",          ahrs.getWorldLinearAccelY());
		//SmartDashboard.putBoolean(  "NavX: IMU_IsMoving",         ahrs.isMoving());
		//SmartDashboard.putBoolean(  "NavX: IMU_IsRotating",       ahrs.isRotating());

		/* Display estimates of velocity/displacement.  Note that these values are  */
		/* not expected to be accurate enough for estimating robot position on a    */
		/* FIRST FRC Robotics Field, due to accelerometer noise and the compounding */
		/* of these errors due to single (velocity) integration and especially      */
		/* double (displacement) integration.                                       */

//		SmartDashboard.putNumber(   "NavX: Velocity_X",           ahrs.getVelocityX());
//		SmartDashboard.putNumber(   "NavX: Velocity_Y",           ahrs.getVelocityY());
//		SmartDashboard.putNumber(   "NavX: Displacement_X",       ahrs.getDisplacementX());
//		SmartDashboard.putNumber(   "NavX: Displacement_Y",       ahrs.getDisplacementY());

		/* Display Raw Gyro/Accelerometer/Magnetometer Values                       */
		/* NOTE:  These values are not normally necessary, but are made available   */
		/* for advanced users.  Before using this data, please consider whether     */
		/* the processed data (see above) will suit your needs.                     */

		//SmartDashboard.putNumber(   "NavX: RawGyro_X",            ahrs.getRawGyroX());
		//SmartDashboard.putNumber(   "NavX: RawGyro_Y",            ahrs.getRawGyroY());
		//SmartDashboard.putNumber(   "NavX: RawGyro_Z",            ahrs.getRawGyroZ());
		//SmartDashboard.putNumber(   "NavX: RawAccel_X",           ahrs.getRawAccelX());
		//SmartDashboard.putNumber(   "NavX: RawAccel_Y",           ahrs.getRawAccelY());
		//SmartDashboard.putNumber(   "NavX: RawAccel_Z",           ahrs.getRawAccelZ());
		//SmartDashboard.putNumber(   "NavX: RawMag_X",             ahrs.getRawMagX());
		//SmartDashboard.putNumber(   "NavX: RawMag_Y",             ahrs.getRawMagY());
		//SmartDashboard.putNumber(   "NavX: RawMag_Z",             ahrs.getRawMagZ());
		//SmartDashboard.putNumber(   "NavX: IMU_Temp_C",           ahrs.getTempC());
		//SmartDashboard.putNumber(   "NavX: IMU_Timestamp",        ahrs.getLastSensorTimestamp());

		/* Omnimount Yaw Axis Information                                           */
		/* For more info, see http://navx-mxp.kauailabs.com/installation/omnimount  */
//		AHRS.BoardYawAxis yaw_axis = ahrs.getBoardYawAxis();
//		SmartDashboard.putString(   "NavX: YawAxisDirection",     yaw_axis.up ? "Up" : "Down" );
//		SmartDashboard.putNumber(   "NavX: YawAxis",              yaw_axis.board_axis.getValue() );

		/* Sensor Board Information                                                 */
//		SmartDashboard.putString(   "NavX: FirmwareVersion",      ahrs.getFirmwareVersion());

		/* Quaternion Data                                                          */
		/* Quaternions are fascinating, and are the most compact representation of  */
		/* orientation data.  All of the Yaw, Pitch and Roll Values can be derived  */
		/* from the Quaternions.  If interested in motion processing, knowledge of  */
		/* Quaternions is highly recommended.                                       */
		//SmartDashboard.putNumber(   "NavX: QuaternionW",          ahrs.getQuaternionW());
		//SmartDashboard.putNumber(   "NavX: QuaternionX",          ahrs.getQuaternionX());
		//SmartDashboard.putNumber(   "NavX: QuaternionY",          ahrs.getQuaternionY());
		//SmartDashboard.putNumber(   "NavX: QuaternionZ",          ahrs.getQuaternionZ());

		/* Connectivity Debugging Support                                           */
		//SmartDashboard.putNumber(   "NavX: IMU_Byte_Count",       ahrs.getByteCount());
		//SmartDashboard.putNumber(   "NavX: IMU_Update_Count",     ahrs.getUpdateCount());
//	}
//}
