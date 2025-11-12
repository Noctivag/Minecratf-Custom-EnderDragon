package com.noctivag.customenderdragon.dragon;package com.noctivag.customenderdragon.dragon;



/**import net.minecraft.entity.boss.BossBar;

 * Dragon variant types with different abilities and characteristics

 *//**

public enum DragonVariant { * Represents the different types of custom dragons available

    FIRE("Fire", "RED"), */

    ICE("Ice", "AQUA"),public enum DragonVariant {

    LIGHTNING("Lightning", "YELLOW"),    FIRE("Fire", BossBar.Color.RED),

    SHADOW("Shadow", "DARK_PURPLE"),    ICE("Ice", BossBar.Color.BLUE),

    VOID("Void", "DARK_GRAY");    LIGHTNING("Lightning", BossBar.Color.YELLOW),

    SHADOW("Shadow", BossBar.Color.PURPLE),

    private final String displayName;    VOID("Void", BossBar.Color.PINK);

    private final String colorName;

    private final String displayName;

    DragonVariant(String displayName, String colorName) {    private final BossBar.Color barColor;

        this.displayName = displayName;

        this.colorName = colorName;    DragonVariant(String displayName, BossBar.Color barColor) {

    }        this.displayName = displayName;

        this.barColor = barColor;

    public String getDisplayName() {    }

        return displayName;

    }    public String getDisplayName() {

        return displayName;

    public String getColorName() {    }

        return colorName;

    }    public BossBar.Color getBarColor() {

        return barColor;

    public static DragonVariant fromString(String name) {    }

        try {

            return valueOf(name.toUpperCase());    public static DragonVariant fromString(String name) {

        } catch (IllegalArgumentException e) {        try {

            return null;            return DragonVariant.valueOf(name.toUpperCase());

        }        } catch (IllegalArgumentException e) {

    }            return null;

}        }

    }
}
