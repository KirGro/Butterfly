package com.team3335.butterfly.subsystems;

import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;

public class USBCamera extends Subsystem{
    private static USBCamera mInstance;

    private static final int kCameraHeight = 360;
    private static final int kCameraWidth = 240;

    private UsbCamera mCamera;
    private CvSource mCameraServer;
    
    public static USBCamera getInstance() {
    	if (mInstance == null) {
            mInstance = new USBCamera();
        }
        return mInstance;
    }

    private USBCamera() {
        mCamera = CameraServer.getInstance().startAutomaticCapture();
        mCamera.setResolution(kCameraHeight, kCameraWidth);
        mCamera.setBrightness(0);
        mCamera.setExposureAuto();

        mCameraServer = CameraServer.getInstance().putVideo("Driver Camera", kCameraHeight, kCameraWidth);

    }

    @Override
    public boolean checkSystem() {
        return false;
    }

    @Override
    public void outputTelemetry() {

    }

    @Override
    public void stop() {

    }


}