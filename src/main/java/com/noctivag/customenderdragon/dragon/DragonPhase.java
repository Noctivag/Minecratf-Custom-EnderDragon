package com.noctivag.customenderdragon.dragon;

/**
 * Represents the different combat phases of a custom dragon
 * Dragons become more powerful and aggressive as they enter later phases
 */
public enum DragonPhase {
    PHASE_1(1, 100, "Awakening"),
    PHASE_2(2, 66, "Enraged"),
    PHASE_3(3, 33, "Desperate Fury");

    private final int phaseNumber;
    private final int healthThreshold;
    private final String phaseName;

    DragonPhase(int phaseNumber, int healthThreshold, String phaseName) {
        this.phaseNumber = phaseNumber;
        this.healthThreshold = healthThreshold;
        this.phaseName = phaseName;
    }

    public int getPhaseNumber() {
        return phaseNumber;
    }

    public int getHealthThreshold() {
        return healthThreshold;
    }

    public String getPhaseName() {
        return phaseName;
    }

    /**
     * Determines the appropriate phase based on health percentage
     */
    public static DragonPhase getPhaseForHealth(double healthPercent) {
        if (healthPercent <= 33) {
            return PHASE_3;
        } else if (healthPercent <= 66) {
            return PHASE_2;
        } else {
            return PHASE_1;
        }
    }
}
