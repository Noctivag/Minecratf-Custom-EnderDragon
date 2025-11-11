package com.noctivag.customenderdragon.abilities;

import com.noctivag.customenderdragon.CustomEnderDragonPlugin;
import com.noctivag.customenderdragon.dragon.CustomDragon;
import com.noctivag.customenderdragon.dragon.DragonVariant;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.Random;

/**
 * Manages and executes custom dragon abilities
 */
public class AbilityManager {
    private final CustomEnderDragonPlugin plugin;
    private final Random random;

    public AbilityManager(CustomEnderDragonPlugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    public void executeAbilities(CustomDragon customDragon) {
        DragonVariant variant = customDragon.getVariant();
        EnderDragon dragon = customDragon.getDragon();

        // Find nearby players
        Collection<Entity> nearbyEntities = dragon.getNearbyEntities(30, 30, 30);
        Player target = findNearestPlayer(dragon.getLocation(), nearbyEntities);

        if (target == null) return;

        String variantPath = "variants." + variant.name() + ".abilities.";

        switch (variant) {
            case FIRE -> executeFireAbilities(customDragon, target, variantPath);
            case ICE -> executeIceAbilities(customDragon, target, variantPath);
            case LIGHTNING -> executeLightningAbilities(customDragon, target, variantPath);
            case SHADOW -> executeShadowAbilities(customDragon, target, variantPath);
            case VOID -> executeVoidAbilities(customDragon, target, variantPath);
        }
    }

    private void executeFireAbilities(CustomDragon customDragon, Player target, String basePath) {
        EnderDragon dragon = customDragon.getDragon();
        int phase = customDragon.getCurrentPhase().getPhaseNumber();

        // Fireball attack
        if (!customDragon.isAbilityOnCooldown("fireball") &&
                plugin.getConfig().getBoolean(basePath + "fireball.enabled", true)) {
            spawnFireball(dragon, target, basePath + "fireball.");
            customDragon.setAbilityCooldown("fireball");
        }

        // Fire aura
        if (plugin.getConfig().getBoolean(basePath + "fire-aura.enabled", true)) {
            applyFireAura(dragon, basePath + "fire-aura.");
        }

        // Meteor shower (Phase 3 only)
        if (phase >= 3 && !customDragon.isAbilityOnCooldown("meteor-shower") &&
                plugin.getConfig().getBoolean(basePath + "meteor-shower.enabled", true)) {
            spawnMeteorShower(dragon, basePath + "meteor-shower.");
            customDragon.setAbilityCooldown("meteor-shower");
        }
    }

    private void executeIceAbilities(CustomDragon customDragon, Player target, String basePath) {
        EnderDragon dragon = customDragon.getDragon();
        int phase = customDragon.getCurrentPhase().getPhaseNumber();

        // Ice shard
        if (!customDragon.isAbilityOnCooldown("ice-shard") &&
                plugin.getConfig().getBoolean(basePath + "ice-shard.enabled", true)) {
            shootIceShard(dragon, target, basePath + "ice-shard.");
            customDragon.setAbilityCooldown("ice-shard");
        }

        // Freeze aura
        if (plugin.getConfig().getBoolean(basePath + "freeze-aura.enabled", true)) {
            applyFreezeAura(dragon, basePath + "freeze-aura.");
        }

        // Blizzard (Phase 3)
        if (phase >= 3 && !customDragon.isAbilityOnCooldown("blizzard") &&
                plugin.getConfig().getBoolean(basePath + "blizzard.enabled", true)) {
            createBlizzard(dragon, basePath + "blizzard.");
            customDragon.setAbilityCooldown("blizzard");
        }
    }

    private void executeLightningAbilities(CustomDragon customDragon, Player target, String basePath) {
        EnderDragon dragon = customDragon.getDragon();
        int phase = customDragon.getCurrentPhase().getPhaseNumber();

        // Lightning strike
        if (!customDragon.isAbilityOnCooldown("lightning-strike") &&
                plugin.getConfig().getBoolean(basePath + "lightning-strike.enabled", true)) {
            strikeLightning(dragon, target, basePath + "lightning-strike.");
            customDragon.setAbilityCooldown("lightning-strike");
        }

        // Thunder aura
        if (plugin.getConfig().getBoolean(basePath + "thunder-aura.enabled", true)) {
            applyThunderAura(dragon, basePath + "thunder-aura.");
        }

        // Storm rage (Phase 3)
        if (phase >= 3 && !customDragon.isAbilityOnCooldown("storm-rage") &&
                plugin.getConfig().getBoolean(basePath + "storm-rage.enabled", true)) {
            unleashStormRage(dragon, basePath + "storm-rage.");
            customDragon.setAbilityCooldown("storm-rage");
        }
    }

    private void executeShadowAbilities(CustomDragon customDragon, Player target, String basePath) {
        EnderDragon dragon = customDragon.getDragon();
        int phase = customDragon.getCurrentPhase().getPhaseNumber();

        // Shadow strike
        if (!customDragon.isAbilityOnCooldown("shadow-strike") &&
                plugin.getConfig().getBoolean(basePath + "shadow-strike.enabled", true)) {
            performShadowStrike(dragon, target, basePath + "shadow-strike.");
            customDragon.setAbilityCooldown("shadow-strike");
        }

        // Teleport
        if (!customDragon.isAbilityOnCooldown("teleport") &&
                plugin.getConfig().getBoolean(basePath + "teleport.enabled", true)) {
            teleportDragon(dragon, target, basePath + "teleport.");
            customDragon.setAbilityCooldown("teleport");
        }
    }

    private void executeVoidAbilities(CustomDragon customDragon, Player target, String basePath) {
        EnderDragon dragon = customDragon.getDragon();
        int phase = customDragon.getCurrentPhase().getPhaseNumber();

        // Void beam
        if (!customDragon.isAbilityOnCooldown("void-beam") &&
                plugin.getConfig().getBoolean(basePath + "void-beam.enabled", true)) {
            fireVoidBeam(dragon, target, basePath + "void-beam.");
            customDragon.setAbilityCooldown("void-beam");
        }

        // Gravity well
        if (!customDragon.isAbilityOnCooldown("gravity-well") &&
                plugin.getConfig().getBoolean(basePath + "gravity-well.enabled", true)) {
            createGravityWell(dragon, basePath + "gravity-well.");
            customDragon.setAbilityCooldown("gravity-well");
        }

        // Reality tear (Phase 3)
        if (phase >= 3 && !customDragon.isAbilityOnCooldown("reality-tear") &&
                plugin.getConfig().getBoolean(basePath + "reality-tear.enabled", true)) {
            tearReality(dragon, basePath + "reality-tear.");
            customDragon.setAbilityCooldown("reality-tear");
        }
    }

    // ========== Fire Abilities ==========
    private void spawnFireball(EnderDragon dragon, Player target, String path) {
        double damage = plugin.getConfig().getDouble(path + "damage", 12.0);
        float power = (float) plugin.getConfig().getDouble(path + "explosion-power", 3.0);

        Vector direction = target.getLocation().toVector()
                .subtract(dragon.getLocation().toVector()).normalize();

        dragon.getWorld().createExplosion(
                dragon.getLocation().add(direction.multiply(5)),
                power,
                true,
                false,
                dragon
        );

        plugin.getParticleManager().spawnAbilityEffect(
                dragon.getLocation(), DragonVariant.FIRE, "fireball");
    }

    private void applyFireAura(EnderDragon dragon, String path) {
        double radius = plugin.getConfig().getDouble(path + "radius", 8.0);
        double damage = plugin.getConfig().getDouble(path + "damage", 2.0);

        dragon.getNearbyEntities(radius, radius, radius).forEach(entity -> {
            if (entity instanceof LivingEntity && !(entity instanceof EnderDragon)) {
                LivingEntity living = (LivingEntity) entity;
                living.damage(damage, dragon);
                living.setFireTicks(60);
                plugin.getParticleManager().spawnAbilityEffect(
                        living.getLocation(), DragonVariant.FIRE, "fire-aura");
            }
        });
    }

    private void spawnMeteorShower(EnderDragon dragon, String path) {
        int meteorCount = plugin.getConfig().getInt(path + "meteor-count", 15);

        for (int i = 0; i < meteorCount; i++) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                Location target = dragon.getLocation().clone()
                        .add(random.nextInt(40) - 20, 20, random.nextInt(40) - 20);

                dragon.getWorld().createExplosion(target, 2.0f, true, false, dragon);
            }, i * 5L);
        }
    }

    // ========== Ice Abilities ==========
    private void shootIceShard(EnderDragon dragon, Player target, String path) {
        double damage = plugin.getConfig().getDouble(path + "damage", 10.0);
        int slowDuration = plugin.getConfig().getInt(path + "slowness-duration", 100);

        target.damage(damage, dragon);
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, slowDuration, 1));

        plugin.getParticleManager().spawnAbilityEffect(
                target.getLocation(), DragonVariant.ICE, "ice-shard");
    }

    private void applyFreezeAura(EnderDragon dragon, String path) {
        double radius = plugin.getConfig().getDouble(path + "radius", 10.0);
        int slowAmplifier = plugin.getConfig().getInt(path + "slowness-amplifier", 2);

        dragon.getNearbyEntities(radius, radius, radius).forEach(entity -> {
            if (entity instanceof LivingEntity && !(entity instanceof EnderDragon)) {
                LivingEntity living = (LivingEntity) entity;
                living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, slowAmplifier));
                plugin.getParticleManager().spawnAbilityEffect(
                        living.getLocation(), DragonVariant.ICE, "freeze-aura");
            }
        });
    }

    private void createBlizzard(EnderDragon dragon, String path) {
        double radius = plugin.getConfig().getDouble(path + "radius", 20.0);
        int duration = plugin.getConfig().getInt(path + "duration", 120);

        for (int i = 0; i < duration; i += 20) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                dragon.getNearbyEntities(radius, radius, radius).forEach(entity -> {
                    if (entity instanceof LivingEntity && !(entity instanceof EnderDragon)) {
                        LivingEntity living = (LivingEntity) entity;
                        living.damage(2.0, dragon);
                        living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 3));
                    }
                });
            }, i);
        }
    }

    // ========== Lightning Abilities ==========
    private void strikeLightning(EnderDragon dragon, Player target, String path) {
        target.getWorld().strikeLightning(target.getLocation());

        double chainRange = plugin.getConfig().getDouble(path + "chain-range", 5.0);
        target.getNearbyEntities(chainRange, chainRange, chainRange).forEach(entity -> {
            if (entity instanceof LivingEntity && entity != target) {
                entity.getWorld().strikeLightning(entity.getLocation());
            }
        });

        plugin.getParticleManager().spawnAbilityEffect(
                target.getLocation(), DragonVariant.LIGHTNING, "lightning-strike");
    }

    private void applyThunderAura(EnderDragon dragon, String path) {
        double radius = plugin.getConfig().getDouble(path + "radius", 12.0);
        double strikeChance = plugin.getConfig().getDouble(path + "strike-chance", 0.1);

        dragon.getNearbyEntities(radius, radius, radius).forEach(entity -> {
            if (entity instanceof LivingEntity && !(entity instanceof EnderDragon)) {
                if (random.nextDouble() < strikeChance) {
                    entity.getWorld().strikeLightning(entity.getLocation());
                }
            }
        });
    }

    private void unleashStormRage(EnderDragon dragon, String path) {
        int strikeCount = plugin.getConfig().getInt(path + "strike-count", 20);

        for (int i = 0; i < strikeCount; i++) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                Location target = dragon.getLocation().clone()
                        .add(random.nextInt(30) - 15, 0, random.nextInt(30) - 15);
                dragon.getWorld().strikeLightning(target);
            }, i * 5L);
        }
    }

    // ========== Shadow Abilities ==========
    private void performShadowStrike(EnderDragon dragon, Player target, String path) {
        double damage = plugin.getConfig().getDouble(path + "damage", 14.0);
        int blindDuration = plugin.getConfig().getInt(path + "blindness-duration", 60);

        target.damage(damage, dragon);
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindDuration, 1));

        plugin.getParticleManager().spawnAbilityEffect(
                target.getLocation(), DragonVariant.SHADOW, "shadow-strike");
    }

    private void teleportDragon(EnderDragon dragon, Player target, String path) {
        double range = plugin.getConfig().getDouble(path + "range", 30.0);

        Vector direction = target.getLocation().toVector()
                .subtract(dragon.getLocation().toVector()).normalize();
        Location teleportLoc = dragon.getLocation().add(direction.multiply(range));

        dragon.teleport(teleportLoc);

        plugin.getParticleManager().spawnAbilityEffect(
                teleportLoc, DragonVariant.SHADOW, "shadow-strike");
    }

    // ========== Void Abilities ==========
    private void fireVoidBeam(EnderDragon dragon, Player target, String path) {
        double damage = plugin.getConfig().getDouble(path + "damage", 20.0);
        int witherDuration = plugin.getConfig().getInt(path + "wither-duration", 100);

        target.damage(damage, dragon);
        target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, witherDuration, 1));

        plugin.getParticleManager().spawnAbilityEffect(
                target.getLocation(), DragonVariant.VOID, "void-beam");
    }

    private void createGravityWell(EnderDragon dragon, String path) {
        double radius = plugin.getConfig().getDouble(path + "radius", 15.0);
        double pullStrength = plugin.getConfig().getDouble(path + "pull-strength", 1.5);
        int duration = plugin.getConfig().getInt(path + "duration", 60);

        Location center = dragon.getLocation();

        for (int i = 0; i < duration; i += 5) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                dragon.getNearbyEntities(radius, radius, radius).forEach(entity -> {
                    if (entity instanceof LivingEntity && !(entity instanceof EnderDragon)) {
                        Vector direction = center.toVector().subtract(entity.getLocation().toVector()).normalize();
                        entity.setVelocity(direction.multiply(pullStrength));
                    }
                });
            }, i);
        }
    }

    private void tearReality(EnderDragon dragon, String path) {
        double damage = plugin.getConfig().getDouble(path + "damage", 30.0);
        double radius = plugin.getConfig().getDouble(path + "radius", 10.0);

        dragon.getNearbyEntities(radius, radius, radius).forEach(entity -> {
            if (entity instanceof LivingEntity && !(entity instanceof EnderDragon)) {
                LivingEntity living = (LivingEntity) entity;
                living.damage(damage, dragon);
                living.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 2));
                living.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100, 5));
            }
        });

        plugin.getParticleManager().spawnAbilityEffect(
                dragon.getLocation(), DragonVariant.VOID, "void-beam");
    }

    private Player findNearestPlayer(Location location, Collection<Entity> entities) {
        Player nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Entity entity : entities) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                double distance = location.distance(player.getLocation());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = player;
                }
            }
        }

        return nearest;
    }
}
