package name.modid.delivery;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;

public final class CustomerGenerator {

    private CustomerGenerator() {}

    public static BlockPos findGround(ServerLevel level, int x, int z) {
        BlockPos top = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z));
        // top is meestal de eerste luchtblok boven de grond. Dus grond is top.below().
        BlockPos ground = top.below();
        return ground;
    }

    public static BlockPos pickDropoff(ServerLevel level, BlockPos center, int minDist, int maxDist) {
        RandomSource r = level.random;

        for (int i = 0; i < 60; i++) {
            int dx = r.nextInt(maxDist * 2 + 1) - maxDist;
            int dz = r.nextInt(maxDist * 2 + 1) - maxDist;

            int d2 = dx * dx + dz * dz;
            if (d2 < minDist * minDist) continue;
            if (d2 > maxDist * maxDist) continue;

            int x = center.getX() + dx;
            int z = center.getZ() + dz;

            BlockPos ground = findGround(level, x, z);
            BlockPos stand = ground.above();

            // Standplek moet lucht zijn en grond moet niet lucht zijn.
            if (!level.getBlockState(ground).isAir() && level.getBlockState(stand).isAir()) {
                return stand;
            }
        }

        // Fallback. Direct bij spawn-achtige plek rond center.
        BlockPos fallbackGround = findGround(level, center.getX(), center.getZ());
        return fallbackGround.above();
    }
}