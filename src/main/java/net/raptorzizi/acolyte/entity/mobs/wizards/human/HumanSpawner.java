package net.raptorzizi.acolyte.entity.mobs.wizards.human;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.raptorzizi.acolyte.registries.ModEntityRegistry;

public class HumanSpawner {

    public static HumanEntity spawnRandom(ServerLevel level, BlockPos pos) {
        int roll = level.random.nextInt(3);
        HumanEntity human = switch (roll) {
            case 0 -> ModEntityRegistry.HUMAN_MAGE.get().create(level);
            case 1 -> ModEntityRegistry.HUMAN_WARRIOR.get().create(level);
            default -> ModEntityRegistry.HUMAN_ARCHER.get().create(level);
        };

        if (human == null) return null;

        human.moveTo(
                pos.getX() + 0.5,
                pos.getY(),
                pos.getZ() + 0.5,
                level.random.nextFloat() * 360f,
                0
        );
        human.finalizeSpawn(
                level,
                level.getCurrentDifficultyAt(pos),
                MobSpawnType.STRUCTURE,
                null
        );
        level.addFreshEntity(human);
        return human;
    }
}