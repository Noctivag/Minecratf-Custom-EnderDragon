package com.noctivag.customenderdragon.utils;

import com.noctivag.customenderdragon.dragon.DragonPhase;
import com.noctivag.customenderdragon.dragon.DragonVariant;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

/**
 * Manages particle effects for custom dragons (Fabric)
 */
public class ParticleManager {

    public void spawnParticles(EnderDragonEntity dragon, DragonVariant variant) {
        if (!(dragon.getWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        ParticleEffect particle = getVariantParticle(variant);
        Vec3d pos = dragon.getPos().add(0, 2, 0);

        spawnParticlesForPlayers(serverWorld, particle, pos.x, pos.y, pos.z, 5, 0.5, 0.5, 0.5, 0.0);

        // Wing trail effects
        spawnWingTrail(serverWorld, dragon, particle);
    }

    private void spawnWingTrail(ServerWorld world, EnderDragonEntity dragon, ParticleEffect particle) {
        Vec3d pos = dragon.getPos();
        Vec3d velocity = dragon.getVelocity();

        if (velocity.lengthSquared() < 0.01) {
            return;
        }

        Vec3d right = new Vec3d(velocity.z, 0, -velocity.x).normalize().multiply(4);
        Vec3d leftWing = pos.add(right);
        Vec3d rightWing = pos.subtract(right);

        spawnParticlesForPlayers(world, particle, leftWing.x, leftWing.y, leftWing.z, 2, 0.1, 0.1, 0.1, 0.0);
        spawnParticlesForPlayers(world, particle, rightWing.x, rightWing.y, rightWing.z, 2, 0.1, 0.1, 0.1, 0.0);
    }

    public void spawnPhaseChangeEffect(EnderDragonEntity dragon, DragonVariant variant, DragonPhase phase) {
        if (!(dragon.getWorld() instanceof ServerWorld serverWorld)) {
            return;
        }

        ParticleEffect particle = getVariantParticle(variant);
        Vec3d pos = dragon.getPos();

        // Create expanding ring effect
        for (int ring = 0; ring < 3; ring++) {
            double radius = 3.0 + (ring * 2);
            int points = 30 + (ring * 10);

            for (int j = 0; j < points; j++) {
                double angle = 2 * Math.PI * j / points;
                double x = pos.x + radius * Math.cos(angle);
                double z = pos.z + radius * Math.sin(angle);
                double y = pos.y + 1;

                spawnParticlesForPlayers(serverWorld, particle, x, y, z, 1, 0, 0, 0, 0.0);
            }
        }
    }

    /**
     * Spawns particles for all nearby players on the server.
     * This is the correct approach for Minecraft 1.21.10+ server-side particle spawning.
     */
    private void spawnParticlesForPlayers(ServerWorld world, ParticleEffect particle,
                                         double x, double y, double z,
                                         int count, double deltaX, double deltaY, double deltaZ,
                                         double speed) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            world.spawnParticles(player, particle, false, x, y, z, count, deltaX, deltaY, deltaZ, speed);
        }
    }

    private ParticleEffect getVariantParticle(DragonVariant variant) {
        return switch (variant) {
            case FIRE -> ParticleTypes.FLAME;
            case ICE -> ParticleTypes.SNOWFLAKE;
            case LIGHTNING -> ParticleTypes.ELECTRIC_SPARK;
            case SHADOW -> ParticleTypes.LARGE_SMOKE;
            case VOID -> ParticleTypes.PORTAL;
        };
    }
}
