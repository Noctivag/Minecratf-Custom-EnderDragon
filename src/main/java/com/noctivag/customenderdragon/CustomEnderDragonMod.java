package com.noctivag.customenderdragon;

import com.noctivag.customenderdragon.abilities.AbilityManager;
import com.noctivag.customenderdragon.commands.DragonCommandRegistration;
import com.noctivag.customenderdragon.config.ModConfig;
import com.noctivag.customenderdragon.dragon.DragonManager;
import com.noctivag.customenderdragon.listeners.DragonEventHandler;
import com.noctivag.customenderdragon.utils.ParticleManager;
import com.noctivag.customenderdragon.visuals.CrystalStructureManager;
import com.noctivag.customenderdragon.visuals.DisplayEntityManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main mod initializer for Custom Ender Dragon (Fabric 1.21.1)
 */
public class CustomEnderDragonMod implements ModInitializer {
    public static final String MOD_ID = "customenderdragon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static CustomEnderDragonMod INSTANCE;

    private DragonManager dragonManager;
    private AbilityManager abilityManager;
    private ParticleManager particleManager;
    private DisplayEntityManager displayEntityManager;
    private CrystalStructureManager crystalStructureManager;

    @Override
    public void onInitialize() {
        INSTANCE = this;
        LOGGER.info("Initializing Custom Ender Dragon mod...");

        // Load configuration
        ModConfig.load();

        // Initialize managers
        this.dragonManager = new DragonManager();
        this.abilityManager = new AbilityManager();
        this.particleManager = new ParticleManager();
        this.displayEntityManager = new DisplayEntityManager();
        this.crystalStructureManager = new CrystalStructureManager();

        // Register commands
        DragonCommandRegistration.register();

        // Register event handlers
        DragonEventHandler.register();

        // Register tick handler for dragons
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            dragonManager.tick();
        });

        LOGGER.info("Custom Ender Dragon mod initialized successfully!");
    }

    // Static getters for managers
    public static DragonManager getDragonManager() {
        return INSTANCE.dragonManager;
    }

    public static AbilityManager getAbilityManager() {
        return INSTANCE.abilityManager;
    }

    public static ParticleManager getParticleManager() {
        return INSTANCE.particleManager;
    }

    public static DisplayEntityManager getDisplayEntityManager() {
        return INSTANCE.displayEntityManager;
    }

    public static CrystalStructureManager getCrystalStructureManager() {
        return INSTANCE.crystalStructureManager;
    }
}
