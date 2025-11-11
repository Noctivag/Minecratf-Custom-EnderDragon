package com.noctivag.customenderdragon.integration;

import com.noctivag.customenderdragon.CustomEnderDragonPlugin;
import com.noctivag.customenderdragon.dragon.CustomDragon;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * PlaceholderAPI expansion for Custom EnderDragon
 *
 * Placeholders:
 * - %customdragon_active_count% - Number of active dragons
 * - %customdragon_nearest_variant% - Variant of nearest dragon
 * - %customdragon_nearest_distance% - Distance to nearest dragon
 * - %customdragon_nearest_phase% - Phase of nearest dragon
 * - %customdragon_nearest_health% - Health of nearest dragon
 * - %customdragon_nearest_health_percent% - Health percentage of nearest dragon
 */
public class DragonPlaceholderExpansion extends PlaceholderExpansion {
    private final CustomEnderDragonPlugin plugin;

    public DragonPlaceholderExpansion(CustomEnderDragonPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "customdragon";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Noctivag";
    }

    @Override
    public @NotNull String getVersion() {
        return "2.0.0";
    }

    @Override
    public boolean persist() {
        return true; // Don't unregister when reloading
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        switch (params.toLowerCase()) {
            case "active_count":
                return String.valueOf(plugin.getDragonManager().getAllDragons().size());

            case "nearest_variant":
                CustomDragon nearest = findNearestDragon(player);
                return nearest != null ? nearest.getVariant().getDisplayName() : "None";

            case "nearest_distance":
                CustomDragon nearestDist = findNearestDragon(player);
                if (nearestDist != null) {
                    double distance = player.getLocation().distance(nearestDist.getLocation());
                    return String.format("%.1f", distance);
                }
                return "N/A";

            case "nearest_phase":
                CustomDragon nearestPhase = findNearestDragon(player);
                return nearestPhase != null ? String.valueOf(nearestPhase.getCurrentPhase().getPhaseNumber()) : "N/A";

            case "nearest_health":
                CustomDragon nearestHealth = findNearestDragon(player);
                if (nearestHealth != null) {
                    return String.format("%.0f", nearestHealth.getDragon().getHealth());
                }
                return "N/A";

            case "nearest_health_percent":
                CustomDragon nearestPercent = findNearestDragon(player);
                if (nearestPercent != null) {
                    double percent = (nearestPercent.getDragon().getHealth() / nearestPercent.getMaxHealth()) * 100;
                    return String.format("%.0f%%", percent);
                }
                return "N/A";

            default:
                return null;
        }
    }

    /**
     * Find the nearest dragon to a player
     */
    private CustomDragon findNearestDragon(Player player) {
        CustomDragon nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (CustomDragon dragon : plugin.getDragonManager().getAllDragons()) {
            if (dragon.getLocation().getWorld() != player.getWorld()) {
                continue;
            }

            double distance = player.getLocation().distance(dragon.getLocation());
            if (distance < minDistance) {
                minDistance = distance;
                nearest = dragon;
            }
        }

        return nearest;
    }
}
