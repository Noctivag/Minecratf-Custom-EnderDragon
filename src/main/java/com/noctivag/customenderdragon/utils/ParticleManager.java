package com.noctivag.customenderdragon.utils;

import com.noctivag.customenderdragon.CustomEnderDragonPlugin;
import com.noctivag.customenderdragon.dragon.DragonPhase;
import com.noctivag.customenderdragon.dragon.DragonVariant;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EnderDragon;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Manages particle effects for custom dragons
 */
public class ParticleManager {
    private final CustomEnderDragonPlugin plugin;

    public ParticleManager(CustomEnderDragonPlugin plugin) {
        this.plugin = plugin;
    }

    public void spawnParticles(EnderDragon dragon, DragonVariant variant) {
        Location loc = dragon.getLocation();
        ConfigurationSection particleConfig = plugin.getConfig()
                .getConfigurationSection("variants." + variant.name() + ".particles");

        if (particleConfig == null) return;

        List<?> particles = plugin.getConfig().getList("variants." + variant.name() + ".particles");
        if (particles == null) return;

        for (Object particleObj : particles) {
            if (particleObj instanceof ConfigurationSection) {
                ConfigurationSection section = (ConfigurationSection) particleObj;
                try {
                    String typeStr = section.getString("type", "FLAME");
                    Particle particle = Particle.valueOf(typeStr);
                    int count = section.getInt("count", 5);
                    double offset = section.getDouble("offset", 0.5);

                    dragon.getWorld().spawnParticle(
                        particle,
                        loc.clone().add(0, 2, 0),
                        count,
                        offset, offset, offset,
                        0
                    );
                } catch (IllegalArgumentException e) {
                    // Invalid particle type, skip
                }
            }
        }

        // Add wing trail effects
        spawnWingTrail(dragon, variant);
    }

    private void spawnWingTrail(EnderDragon dragon, DragonVariant variant) {
        Location loc = dragon.getLocation();
        Vector direction = dragon.getVelocity().normalize();

        if (direction.lengthSquared() < 0.01) return;

        // Calculate wing positions
        Vector right = direction.clone().crossProduct(new Vector(0, 1, 0)).normalize().multiply(4);
        Location leftWing = loc.clone().add(right);
        Location rightWing = loc.clone().subtract(right);

        Particle trailParticle = getTrailParticle(variant);

        dragon.getWorld().spawnParticle(trailParticle, leftWing, 2, 0.1, 0.1, 0.1, 0);
        dragon.getWorld().spawnParticle(trailParticle, rightWing, 2, 0.1, 0.1, 0.1, 0);
    }

    private Particle getTrailParticle(DragonVariant variant) {
        return switch (variant) {
            case FIRE -> Particle.FLAME;
            case ICE -> Particle.SNOWFLAKE;
            case LIGHTNING -> Particle.ELECTRIC_SPARK;
            case SHADOW -> Particle.SMOKE_LARGE;
            case VOID -> Particle.PORTAL;
        };
    }

    public void spawnPhaseChangeEffect(EnderDragon dragon, DragonVariant variant, DragonPhase phase) {
        Location loc = dragon.getLocation();
        Particle mainParticle = getTrailParticle(variant);

        // Create expanding ring effect
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                double radius = 3.0 + (finalI * 2);
                int points = 30 + (finalI * 10);

                for (int j = 0; j < points; j++) {
                    double angle = 2 * Math.PI * j / points;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);

                    Location particleLoc = loc.clone().add(x, 1, z);
                    dragon.getWorld().spawnParticle(
                        mainParticle,
                        particleLoc,
                        5,
                        0.1, 0.1, 0.1,
                        0
                    );
                }
            }, i * 5L);
        }

        // Upward burst
        dragon.getWorld().spawnParticle(
            mainParticle,
            loc.clone().add(0, 2, 0),
            50,
            0.5, 2, 0.5,
            0.1
        );
    }

    public void spawnAbilityEffect(Location location, DragonVariant variant, String abilityName) {
        Particle particle = getTrailParticle(variant);

        switch (abilityName) {
            case "fireball", "fire-aura" -> {
                location.getWorld().spawnParticle(Particle.FLAME, location, 30, 1, 1, 1, 0.05);
                location.getWorld().spawnParticle(Particle.LAVA, location, 10, 0.5, 0.5, 0.5, 0);
            }
            case "ice-shard", "freeze-aura" -> {
                location.getWorld().spawnParticle(Particle.SNOWFLAKE, location, 30, 1, 1, 1, 0.05);
                location.getWorld().spawnParticle(Particle.CLOUD, location, 15, 0.5, 0.5, 0.5, 0);
            }
            case "lightning-strike" -> {
                location.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, location, 40, 0.5, 2, 0.5, 0.1);
                location.getWorld().spawnParticle(Particle.END_ROD, location, 20, 0.3, 1, 0.3, 0.05);
            }
            case "shadow-strike" -> {
                location.getWorld().spawnParticle(Particle.SMOKE_LARGE, location, 30, 1, 1, 1, 0.05);
                location.getWorld().spawnParticle(Particle.SQUID_INK, location, 15, 0.5, 0.5, 0.5, 0);
            }
            case "void-beam" -> {
                location.getWorld().spawnParticle(Particle.PORTAL, location, 50, 1, 1, 1, 0.1);
                location.getWorld().spawnParticle(Particle.DRAGON_BREATH, location, 25, 0.5, 0.5, 0.5, 0.05);
            }
        }
    }

    public void spawnCircleEffect(Location center, double radius, Particle particle, int points) {
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            Location particleLoc = center.clone().add(x, 0, z);
            center.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
        }
    }

    public void spawnSpiralEffect(Location center, Particle particle, int height, double radius) {
        for (int i = 0; i < height * 10; i++) {
            double angle = i * 0.3;
            double y = i * 0.1;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);

            Location particleLoc = center.clone().add(x, y, z);
            center.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
        }
    }
}
