package com.noctivag.customenderdragon.dragon;

/**
 * Represents the different types of custom dragons available
 */
public enum DragonVariant {
    FIRE("Fire"),
    ICE("Ice"),
    LIGHTNING("Lightning"),
    SHADOW("Shadow"),
    VOID("Void");

    private final String displayName;

    DragonVariant(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static DragonVariant fromString(String name) {
        try {
            return DragonVariant.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
