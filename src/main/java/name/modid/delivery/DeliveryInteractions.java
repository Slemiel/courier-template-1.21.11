package name.modid.delivery;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;

public final class DeliveryInteractions {
    private DeliveryInteractions() {}

    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (world.isClientSide()) return InteractionResult.PASS;
            if (!(player instanceof ServerPlayer sp)) return InteractionResult.PASS;

            DeliveryState s = DeliveryManager.get(sp);

            if (s.stage == DeliveryState.Stage.GO_RESTAURANT) {
                if (DeliveryManager.isAtPickup(sp)) {
                    DeliveryManager.onPickup(sp);
                    return InteractionResult.SUCCESS;
                }
                if (s.pickupPos != null) {
                    sp.sendSystemMessage(Component.literal("Je bent niet bij de pickup. Ga naar: " +
                            s.pickupPos.getX() + " " + s.pickupPos.getY() + " " + s.pickupPos.getZ()));
                }
                return InteractionResult.PASS;
            }

            if (s.stage == DeliveryState.Stage.GO_DROPOFF) {
                if (DeliveryManager.isAtDropoff(sp)) {
                    DeliveryManager.onDropoff(sp);
                    return InteractionResult.SUCCESS;
                }
                if (s.dropoffPos != null) {
                    sp.sendSystemMessage(Component.literal("Je bent niet bij de dropoff. Ga naar: " +
                            s.dropoffPos.getX() + " " + s.dropoffPos.getY() + " " + s.dropoffPos.getZ()));
                }
                return InteractionResult.PASS;
            }

            return InteractionResult.PASS;
        });
    }
}