package com.team3335.butterfly.subsystems;

import java.io.File;
import java.io.IOException;

import com.team3335.butterfly.Preferences;
import com.team3335.butterfly.loops.Loop;

public class PreferencesHandler extends Subsystem{
    private static PreferencesHandler mInstance;
    private File mPrefFile;

    //private PeriodicIO mPeriodicIO = new PeriodicIO();

	private final Loop mLoop = new Loop() {
        @Override
        public void onStart(double timestamp) {
            synchronized (PreferencesHandler.this) {
                //startLogging();
            }
        }

        @Override
        public void onLoop(double timestamp) {
            synchronized (PreferencesHandler.this) {
                updateValues();
            }
        }

        @Override
        public void onStop(double timestamp) {
            stop();
            //stopLogging();
        }
    };

	private PreferencesHandler() {
        try {
            mPrefFile = new File("/home/lvuser/preferences.csv");
            if(!mPrefFile.exists()) mPrefFile.createNewFile();
        } catch (IOException e) {

        }

    }
    
    private void updateValues() {
        Preferences.class.getFields()[0].toString();
    }

	@Override
	public boolean checkSystem() {
		return false;
	}

	@Override
	public void stop() {

	}

    @Override
    public void outputTelemetry() {

    }
}