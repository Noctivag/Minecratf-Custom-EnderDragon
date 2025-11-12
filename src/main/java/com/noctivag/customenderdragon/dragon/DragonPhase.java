package com.noctivag.customenderdragon.dragon;

/**
 * Dragon combat phases based on health percentage
 */
public enum DragonPhase {
    PHASE_1(1, 100, 67),
    PHASE_2(2, 66, 34),
    PHASE_3(3, 33, 0);

    private final int phaseNumber;
    private final double healthPercentStart;
    private final double healthPercentEnd;

    DragonPhase(int phaseNumber, double healthPercentStart, double healthPercentEnd) {
        this.phaseNumber = phaseNumber;
        this.healthPercentStart = healthPercentStart;
        this.healthPercentEnd = healthPercentEnd;
    }

    public int getPhaseNumber() {
        return phaseNumber;
    }

    public double getHealthPercentStart() {
        return healthPercentStart;
    }

    public double getHealthPercentEnd() {
        return healthPercentEnd;
    }

    public static DragonPhase fromHealthPercent(double healthPercent) {
        for (DragonPhase phase : values()) {
            if (healthPercent <= phase.healthPercentStart && healthPercent > phase.healthPercentEnd) {
                return phase;
            }
        }
        return PHASE_3;
    }
}
