package com.noctivag.customenderdragon.integration;

import com.noctivag.customenderdragon.CustomEnderDragonPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Manages integrations with other plugins (Vault, PlaceholderAPI, WorldGuard)
 * All integrations are OPTIONAL - plugin works perfectly without them
 */
public class PluginIntegrationManager {
    private final CustomEnderDragonPlugin plugin;

    // Integration flags
    private boolean vaultEnabled = false;
    private boolean placeholderAPIEnabled = false;
    private boolean worldGuardEnabled = false;

    // Vault economy
    private Economy economy = null;

    public PluginIntegrationManager(CustomEnderDragonPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Initialize all plugin integrations
     */
    public void setupIntegrations() {
        setupVault();
        setupPlaceholderAPI();
        setupWorldGuard();

        // Log integration status
        plugin.getLogger().info("━━━━━━━━ Plugin Integrations ━━━━━━━━");
        plugin.getLogger().info("  Vault: " + (vaultEnabled ? "✓ Enabled" : "✗ Not Found"));
        plugin.getLogger().info("  PlaceholderAPI: " + (placeholderAPIEnabled ? "✓ Enabled" : "✗ Not Found"));
        plugin.getLogger().info("  WorldGuard: " + (worldGuardEnabled ? "✓ Enabled" : "✗ Not Found"));
        plugin.getLogger().info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    /**
     * Setup Vault integration for economy rewards
     */
    private void setupVault() {
        if (!plugin.getConfig().getBoolean("integrations.vault.enabled", true)) {
            plugin.getLogger().info("Vault integration disabled in config");
            return;
        }

        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager()
                .getRegistration(Economy.class);

        if (rsp == null) {
            plugin.getLogger().warning("Vault found but no economy plugin detected!");
            return;
        }

        economy = rsp.getProvider();
        vaultEnabled = true;
        plugin.getLogger().info("Vault economy integration enabled!");
    }

    /**
     * Setup PlaceholderAPI integration
     */
    private void setupPlaceholderAPI() {
        if (!plugin.getConfig().getBoolean("integrations.placeholderapi.enabled", true)) {
            plugin.getLogger().info("PlaceholderAPI integration disabled in config");
            return;
        }

        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return;
        }

        // Register our expansion
        new DragonPlaceholderExpansion(plugin).register();
        placeholderAPIEnabled = true;
        plugin.getLogger().info("PlaceholderAPI integration enabled!");
    }

    /**
     * Setup WorldGuard integration for region protection
     */
    private void setupWorldGuard() {
        if (!plugin.getConfig().getBoolean("integrations.worldguard.enabled", true)) {
            plugin.getLogger().info("WorldGuard integration disabled in config");
            return;
        }

        if (plugin.getServer().getPluginManager().getPlugin("WorldGuard") == null) {
            return;
        }

        worldGuardEnabled = true;
        plugin.getLogger().info("WorldGuard integration enabled!");
    }

    /**
     * Give economy reward to player (if Vault is enabled)
     */
    public boolean giveMoneyReward(org.bukkit.entity.Player player, double amount) {
        if (!vaultEnabled || economy == null) {
            return false;
        }

        if (amount <= 0) {
            return false;
        }

        economy.depositPlayer(player, amount);
        return true;
    }

    /**
     * Check if location is in a protected WorldGuard region
     */
    public boolean canSpawnInLocation(org.bukkit.Location location) {
        if (!worldGuardEnabled) {
            return true; // No WorldGuard = no restrictions
        }

        // WorldGuard integration would go here
        // For now, we'll return true (allow spawning)
        // In a full implementation, check region flags
        return true;
    }

    // ========== Getters ==========

    public boolean isVaultEnabled() {
        return vaultEnabled;
    }

    public boolean isPlaceholderAPIEnabled() {
        return placeholderAPIEnabled;
    }

    public boolean isWorldGuardEnabled() {
        return worldGuardEnabled;
    }

    public Economy getEconomy() {
        return economy;
    }
}
