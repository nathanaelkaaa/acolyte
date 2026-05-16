package net.raptorzizi.acolyte.setup;

import net.minecraft.world.level.GameRules;

public class ModGameRules {

    public static final GameRules.Key<GameRules.BooleanValue> ALLOW_DEMON_STRUCTURE_SPAWNING =
            GameRules.register("allowDemonStructureSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));

    public static final GameRules.Key<GameRules.BooleanValue> ALLOW_HUMAN_STRUCTURE_SPAWNING =
            GameRules.register("allowHumanStructureSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));

    public static final GameRules.Key<GameRules.BooleanValue> ALLOW_HORN_MERCHANT_SPAWNING =
            GameRules.register("allowHornMerchantSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));

    public static final GameRules.Key<GameRules.BooleanValue> ALLOW_DEMON_NATURAL_SPAWNING =
            GameRules.register("allowDemonNaturalSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));

    public static final GameRules.Key<GameRules.BooleanValue> ALLOW_HUMAN_NATURAL_SPAWNING =
            GameRules.register("allowHumanNaturalSpawning", GameRules.Category.SPAWNING, GameRules.BooleanValue.create(true));

    public static void init() {
    }
}
