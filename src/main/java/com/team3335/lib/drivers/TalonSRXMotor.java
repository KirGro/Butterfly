package com.team3335.lib.drivers;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXPIDSetConfiguration;

import edu.wpi.first.wpilibj.DriverStation;
import com.team3335.butterfly.Constants;

public class TalonSRXMotor extends Motor implements IAdvancedMotor{

    private final static int kTimeoutMs = 100;

    public static class Configuration {
        public NeutralMode NEUTRAL_MODE = NeutralMode.Coast;
        // This is factory default.
        public double NEUTRAL_DEADBAND = 0.08;

        public boolean ENABLE_CURRENT_LIMIT = false;
        public boolean ENABLE_SOFT_LIMIT = false;
        public boolean ENABLE_LIMIT_SWITCH = false;
        public int FORWARD_SOFT_LIMIT = 0;
        public int REVERSE_SOFT_LIMIT = 0;

        public boolean INVERTED = false;
        public boolean SENSOR_PHASE = false;

        public int CONTROL_FRAME_PERIOD_MS = 5;
        public int MOTION_CONTROL_FRAME_PERIOD_MS = 100;
        public int GENERAL_STATUS_FRAME_RATE_MS = 5;
        public int FEEDBACK_STATUS_FRAME_RATE_MS = 100;
        public int QUAD_ENCODER_STATUS_FRAME_RATE_MS = 100;
        public int ANALOG_TEMP_VBAT_STATUS_FRAME_RATE_MS = 100;
        public int PULSE_WIDTH_STATUS_FRAME_RATE_MS = 100;

        public VelocityMeasPeriod VELOCITY_MEASUREMENT_PERIOD = VelocityMeasPeriod.Period_100Ms;
        public int VELOCITY_MEASUREMENT_ROLLING_AVERAGE_WINDOW = 64;

        public double OPEN_LOOP_RAMP_RATE = 0.0;
        public double CLOSED_LOOP_RAMP_RATE = 0.0;
    }

    private static final Configuration kDefaultConfiguration = new Configuration();
    
