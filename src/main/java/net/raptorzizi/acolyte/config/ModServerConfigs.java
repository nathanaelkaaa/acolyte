package net.raptorzizi.acolyte.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ModServerConfigs {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Integer> TAVERN_SPAWNRATE;

    static {
        {
            BUILDER.push("Worldgen");
            TAVERN_SPAWNRATE = BUILDER.comment("The weight of the tavern spawning in a village. Default: 4")
                    .define("tavernWeight", 10);
            BUILDER.pop();
        }

        SPEC = BUILDER.build();
    }
}