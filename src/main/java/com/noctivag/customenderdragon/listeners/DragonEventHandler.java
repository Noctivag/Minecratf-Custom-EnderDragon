package com.noctivag.customenderdragon.listeners;

import com.noctivag.customenderdragon.CustomEnderDragonMod;
import com.noctivag.customenderdragon.dragon.CustomDragon;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Handles Fabric events for custom dragons
 */
public class DragonEventHandler {

    public static void register() {
        // Listen for dragon death
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof EnderDragonEntity dragon) {
                onDragonDeath(dragon, damageSource);
            }
        });
    }

    private static void onDragonDeath(EnderDragonEntity dragon, DamageSource source) {
        CustomDragon customDragon = CustomEnderDragonMod.getDragonManager().getCustomDragon(dragon);
        
        if (customDragon == null) {
            return;
        }

        // Broadcast death message
        if (dragon.getWorld() instanceof ServerWorld serverWorld) {
            Text message = Text.literal(customDragon.getVariant().getDisplayName() + 
                " Dragon has been slain!").formatted(Formatting.YELLOW, Formatting.BOLD);

            serverWorld.getPlayers().forEach(player -> {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    serverPlayer.sendMessage(message, false);
                }
            });
        }

        // Drop custom loot (TODO: Implement based on config)
        // For now, dragons drop vanilla loot

        // Remove from manager
        CustomEnderDragonMod.getDragonManager().removeDragon(dragon.getUuid());
    }
}
