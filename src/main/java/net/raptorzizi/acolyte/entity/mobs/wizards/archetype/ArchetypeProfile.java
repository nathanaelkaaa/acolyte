package net.raptorzizi.acolyte.entity.mobs.wizards.archetype;

import com.google.gson.*;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.BlockPos;
import net.raptorzizi.acolyte.AcolyteMod;

import javax.annotation.Nullable;
import java.util.*;

public class ArchetypeProfile {

    public final int weight;
    public final int tier;
    public String profileId;
    @Nullable public final String customName;
    @Nullable public final ResourceLocation customSkin;
    @Nullable public final Map<String, Double> statOverrides;
    public final int xpReward;
    public final int attackInterval;
    public final int barrageAttackInterval;
    public final int barrageProjectileCount;
    public final int singleUseSpellDelay;
    public final float spellsQuality;

    @Nullable public final Item mainhand;
    @Nullable public final Item offhand;
    @Nullable public final Item head;
    @Nullable public final Item chest;
    @Nullable public final Item legs;
    @Nullable public final Item feet;

    @Nullable public final AbstractSpell barrageSpell;
    @Nullable public final AbstractSpell singleUseSpell;
    public final List<AbstractSpell> attackSpells;
    public final List<AbstractSpell> defenseSpells;
    public final List<AbstractSpell> mobilitySpells;
    public final List<AbstractSpell> utilitySpells;
    @Nullable public final List<String> allowedBiomes;

    private ArchetypeProfile(
            int weight,
            int tier,
            @Nullable String customName,
            @Nullable ResourceLocation customSkin,
            @Nullable Map<String, Double> statOverrides,
            int xpReward,
            int attackInterval,
            int barrageAttackInterval,
            int barrageProjectileCount,
            int singleUseSpellDelay,
            float spellsQuality,
            @Nullable Item mainhand, @Nullable Item offhand,
            @Nullable Item head, @Nullable Item chest,
            @Nullable Item legs, @Nullable Item feet,
            @Nullable AbstractSpell barrageSpell, @Nullable AbstractSpell singleUseSpell,
            List<AbstractSpell> attack, List<AbstractSpell> defense,
            List<AbstractSpell> mobility, List<AbstractSpell> utility,
            @Nullable List<String> allowedBiomes) {
        this.weight = weight;
        this.tier = tier;
        this.customName = customName;
        this.customSkin = customSkin;
        this.statOverrides = statOverrides;
        this.xpReward = xpReward;
        this.attackInterval = attackInterval;
        this.barrageAttackInterval  = barrageAttackInterval;
        this.barrageProjectileCount = barrageProjectileCount;
        this.singleUseSpellDelay    = singleUseSpellDelay;
        this.spellsQuality          = spellsQuality;
        this.mainhand = mainhand;
        this.offhand = offhand;
        this.head = head;
        this.chest = chest;
        this.legs = legs;
        this.feet = feet;
        this.barrageSpell = barrageSpell;
        this.singleUseSpell = singleUseSpell;
        this.attackSpells = attack;
        this.defenseSpells = defense;
        this.mobilitySpells = mobility;
        this.utilitySpells = utility;
        this.allowedBiomes = allowedBiomes;
    }

    public static ArchetypeProfile fromJson(JsonObject json) {
        int weight = json.has("weight") ? json.get("weight").getAsInt() : 1;
        int tier = json.has("tier") ? json.get("tier").getAsInt() : 1;
        String profileId = json.has("id") ? json.get("id").getAsString() : null;
        String customName = json.has("name") ? json.get("name").getAsString() : null;

        ResourceLocation customSkin = null;
        if (json.has("skin")) {
            String skinPath = json.get("skin").getAsString();
            customSkin = skinPath.contains(":")
                    ? ResourceLocation.parse(skinPath)
                    : ResourceLocation.fromNamespaceAndPath("acolyte", skinPath);
            AcolyteMod.LOGGER.info("[ArchetypeProfile] skin parsed: {}", customSkin);
        }

        int xpReward = 15;
        Map<String, Double> statOverrides = null;
        int attackInterval   = 30;
        int barrageAttackInterval  = 80;
        int barrageProjectileCount = 1;
        int singleUseSpellDelay    = 30;
        float spellsQuality        = 0.3f;


        if (json.has("stats")) {
            JsonObject stats = json.getAsJsonObject("stats");
            statOverrides = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : stats.entrySet()) {
                switch (entry.getKey()) {
                    case "xp_reward"                -> xpReward               = entry.getValue().getAsInt();
                    case "attack_interval"          -> attackInterval         = entry.getValue().getAsInt();
                    case "barrage_attack_interval"  -> barrageAttackInterval  = entry.getValue().getAsInt();
                    case "barrage_projectile_count" -> barrageProjectileCount = entry.getValue().getAsInt();
                    case "single_use_spell_delay"   -> singleUseSpellDelay    = entry.getValue().getAsInt();
                    case "spells_quality"           -> spellsQuality          = entry.getValue().getAsFloat();
                    default -> statOverrides.put(entry.getKey(), entry.getValue().getAsDouble());
                }
            }
            if (statOverrides.isEmpty()) statOverrides = null;
        }

