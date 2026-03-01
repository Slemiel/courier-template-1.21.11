package name.modid.delivery;

import net.minecraft.core.BlockPos;

public class DeliveryState {
    public enum Stage {
        NONE,
        GO_RESTAURANT,
        GO_DROPOFF
    }

    public Stage stage = Stage.NONE;

    // Dit was bij jou kwijt geraakt. Courier en DeliveryInteractions verwachten dit.
    public BlockPos restaurantPos = null;
    public BlockPos dropoffPos = null;

    public String restaurantName = "Restaurant";

    public long pickupGameTime = 0L;

    public void clear() {
        stage = Stage.NONE;
        restaurantPos = null;
        dropoffPos = null;
        restaurantName = "Restaurant";
        pickupGameTime = 0L;
    }
}