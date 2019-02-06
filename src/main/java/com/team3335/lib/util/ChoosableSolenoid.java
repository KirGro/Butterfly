package com.team3335.lib.util;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;

public class ChoosableSolenoid {
	private int module, side1, side2;
	
	private DoubleSolenoid doubleSole;
	private Solenoid singleSole;
	
	private SolenoidState requestedCurrentState = SolenoidState.OFF;
	private SolenoidState trueCurrentState = SolenoidState.OFF;
	
	public ChoosableSolenoid(int mod, int s1) {
		side1 = s1;
		side2 = -1;
		module = mod;
		singleSole = new Solenoid(module, side1);
	}
	
	public ChoosableSolenoid(int mod, int s1, int s2) {
		side1 = s1;
		side2 = s2;
		module = mod;
		doubleSole = new DoubleSolenoid(module, side1, side2);
	}
	
	public void setState(SolenoidState state) {
		if(requestedCurrentState != state) {
			requestedCurrentState = state;
			if(side2 != -1) {
				switch(requestedCurrentState) {
					case FORCED_FORWARD:
						doubleSole.set(Value.kForward);
						trueCurrentState = SolenoidState.FORCED_FORWARD;
						break;
					case FORCED_REVERSE:
						doubleSole.set(Value.kReverse);
						trueCurrentState = SolenoidState.FORCED_REVERSE;
						break;
					case NEUTRAL:
					case OFF:
						doubleSole.set(Value.kOff);
						trueCurrentState = SolenoidState.OFF;
						break;
					default:
						DriverStation.reportError("ChoosanleSolenoid in doubleSole state has encounter an unpected requested SolenoidState, no action has been taken.", true);
				}
			} else {
				switch(requestedCurrentState) {
					case FORCED_FORWARD:
						singleSole.set(true);
						trueCurrentState = SolenoidState.FORCED_FORWARD;
						break;
					case OFF:
					case NEUTRAL:
					case FORCED_REVERSE:
						singleSole.set(false);
						trueCurrentState = SolenoidState.NEUTRAL;
						break;
					default:
						DriverStation.reportError("ChoosanleSolenoid in singleSole state has encounter an unpected requested SolenoidState, no action has been taken.", true);
				}
			}
		}
	}
	
	public SolenoidState getRequestedState() {
		return requestedCurrentState;
	}
	
	public SolenoidState getTrueState() {
		return trueCurrentState;
	}
	
	
	public enum SolenoidState {
		FORCED_FORWARD,
		FORCED_REVERSE,
		OFF,
		NEUTRAL
	}
}
