package net.raptorzizi.acolyte.util;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.neoforge.common.Tags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

public class ModUtils {

    public static <T extends Mob> boolean checkDemonSpawnRules(EntityType<T> type, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return !pLevel.getBiome(pPos).is(Tags.Biomes.NO_DEFAULT_MONSTERS)
                && pLevel.getDifficulty() != Difficulty.PEACEFUL
                && Monster.isDarkEnoughToSpawn(pLevel, pPos, pRandom)
                && Monster.checkMobSpawnRules(type, pLevel, pSpawnType, pPos, pRandom);
    }

    public static <T extends Mob> boolean checkHumanSpawnRules(EntityType<T> type, ServerLevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return !pLevel.getBiome(pPos).is(Tags.Biomes.NO_DEFAULT_MONSTERS)
                && pLevel.getDifficulty() != Difficulty.PEACEFUL
                && Mob.checkMobSpawnRules(type, pLevel, pSpawnType, pPos, pRandom);
    }

    public static ResourceLocation resolveAnimFile(AnimationHolder holder) {
        return holder.getForPlayer()
                .filter(rl -> !rl.getNamespace().equals(IronsSpellbooks.MODID))
                .map(rl -> ResourceLocation.fromNamespaceAndPath(rl.getNamespace(), "animations/" + rl.getPath() + ".json"))
                .orElse(AbstractSpellCastingMob.animationInstantCast);
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