        List<String> allowedBiomes = null;
        if (json.has("biomes")) {
            allowedBiomes = new ArrayList<>();
            for (JsonElement el : json.getAsJsonArray("biomes")) {
                allowedBiomes.add(el.getAsString());
            }
            if (allowedBiomes.isEmpty()) allowedBiomes = null;
        }

        JsonObject eq = json.has("equipment") ? json.getAsJsonObject("equipment") : new JsonObject();
        JsonObject spellsObj = json.has("spells") ? json.getAsJsonObject("spells") : json;

        ArchetypeProfile profile = new ArchetypeProfile(
                weight,
                tier,
                customName,
                customSkin,
                statOverrides,
                xpReward,
                attackInterval,
                barrageAttackInterval,
                barrageProjectileCount,
                singleUseSpellDelay,
                spellsQuality,
                parseItem(eq, "mainhand"),
                parseItem(eq, "offhand"),
                parseItem(eq, "head"),
                parseItem(eq, "chest"),
                parseItem(eq, "legs"),
                parseItem(eq, "feet"),
                spellsObj.has("barrage_spell")    ? parseSpell(spellsObj.get("barrage_spell").getAsString())    : null,
                spellsObj.has("single_use_spell") ? parseSpell(spellsObj.get("single_use_spell").getAsString()) : null,
                parseSpellList(spellsObj, "attack_spells"),
                parseSpellList(spellsObj, "defense_spells"),
                parseSpellList(spellsObj, "mobility_spells"),
                parseSpellList(spellsObj, "utility_spells"),
                allowedBiomes
        );
        profile.profileId = profileId;
        return profile;
    }

    @Nullable
    private static Item parseItem(JsonObject json, String key) {
        if (!json.has(key)) return null;
        String id = json.get(key).getAsString();
        if (id.equals("minecraft:air") || id.isEmpty()) return null;
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
        return (item == Items.AIR) ? null : item;
    }

    private static List<AbstractSpell> parseSpellList(JsonObject json, String key) {
        List<AbstractSpell> list = new ArrayList<>();
        if (!json.has(key)) return list;
        for (JsonElement el : json.getAsJsonArray(key)) {
            AbstractSpell spell = parseSpell(el.getAsString());
            if (spell != null) list.add(spell);
        }
        return list;
    }

    @Nullable
    private static AbstractSpell parseSpell(String id) {
        return SpellRegistry.getSpell(ResourceLocation.parse(id));
    }
    public double getStatOverride(String key, double defaultValue) {
        if (statOverrides == null) return defaultValue;
        return statOverrides.getOrDefault(key, defaultValue);
    }

    public boolean hasStatOverride(String key) {
        return statOverrides != null && statOverrides.containsKey(key);
    }

    public boolean matchesBiome(ServerLevelAccessor level, BlockPos pos) {
        if (allowedBiomes == null) return true;
        Holder<Biome> biome = level.getBiome(pos);
        for (String entry : allowedBiomes) {
            if (entry.startsWith("#")) {
                TagKey<Biome> tag = TagKey.create(Registries.BIOME, ResourceLocation.parse(entry.substring(1)));
                if (biome.is(tag)) return true;
            } else {
                if (biome.is(ResourceLocation.parse(entry))) return true;
            }
        }
        return false;
    }
}