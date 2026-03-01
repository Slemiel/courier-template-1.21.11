package name.modid.hud;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HudManager {

    private static final Map<UUID, ServerBossEvent> BARS = new HashMap<>();

    public static void clear(ServerPlayer p) {
        ServerBossEvent bar = BARS.remove(p.getUUID());
        if (bar != null) bar.removePlayer(p);
    }

    private static ServerBossEvent bar(ServerPlayer p) {
        return BARS.computeIfAbsent(p.getUUID(), id -> {
            ServerBossEvent b = new ServerBossEvent(
                    Component.literal("Courier"),
                    BossEvent.BossBarColor.BLUE,
                    BossEvent.BossBarOverlay.PROGRESS
            );
            b.addPlayer(p);
            b.setVisible(true);
            b.setProgress(0.0f);
            return b;
        });
    }

    public static void showPickup(ServerPlayer p, String restaurantName) {
        ServerBossEvent b = bar(p);
        b.setColor(BossEvent.BossBarColor.YELLOW);
        b.setName(Component.literal("Pickup bij: " + restaurantName));
        b.setProgress(0.25f);
        b.setVisible(true);
    }

    public static void showDropoff(ServerPlayer p) {
        ServerBossEvent b = bar(p);
        b.setColor(BossEvent.BossBarColor.GREEN);
        b.setName(Component.literal("Bezorg bij klant"));
        b.setProgress(0.70f);
        b.setVisible(true);
    }

    public static void showDelivered(ServerPlayer p, int coins, int xp, int seconds, String bonus) {
        ServerBossEvent b = bar(p);
        b.setColor(BossEvent.BossBarColor.PURPLE);
        String extra = (bonus == null || bonus.isBlank()) ? "" : (" | " + bonus);
        b.setName(Component.literal("Bezorgd: +" + coins + "C +" + xp + "XP | " + seconds + "s" + extra));
        b.setProgress(1.0f);
        b.setVisible(true);
    }
}