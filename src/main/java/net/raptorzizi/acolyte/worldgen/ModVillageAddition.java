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

        addBuildingToPool(templatePoolRegistry, processorListRegistry,
                ResourceLocation.parse("minecraft:village/plains/town_centers"),
                "acolyte:town_centers/plains_tavern_01", 150);

        addBuildingToPool(templatePoolRegistry, processorListRegistry,
                ResourceLocation.parse("minecraft:village/desert/town_centers"),
                "acolyte:town_centers/desert_tavern_01", 150);

        addBuildingToPool(templatePoolRegistry, processorListRegistry,
                ResourceLocation.parse("minecraft:village/taiga/town_centers"),
                "acolyte:town_centers/taiga_tavern_01", 150);

        addBuildingToPool(templatePoolRegistry, processorListRegistry,
                ResourceLocation.parse("minecraft:village/savanna/town_centers"),
                "acolyte:town_centers/savanna_tavern_01", 150);

        addBuildingToPool(templatePoolRegistry, processorListRegistry,
                ResourceLocation.parse("minecraft:village/snowy/town_centers"),
                "acolyte:town_centers/snowy_tavern_01", 150);
    }
}