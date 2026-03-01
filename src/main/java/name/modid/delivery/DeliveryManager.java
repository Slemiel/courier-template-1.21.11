package name.modid.delivery;

import name.modid.stats.StatsManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class DeliveryManager {

    private DeliveryManager() {}

    private static final Map<UUID, DeliveryState> STATE = new ConcurrentHashMap<>();

    public static DeliveryState get(ServerPlayer player) {
        return STATE.computeIfAbsent(player.getUUID(), k -> new DeliveryState());
    }

    public static void clear(ServerPlayer player) {
        get(player).clear();
    }

    public static void startOrder(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        DeliveryState s = get(player);
        s.clear();

        DeliveryConfig.Restaurant r = pickRestaurant(level, DeliveryConfig.RESTAURANTS);

        // Zorg dat pickup op de grond ligt, nooit in de lucht.
        BlockPos ground = CustomerGenerator.findGround(level, r.pos().getX(), r.pos().getZ());
        BlockPos pickup = ground.above();

        // Dropoff kiezen op begaanbare plek.
        BlockPos dropoff = CustomerGenerator.pickDropoff(
                level,
                pickup,
                DeliveryConfig.DROPOFF_MIN_DISTANCE,
                DeliveryConfig.DROPOFF_MAX_DISTANCE
        );

        s.restaurantName = r.name();
        s.pickupPos = pickup;
        s.dropoffPos = dropoff;
        s.stage = DeliveryState.Stage.GO_RESTAURANT;

        player.sendSystemMessage(Component.literal("Nieuwe order. Pickup bij: " + s.restaurantName));
        player.sendSystemMessage(Component.literal("Pickup: " + s.pickupPos.getX() + " " + s.pickupPos.getY() + " " + s.pickupPos.getZ()));
        player.sendSystemMessage(Component.literal("Dropoff: " + s.dropoffPos.getX() + " " + s.dropoffPos.getY() + " " + s.dropoffPos.getZ()));
        player.sendSystemMessage(Component.literal("Order gestart."));
    }

    private static DeliveryConfig.Restaurant pickRestaurant(ServerLevel level, List<DeliveryConfig.Restaurant> list) {
        if (list == null || list.isEmpty()) {
            // Spawn fallback, via LevelData velden, geen getSpawnPos.
            int x = level.getLevelData().getXSpawn();
            int y = level.getLevelData().getYSpawn();
            int z = level.getLevelData().getZSpawn();
            return new DeliveryConfig.Restaurant("Restaurant", new BlockPos(x, y, z));
        }
        int idx = level.random.nextInt(list.size());
        return list.get(idx);
    }

    public static boolean isAtPickup(ServerPlayer player) {
        DeliveryState s = get(player);
        if (s.stage != DeliveryState.Stage.GO_RESTAURANT) return false;
        if (s.pickupPos == null) return false;
        return closeEnough(player.blockPosition(), s.pickupPos, DeliveryConfig.PICKUP_RADIUS);
    }

    public static boolean isAtDropoff(ServerPlayer player) {
        DeliveryState s = get(player);
        if (s.stage != DeliveryState.Stage.GO_DROPOFF) return false;
        if (s.dropoffPos == null) return false;
        return closeEnough(player.blockPosition(), s.dropoffPos, DeliveryConfig.DROPOFF_RADIUS);
    }

    public static void onPickup(ServerPlayer player) {
        DeliveryState s = get(player);
        if (s.stage != DeliveryState.Stage.GO_RESTAURANT) return;
        if (!isAtPickup(player)) return;

        s.stage = DeliveryState.Stage.GO_DROPOFF;
        s.pickupGameTime = player.level().getGameTime();

        player.sendSystemMessage(Component.literal("Pickup gelukt. Bezorg bij klant."));
    }

    public static void onDropoff(ServerPlayer player) {
        DeliveryState s = get(player);
        if (s.stage != DeliveryState.Stage.GO_DROPOFF) return;
        if (!isAtDropoff(player)) return;

        long seconds = Math.max(0L, (player.level().getGameTime() - s.pickupGameTime) / 20L);

        int coins = 50;
        int xp = 40;

        boolean speedBonus = seconds > 0 && seconds <= 35;
        if (speedBonus) {
            coins += 20;
            xp += 20;
        }

        StatsManager.addCoins(player, coins);
        StatsManager.addXp(player, xp);

        String msg = "Bezorgd. +" + coins + " Coins, +" + xp + " XP. Tijd: " + seconds + "s";
        if (speedBonus) msg += ". Speed bonus";

        player.sendSystemMessage(Component.literal(msg));
        StatsManager.sendStatus(player);

        // Auto nieuwe order
        startOrder(player);
    }

    private static boolean closeEnough(BlockPos a, BlockPos b, int r) {
        int dx = a.getX() - b.getX();
        int dy = a.getY() - b.getY();
        int dz = a.getZ() - b.getZ();
        return dx * dx + dy * dy + dz * dz <= r * r;
    }
}