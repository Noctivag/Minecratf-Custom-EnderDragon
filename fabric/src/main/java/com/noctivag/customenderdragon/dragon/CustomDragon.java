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
        // Create 3D decorations around dragon
        decorations = CustomEnderDragonMod.getDisplayEntityManager().createDragonDecorations(dragon, variant);

        // Create crystal arena structures
        arena = CustomEnderDragonMod.getCrystalStructureManager().createCrystalArena(dragon.getBlockPos(), variant, dragon.getWorld());
    }

    private void setupDragon() {
        ModConfig.VariantConfig config = ModConfig.getVariantConfig(variant);
        
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
    }

    /**
     * Called every tick to update dragon effects
     */
    public void tick() {
        if (dragon.isDead() || dragon.isRemoved()) {
            return;
        }

        // Update phase based on health
        updatePhase();

        // Particle effects every tick
        particleTickCounter++;
        if (particleTickCounter >= 5) {
            CustomEnderDragonMod.getParticleManager().spawnParticles(dragon, variant);
            particleTickCounter = 0;
        }

        // Ability execution every second
        abilityTickCounter++;
        if (abilityTickCounter >= 40) {
            CustomEnderDragonMod.getAbilityManager().executeAbilities(this);
            abilityTickCounter = 0;
        }

        // Update decorations
        if (decorations != null) {
            decorations.update(dragon);
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
        CustomEnderDragonMod.getParticleManager().spawnPhaseChangeEffect(dragon, variant, currentPhase);
        
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
        if (decorations != null) {
            decorations.remove();
        }
        if (arena != null) {
            arena.remove();
        }
        dragon.remove(EnderDragonEntity.RemovalReason.DISCARDED);
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
