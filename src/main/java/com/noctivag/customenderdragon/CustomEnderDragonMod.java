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
        try {
            INSTANCE = this;
            LOGGER.info("Initializing Custom Ender Dragon mod...");

            // Load configuration
            try {
                ModConfig.load();
            } catch (Exception e) {
                LOGGER.error("Failed to load config, continuing with defaults", e);
            }

            // Initialize managers with error handling
            try {
                this.dragonManager = new DragonManager();
            } catch (Exception e) {
                LOGGER.error("Failed to initialize DragonManager", e);
                throw e;
            }

            try {
                this.abilityManager = new AbilityManager();
            } catch (Exception e) {
                LOGGER.error("Failed to initialize AbilityManager", e);
                throw e;
            }

            try {
                this.particleManager = new ParticleManager();
            } catch (Exception e) {
                LOGGER.error("Failed to initialize ParticleManager", e);
                throw e;
            }

            try {
                this.displayEntityManager = new DisplayEntityManager();
            } catch (Exception e) {
                LOGGER.error("Failed to initialize DisplayEntityManager", e);
                throw e;
            }

            try {
                this.crystalStructureManager = new CrystalStructureManager();
            } catch (Exception e) {
                LOGGER.error("Failed to initialize CrystalStructureManager", e);
                throw e;
            }

            // Register commands
            try {
                DragonCommandRegistration.register();
            } catch (Exception e) {
                LOGGER.error("Failed to register commands", e);
                throw e;
            }

            // Register event handlers
            try {
                DragonEventHandler.register();
            } catch (Exception e) {
                LOGGER.error("Failed to register event handlers", e);
                throw e;
            }

            // Register tick handler for dragons
            try {
                ServerTickEvents.END_SERVER_TICK.register(server -> {
                    try {
                        if (dragonManager != null) {
                            dragonManager.tick();
                        }
                    } catch (Exception e) {
                        LOGGER.error("Error during dragon manager tick", e);
                    }
                });
            } catch (Exception e) {
                LOGGER.error("Failed to register tick handler", e);
                throw e;
            }

            LOGGER.info("Custom Ender Dragon mod initialized successfully!");
        } catch (Exception e) {
            LOGGER.error("CRITICAL: Failed to initialize Custom Ender Dragon mod", e);
            throw new RuntimeException("Failed to initialize mod", e);
        }
    }

    // Static getters for managers with null safety
    public static DragonManager getDragonManager() {
        if (INSTANCE == null) {
            LOGGER.error("Mod INSTANCE is null, mod may not be initialized");
            return null;
        }
        return INSTANCE.dragonManager;
    }

    public static AbilityManager getAbilityManager() {
        if (INSTANCE == null) {
            LOGGER.error("Mod INSTANCE is null, mod may not be initialized");
            return null;
        }
        return INSTANCE.abilityManager;
    }

    public static ParticleManager getParticleManager() {
        if (INSTANCE == null) {
            LOGGER.error("Mod INSTANCE is null, mod may not be initialized");
            return null;
        }
        return INSTANCE.particleManager;
    }

    public static DisplayEntityManager getDisplayEntityManager() {
        if (INSTANCE == null) {
            LOGGER.error("Mod INSTANCE is null, mod may not be initialized");
            return null;
        }
        return INSTANCE.displayEntityManager;
    }

    public static CrystalStructureManager getCrystalStructureManager() {
        if (INSTANCE == null) {
            LOGGER.error("Mod INSTANCE is null, mod may not be initialized");
            return null;
        }
        return INSTANCE.crystalStructureManager;
    }
}
