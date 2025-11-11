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

        // Clear default drops
        event.getDrops().clear();
        event.setDroppedExp(0);

        // Drop custom loot
        dropCustomLoot(customDragon, dragon.getLocation());

        // Broadcast death message
        String message = plugin.getConfig().getString("messages.dragon-killed",
                "&e%variant% Dragon has been slain!")
                .replace("%variant%", customDragon.getVariant().getDisplayName())
                .replace("&", "§");

        dragon.getWorld().getPlayers().forEach(player ->
                player.sendMessage(Component.text(message)));

        // Remove from manager
        plugin.getDragonManager().removeDragon(dragon.getUniqueId());

        // Experience drop (5x normal)
        event.setDroppedExp(12000);
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
