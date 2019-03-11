package com.team3335.butterfly.planners;

import java.util.LinkedList;
import java.util.Optional;

import com.team3335.butterfly.Preferences;

public class SuperstructureMotionPlanner {
    private Targeting mTargeting = Preferences.pDefaultTargeting;
    private Placing mPlacing = Placing.LOW_HATCH;

    public enum Targeting {
        HATCH,
        CARGO,
        CLIMB;
    }

    public enum Placing {
        LOW_HATCH,
        MID_HATCH,
        LOW_CARGO,
        SHIP_CARGO,
        MID_CARGO;
    }
}