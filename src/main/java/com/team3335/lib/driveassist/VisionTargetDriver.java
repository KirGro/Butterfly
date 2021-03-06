package com.team3335.lib.driveassist;

import com.team3335.butterfly.Constants;
import com.team3335.butterfly.states.DrivetrainState.*;
import com.team3335.butterfly.subsystems.Limelight;
import com.team3335.butterfly.subsystems.Limelight.Target;
import com.team3335.lib.util.DriveIntent;
import com.team3335.lib.util.PathIntent;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Add your docs here.
 */
public class VisionTargetDriver {
    private Limelight mLimelight = Limelight.getInstance();
    private ButterflyDriveHelper mButterflyDriveHelper = new ButterflyDriveHelper();

    private double lastGoodS = 0;
    private double lastGoodSTime = 0;

    public VisionTargetDriver(){

    }

    public DriveIntent pureVisionDriveRaw(Target target){
        if(mLimelight.getTargetSelected()!=target) {
            mLimelight.setPipeline(target);
        }/*
        if(mLimelight.isTarget()){
            return DriveIntent.MECANUM_BRAKE;
        }*/
        if(false) return oldWay();
        else return smartPath();
    }

    private DriveIntent oldWay() {
        double x = mLimelight.getSidewaysAngle();
    	double horizontalScalar = Math.abs(x)>7 ? x * .05: x * .1;
        double lastGoodS = mLimelight.getSidewaysAngle();
    	double distance = mLimelight.getDistance();
    	double angle = lastGoodS<-50 ? 90-lastGoodS : -lastGoodS;
    	double sidewaysComp = distance * Math.tan(angle) * (lastGoodS<-50 ? 1 : -1);
    	double distanceComp =distance/12 - 1;
    	if(distanceComp<0) distanceComp = 0;
    	distanceComp/=100;
    	SmartDashboard.putNumber("Distance", distance);
    	SmartDashboard.putNumber("Distance Comp", distanceComp);
    	return mButterflyDriveHelper.butterflyDrive(-distanceComp, (distance<300 ? sidewaysComp*.1 : 0), horizontalScalar, 0, DriveModeState.MECANUM_ROBOT_RELATIVE, DrivetrainWheelState.MECANUM, true, true);
    }

    private DriveIntent smartPath() {
        //Grab values
        double scew = mLimelight.getSidewaysAngle();
        if(scew!=0&&scew!=-90) {
            lastGoodS = (lastGoodS<-45 ? 90-scew : -scew);
            lastGoodSTime = Timer.getFPGATimestamp();  
        }
        if(Timer.getFPGATimestamp()-lastGoodSTime>5) {
            lastGoodS = 0;
        }

        double angle = mLimelight.getSidewaysAngle();
        double distanceInches = mLimelight.getDistance()/10;
        double rotationInches = distanceInches*Math.PI*angle/180;

        double sidewaysInches = distanceInches * Math.tan(lastGoodS) * (lastGoodS<-45 ? 1 : -1);

        if(Math.abs(angle)>1) {
            return mButterflyDriveHelper.butterflyDrive(0, 0, angle/7, 0, DriveModeState.MECANUM_ROBOT_RELATIVE, DrivetrainWheelState.MECANUM, true);
        } else if(distanceInches>30) {
            return mButterflyDriveHelper.butterflyDrive(-distanceInches/100, 0, 0, 0, DriveModeState.MECANUM_ROBOT_RELATIVE, DrivetrainWheelState.MECANUM, true);
        } else if(Math.abs(lastGoodS)>1){
            return mButterflyDriveHelper.butterflyDrive(0, -sidewaysInches*4, rotationInches, 0, DriveModeState.MECANUM_ROBOT_RELATIVE, DrivetrainWheelState.MECANUM, true);
        } else {
            return mButterflyDriveHelper.butterflyDrive(-distanceInches/100, 0, 0, 0, DriveModeState.MECANUM_ROBOT_RELATIVE, DrivetrainWheelState.MECANUM, true);
        }
    }

    public PathIntent pureVisionDriveControl(Target target) {
        if(mLimelight.getTargetSelected()!=target) {
            mLimelight.setPipeline(target);
        }
        //return fixInOrder();
        return directVectorTravel();
    }

