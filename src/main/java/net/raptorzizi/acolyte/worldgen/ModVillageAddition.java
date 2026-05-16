package net.raptorzizi.acolyte.worldgen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.raptorzizi.acolyte.config.ModCommonConfigs;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber()
public class ModVillageAddition {

    private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = ResourceKey.create(Registries.PROCESSOR_LIST,
            ResourceLocation.fromNamespaceAndPath("minecraft", "empty"));

    private static void addBuildingToPool(Registry<StructureTemplatePool> templatePoolRegistry,
                                          Registry<StructureProcessorList> processorListRegistry,
                                          ResourceLocation poolRL, String nbtPieceRL, int weight) {

        Holder<StructureProcessorList> emptyProcessorList =
                processorListRegistry.getHolderOrThrow(EMPTY_PROCESSOR_LIST_KEY);

        StructureTemplatePool pool = templatePoolRegistry.get(poolRL);

        if (pool == null) return;

        SinglePoolElement piece = SinglePoolElement.legacy(nbtPieceRL,
                emptyProcessorList).apply(StructureTemplatePool.Projection.RIGID);

        for (int i = 0; i < weight; i++) {
            pool.templates.add(piece);
        }

        List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(pool.rawTemplates);
        listOfPieceEntries.add(new Pair<>(piece, weight));
        pool.rawTemplates = listOfPieceEntries;
    }

    @SubscribeEvent
    public static void addNewVillageBuilding(final ServerStartingEvent event) {
        Registry<StructureTemplatePool> templatePoolRegistry =
                event.getServer().registryAccess().registry(Registries.TEMPLATE_POOL).orElseThrow();
        Registry<StructureProcessorList> processorListRegistry =
                event.getServer().registryAccess().registry(Registries.PROCESSOR_LIST).orElseThrow();

        addTavern(templatePoolRegistry, processorListRegistry, "plains",
                "acolyte:streets/plains_tavern_01", "acolyte:tavern/houses/tavern_plains",
                ModCommonConfigs.PLAINS_TAVERN_POOL.get(),
                ModCommonConfigs.PLAINS_TAVERN_STREETS_WEIGHT.get(),
                ModCommonConfigs.PLAINS_TAVERN_HOUSES_WEIGHT.get());

        addTavern(templatePoolRegistry, processorListRegistry, "desert",
                "acolyte:streets/desert_tavern_01", "acolyte:tavern/houses/tavern_desert",
                ModCommonConfigs.DESERT_TAVERN_POOL.get(),
                ModCommonConfigs.DESERT_TAVERN_STREETS_WEIGHT.get(),
                ModCommonConfigs.DESERT_TAVERN_HOUSES_WEIGHT.get());

        addTavern(templatePoolRegistry, processorListRegistry, "taiga",
                "acolyte:streets/taiga_tavern_01", "acolyte:tavern/houses/tavern_taiga",
                ModCommonConfigs.TAIGA_TAVERN_POOL.get(),
                ModCommonConfigs.TAIGA_TAVERN_STREETS_WEIGHT.get(),
                ModCommonConfigs.TAIGA_TAVERN_HOUSES_WEIGHT.get());

        addTavern(templatePoolRegistry, processorListRegistry, "savanna",
                "acolyte:streets/savanna_tavern_01", "acolyte:tavern/houses/tavern_savanna",
                ModCommonConfigs.SAVANNA_TAVERN_POOL.get(),
                ModCommonConfigs.SAVANNA_TAVERN_STREETS_WEIGHT.get(),
                ModCommonConfigs.SAVANNA_TAVERN_HOUSES_WEIGHT.get());

        addTavern(templatePoolRegistry, processorListRegistry, "snowy",
                "acolyte:streets/snowy_tavern_01", "acolyte:tavern/houses/tavern_snowy",
                ModCommonConfigs.SNOWY_TAVERN_POOL.get(),
                ModCommonConfigs.SNOWY_TAVERN_STREETS_WEIGHT.get(),
                ModCommonConfigs.SNOWY_TAVERN_HOUSES_WEIGHT.get());
    }

    private static void addTavern(Registry<StructureTemplatePool> templatePoolRegistry,
                                   Registry<StructureProcessorList> processorListRegistry,
                                   String biome, String streetPiece, String housePiece,
                                   String pool, int streetsWeight, int housesWeight) {
        switch (pool) {
            case "streets" -> addBuildingToPool(templatePoolRegistry, processorListRegistry,
                    ResourceLocation.parse("minecraft:village/" + biome + "/streets"), streetPiece, streetsWeight);
            case "houses"  -> addBuildingToPool(templatePoolRegistry, processorListRegistry,
                    ResourceLocation.parse("minecraft:village/" + biome + "/houses"), housePiece, housesWeight);
            case "both"    -> {
                addBuildingToPool(templatePoolRegistry, processorListRegistry,
                        ResourceLocation.parse("minecraft:village/" + biome + "/streets"), streetPiece, streetsWeight);
                addBuildingToPool(templatePoolRegistry, processorListRegistry,
                        ResourceLocation.parse("minecraft:village/" + biome + "/houses"), housePiece, housesWeight);
            }
            // "disabled" → nothing
        }
    }
}