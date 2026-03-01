package name.modid;

import name.modid.delivery.DeliveryInteractions;
import name.modid.delivery.DeliveryManager;
import name.modid.delivery.DeliveryState;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Courier implements ModInitializer {

	public static final String MOD_ID = "courier";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Courier geladen");

		DeliveryInteractions.register();

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(Commands.literal("courier")
					.then(Commands.literal("start").executes(ctx -> {
						ServerPlayer p = ctx.getSource().getPlayerOrException();
						DeliveryManager.startOrder(p);
						return 1;
					}))
					.then(Commands.literal("status").executes(ctx -> {
						ServerPlayer p = ctx.getSource().getPlayerOrException();
						DeliveryState s = DeliveryManager.get(p);

						p.sendSystemMessage(Component.literal("Stage: " + s.stage));
						if (s.pickupPos != null) {
							p.sendSystemMessage(Component.literal("Pickup: " + s.pickupPos.getX() + " " + s.pickupPos.getY() + " " + s.pickupPos.getZ()));
						}
						if (s.dropoffPos != null) {
							p.sendSystemMessage(Component.literal("Dropoff: " + s.dropoffPos.getX() + " " + s.dropoffPos.getY() + " " + s.dropoffPos.getZ()));
						}
						if (s.restaurantName != null && !s.restaurantName.isBlank()) {
							p.sendSystemMessage(Component.literal("Restaurant: " + s.restaurantName));
						}
						return 1;
					}))
					.then(Commands.literal("clear").executes(ctx -> {
						ServerPlayer p = ctx.getSource().getPlayerOrException();
						DeliveryManager.clear(p);
						p.sendSystemMessage(Component.literal("Order verwijderd."));
						return 1;
					}))
			);
		});
	}
}