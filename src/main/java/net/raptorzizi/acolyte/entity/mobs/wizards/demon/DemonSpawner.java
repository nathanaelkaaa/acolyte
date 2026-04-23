package net.raptorzizi.acolyte.entity.mobs.wizards.demon;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.raptorzizi.acolyte.registries.ModEntityRegistry;

public class DemonSpawner {

    public static DemonEntity spawnRandom(ServerLevel level, BlockPos pos) {
        int roll = level.random.nextInt(3);
        DemonEntity demon = switch (roll) {
            case 0 -> ModEntityRegistry.DEMON_ARCHER.get().create(level);
            case 1 -> ModEntityRegistry.DEMON_WARRIOR.get().create(level);
            default -> ModEntityRegistry.DEMON_MAGE.get().create(level);
        };

        if (demon == null) return null;

        demon.moveTo(
                pos.getX() + 0.5,
                pos.getY(),
                pos.getZ() + 0.5,
                level.random.nextFloat() * 360f,
                0
        );
        demon.finalizeSpawn(
                level,
                level.getCurrentDifficultyAt(pos),
                MobSpawnType.STRUCTURE,
                null
        );
        level.addFreshEntity(demon);
        return demon;
    }
}