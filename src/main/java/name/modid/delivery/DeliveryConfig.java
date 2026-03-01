package name.modid.delivery;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public final class DeliveryConfig {

    private DeliveryConfig() {}

    public record Restaurant(String name, BlockPos pos) {}

    public static final int PICKUP_RADIUS = 6;
    public static final int DROPOFF_RADIUS = 12;

    public static final int DROPOFF_MIN_DISTANCE = 120;
    public static final int DROPOFF_MAX_DISTANCE = 400;

    public static final List<Restaurant> RESTAURANTS = new ArrayList<>();

    static {
        // Tijdelijke defaults. Jij stuurt later vaste coordinaten door.
        // Let op. Y wordt genegeerd, we projecteren naar grond.
        RESTAURANTS.add(new Restaurant("Fruits Corner", new BlockPos(0, 0, 0)));
        RESTAURANTS.add(new Restaurant("Pizza Plaza", new BlockPos(-35, 0, 25)));
    }
}