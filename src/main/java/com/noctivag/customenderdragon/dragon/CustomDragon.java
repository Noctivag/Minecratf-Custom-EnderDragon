package com.noctivag.customenderdragon.dragon;

import com.noctivag.customenderdragon.CustomEnderDragonMod;
import com.noctivag.customenderdragon.config.ModConfig;
import com.noctivag.customenderdragon.visuals.CrystalStructureManager;
import com.noctivag.customenderdragon.visuals.DisplayEntityManager;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a custom EnderDragon with unique abilities and phases
 */
public class CustomDragon {
    private final EnderDragonEntity dragon;
    private final DragonVariant variant;
    private DragonPhase currentPhase;
    private final Map<String, Long> abilityCooldowns;
    private double maxHealth;
    private int particleTickCounter = 0;
    private int abilityTickCounter = 0;

    // 3D Visual Decorations
    private DisplayEntityManager.DragonDecorations decorations;
    private CrystalStructureManager.CrystalArena arena;

    public CustomDragon(EnderDragonEntity dragon, DragonVariant variant) {
        this.dragon = dragon;
        this.variant = variant;
        this.currentPhase = DragonPhase.PHASE_1;
        this.abilityCooldowns = new HashMap<>();

        setupDragon();
        setup3DVisuals();
    }

    /**
     * Sets up 3D visual decorations and arena structures
     */
    private void setup3DVisuals() {
        try {
            // Create 3D decorations around dragon
            decorations = CustomEnderDragonMod.getDisplayEntityManager().createDragonDecorations(dragon, variant);

            // Create crystal arena structures - use direct position instead of getPos()
            net.minecraft.util.math.BlockPos dragonPos = new net.minecraft.util.math.BlockPos(
                (int)dragon.getX(), 
                (int)dragon.getY(), 
                (int)dragon.getZ()
            );
            // Pass null for world since this is just a stub implementation
            arena = CustomEnderDragonMod.getCrystalStructureManager().createCrystalArena(dragonPos, variant, null);
        } catch (Exception e) {
            CustomEnderDragonMod.LOGGER.error("Failed to setup 3D visuals for dragon", e);
        }
    }

    private void setupDragon() {
        try {
            ModConfig.VariantConfig config = ModConfig.getVariantConfig(variant);
            if (config == null) {
                CustomEnderDragonMod.LOGGER.warn("Config not found for variant {}, using defaults", variant);
                this.maxHealth = 200.0;
                return;
            }
            
            // Set dragon attributes
            dragon.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(config.health);
            dragon.setHealth(config.health);
            this.maxHealth = config.health;

        // Set custom name with color
        Formatting formatting = switch (variant) {
            case FIRE -> Formatting.RED;
            case ICE -> Formatting.AQUA;
            case LIGHTNING -> Formatting.YELLOW;
            case SHADOW -> Formatting.DARK_PURPLE;
            case VOID -> Formatting.DARK_GRAY;
        };
        
            dragon.setCustomName(Text.literal(config.displayName + " Dragon").formatted(formatting, Formatting.BOLD));
            dragon.setCustomNameVisible(true);
        } catch (Exception e) {
            CustomEnderDragonMod.LOGGER.error("Failed to setup dragon attributes", e);
        }
    }

    /**
     * Called every tick to update dragon effects
     */
    public void tick() {
        if (dragon.isDead() || dragon.isRemoved()) {
            return;
        }

        try {
            // Update phase based on health
            updatePhase();

            // Particle effects every 5 ticks
            particleTickCounter++;
            if (particleTickCounter >= 5) {
                if (CustomEnderDragonMod.getParticleManager() != null) {
                    CustomEnderDragonMod.getParticleManager().spawnParticles(dragon, variant);
                }
                particleTickCounter = 0;
            }

            // Ability execution every 2 seconds (40 ticks)
            abilityTickCounter++;
            if (abilityTickCounter >= 40) {
                if (CustomEnderDragonMod.getAbilityManager() != null) {
                    CustomEnderDragonMod.getAbilityManager().executeAbilities(this);
                }
                abilityTickCounter = 0;
            }

            // Update decorations
            if (decorations != null) {
                decorations.update(dragon);
            }
        } catch (Exception e) {
            CustomEnderDragonMod.LOGGER.error("Error during dragon tick", e);
        }
    }

    private void updatePhase() {
        double healthPercent = (dragon.getHealth() / maxHealth) * 100;
        DragonPhase newPhase = DragonPhase.fromHealthPercent(healthPercent);

        if (newPhase != currentPhase) {
            currentPhase = newPhase;
            onPhaseChange();
        }
    }

    private void onPhaseChange() {
        try {
            if (CustomEnderDragonMod.getParticleManager() != null) {
                CustomEnderDragonMod.getParticleManager().spawnPhaseChangeEffect(dragon, variant, currentPhase);
            }
            
            // Broadcast phase change
            World world = dragon.getWorld();
            if (!world.isClient) {
                Text message = Text.literal("The " + variant.getDisplayName() + " Dragon has entered Phase " + 
                    currentPhase.getPhaseNumber() + "!").formatted(Formatting.GOLD, Formatting.BOLD);
                
                world.getPlayers().forEach(player -> {
                    if (player instanceof ServerPlayerEntity serverPlayer) {
                        serverPlayer.sendMessage(message, false);
                    }
                });
            }
        } catch (Exception e) {
            CustomEnderDragonMod.LOGGER.error("Error during phase change", e);
        }
    }

    public boolean isAbilityOnCooldown(String abilityName) {
        Long lastUsed = abilityCooldowns.get(abilityName);
        if (lastUsed == null) {
            return false;
        }

        ModConfig.AbilityConfig abilityConfig = ModConfig.getAbilityConfig(variant, abilityName);
        long cooldown = abilityConfig != null ? abilityConfig.cooldown : 100;
        
        return (System.currentTimeMillis() - lastUsed) < (cooldown * 50); // Convert ticks to ms
    }

    public void setAbilityCooldown(String abilityName) {
        abilityCooldowns.put(abilityName, System.currentTimeMillis());
    }

    public void remove() {
        try {
            if (decorations != null) {
                decorations.remove();
            }
            if (arena != null) {
                arena.remove();
            }
            dragon.remove(EnderDragonEntity.RemovalReason.DISCARDED);
        } catch (Exception e) {
            CustomEnderDragonMod.LOGGER.error("Error removing dragon", e);
        }
    }

    // Getters
    public EnderDragonEntity getDragon() {
        return dragon;
    }

    public DragonVariant getVariant() {
        return variant;
    }

    public DragonPhase getCurrentPhase() {
        return currentPhase;
    }

    public UUID getUUID() {
        return dragon.getUuid();
    }
}
