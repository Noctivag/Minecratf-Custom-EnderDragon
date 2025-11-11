package com.noctivag.customenderdragon;

import com.noctivag.customenderdragon.abilities.AbilityManager;
import com.noctivag.customenderdragon.commands.DragonCommand;
import com.noctivag.customenderdragon.dragon.DragonManager;
import com.noctivag.customenderdragon.listeners.DragonListener;
import com.noctivag.customenderdragon.utils.ParticleManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Custom EnderDragon Plugin - Next Level Edition
 *
 * Features:
 * - 5 Unique Dragon Variants (Fire, Ice, Lightning, Shadow, Void)
 * - Dynamic Phase System (3 phases based on health)
 * - Custom Abilities per Variant
 * - Epic Particle Effects
 * - Boss Bar with Phase Indicators
 * - Custom Loot System
 * - Fully Configurable
 *
 * @author Noctivag
 * @version 2.0.0
 */
public class CustomEnderDragonPlugin extends JavaPlugin {

    private DragonManager dragonManager;
    private ParticleManager particleManager;
    private AbilityManager abilityManager;

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();

        // Initialize managers
        dragonManager = new DragonManager(this);
        particleManager = new ParticleManager(this);
        abilityManager = new AbilityManager(this);

        // Register commands
        DragonCommand dragonCommand = new DragonCommand(this);
        getCommand("customdragon").setExecutor(dragonCommand);
        getCommand("customdragon").setTabCompleter(dragonCommand);

        // Register listeners
        getServer().getPluginManager().registerEvents(new DragonListener(this), this);

        // Startup message
        getLogger().info("╔══════════════════════════════════════╗");
        getLogger().info("║   Custom EnderDragon Plugin v2.0    ║");
        getLogger().info("║          Next Level Edition          ║");
        getLogger().info("╠══════════════════════════════════════╣");
        getLogger().info("║  Features:                           ║");
        getLogger().info("║  • 5 Dragon Variants                 ║");
        getLogger().info("║  • 3-Phase Combat System             ║");
        getLogger().info("║  • Custom Abilities                  ║");
        getLogger().info("║  • Epic Particle Effects             ║");
        getLogger().info("║  • Boss Bars & Custom Loot           ║");
        getLogger().info("╚══════════════════════════════════════╝");
        getLogger().info("");
        getLogger().info("Available Variants:");
        getLogger().info("  • FIRE      - Meteors & Flames");
        getLogger().info("  • ICE       - Blizzards & Freezing");
        getLogger().info("  • LIGHTNING - Thunder & Chain Strikes");
        getLogger().info("  • SHADOW    - Teleportation & Darkness");
        getLogger().info("  • VOID      - Reality Warping & Wither");
        getLogger().info("");
        getLogger().info("Use /customdragon help for commands!");
    }

    @Override
    public void onDisable() {
        // Clean up all dragons
        if (dragonManager != null) {
            dragonManager.shutdown();
        }

        getLogger().info("Custom EnderDragon Plugin disabled. All dragons removed.");
    }

    public DragonManager getDragonManager() {
        return dragonManager;
    }

    public ParticleManager getParticleManager() {
        return particleManager;
    }

    public AbilityManager getAbilityManager() {
        return abilityManager;
    }
}
