package com.noctivag.customenderdragon.api;

import com.noctivag.customenderdragon.CustomEnderDragonPlugin;
import com.noctivag.customenderdragon.dragon.CustomDragon;
import com.noctivag.customenderdragon.dragon.DragonVariant;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.UUID;

/**
 * Public API for Custom EnderDragon Plugin
 *
 * Other plugins can use this API to interact with custom dragons
 *
 * Usage example:
 * <pre>
 * CustomEnderDragonAPI api = CustomEnderDragonAPI.getInstance();
 * CustomDragon dragon = api.spawnDragon(location, DragonVariant.FIRE);
 * </pre>
 */
public class CustomEnderDragonAPI {
    private static CustomEnderDragonAPI instance;
    private final CustomEnderDragonPlugin plugin;

    private CustomEnderDragonAPI(CustomEnderDragonPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Initialize the API (called by plugin on enable)
     */
    public static void initialize(CustomEnderDragonPlugin plugin) {
        if (instance == null) {
            instance = new CustomEnderDragonAPI(plugin);
        }
    }

    /**
     * Get the API instance
     *
     * @return API instance, or null if plugin is not loaded
     */
    public static CustomEnderDragonAPI getInstance() {
        return instance;
    }

    /**
     * Check if the API is available
     *
     * @return true if plugin is loaded and API is available
     */
    public static boolean isAvailable() {
        return instance != null;
    }

    /**
     * Spawn a custom dragon at a location
     *
     * @param location Location to spawn the dragon
     * @param variant Dragon variant
     * @return The spawned CustomDragon, or null if failed
     */
    public CustomDragon spawnDragon(Location location, DragonVariant variant) {
        return plugin.getDragonManager().spawnDragon(location, variant);
    }

    /**
     * Get a custom dragon by UUID
     *
     * @param uuid Dragon UUID
     * @return CustomDragon, or null if not found
     */
    public CustomDragon getDragon(UUID uuid) {
        return plugin.getDragonManager().getCustomDragon(uuid);
    }

    /**
     * Get a custom dragon by EnderDragon entity
     *
     * @param dragon EnderDragon entity
     * @return CustomDragon, or null if not a custom dragon
     */
    public CustomDragon getDragon(EnderDragon dragon) {
        return plugin.getDragonManager().getCustomDragon(dragon);
    }

    /**
     * Get all active custom dragons
     *
     * @return Collection of all active custom dragons
     */
    public Collection<CustomDragon> getAllDragons() {
        return plugin.getDragonManager().getAllDragons();
    }

    /**
     * Remove a custom dragon
     *
     * @param uuid Dragon UUID
     */
    public void removeDragon(UUID uuid) {
        plugin.getDragonManager().removeDragon(uuid);
    }

    /**
     * Remove all custom dragons
     *
     * @return Number of dragons removed
     */
    public int removeAllDragons() {
        return plugin.getDragonManager().removeAllDragons();
    }

    /**
     * Check if an EnderDragon is a custom dragon
     *
     * @param dragon EnderDragon entity
     * @return true if it's a custom dragon
     */
    public boolean isCustomDragon(EnderDragon dragon) {
        return plugin.getDragonManager().isCustomDragon(dragon);
    }

    /**
     * Get the number of active custom dragons
     *
     * @return Number of active dragons
     */
    public int getDragonCount() {
        return plugin.getDragonManager().getAllDragons().size();
    }

    /**
     * Get plugin version
     *
     * @return Plugin version string
     */
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    /**
     * Check if a specific integration is enabled
     *
     * @param integration Integration name (vault, placeholderapi, worldguard)
     * @return true if integration is enabled
     */
    public boolean isIntegrationEnabled(String integration) {
        return switch (integration.toLowerCase()) {
            case "vault" -> plugin.getPluginIntegrationManager().isVaultEnabled();
            case "placeholderapi" -> plugin.getPluginIntegrationManager().isPlaceholderAPIEnabled();
            case "worldguard" -> plugin.getPluginIntegrationManager().isWorldGuardEnabled();
            default -> false;
        };
    }
}