    public static TalonSRX setupTalon(TalonSRX talon, Configuration config) {
    	//TODO actually write own, currently copy from le Cheesy Poofs
        talon.set(ControlMode.PercentOutput, 0.0);

        talon.changeMotionControlFramePeriod(config.MOTION_CONTROL_FRAME_PERIOD_MS);
        talon.clearMotionProfileHasUnderrun(kTimeoutMs);
        talon.clearMotionProfileTrajectories();

        talon.clearStickyFaults(kTimeoutMs);

        talon.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector,
                LimitSwitchNormal.NormallyOpen, kTimeoutMs);
        talon.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector,
                LimitSwitchNormal.NormallyOpen, kTimeoutMs);
        talon.overrideLimitSwitchesEnable(config.ENABLE_LIMIT_SWITCH);

        // Turn off re-zeroing by default.
        talon.configSetParameter(
                ParamEnum.eClearPositionOnLimitF, 0, 0, 0, kTimeoutMs);
        talon.configSetParameter(
                ParamEnum.eClearPositionOnLimitR, 0, 0, 0, kTimeoutMs);

        talon.configNominalOutputForward(0, kTimeoutMs);
        talon.configNominalOutputReverse(0, kTimeoutMs);
        talon.configNeutralDeadband(config.NEUTRAL_DEADBAND, kTimeoutMs);

        talon.configPeakOutputForward(1.0, kTimeoutMs);
        talon.configPeakOutputReverse(-1.0, kTimeoutMs);

        talon.setNeutralMode((com.ctre.phoenix.motorcontrol.NeutralMode) config.NEUTRAL_MODE);

        talon.configForwardSoftLimitThreshold(config.FORWARD_SOFT_LIMIT, kTimeoutMs);
        talon.configForwardSoftLimitEnable(config.ENABLE_SOFT_LIMIT, kTimeoutMs);

        talon.configReverseSoftLimitThreshold(config.REVERSE_SOFT_LIMIT, kTimeoutMs);
        talon.configReverseSoftLimitEnable(config.ENABLE_SOFT_LIMIT, kTimeoutMs);
        talon.overrideSoftLimitsEnable(config.ENABLE_SOFT_LIMIT);

        talon.setInverted(config.INVERTED);
        talon.setSensorPhase(config.SENSOR_PHASE);

        talon.selectProfileSlot(0, 0);

        talon.configVelocityMeasurementPeriod(config.VELOCITY_MEASUREMENT_PERIOD, kTimeoutMs);
        talon.configVelocityMeasurementWindow(config.VELOCITY_MEASUREMENT_ROLLING_AVERAGE_WINDOW,
                kTimeoutMs);

        talon.configOpenloopRamp(config.OPEN_LOOP_RAMP_RATE, kTimeoutMs);
        talon.configClosedloopRamp(config.CLOSED_LOOP_RAMP_RATE, kTimeoutMs);

        talon.configVoltageCompSaturation(0.0, kTimeoutMs);
        talon.configVoltageMeasurementFilter(32, kTimeoutMs);
        talon.enableVoltageCompensation(false);

        talon.enableCurrentLimit(config.ENABLE_CURRENT_LIMIT);

        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_1_General,
                config.GENERAL_STATUS_FRAME_RATE_MS, kTimeoutMs);
        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0,
                config.FEEDBACK_STATUS_FRAME_RATE_MS, kTimeoutMs);

        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature,
                config.QUAD_ENCODER_STATUS_FRAME_RATE_MS, kTimeoutMs);
        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_4_AinTempVbat,
                config.ANALOG_TEMP_VBAT_STATUS_FRAME_RATE_MS, kTimeoutMs);
        talon.setStatusFramePeriod(StatusFrameEnhanced.Status_8_PulseWidth,
                config.PULSE_WIDTH_STATUS_FRAME_RATE_MS, kTimeoutMs);

        talon.setControlFramePeriod(ControlFrame.Control_3_General, config.CONTROL_FRAME_PERIOD_MS);

        return talon;
    }
	
        private EncoderType encoderType;
	private FeedbackType feedbackType;
        private TalonSRX talonMotor;
        private double encoderToOutputRatio;
	
	public TalonSRXMotor(int CANId) {
		this(CANId, -1, EncoderType.NONE);
	}

	public TalonSRXMotor(int CANId, int PDPId) {
		this(CANId, PDPId, EncoderType.NONE);
		
	}
	
	public TalonSRXMotor(int CANId, int PDPId, EncoderType encoderType) {
		super(CANId, PDPId);
		talonMotor = new TalonSRX(CANId);
		setupTalon(talonMotor, kDefaultConfiguration);
		this.encoderType = encoderType;
		switch(encoderType) {
			case SRX: 
				feedbackType = FeedbackType.RELATIVE;
				setFeedbackType(feedbackType);
				break;
			default: {
				
			}
		}
	}

	@Override
	public void driveRaw(double speed) {
		talonMotor.set(ControlMode.PercentOutput, speed);
	}

	@Override
	public void driveRaw(double speed, boolean reverseInput) {
		talonMotor.set(ControlMode.PercentOutput, reverseInput?-speed:speed);
	}
	
	@Override
	public void setBrakeMode(boolean on) {
		if(mBrakeMode != on) {
			mBrakeMode = on;
			talonMotor.setNeutralMode(on ? NeutralMode.Brake : NeutralMode.Coast);
		}
	}	

	@Override
	public void setFeedbackType(FeedbackType type) {
		if(encoderType == EncoderType.SRX) {
			ErrorCode sensorPresent = ErrorCode.NotImplemented;
			switch(type) {
				case RELATIVE: {
					sensorPresent = talonMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 100);
				} break;
				case ABSOLUTE: {
					sensorPresent = talonMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute, 0, 100);
				}
			}
			if(sensorPresent != ErrorCode.OK) {
				DriverStation.reportError("Could not detect requested encoder on CAN bus device with ID "+mCANId+", error code: "+sensorPresent, false);
				this.encoderType = EncoderType.NONE;
			}
		}
	}

	@Override
	public void invertMotor(boolean inverted) {
		talonMotor.setInverted(inverted);
		
	}

	@Override
	public int getEncoderPosition() {
	        return talonMotor.getSelectedSensorPosition();
	}
	
	@Override
	public void zeroEncoder() {
		talonMotor.setSelectedSensorPosition(0, 0, 0);
	}

	@Override
	public int getEncoderRotation() {
		return getEncoderPosition()%Constants.kSRXEncoderCPR;
	}

	@Override
	public int getEncoderVelocity() {
		return talonMotor.getSelectedSensorVelocity();
        }

        
        
        @Override 
        public void configForPathFollowing(TalonSRXPIDSetConfiguration configPID) {
                talonMotor.selectProfileSlot(0, 0);
                talonMotor.configurePID(configPID, 0, kTimeoutMs, true);
                talonMotor.config_kP(0, .9, Constants.kLongCANTimeoutMs);
                talonMotor.config_kI(0, 0, Constants.kLongCANTimeoutMs);
                talonMotor.config_kD(0, 10, Constants.kLongCANTimeoutMs);
                talonMotor.config_kF(0, 0, Constants.kLongCANTimeoutMs);
                talonMotor.config_IntegralZone(0, 0, Constants.kLongCANTimeoutMs);
                talonMotor.configNeutralDeadband(0.0, 0);
        }

        @Override
        public void configForInput(TalonSRXPIDSetConfiguration configPID) {
                talonMotor.configNeutralDeadband(kDefaultConfiguration.NEUTRAL_DEADBAND, 0);
        }

        @Override 
        public void setControlPointFromCurrent(double rotations) {
                int pos = ((int) Math.round(rotations*encoderToOutputRatio)) * Constants.kSRXEncoderCPR;
                talonMotor.set(ControlMode.Position, pos+getEncoderPosition());
        }

        @Override
        public void setEncoderToOutputRatio(double ratio) {
                encoderToOutputRatio = ratio;
        }
	
}
