package com.team3335.lib.util;

public class LogisticalGrowthMap {

    public static LogisticalGrowthConstants joystickConstants = new LogisticalGrowthConstants();

    public static double map(double x, LogisticalGrowthConstants lgs) {
        double absx = Math.abs(x);
        double step1 = lgs.halfMaxiumEffects + (Math.log(Math.pow(2, (1/lgs.symetry))-1) / lgs.hillSlope);
        double numerator = lgs.top-lgs.bottom;
        double denominator = (1 + Math.pow(10, (lgs.hillSlope * (step1 - absx))));
        return (lgs.bottom + numerator / denominator) * absx / x;
    }


    public static class LogisticalGrowthConstants {
        public double bottom = 0;
        public double top = 1;
        public double symetry = 1;
        public double hillSlope = 6;
        public double halfMaxiumEffects = .62;
    }
}