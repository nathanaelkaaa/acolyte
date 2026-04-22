package net.raptorzizi.acolyte.block.tavern_spawn_marker;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.raptorzizi.acolyte.registries.ModBlockEntityRegistry;
import org.jetbrains.annotations.Nullable;

public class TavernSpawnMarkerBlock extends Block implements EntityBlock {

    public TavernSpawnMarkerBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TavernSpawnMarkerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return type == ModBlockEntityRegistry.TAVERN_SPAWN_MARKER_BE.get()
                ? (lvl, pos, st, be) -> TavernSpawnMarkerBlockEntity.tick(lvl, pos, st, (TavernSpawnMarkerBlockEntity) be)
                : null;
    }
}