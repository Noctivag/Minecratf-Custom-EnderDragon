package com.noctivag.customenderdragon.dragon;

import com.noctivag.customenderdragon.CustomEnderDragonPlugin;
import com.noctivag.customenderdragon.visuals.CrystalStructureManager;
import com.noctivag.customenderdragon.visuals.DisplayEntityManager;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a custom EnderDragon with unique abilities and phases
 */
public class CustomDragon {
    private final CustomEnderDragonPlugin plugin;
    private final EnderDragon dragon;
    private final DragonVariant variant;
    private DragonPhase currentPhase;
    private BossBar bossBar;
    private BukkitTask particleTask;
    private BukkitTask abilityTask;
    private final Map<String, Long> abilityCooldowns;
    private double maxHealth;

    // 3D Visual Decorations
    private DisplayEntityManager.DragonDecorations decorations;
    private CrystalStructureManager.CrystalArena arena;

    public CustomDragon(CustomEnderDragonPlugin plugin, EnderDragon dragon, DragonVariant variant) {
        this.plugin = plugin;
        this.dragon = dragon;
        this.variant = variant;
        this.currentPhase = DragonPhase.PHASE_1;
        this.abilityCooldowns = new HashMap<>();

        setupDragon();
        createBossBar();
        setup3DVisuals();
        startParticleEffects();
        startAbilityTasks();
    }

    /**
     * Sets up 3D visual decorations and arena structures
     */
    private void setup3DVisuals() {
        // Create 3D decorations around dragon
        decorations = plugin.getDisplayEntityManager().createDragonDecorations(dragon, variant);

        // Create crystal arena structures
        arena = plugin.getCrystalStructureManager().createCrystalArena(dragon.getLocation(), variant);
    }

    private void setupDragon() {
        // Get configured stats
        String variantPath = "variants." + variant.name();
        double health = plugin.getConfig().getDouble(variantPath + ".health", 200.0);
        String displayName = plugin.getConfig().getString(variantPath + ".display-name", variant.getDisplayName());

        // Set dragon attributes
        dragon.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        dragon.setHealth(health);
        this.maxHealth = health;

        // Set custom name with color
        dragon.customName(Component.text(displayName + " Dragon")
                .decoration(TextDecoration.BOLD, true));
        dragon.setCustomNameVisible(true);

        // Make dragon aggressive
        dragon.setPhase(EnderDragon.Phase.CHARGE_PLAYER);
    }

    private void createBossBar() {
        if (!plugin.getConfig().getBoolean("general.boss-bar-enabled", true)) {
            return;
        }

        String displayName = plugin.getConfig().getString("variants." + variant.name() + ".display-name",
                variant.getDisplayName());

        bossBar = BossBar.bossBar(
            Component.text(displayName + " Dragon - Phase " + currentPhase.getPhaseNumber())
                    .decoration(TextDecoration.BOLD, true),
            1.0f,
            net.kyori.adventure.bossbar.BossBar.Color.valueOf(variant.getBarColor().name()),
            net.kyori.adventure.bossbar.BossBar.Overlay.PROGRESS
        );
    }

    private void startParticleEffects() {
        particleTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!dragon.isValid() || dragon.isDead()) {
                    cancel();
                    return;
                }

                plugin.getParticleManager().spawnParticles(dragon, variant);
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }

    private void startAbilityTasks() {
        abilityTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!dragon.isValid() || dragon.isDead()) {
                    cancel();
                    return;
                }

                updatePhase();
                updateBossBar();
                executeAbilities();
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void updatePhase() {
        if (!plugin.getConfig().getBoolean("phases.enabled", true)) {
            return;
        }

        double healthPercent = (dragon.getHealth() / maxHealth) * 100;
        DragonPhase newPhase = DragonPhase.getPhaseForHealth(healthPercent);

        if (newPhase != currentPhase) {
            currentPhase = newPhase;
            onPhaseChange();
        }
    }

    private void onPhaseChange() {
        // Broadcast phase change
        String message = plugin.getConfig().getString("messages.phase-change",
                "&6%variant% Dragon entered Phase %phase%!")
                .replace("%variant%", variant.getDisplayName())
                .replace("%phase%", String.valueOf(currentPhase.getPhaseNumber()));

        dragon.getWorld().getPlayers().forEach(player -> {
            if (player.getLocation().distance(dragon.getLocation()) < 100) {
                player.sendMessage(Component.text(message.replace("&", "§")));
            }
        });

        // Enhanced effects for phase change
        plugin.getParticleManager().spawnPhaseChangeEffect(dragon, variant, currentPhase);
    }

    private void updateBossBar() {
        if (bossBar == null) return;

        double healthPercent = dragon.getHealth() / maxHealth;
        bossBar.progress((float) healthPercent);

        String displayName = plugin.getConfig().getString("variants." + variant.name() + ".display-name",
                variant.getDisplayName());
        bossBar.name(Component.text(displayName + " Dragon - Phase " + currentPhase.getPhaseNumber() +
                " [" + currentPhase.getPhaseName() + "]")
                .decoration(TextDecoration.BOLD, true));
    }

    private void executeAbilities() {
        // Let the ability manager handle this
        plugin.getAbilityManager().executeAbilities(this);
    }

    public boolean isAbilityOnCooldown(String abilityName) {
        if (!abilityCooldowns.containsKey(abilityName)) {
            return false;
        }

        long lastUsed = abilityCooldowns.get(abilityName);
        int cooldown = plugin.getConfig().getInt("variants." + variant.name() +
                ".abilities." + abilityName + ".cooldown", 100);

        return (System.currentTimeMillis() - lastUsed) < (cooldown * 50L); // Convert ticks to ms
    }

    public void setAbilityCooldown(String abilityName) {
        abilityCooldowns.put(abilityName, System.currentTimeMillis());
    }

    public void addPlayerToBossBar(Player player) {
        if (bossBar != null) {
            player.showBossBar(bossBar);
        }
    }

    public void removePlayerFromBossBar(Player player) {
        if (bossBar != null) {
            player.hideBossBar(bossBar);
        }
    }

    public void remove() {
        if (particleTask != null) {
            particleTask.cancel();
        }
        if (abilityTask != null) {
            abilityTask.cancel();
        }
        if (bossBar != null) {
            // Hide boss bar from all players
            dragon.getWorld().getPlayers().forEach(this::removePlayerFromBossBar);
        }

        // Remove 3D visual decorations
        if (decorations != null) {
            decorations.removeAll();
        }

        // Remove arena structures
        if (arena != null) {
            arena.removeAll();
        }

        dragon.remove();
    }

    public EnderDragon getDragon() {
        return dragon;
    }

    public DragonVariant getVariant() {
        return variant;
    }

    public DragonPhase getCurrentPhase() {
        return currentPhase;
    }

    public UUID getUniqueId() {
        return dragon.getUniqueId();
    }

    public Location getLocation() {
        return dragon.getLocation();
    }

    public double getMaxHealth() {
        return maxHealth;
    }
}
