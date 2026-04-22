package net.raptorzizi.acolyte.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ModClientConfigs {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    static {
        SPEC = BUILDER.build();
    }
}