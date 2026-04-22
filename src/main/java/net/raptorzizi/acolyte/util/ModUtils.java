package net.raptorzizi.acolyte.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.common.Tags;
import net.raptorzizi.acolyte.registries.ModEntityRegistry;

public class ModUtils {

    public static boolean checkDemonMageSpawnRules(ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return !pLevel.getBiome(pPos).is(Tags.Biomes.NO_DEFAULT_MONSTERS)
                && pLevel.getDifficulty() != Difficulty.PEACEFUL
                && Monster.isDarkEnoughToSpawn(pLevel, pPos, pRandom)
                && Monster.checkMobSpawnRules(ModEntityRegistry.DEMON_MAGE.get(), pLevel, pSpawnType, pPos, pRandom);
    }

    public static boolean checkDemonWarriorSpawnRules(ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return !pLevel.getBiome(pPos).is(Tags.Biomes.NO_DEFAULT_MONSTERS)
                && pLevel.getDifficulty() != Difficulty.PEACEFUL
                && Monster.isDarkEnoughToSpawn(pLevel, pPos, pRandom)
                && Monster.checkMobSpawnRules(ModEntityRegistry.DEMON_WARRIOR.get(), pLevel, pSpawnType, pPos, pRandom);
    }

    public static boolean checkDemonArcherSpawnRules(ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return !pLevel.getBiome(pPos).is(Tags.Biomes.NO_DEFAULT_MONSTERS)
                && pLevel.getDifficulty() != Difficulty.PEACEFUL
                && Monster.isDarkEnoughToSpawn(pLevel, pPos, pRandom)
                && Monster.checkMobSpawnRules(ModEntityRegistry.DEMON_ARCHER.get(), pLevel, pSpawnType, pPos, pRandom);
    }

    public static String resolveBiomeFolder(ServerLevelAccessor level, BlockPos pos) {
        ResourceLocation biome = level.getBiome(pos).unwrapKey()
                .map(k -> k.location())
                .orElse(null);

        if (biome == null) return "plains";

        String path = biome.getPath();

        if (path.contains("taiga"))         return "taiga";
        if (path.contains("desert"))        return "desert";
        if (path.contains("jungle"))        return "jungle";
        if (path.contains("savanna"))       return "savanna";
        if (path.contains("snowy") || path.contains("frozen")) return "snowy";
        if (path.contains("swamp"))         return "swamp";
        if (path.contains("badlands"))      return "desert";

        return "plains";
    }
}