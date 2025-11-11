package com.noctivag.customenderdragon.commands;

import com.noctivag.customenderdragon.CustomEnderDragonPlugin;
import com.noctivag.customenderdragon.dragon.CustomDragon;
import com.noctivag.customenderdragon.dragon.DragonVariant;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the /customdragon command
 */
public class DragonCommand implements CommandExecutor, TabCompleter {
    private final CustomEnderDragonPlugin plugin;

    public DragonCommand(CustomEnderDragonPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String prefix = plugin.getConfig().getString("messages.prefix", "&5&l[CustomDragon]&r ")
                .replace("&", "§");

        if (args.length == 0) {
            sendHelp(sender, prefix);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "spawn" -> {
                return handleSpawn(sender, args, prefix);
            }
            case "list" -> {
                return handleList(sender, prefix);
            }
            case "remove" -> {
                return handleRemove(sender, prefix);
            }
            case "reload" -> {
                return handleReload(sender, prefix);
            }
            default -> {
                sendHelp(sender, prefix);
                return true;
            }
        }
    }

    private boolean handleSpawn(CommandSender sender, String[] args, String prefix) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command!", NamedTextColor.RED));
            return true;
        }

        if (!player.hasPermission("customdragon.spawn")) {
            String message = plugin.getConfig().getString("messages.no-permission",
                    "&cYou don't have permission to do that!").replace("&", "§");
            player.sendMessage(Component.text(prefix + message));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(Component.text(prefix + "§cUsage: /customdragon spawn <FIRE|ICE|LIGHTNING|SHADOW|VOID>"));
            return true;
        }

        DragonVariant variant = DragonVariant.fromString(args[1]);
        if (variant == null) {
            String message = plugin.getConfig().getString("messages.invalid-variant",
                    "&cInvalid variant! Use: FIRE, ICE, LIGHTNING, SHADOW, VOID").replace("&", "§");
            player.sendMessage(Component.text(prefix + message));
            return true;
        }

        // Check specific variant permission
        if (!player.hasPermission("customdragon.spawn." + variant.name().toLowerCase())) {
            String message = plugin.getConfig().getString("messages.no-permission",
                    "&cYou don't have permission to spawn this variant!").replace("&", "§");
            player.sendMessage(Component.text(prefix + message));
            return true;
        }

        // Check if variant is enabled
        if (!plugin.getConfig().getBoolean("variants." + variant.name() + ".enabled", true)) {
            player.sendMessage(Component.text(prefix + "§cThis dragon variant is currently disabled!"));
            return true;
        }

        // Spawn the dragon
        CustomDragon dragon = plugin.getDragonManager().spawnDragon(player.getLocation(), variant);

        String message = plugin.getConfig().getString("messages.dragon-spawned",
                "&aSpawned %variant% Dragon at your location!")
                .replace("%variant%", variant.getDisplayName())
                .replace("&", "§");
        player.sendMessage(Component.text(prefix + message));

        return true;
    }

    private boolean handleList(CommandSender sender, String prefix) {
        if (!sender.hasPermission("customdragon.use")) {
            String message = plugin.getConfig().getString("messages.no-permission",
                    "&cYou don't have permission to do that!").replace("&", "§");
            sender.sendMessage(Component.text(prefix + message));
            return true;
        }

        int count = plugin.getDragonManager().getAllDragons().size();

        if (count == 0) {
            String message = plugin.getConfig().getString("messages.no-dragons",
                    "&cNo custom dragons found!").replace("&", "§");
            sender.sendMessage(Component.text(prefix + message));
        } else {
            sender.sendMessage(Component.text(prefix + "§eActive Custom Dragons: §a" + count));

            plugin.getDragonManager().getAllDragons().forEach(dragon -> {
                sender.sendMessage(Component.text(
                        "  §7- §f" + dragon.getVariant().getDisplayName() + " Dragon " +
                                "§7(Phase " + dragon.getCurrentPhase().getPhaseNumber() + ") " +
                                "§7at §f" + formatLocation(dragon.getLocation())
                ));
            });
        }

        return true;
    }

    private boolean handleRemove(CommandSender sender, String prefix) {
        if (!sender.hasPermission("customdragon.remove")) {
            String message = plugin.getConfig().getString("messages.no-permission",
                    "&cYou don't have permission to do that!").replace("&", "§");
            sender.sendMessage(Component.text(prefix + message));
            return true;
        }

        int count = plugin.getDragonManager().removeAllDragons();

        if (count == 0) {
            String message = plugin.getConfig().getString("messages.no-dragons",
                    "&cNo custom dragons found!").replace("&", "§");
            sender.sendMessage(Component.text(prefix + message));
        } else {
            String message = plugin.getConfig().getString("messages.dragon-removed",
                    "&cRemoved %count% custom dragon(s)!")
                    .replace("%count%", String.valueOf(count))
                    .replace("&", "§");
            sender.sendMessage(Component.text(prefix + message));
        }

        return true;
    }

    private boolean handleReload(CommandSender sender, String prefix) {
        if (!sender.hasPermission("customdragon.reload")) {
            String message = plugin.getConfig().getString("messages.no-permission",
                    "&cYou don't have permission to do that!").replace("&", "§");
            sender.sendMessage(Component.text(prefix + message));
            return true;
        }

        plugin.reloadConfig();

        String message = plugin.getConfig().getString("messages.config-reloaded",
                "&aConfiguration reloaded successfully!").replace("&", "§");
        sender.sendMessage(Component.text(prefix + message));

        return true;
    }

    private void sendHelp(CommandSender sender, String prefix) {
        sender.sendMessage(Component.text("§5§l========== Custom EnderDragon =========="));
        sender.sendMessage(Component.text("§e/customdragon spawn <variant> §7- Spawn a custom dragon"));
        sender.sendMessage(Component.text("§7  Variants: §fFIRE, ICE, LIGHTNING, SHADOW, VOID"));
        sender.sendMessage(Component.text("§e/customdragon list §7- List all active custom dragons"));
        sender.sendMessage(Component.text("§e/customdragon remove §7- Remove all custom dragons"));
        sender.sendMessage(Component.text("§e/customdragon reload §7- Reload configuration"));
        sender.sendMessage(Component.text("§5§l===================================="));
    }

    private String formatLocation(org.bukkit.Location loc) {
        return String.format("%s (%d, %d, %d)",
                loc.getWorld().getName(),
                loc.getBlockX(),
                loc.getBlockY(),
                loc.getBlockZ());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("spawn", "list", "remove", "reload"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("spawn")) {
            completions.addAll(Arrays.stream(DragonVariant.values())
                    .map(v -> v.name())
                    .collect(Collectors.toList()));
        }

        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}
