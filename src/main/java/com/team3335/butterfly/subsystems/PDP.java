package com.team3335.butterfly.subsystems;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PDP extends Subsystem {
    private static PDP mInstance;
    private PeriodicIO mPeriodicIO = new PeriodicIO();
    private static final int kPDPPorts = 16;

    private PowerDistributionPanel mPDP;
    private final String[] mPortMap = {"Front Right Drivetrain",
                                        "Back Right Drivetrain",
                                        "Back Left Drivetrain",
                                        "Front Left Drivetrain",
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        "Unused Victor 1",
                                        "Unused Victor 2",
                                        "Unused Victor 3",
                                        "Unused Talon 1",
                                        "Elevator Master",
                                        "Elevator Slave 1"};

    public static PDP getInstance() {
    	if (mInstance == null) {
            mInstance = new PDP();
        }
        return mInstance;
    }

    private PDP() {
        mPDP = new PowerDistributionPanel(0);

    }

    public double getCurrentByID(int port) {
        if((port < kPDPPorts) && (port > -1)) {
            return mPeriodicIO.currents[port];
        } else {
            return Double.NaN;
        }
    }

    public double getCurrentByName(String name) {
        for(int i=0;i<kPDPPorts;i++) {
            if(name.equals(mPortMap[i])) {
                return mPeriodicIO.currents[i];
            }
        }
        return Double.NaN;
    }

    @Override
    public void readPeriodicInputs() {
        for(int i=0;i<kPDPPorts;i++) {
            mPeriodicIO.currents[i] = mPDP.getCurrent(i);
        }
        mPeriodicIO.totalCurrent = mPDP.getTotalCurrent();
        mPeriodicIO.temperature = mPDP.getTemperature();
    }


    @Override
    public boolean checkSystem() {
        return false;
    }

    @Override
    public void outputTelemetry() {
        SmartDashboard.putNumber("PDP Total Current", mPeriodicIO.totalCurrent);
        SmartDashboard.putNumber("PDP Temperature", mPeriodicIO.temperature);
        for(int i=0;i<kPDPPorts;i++) {
            SmartDashboard.putNumber("PDP Port "+i+" Current", mPeriodicIO.currents[i]);
        }
    }

    @Override
    public void stop() {

    }

    public class PeriodicIO {
        //inputs
        double[] currents = new double[16];
        double totalCurrent;
        double temperature; //in Celcius
    }

}