    private PathIntent directVectorTravel() {
        //Grab values
        double scew = mLimelight.getSidewaysAngle();
        if(scew!=0&&scew!=-90) {
            lastGoodS = (lastGoodS<-45 ? 90-scew : -scew);
            lastGoodSTime = Timer.getFPGATimestamp();  
        }
        if(Timer.getFPGATimestamp()-lastGoodSTime>5) {
            lastGoodS = 0;
        }

        double angle = mLimelight.getSidewaysAngle();
        double distanceInches = mLimelight.getDistance();
        if(distanceInches <1 || !mLimelight.hasTarget()) {
            return PathIntent.STOPED;
        }

        //Run distance calculations
        double rotationInches = distanceInches*Math.PI*angle/180;
        double sidewaysInches = distanceInches * Math.tan(lastGoodS) * (lastGoodS<-45 ? 1 : -1);

        if(distanceInches>36) {
            sidewaysInches = 0;
        }

        //rot = angle/360   x   wheelWidth x pi / circumference
        //Calculate wheel rotations
        double turningRotations = (angle * Constants.kMecanumWheelWidth) / (360 * Constants.kDrivetrainWheelDiameterInches);
        double sideRotations = sidewaysInches / (Constants.kDrivetrainWheelDiameterInches * Math.PI);
        double forwardRotations = distanceInches / (Constants.kDrivetrainWheelDiameterInches * Math.PI);

        
        turningRotations*= -10;
        sideRotations*= .5;
        forwardRotations*= .5;

        
		double mFR = (forwardRotations + sideRotations + turningRotations);
		double mFL = (forwardRotations - sideRotations - turningRotations);
		double mBR = (forwardRotations - sideRotations + turningRotations);
		double mBL = (forwardRotations + sideRotations - turningRotations);

        SmartDashboard.putNumber("ForwardR", forwardRotations);
        SmartDashboard.putNumber("SidewayR", sideRotations);
        SmartDashboard.putNumber("TurningR", turningRotations);

        return new PathIntent(mFR, mFL, mBR, mBL, DriveModeState.MECANUM_ROBOT_RELATIVE, true);
    }

    private PathIntent fixInOrder() {
        //Grab values
        double scew = mLimelight.getSidewaysAngle();
        if(scew!=0&&scew!=-90) {
            lastGoodS = (lastGoodS<-45 ? 90-scew : -scew);
            lastGoodSTime = Timer.getFPGATimestamp();  
        }
        if(Timer.getFPGATimestamp()-lastGoodSTime>5) {
            lastGoodS = 0;
        }

        double angle = mLimelight.getSidewaysAngle();
        double distanceInches = mLimelight.getDistance();
        if(distanceInches <1 || !mLimelight.hasTarget()) {
            return PathIntent.STOPED;
        }

        //Run distance calculations
        double rotationInches = distanceInches*Math.PI*angle/180;
        double sidewaysInches = distanceInches * Math.tan(lastGoodS) * (lastGoodS<-45 ? 1 : -1);
        double forwardInches = distanceInches / Math.tan(lastGoodS) * (lastGoodS<-45 ? 1 : -1);

        if(distanceInches>36) {
            sidewaysInches = 0;
        }

        //rot = angle/360   x   wheelWidth x pi / circumference
        //Calculate wheel rotations
        double turningRotations = (angle * Constants.kMecanumWheelWidth) / (360 * Constants.kDrivetrainWheelDiameterInches);
        double sideRotations = sidewaysInches / (Constants.kDrivetrainWheelDiameterInches * Math.PI);
        double forwardRotations = forwardInches / (Constants.kDrivetrainWheelDiameterInches * Math.PI);
        turningRotations*= -20;
        sideRotations*= 1;
        forwardRotations*= .5;


        if(angle>2) {
            sideRotations = 0;
            forwardRotations = 0;
        } else if(distanceInches > 36) {
        } else if(angle>1) {
            //sideRotations = 0;
            //forwardRotations = 0;
        } 

        
		double mFR = (forwardRotations + sideRotations + turningRotations);
		double mFL = (forwardRotations - sideRotations - turningRotations);
		double mBR = (forwardRotations - sideRotations + turningRotations);
		double mBL = (forwardRotations + sideRotations - turningRotations);

        SmartDashboard.putNumber("ForwardR", forwardRotations);
        SmartDashboard.putNumber("SidewayR", sideRotations);
        SmartDashboard.putNumber("TurningR", turningRotations);

        return new PathIntent(mFR, mFL, mBR, mBL, DriveModeState.MECANUM_ROBOT_RELATIVE, true);
    }
}
