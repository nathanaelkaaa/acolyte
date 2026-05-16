package net.raptorzizi.acolyte.block.demon_spawn_marker;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.raptorzizi.acolyte.registries.ModBlockEntityRegistry;
import net.raptorzizi.acolyte.registries.ModEntityRegistry;
import net.raptorzizi.acolyte.setup.ModGameRules;

public class DemonSpawnMarkerBlockEntity extends BlockEntity {

    private int spawnCount = 1;
    private boolean hasSpawned = false;

    public DemonSpawnMarkerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityRegistry.DEMON_SPAWN_MARKER_BE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DemonSpawnMarkerBlockEntity be) {
        if (level.isClientSide || be.hasSpawned) return;

        ServerLevel serverLevel = (ServerLevel) level;

        if (!serverLevel.getGameRules().getBoolean(ModGameRules.ALLOW_DEMON_STRUCTURE_SPAWNING)) return;

        for (int i = 0; i < be.spawnCount; i++) {
            BlockPos spawnPos = findValidSpawnPos(serverLevel, pos);
            if (spawnPos == null) spawnPos = pos.above();

            EntityType<?> type = switch (serverLevel.random.nextInt(3)) {
                case 0 -> ModEntityRegistry.DEMON_WARRIOR.get();
                case 1 -> ModEntityRegistry.DEMON_ARCHER.get();
                default -> ModEntityRegistry.DEMON_MAGE.get();
            };

            Mob demon = (Mob) type.create(serverLevel);
            if (demon == null) continue;

            demon.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5,
                    serverLevel.random.nextFloat() * 360f, 0);
            demon.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(spawnPos),
                    MobSpawnType.STRUCTURE, null);
            demon.setPersistenceRequired();
            serverLevel.addFreshEntity(demon);
        }

        be.hasSpawned = true;
        be.setChanged();
    }

    private static BlockPos findValidSpawnPos(ServerLevel level, BlockPos center) {
        for (int y = 1; y <= 3; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos candidate = center.offset(x, y, z);
                    BlockPos below = candidate.below();
                    BlockPos above = candidate.above();
                    if (!level.getBlockState(candidate).isSolid()
                            && !level.getBlockState(above).isSolid()
                            && !level.getBlockState(below).isAir()) {
                        return candidate;
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("spawnCount", spawnCount);
        tag.putBoolean("hasSpawned", hasSpawned);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("spawnCount")) {
            spawnCount = Math.max(1, tag.getInt("spawnCount"));
        }
        hasSpawned = tag.getBoolean("hasSpawned");
    }
}
