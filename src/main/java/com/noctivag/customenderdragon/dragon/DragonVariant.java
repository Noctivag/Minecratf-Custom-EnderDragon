package com.noctivag.customenderdragon.dragon;

import net.minecraft.entity.boss.BossBar;

/**
 * Represents the different types of custom dragons available
 */
public enum DragonVariant {
    FIRE("Fire", BossBar.Color.RED),
    ICE("Ice", BossBar.Color.BLUE),
    LIGHTNING("Lightning", BossBar.Color.YELLOW),
    SHADOW("Shadow", BossBar.Color.PURPLE),
    VOID("Void", BossBar.Color.PINK);

    private final String displayName;
    private final BossBar.Color barColor;

    DragonVariant(String displayName, BossBar.Color barColor) {
        this.displayName = displayName;
        this.barColor = barColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public BossBar.Color getBarColor() {
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
