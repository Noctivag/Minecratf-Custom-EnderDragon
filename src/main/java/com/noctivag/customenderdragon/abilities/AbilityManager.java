package com.noctivag.customenderdragon.abilities;

import com.noctivag.customenderdragon.dragon.CustomDragon;
import com.noctivag.customenderdragon.dragon.DragonVariant;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.Random;

/**
 * Manages and executes custom dragon abilities for Fabric 1.21.1
 */
public class AbilityManager {
    private final Random random = new Random();

    public void executeAbilities(CustomDragon customDragon) {
        DragonVariant variant = customDragon.getVariant();
        EnderDragonEntity dragon = customDragon.getDragon();

        if (dragon.getWorld().isClient) {
            return;
        }

        // Find nearest player
        PlayerEntity target = dragon.getWorld().getClosestPlayer(dragon, 30.0);
        if (target == null) {
            return;
        }

        switch (variant) {
            case FIRE -> executeFireAbilities(customDragon, target);
            case ICE -> executeIceAbilities(customDragon, target);
            case LIGHTNING -> executeLightningAbilities(customDragon, target);
            case SHADOW -> executeShadowAbilities(customDragon, target);
            case VOID -> executeVoidAbilities(customDragon, target);
        }
    }

    private void executeFireAbilities(CustomDragon customDragon, PlayerEntity target) {
        EnderDragonEntity dragon = customDragon.getDragon();
        int phase = customDragon.getCurrentPhase().getPhaseNumber();

        // Fireball attack
        if (!customDragon.isAbilityOnCooldown("fireball")) {
            spawnFireball(dragon, target);
            customDragon.setAbilityCooldown("fireball");
        }

        // Fire aura
        applyFireAura(dragon);

        // Meteor shower (Phase 3 only)
        if (phase >= 3 && !customDragon.isAbilityOnCooldown("meteor-shower")) {
            spawnMeteorShower(dragon);
            customDragon.setAbilityCooldown("meteor-shower");
        }
    }

    private void executeIceAbilities(CustomDragon customDragon, PlayerEntity target) {
        EnderDragonEntity dragon = customDragon.getDragon();

        // Ice shard damage + slowness
        if (!customDragon.isAbilityOnCooldown("ice-shard")) {
            target.damage(dragon.getDamageSources().mobAttack(dragon), 10.0f);
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 1));
            customDragon.setAbilityCooldown("ice-shard");
        }

        // Freeze aura
        applyFreezeAura(dragon);
    }

    private void executeLightningAbilities(CustomDragon customDragon, PlayerEntity target) {
        EnderDragonEntity dragon = customDragon.getDragon();
        
        spawnLightningStrike(dragon, customDragon, target);
    }

    private void spawnLightningStrike(EnderDragonEntity dragon, CustomDragon customDragon, PlayerEntity target) {
        if (!customDragon.isAbilityOnCooldown("lightning-strike") && dragon.getWorld() instanceof ServerWorld serverWorld) {
            // Strike lightning at target position
            net.minecraft.util.math.BlockPos targetPos = new net.minecraft.util.math.BlockPos(
                (int)target.getX(), 
                (int)target.getY(), 
                (int)target.getZ()
            );
            EntityType.LIGHTNING_BOLT.spawn(serverWorld, targetPos, SpawnReason.TRIGGERED);
            customDragon.setAbilityCooldown("lightning-strike");
        }
    }

    private void executeShadowAbilities(CustomDragon customDragon, PlayerEntity target) {
        EnderDragonEntity dragon = customDragon.getDragon();

        if (!customDragon.isAbilityOnCooldown("shadow-strike")) {
            target.damage(dragon.getDamageSources().mobAttack(dragon), 15.0f);
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 0));
            customDragon.setAbilityCooldown("shadow-strike");
        }
    }

    private void executeVoidAbilities(CustomDragon customDragon, PlayerEntity target) {
        EnderDragonEntity dragon = customDragon.getDragon();

        if (!customDragon.isAbilityOnCooldown("void-pulse")) {
            target.damage(dragon.getDamageSources().mobAttack(dragon), 20.0f);
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 100, 1));
            customDragon.setAbilityCooldown("void-pulse");
        }
    }

    // Helper methods
    private void spawnFireball(EnderDragonEntity dragon, PlayerEntity target) {
        Vec3d dragonPos = new Vec3d(dragon.getX(), dragon.getY(), dragon.getZ());
        Vec3d targetPos = new Vec3d(target.getX(), target.getY(), target.getZ());
        Vec3d direction = new Vec3d(target.getX() - dragon.getX(), target.getY() - dragon.getY(), target.getZ() - dragon.getZ()).normalize();
        DragonFireballEntity fireball = new DragonFireballEntity(dragon.getWorld(), dragon, direction);
        fireball.setPosition(dragon.getX(), dragon.getY() + 2, dragon.getZ());
        dragon.getWorld().spawnEntity(fireball);
    }

    private void applyFireAura(EnderDragonEntity dragon) {
        Box box = dragon.getBoundingBox().expand(10.0);
        List<Entity> nearbyEntities = dragon.getWorld().getOtherEntities(dragon, box, 
            entity -> entity instanceof LivingEntity);
                
        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity living) {
                living.setOnFireFor(5);
            }
        }
    }

    private void applyFreezeAura(EnderDragonEntity dragon) {
        Box box = dragon.getBoundingBox().expand(10.0);
        List<Entity> nearbyEntities = dragon.getWorld().getOtherEntities(dragon, box,
            entity -> entity instanceof LivingEntity);

        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity living) {
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 2));
            }
        }
    }

    private void spawnMeteorShower(EnderDragonEntity dragon) {
        if (!(dragon.getWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        for (int i = 0; i < 15; i++) {
            int offsetX = random.nextInt(40) - 20;
            int offsetZ = random.nextInt(40) - 20;
            
            serverWorld.createExplosion(null, 
                dragon.getX() + offsetX, 
                dragon.getY() + 20, 
                dragon.getZ() + offsetZ, 
                2.0f, 
                ServerWorld.ExplosionSourceType.MOB);
        }
    }
}
