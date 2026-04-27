package net.raptorzizi.acolyte.entity.mobs.horn_merchant;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.levelgen.Heightmap;
import net.raptorzizi.acolyte.registries.ModEntityRegistry;

public class HornMerchantSpawner implements CustomSpawner {
    private int tickDelay = 24000;

    @Override
    public int tick(ServerLevel level, boolean spawnEnemies, boolean spawnFriendlies) {
        if (--this.tickDelay > 0) {
            return 0;
        }

        this.tickDelay = 24000 + level.random.nextInt(12000);
        if (level.random.nextInt(100) > 15) {
            return 0;
        }

        return spawnMerchantNearPlayer(level);
    }

    private int spawnMerchantNearPlayer(ServerLevel level) {
        ServerPlayer player = level.getRandomPlayer();
        if (player == null) return 0;

        RandomSource random = level.random;

        int x = (24 + random.nextInt(16)) * (random.nextBoolean() ? 1 : -1);
        int z = (24 + random.nextInt(16)) * (random.nextBoolean() ? 1 : -1);

        BlockPos playerPos = player.blockPosition();
        BlockPos targetPos = playerPos.offset(x, 0, z);

        BlockPos surfacePos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, targetPos);

        if (!level.getWorldBorder().isWithinBounds(surfacePos) || !level.getBlockState(surfacePos).isAir()) {
            return 0;
        }

        HornMerchantEntity merchant = ModEntityRegistry.HORN_MERCHANT.get().spawn(level, surfacePos, MobSpawnType.EVENT);
        if (merchant != null) {
            merchant.setDespawnDelay(48000);
            return 1;
        }

        return 0;
    }
}