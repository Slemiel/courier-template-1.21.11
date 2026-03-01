package name.modid.delivery;

import net.minecraft.core.BlockPos;

public class DeliveryState {

    public enum Stage {
        NONE,
        GO_RESTAURANT,
        GO_DROPOFF
    }

    public Stage stage = Stage.NONE;

    public String restaurantName = "";
    public BlockPos pickupPos = null;
    public BlockPos dropoffPos = null;

    public long pickupGameTime = 0L;

    public void clear() {
        stage = Stage.NONE;
        restaurantName = "";
        pickupPos = null;
        dropoffPos = null;
        pickupGameTime = 0L;
    }
}