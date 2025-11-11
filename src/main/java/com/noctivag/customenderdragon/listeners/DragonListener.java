package com.noctivag.customenderdragon.listeners;

import com.noctivag.customenderdragon.CustomEnderDragonPlugin;
import com.noctivag.customenderdragon.dragon.CustomDragon;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

/**
 * Handles events related to custom dragons
 */
public class DragonListener implements Listener {
    private final CustomEnderDragonPlugin plugin;
    private final Random random;

    public DragonListener(CustomEnderDragonPlugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDragonDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof EnderDragon dragon)) {
            return;
        }

        CustomDragon customDragon = plugin.getDragonManager().getCustomDragon(dragon);
        if (customDragon == null) {
            return;
        }

        // Get killer
        Player killer = dragon.getKiller();

        // Clear default drops
        event.getDrops().clear();
        event.setDroppedExp(0);

        // Drop custom loot
        dropCustomLoot(customDragon, dragon.getLocation());

        // Give economy reward (if Vault is enabled)
        giveEconomyReward(killer, customDragon);

        // Broadcast death message
        String message = plugin.getConfig().getString("messages.dragon-killed",
                "&e%variant% Dragon has been slain!")
                .replace("%variant%", customDragon.getVariant().getDisplayName())
                .replace("&", "§");

        if (killer != null) {
            message = message + " &7by &e" + killer.getName();
        }

        final String finalMessage = message;
        dragon.getWorld().getPlayers().forEach(player ->
                player.sendMessage(Component.text(finalMessage)));

        // Play custom sound effects
        playDeathSounds(dragon.getLocation());

        // Remove from manager
        plugin.getDragonManager().removeDragon(dragon.getUniqueId());

        // Experience drop (5x normal)
        event.setDroppedExp(12000);
    }

    private void giveEconomyReward(Player killer, CustomDragon customDragon) {
        if (killer == null) return;

        if (!plugin.getConfig().getBoolean("integrations.vault.money-rewards-enabled", true)) {
            return;
        }

        double baseAmount = plugin.getConfig().getDouble("integrations.vault.base-reward-amount", 1000.0);
        double multiplier = plugin.getConfig().getDouble(
                "integrations.vault.variant-multipliers." + customDragon.getVariant().name(), 1.0);

        double totalReward = baseAmount * multiplier;

        if (plugin.getPluginIntegrationManager().giveMoneyReward(killer, totalReward)) {
            killer.sendMessage(Component.text("§a§l+ $" + String.format("%.2f", totalReward) +
                    " §7(Dragon Kill Reward)"));
        }
    }

    private void playDeathSounds(Location location) {
        if (!plugin.getConfig().getBoolean("general.custom-sounds-enabled", true)) {
            return;
        }

        float volume = (float) plugin.getConfig().getDouble("general.sound-volume", 1.0);
        float pitch = (float) plugin.getConfig().getDouble("general.sound-pitch", 1.0);

        // Play epic death sound
        location.getWorld().playSound(location, org.bukkit.Sound.ENTITY_ENDER_DRAGON_DEATH, volume, pitch);
        location.getWorld().playSound(location, org.bukkit.Sound.ENTITY_GENERIC_EXPLODE, volume * 0.8f, pitch * 0.5f);
    }

    private void dropCustomLoot(CustomDragon customDragon, Location location) {
        String variantPath = "variants." + customDragon.getVariant().name() + ".loot";
        List<?> lootList = plugin.getConfig().getList(variantPath);

        if (lootList == null) return;

        for (Object lootObj : lootList) {
            if (lootObj instanceof ConfigurationSection section) {
                String materialName = section.getString("type");
                if (materialName == null) continue;

                try {
                    Material material = Material.valueOf(materialName);
                    int chance = section.getInt("chance", 100);

                    if (random.nextInt(100) < chance) {
                        String amountStr = section.getString("amount", "1");
                        int amount = parseAmount(amountStr);

                        ItemStack item = new ItemStack(material, amount);
                        location.getWorld().dropItemNaturally(location, item);
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid material: " + materialName);
                }
            }
        }
    }

    private int parseAmount(String amountStr) {
        if (amountStr.contains("-")) {
            String[] parts = amountStr.split("-");
            int min = Integer.parseInt(parts[0]);
            int max = Integer.parseInt(parts[1]);
            return min + random.nextInt(max - min + 1);
        }
        return Integer.parseInt(amountStr);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Add player to nearby dragon boss bars
        plugin.getDragonManager().getAllDragons().forEach(dragon -> {
            if (dragon.getLocation().getWorld() == player.getWorld() &&
                    dragon.getLocation().distance(player.getLocation()) < 100) {
                dragon.addPlayerToBossBar(player);
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Remove player from all boss bars
        plugin.getDragonManager().getAllDragons()
                .forEach(dragon -> dragon.removePlayerFromBossBar(player));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Only check if player changed blocks (not just head movement)
        if (event.getFrom().getBlock().equals(event.getTo().getBlock())) {
            return;
        }

        Player player = event.getPlayer();

        // Update boss bar visibility based on distance
        plugin.getDragonManager().getAllDragons().forEach(dragon -> {
            if (dragon.getLocation().getWorld() != player.getWorld()) {
                return;
            }

            double distance = dragon.getLocation().distance(player.getLocation());

            if (distance < 100) {
                dragon.addPlayerToBossBar(player);
            } else {
                dragon.removePlayerFromBossBar(player);
            }
        });
    }
}
