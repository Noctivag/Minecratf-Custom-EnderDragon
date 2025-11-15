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
        ServerWorld world = customDragon.getWorld();

        if (world == null || world.isClient) {
            return;
        }

        // Find nearest player
        PlayerEntity target = world.getClosestPlayer(dragon, 30.0);
        if (target == null) {
            return;
        }

        switch (variant) {
            case FIRE -> executeFireAbilities(customDragon, world, target);
            case ICE -> executeIceAbilities(customDragon, world, target);
            case LIGHTNING -> executeLightningAbilities(customDragon, world, target);
            case SHADOW -> executeShadowAbilities(customDragon, world, target);
            case VOID -> executeVoidAbilities(customDragon, world, target);
        }
    }

    private void executeFireAbilities(CustomDragon customDragon, ServerWorld world, PlayerEntity target) {
        EnderDragonEntity dragon = customDragon.getDragon();
        int phase = customDragon.getCurrentPhase().getPhaseNumber();

        // Fireball attack
        if (!customDragon.isAbilityOnCooldown("fireball")) {
            spawnFireball(world, dragon, target);
            customDragon.setAbilityCooldown("fireball");
        }

        // Fire aura
        applyFireAura(world, dragon);

        // Meteor shower (Phase 3 only)
        if (phase >= 3 && !customDragon.isAbilityOnCooldown("meteor-shower")) {
            spawnMeteorShower(world, dragon);
            customDragon.setAbilityCooldown("meteor-shower");
        }
    }

    private void executeIceAbilities(CustomDragon customDragon, ServerWorld world, PlayerEntity target) {
        EnderDragonEntity dragon = customDragon.getDragon();

        // Ice shard damage + slowness
        if (!customDragon.isAbilityOnCooldown("ice-shard")) {
            target.damage(dragon.getDamageSources().mobAttack(dragon), 10.0f);
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 1));
            customDragon.setAbilityCooldown("ice-shard");
        }

        // Freeze aura
        applyFreezeAura(world, dragon);
    }

    private void executeLightningAbilities(CustomDragon customDragon, ServerWorld world, PlayerEntity target) {
        EnderDragonEntity dragon = customDragon.getDragon();

        spawnLightningStrike(world, customDragon, target);
    }

    private void spawnLightningStrike(ServerWorld world, CustomDragon customDragon, PlayerEntity target) {
        if (!customDragon.isAbilityOnCooldown("lightning-strike")) {
            // Strike lightning at target position
            net.minecraft.util.math.BlockPos targetPos = new net.minecraft.util.math.BlockPos(
                (int)target.getX(), 
                (int)target.getY(), 
                (int)target.getZ()
            );
            EntityType.LIGHTNING_BOLT.spawn(world, targetPos, SpawnReason.TRIGGERED);
            customDragon.setAbilityCooldown("lightning-strike");
        }
    }

    private void executeShadowAbilities(CustomDragon customDragon, ServerWorld world, PlayerEntity target) {
        EnderDragonEntity dragon = customDragon.getDragon();

        if (!customDragon.isAbilityOnCooldown("shadow-strike")) {
            target.damage(dragon.getDamageSources().mobAttack(dragon), 15.0f);
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 0));
            customDragon.setAbilityCooldown("shadow-strike");
        }
    }

    private void executeVoidAbilities(CustomDragon customDragon, ServerWorld world, PlayerEntity target) {
        EnderDragonEntity dragon = customDragon.getDragon();

        if (!customDragon.isAbilityOnCooldown("void-pulse")) {
            target.damage(dragon.getDamageSources().mobAttack(dragon), 20.0f);
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 100, 1));
            customDragon.setAbilityCooldown("void-pulse");
        }
    }

    // Helper methods
    private void spawnFireball(ServerWorld world, EnderDragonEntity dragon, PlayerEntity target) {
        Vec3d dragonPos = new Vec3d(dragon.getX(), dragon.getY(), dragon.getZ());
        Vec3d targetPos = new Vec3d(target.getX(), target.getY(), target.getZ());
        Vec3d direction = new Vec3d(target.getX() - dragon.getX(), target.getY() - dragon.getY(), target.getZ() - dragon.getZ()).normalize();
        DragonFireballEntity fireball = new DragonFireballEntity(world, dragon, direction);
        fireball.setPosition(dragon.getX(), dragon.getY() + 2, dragon.getZ());
        world.spawnEntity(fireball);
    }

    private void applyFireAura(ServerWorld world, EnderDragonEntity dragon) {
        Box box = dragon.getBoundingBox().expand(10.0);
        List<Entity> nearbyEntities = world.getOtherEntities(dragon, box, 
            entity -> entity instanceof LivingEntity);
                
        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity living) {
                living.setOnFireFor(5);
            }
        }
    }

    private void applyFreezeAura(ServerWorld world, EnderDragonEntity dragon) {
        Box box = dragon.getBoundingBox().expand(10.0);
        List<Entity> nearbyEntities = world.getOtherEntities(dragon, box,
            entity -> entity instanceof LivingEntity);

        for (Entity entity : nearbyEntities) {
            if (entity instanceof LivingEntity living) {
                living.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 2));
            }
        }
    }

    private void spawnMeteorShower(ServerWorld world, EnderDragonEntity dragon) {
        for (int i = 0; i < 15; i++) {
            int offsetX = random.nextInt(40) - 20;
            int offsetZ = random.nextInt(40) - 20;
            
            world.createExplosion(null, 
                dragon.getX() + offsetX, 
                dragon.getY() + 20, 
                dragon.getZ() + offsetZ, 
                2.0f, 
                ServerWorld.ExplosionSourceType.MOB);
        }
    }
}
