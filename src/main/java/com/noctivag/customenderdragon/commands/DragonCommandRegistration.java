package com.noctivag.customenderdragon.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.noctivag.customenderdragon.CustomEnderDragonMod;
import com.noctivag.customenderdragon.dragon.DragonVariant;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Registers dragon commands for Fabric using Brigadier
 */
public class DragonCommandRegistration {

    public static void register() {
        CommandRegistrationCallback.EVENT.register(DragonCommandRegistration::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher,
                                         CommandRegistryAccess registryAccess,
                                         CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("customdragon")
            .requires(source -> source.hasPermissionLevel(2))
            .then(CommandManager.literal("spawn")
                .then(CommandManager.argument("variant", StringArgumentType.word())
                    .suggests((context, builder) -> {
                        for (DragonVariant variant : DragonVariant.values()) {
                            builder.suggest(variant.name());
                        }
                        return builder.buildFuture();
                    })
                    .executes(DragonCommandRegistration::spawnDragon)))
            .then(CommandManager.literal("list")
                .executes(DragonCommandRegistration::listDragons))
            .then(CommandManager.literal("remove")
                .executes(DragonCommandRegistration::removeAllDragons))
            .then(CommandManager.literal("reload")
                .executes(DragonCommandRegistration::reloadConfig)));
    }

    private static int spawnDragon(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        if (!(source.getEntity() instanceof ServerPlayerEntity player)) {
            source.sendError(Text.literal("Only players can use this command!"));
            return 0;
        }

        String variantName = StringArgumentType.getString(context, "variant");
        DragonVariant variant = DragonVariant.fromString(variantName);

        if (variant == null) {
            source.sendError(Text.literal("Invalid variant! Use: FIRE, ICE, LIGHTNING, SHADOW, VOID"));
            return 0;
        }

        ServerWorld world = player.getServerWorld();
        CustomEnderDragonMod.getDragonManager().spawnDragon(world, player.getBlockPos(), variant);

        source.sendFeedback(() -> Text.literal("Spawned " + variant.getDisplayName() + " Dragon!")
            .formatted(Formatting.GREEN), true);

        return 1;
    }

    private static int listDragons(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        int count = CustomEnderDragonMod.getDragonManager().getAllDragons().size();

        if (count == 0) {
            source.sendFeedback(() -> Text.literal("No custom dragons found!").formatted(Formatting.RED), false);
        } else {
            source.sendFeedback(() -> Text.literal("Active Custom Dragons: " + count).formatted(Formatting.YELLOW), false);
            
            CustomEnderDragonMod.getDragonManager().getAllDragons().forEach(dragon -> {
                source.sendFeedback(() -> Text.literal("  - " + dragon.getVariant().getDisplayName() + 
                    " Dragon (Phase " + dragon.getCurrentPhase().getPhaseNumber() + ")").formatted(Formatting.GRAY), false);
            });
        }

        return 1;
    }

    private static int removeAllDragons(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        int count = CustomEnderDragonMod.getDragonManager().removeAllDragons();

        source.sendFeedback(() -> Text.literal("Removed " + count + " custom dragon(s)!").formatted(Formatting.GREEN), true);
        return 1;
    }

    private static int reloadConfig(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        
        try {
            com.noctivag.customenderdragon.config.ModConfig.load();
            source.sendFeedback(() -> Text.literal("Configuration reloaded successfully!").formatted(Formatting.GREEN), true);
            return 1;
        } catch (Exception e) {
            source.sendError(Text.literal("Failed to reload configuration: " + e.getMessage()));
            return 0;
        }
    }
}
