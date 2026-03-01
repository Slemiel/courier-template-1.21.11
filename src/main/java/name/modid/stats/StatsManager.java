package name.modid.stats;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class StatsManager {

    private StatsManager() {}

    private static final class Stats {
        int coins = 0;
        int xp = 0;
        int level = 1;
    }

    private static final Map<UUID, Stats> DATA = new HashMap<>();

    private static Stats s(ServerPlayer p) {
        return DATA.computeIfAbsent(p.getUUID(), k -> new Stats());
    }

    public static void addCoins(ServerPlayer p, int amount) {
        if (amount <= 0) return;
        s(p).coins += amount;
    }

    public static void addXp(ServerPlayer p, int amount) {
        if (amount <= 0) return;

        Stats st = s(p);
        st.xp += amount;

        // Simpel level systeem. 100 xp per level, oplopend met 50 per level.
        while (st.xp >= xpForNextLevel(st.level)) {
            st.xp -= xpForNextLevel(st.level);
            st.level++;
        }
    }

    private static int xpForNextLevel(int currentLevel) {
        return 100 + (currentLevel - 1) * 50;
    }

    public static int getCoins(ServerPlayer p) {
        return s(p).coins;
    }

    public static int getXp(ServerPlayer p) {
        return s(p).xp;
    }

    public static int getLevel(ServerPlayer p) {
        return s(p).level;
    }

    public static int getNextXp(ServerPlayer p) {
        return xpForNextLevel(s(p).level);
    }

    public static void sendStatus(ServerPlayer p) {
        p.sendSystemMessage(Component.literal(
                "Coins=" + getCoins(p) + " XP=" + getXp(p) + " Level=" + getLevel(p) + " NextXP=" + getNextXp(p)
        ));
    }
}