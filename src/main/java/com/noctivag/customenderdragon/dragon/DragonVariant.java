package com.noctivag.customenderdragon.dragon;

import org.bukkit.boss.BarColor;

/**
 * Represents the different types of custom dragons available
 */
public enum DragonVariant {
    FIRE("Fire", BarColor.RED),
    ICE("Ice", BarColor.BLUE),
    LIGHTNING("Lightning", BarColor.YELLOW),
    SHADOW("Shadow", BarColor.PURPLE),
    VOID("Void", BarColor.PINK);

    private final String displayName;
    private final BarColor barColor;

    DragonVariant(String displayName, BarColor barColor) {
        this.displayName = displayName;
        this.barColor = barColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BarColor getBarColor() {
        return barColor;
    }

    public static DragonVariant fromString(String name) {
        try {
            return DragonVariant.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
