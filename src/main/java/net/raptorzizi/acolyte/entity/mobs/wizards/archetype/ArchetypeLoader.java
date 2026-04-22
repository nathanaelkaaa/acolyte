package net.raptorzizi.acolyte.entity.mobs.wizards.archetype;

import com.google.gson.*;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.raptorzizi.acolyte.AcolyteMod;

import java.io.*;
import java.util.*;

public class ArchetypeLoader extends SimplePreparableReloadListener<Map<String, List<ArchetypeProfile>>> {

    public static final ArchetypeLoader INSTANCE = new ArchetypeLoader();
    private static final Gson GSON = new GsonBuilder().create();
    private Map<String, List<ArchetypeProfile>> profiles = new HashMap<>();

    @Override
    protected Map<String, List<ArchetypeProfile>> prepare(ResourceManager manager, ProfilerFiller profiler) {
        Map<String, List<ArchetypeProfile>> result = new HashMap<>();
        String folder = "archetype";

        manager.listResources(folder, path -> path.getPath().endsWith(".json")).forEach((location, resource) -> {
            String path = location.getPath();
            String name = path.substring(folder.length() + 1, path.length() - 5);

            try (Reader reader = new InputStreamReader(resource.open())) {
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                List<ArchetypeProfile> list = new ArrayList<>();
                for (JsonElement el : json.getAsJsonArray("profiles")) {
                    list.add(ArchetypeProfile.fromJson(el.getAsJsonObject()));
                }
                result.put(name, list);
            } catch (Exception e) {
                AcolyteMod.LOGGER.error("Failed to load archetype profile: {}", location, e);
            }
        });

        return result;
    }

    @Override
    protected void apply(Map<String, List<ArchetypeProfile>> prepared, ResourceManager manager, ProfilerFiller profiler) {
        this.profiles = prepared;
        this.profiles.forEach((archetypeName, list) -> {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).profileId == null) {
                    list.get(i).profileId = archetypeName + ":" + i;
                }
            }
        });
        AcolyteMod.LOGGER.info("Loaded {} archetype profiles", profiles.size());
    }

    public ArchetypeProfile getProfileById(String archetypeName, String profileId) {
        return profiles.getOrDefault(archetypeName, List.of())
                .stream()
                .filter(p -> profileId.equals(p.profileId))
                .findFirst()
                .orElse(null);
    }

    public ArchetypeProfile rollProfile(String archetypeName, Random random) {
        List<ArchetypeProfile> list = profiles.getOrDefault(archetypeName, List.of());
        if (list.isEmpty()) return null;

        int totalWeight = list.stream().mapToInt(p -> p.weight).sum();
        int roll = random.nextInt(totalWeight);
        int cumulative = 0;
        for (ArchetypeProfile profile : list) {
            cumulative += profile.weight;
            if (roll < cumulative) return profile;
        }
        return list.get(0);
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(INSTANCE);
    }
}