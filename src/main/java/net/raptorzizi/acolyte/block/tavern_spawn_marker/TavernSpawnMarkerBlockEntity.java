package net.raptorzizi.acolyte.block.tavern_spawn_marker;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.raptorzizi.acolyte.entity.mobs.wizards.human.HumanEntity;
import net.raptorzizi.acolyte.entity.mobs.wizards.human.HumanMageEntity;
import net.raptorzizi.acolyte.entity.mobs.wizards.human.HumanSpawner;
import net.raptorzizi.acolyte.registries.ModBlockEntityRegistry;

public class TavernSpawnMarkerBlockEntity extends BlockEntity {

    private static final int DETECTION_RADIUS = 20;
    private static final int SPAWN_RADIUS = 5;
    private static final int CHECK_INTERVAL = 400;

    public TavernSpawnMarkerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityRegistry.TAVERN_SPAWN_MARKER_BE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TavernSpawnMarkerBlockEntity be) {
        if (level.isClientSide) return;

        long offset = (pos.getX() * 31L + pos.getZ() * 17L) % CHECK_INTERVAL;
        if ((level.getGameTime() + offset) % CHECK_INTERVAL != 0) return;

        ServerLevel serverLevel = (ServerLevel) level;

        if (serverLevel.random.nextFloat() > 0.4f) return;

        int count = serverLevel.getEntitiesOfClass(
                HumanMageEntity.class,
                new AABB(pos).inflate(DETECTION_RADIUS)
        ).size();

        int minCount = 1 + serverLevel.random.nextInt(2);

        if (count < minCount) {
            BlockPos spawnPos = findValidSpawnPos(serverLevel, pos);
            if (spawnPos == null) return;

            HumanEntity human = HumanSpawner.spawnRandom(serverLevel, spawnPos);
            if (human == null) return;
            human.setTavernCenter(pos);

            human.moveTo(
                    spawnPos.getX() + 0.5,
                    spawnPos.getY(),
                    spawnPos.getZ() + 0.5,
                    serverLevel.random.nextFloat() * 360f,
                    0
            );
            human.finalizeSpawn(
                    serverLevel,
                    serverLevel.getCurrentDifficultyAt(spawnPos),
                    MobSpawnType.STRUCTURE,
                    null
            );
            serverLevel.addFreshEntity(human);
        }
    }

    private static BlockPos findValidSpawnPos(ServerLevel level, BlockPos center) {
        for (int attempts = 0; attempts < 20; attempts++) {
            int x = center.getX() + level.random.nextInt(SPAWN_RADIUS * 2) - SPAWN_RADIUS;
            int z = center.getZ() + level.random.nextInt(SPAWN_RADIUS * 2) - SPAWN_RADIUS;

            for (int y = center.getY() + 3; y >= center.getY() - 3; y--) {
                BlockPos candidate = new BlockPos(x, y, z);
                BlockPos below = candidate.below();
                BlockPos above = candidate.above();

                boolean floorSolid = level.getBlockState(below).isSolidRender(level, below);
                boolean feetEmpty = !level.getBlockState(candidate).isSolid();
                boolean headEmpty = !level.getBlockState(above).isSolid();
                boolean notFloating = !level.getBlockState(below).isAir();

                if (floorSolid && feetEmpty && headEmpty && notFloating) {
                    return candidate;
                }
            }
        }
        return null;
    }
}