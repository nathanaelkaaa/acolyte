package net.raptorzizi.acolyte.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class ModCommonConfigs {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    // -------------------------------------------------------------------------
    // Tavern village pool placement
    // Valid values: "streets", "houses", "both", "disabled"
    // These settings are read before world creation and apply to all new worlds.
    // -------------------------------------------------------------------------

    public static final ModConfigSpec.ConfigValue<String> PLAINS_TAVERN_POOL;
    public static final ModConfigSpec.ConfigValue<String> DESERT_TAVERN_POOL;
    public static final ModConfigSpec.ConfigValue<String> TAIGA_TAVERN_POOL;
    public static final ModConfigSpec.ConfigValue<String> SAVANNA_TAVERN_POOL;
    public static final ModConfigSpec.ConfigValue<String> SNOWY_TAVERN_POOL;

    public static final ModConfigSpec.IntValue PLAINS_TAVERN_STREETS_WEIGHT;
    public static final ModConfigSpec.IntValue PLAINS_TAVERN_HOUSES_WEIGHT;
    public static final ModConfigSpec.IntValue DESERT_TAVERN_STREETS_WEIGHT;
    public static final ModConfigSpec.IntValue DESERT_TAVERN_HOUSES_WEIGHT;
    public static final ModConfigSpec.IntValue TAIGA_TAVERN_STREETS_WEIGHT;
    public static final ModConfigSpec.IntValue TAIGA_TAVERN_HOUSES_WEIGHT;
    public static final ModConfigSpec.IntValue SAVANNA_TAVERN_STREETS_WEIGHT;
    public static final ModConfigSpec.IntValue SAVANNA_TAVERN_HOUSES_WEIGHT;
    public static final ModConfigSpec.IntValue SNOWY_TAVERN_STREETS_WEIGHT;
    public static final ModConfigSpec.IntValue SNOWY_TAVERN_HOUSES_WEIGHT;

    static {
        BUILDER.push("tavern_placement");
        BUILDER.comment("Choose which village pool each tavern is added to.",
                        "Valid values: \"streets\", \"houses\", \"both\", \"disabled\"",
                        "These settings apply globally to all worlds.");

        BUILDER.push("plains");
        PLAINS_TAVERN_POOL           = BUILDER.comment("Plains village tavern pool placement.")
                .define("pool", "streets", ModCommonConfigs::isValidPool);
        PLAINS_TAVERN_STREETS_WEIGHT = BUILDER.comment("Weight in the streets pool.")
                .defineInRange("streets_weight", 3, 1, 100);
        PLAINS_TAVERN_HOUSES_WEIGHT  = BUILDER.comment("Weight in the houses pool.")
                .defineInRange("houses_weight",  12, 1, 100);
        BUILDER.pop();

        BUILDER.push("desert");
        DESERT_TAVERN_POOL           = BUILDER.comment("Desert village tavern pool placement.")
                .define("pool", "streets", ModCommonConfigs::isValidPool);
        DESERT_TAVERN_STREETS_WEIGHT = BUILDER.comment("Weight in the streets pool.")
                .defineInRange("streets_weight", 3, 1, 100);
        DESERT_TAVERN_HOUSES_WEIGHT  = BUILDER.comment("Weight in the houses pool.")
                .defineInRange("houses_weight",  12, 1, 100);
        BUILDER.pop();

        BUILDER.push("taiga");
        TAIGA_TAVERN_POOL           = BUILDER.comment("Taiga village tavern pool placement.")
                .define("pool", "streets", ModCommonConfigs::isValidPool);
        TAIGA_TAVERN_STREETS_WEIGHT = BUILDER.comment("Weight in the streets pool.")
                .defineInRange("streets_weight", 3, 1, 100);
        TAIGA_TAVERN_HOUSES_WEIGHT  = BUILDER.comment("Weight in the houses pool.")
                .defineInRange("houses_weight",  12, 1, 100);
        BUILDER.pop();

        BUILDER.push("savanna");
        SAVANNA_TAVERN_POOL           = BUILDER.comment("Savanna village tavern pool placement.")
                .define("pool", "streets", ModCommonConfigs::isValidPool);
        SAVANNA_TAVERN_STREETS_WEIGHT = BUILDER.comment("Weight in the streets pool.")
                .defineInRange("streets_weight", 3, 1, 100);
        SAVANNA_TAVERN_HOUSES_WEIGHT  = BUILDER.comment("Weight in the houses pool.")
                .defineInRange("houses_weight",  12, 1, 100);
        BUILDER.pop();

        BUILDER.push("snowy");
        SNOWY_TAVERN_POOL           = BUILDER.comment("Snowy village tavern pool placement.")
                .define("pool", "streets", ModCommonConfigs::isValidPool);
        SNOWY_TAVERN_STREETS_WEIGHT = BUILDER.comment("Weight in the streets pool.")
                .defineInRange("streets_weight", 3, 1, 100);
        SNOWY_TAVERN_HOUSES_WEIGHT  = BUILDER.comment("Weight in the houses pool.")
                .defineInRange("houses_weight",  12, 1, 100);
        BUILDER.pop();

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    private static final List<String> VALID_POOLS = List.of("streets", "houses", "both", "disabled");

    private static boolean isValidPool(Object value) {
        return value instanceof String s && VALID_POOLS.contains(s);
    }
}
