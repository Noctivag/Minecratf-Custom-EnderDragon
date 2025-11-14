package com.noctivag.customenderdragon.listeners;

import com.noctivag.customenderdragon.CustomEnderDragonMod;
import com.noctivag.customenderdragon.config.ModConfig;
import com.noctivag.customenderdragon.dragon.CustomDragon;
import com.noctivag.customenderdragon.dragon.DragonVariant;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Handles Fabric events for custom dragons
 */
public class DragonEventHandler {

    private static final Random RANDOM = new Random();
    private static final Set<UUID> PROCESSED_DRAGONS = new HashSet<>();

    public static void register() {
        // Use server tick to detect and convert dragons safely
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world instanceof ServerWorld serverWorld) {
                try {
                    checkForNewDragons(serverWorld);
                } catch (Exception e) {
                    CustomEnderDragonMod.LOGGER.error("Error checking for new dragons", e);
                }
            }
        });

        // Listen for dragon death
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof EnderDragonEntity dragon) {
                onDragonDeath(dragon, damageSource);
            }
        });
    }

    private static void checkForNewDragons(ServerWorld world) {
        // Safety check: ensure managers are initialized
        if (CustomEnderDragonMod.getDragonManager() == null) {
            return;
        }

        // Don't convert if disabled in config
        ModConfig.Config config = ModConfig.getConfig();
        if (config == null || config.general == null || !config.general.enableCustomDragons) {
            return;
        }

        // Check all entities in the world for EnderDragons
        world.iterateEntities().forEach(entity -> {
            if (entity instanceof EnderDragonEntity dragon) {
                UUID dragonId = dragon.getUuid();
                
                // Skip if already processed or already a custom dragon
                if (PROCESSED_DRAGONS.contains(dragonId) || 
                    CustomEnderDragonMod.getDragonManager().isCustomDragon(dragon)) {
                    return;
                }
                
                // Mark as processed to avoid double-conversion
                PROCESSED_DRAGONS.add(dragonId);
                
                // Convert to custom dragon
                convertDragon(dragon, world);
            }
        });
    }

    private static void convertDragon(EnderDragonEntity dragon, ServerWorld world) {

        // Select random variant based on config weights
        DragonVariant variant = selectRandomVariant();

        try {
            // Register as custom dragon
            CustomDragon customDragon = new CustomDragon(dragon, variant);
            CustomEnderDragonMod.getDragonManager().registerExistingDragon(dragon, customDragon);

            // Broadcast spawn message
            Text message = Text.literal("A " + variant.getDisplayName() + 
                " Dragon has awakened!").formatted(Formatting.DARK_PURPLE, Formatting.BOLD);
            
            world.getPlayers().forEach(player -> {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    serverPlayer.sendMessage(message, false);
                }
            });

            CustomEnderDragonMod.LOGGER.info("Converted vanilla EnderDragon to {} variant", variant.name());
        } catch (Exception e) {
            CustomEnderDragonMod.LOGGER.error("Failed to convert EnderDragon to custom variant", e);
        }
    }

    private static DragonVariant selectRandomVariant() {
        ModConfig.Config config = ModConfig.getConfig();
        if (config == null || config.variantWeights == null) {
            CustomEnderDragonMod.LOGGER.warn("Config not loaded, defaulting to FIRE variant");
            return DragonVariant.FIRE;
        }
        
        ModConfig.VariantWeightsConfig weights = config.variantWeights;
        
        // Calculate total weight
        int totalWeight = weights.fireWeight + weights.iceWeight + 
                         weights.lightningWeight + weights.shadowWeight + 
                         weights.voidWeight;

        if (totalWeight <= 0) {
            // Fallback to FIRE if all weights are 0
            return DragonVariant.FIRE;
        }

        // Select random variant based on weights
        int roll = RANDOM.nextInt(totalWeight);
        int currentWeight = 0;

        currentWeight += weights.fireWeight;
        if (roll < currentWeight) return DragonVariant.FIRE;

        currentWeight += weights.iceWeight;
        if (roll < currentWeight) return DragonVariant.ICE;

        currentWeight += weights.lightningWeight;
        if (roll < currentWeight) return DragonVariant.LIGHTNING;

        currentWeight += weights.shadowWeight;
        if (roll < currentWeight) return DragonVariant.SHADOW;

        return DragonVariant.VOID;
    }

    private static void onDragonDeath(EnderDragonEntity dragon, DamageSource source) {
        // Clean up from processed list
        PROCESSED_DRAGONS.remove(dragon.getUuid());
        
        CustomDragon customDragon = CustomEnderDragonMod.getDragonManager().getCustomDragon(dragon);
        
        if (customDragon == null) {
            return;
        }

        // Broadcast death message
        if (dragon.getWorld() instanceof ServerWorld serverWorld) {
            Text message = Text.literal(customDragon.getVariant().getDisplayName() + 
                " Dragon has been slain!").formatted(Formatting.YELLOW, Formatting.BOLD);

            serverWorld.getPlayers().forEach(player -> {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    serverPlayer.sendMessage(message, false);
                }
            });
        }

        // Drop custom loot (TODO: Implement based on config)
        // For now, dragons drop vanilla loot

        // Remove from manager
        CustomEnderDragonMod.getDragonManager().removeDragon(dragon.getUuid());
    }
}
