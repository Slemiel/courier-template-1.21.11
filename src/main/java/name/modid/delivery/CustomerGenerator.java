package name.modid.delivery;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;

public final class CustomerGenerator {

    private CustomerGenerator() {}

    // Kies een dropoff op "grond" met Heightmap, dus nooit in de lucht.
    // origin = restaurant of spelerpositie. minDist en maxDist in blokken.
    public static BlockPos pickDropoff(ServerLevel level, BlockPos origin, int minDist, int maxDist) {
        RandomSource rnd = level.getRandom();

        for (int tries = 0; tries < 80; tries++) {
            int dx = rnd.nextInt(maxDist * 2 + 1) - maxDist;
            int dz = rnd.nextInt(maxDist * 2 + 1) - maxDist;

            int distManhattan = Math.abs(dx) + Math.abs(dz);
            if (distManhattan < minDist) continue;

            int x = origin.getX() + dx;
            int z = origin.getZ() + dz;

            BlockPos ground = findGround(level, x, z);
            if (ground == null) continue;

            // Dropoff is 1 blok boven de grond, daar staat de speler dus.
            BlockPos dropoff = ground.above();

            // Simpele check: genoeg ruimte om te staan.
            if (!level.getBlockState(dropoff).isAir()) continue;
            if (!level.getBlockState(dropoff.above()).isAir()) continue;

            return dropoff;
        }

        // Fallback: vlak bij origin op de grond.
        BlockPos fallbackGround = findGround(level, origin.getX(), origin.getZ());
        return fallbackGround != null ? fallbackGround.above() : origin;
    }

    // Ground zoeken met Heightmap. Dit voorkomt getMinBuildHeight/getMaxBuildHeight issues.
    public static BlockPos findGround(ServerLevel level, int x, int z) {
        int topY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        // getHeight geeft de "eerste vrije" y boven de top. Dus ground = y-1.
        int y = topY - 1;

        // Als je in void of rare wereld zit, kan dit heel laag zijn.
        // Dan geven we null terug zodat de caller opnieuw probeert.
        if (y < -64) return null;

        return new BlockPos(x, y, z);
    }
}