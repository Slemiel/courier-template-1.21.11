package name.modid.delivery;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;

public final class DeliveryInteractions {

    private DeliveryInteractions() {}

    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!isServer(world)) return InteractionResult.PASS;
            if (!(player instanceof ServerPlayer sp)) return InteractionResult.PASS;

            DeliveryState s = DeliveryManager.get(sp);

            // Debug. Handig als het weer stuk gaat.
            // sp.sendSystemMessage(Component.literal("UseBlock. stage=" + s.stage));

            if (s.stage == DeliveryState.Stage.GO_RESTAURANT) {
                if (DeliveryManager.isAtPickup(sp)) {
                    DeliveryManager.onPickup(sp);
                    return InteractionResult.SUCCESS;
                } else {
                    sp.sendSystemMessage(Component.literal("Je bent niet bij de pickup. Ga naar: "
                            + s.pickupPos.getX() + " " + s.pickupPos.getY() + " " + s.pickupPos.getZ()));
                    return InteractionResult.PASS;
                }
            }

            if (s.stage == DeliveryState.Stage.GO_DROPOFF) {
                if (DeliveryManager.isAtDropoff(sp)) {
                    DeliveryManager.onDropoff(sp);
                    return InteractionResult.SUCCESS;
                } else {
                    sp.sendSystemMessage(Component.literal("Je bent niet bij de dropoff. Ga naar: "
                            + s.dropoffPos.getX() + " " + s.dropoffPos.getY() + " " + s.dropoffPos.getZ()));
                    return InteractionResult.PASS;
                }
            }

            return InteractionResult.PASS;
        });
    }

    private static boolean isServer(Level world) {
        // Gebruik methode, geen field, want field gaf eerder die private error.
        return !world.isClientSide();
    }
}