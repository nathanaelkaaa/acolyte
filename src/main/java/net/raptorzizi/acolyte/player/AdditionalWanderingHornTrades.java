package net.raptorzizi.acolyte.player;

import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.ISpellContainer;
import io.redspace.ironsspellbooks.item.InkItem;
import io.redspace.ironsspellbooks.loot.SpellFilter;
import io.redspace.ironsspellbooks.player.AdditionalWanderingTrades;
import io.redspace.ironsspellbooks.registries.ItemRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.raptorzizi.acolyte.registries.ModItemsRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class AdditionalWanderingHornTrades {

    public static class SimpleTrade implements VillagerTrades.ItemListing {
        final BiFunction<Entity, RandomSource, MerchantOffer> getOffer;

        protected SimpleTrade(BiFunction<Entity, RandomSource, MerchantOffer> getOffer) {
            this.getOffer = getOffer;
        }

        public static SimpleTrade of(BiFunction<Entity, RandomSource, MerchantOffer> getOffer) {
            return new SimpleTrade(getOffer);
        }

        public MerchantOffer getOffer(Entity pTrader, RandomSource pRandom) {
            return getOffer.apply(pTrader, pRandom);
        }
    }

    public static class SimpleSell extends SimpleTrade {
        public SimpleSell(int tradeCount, ItemStack sell, int minHorns, int maxHorns) {
            super((trader, random) -> {
                int hornCost = Math.max(1, random.nextIntBetweenInclusive(minHorns, maxHorns));
                return new MerchantOffer(
                        new ItemCost(ModItemsRegistry.DEMON_HORN.get(), hornCost),
                        sell,
                        tradeCount,
                        0,
                        .05f
                );
            });
        }
    }

    public static class RandomInkSellTrade extends SimpleTrade {
        public RandomInkSellTrade() {
            this(false);
        }
        public RandomInkSellTrade(boolean isRare) {
            super((trader, random) -> {
                List<Item> possibleInks = isRare ?
                        List.of(ItemRegistry.INK_EPIC.get(), ItemRegistry.INK_LEGENDARY.get()) :
                        List.of(ItemRegistry.INK_UNCOMMON.get(), ItemRegistry.INK_RARE.get());

                InkItem item = (InkItem) possibleInks.get(random.nextInt(possibleInks.size()));

                int cost = (isRare ? 2 : 0) + item.getRarity().getValue() * 4 + random.nextIntBetweenInclusive(1, 2);
                int maxUses = isRare ? 2 : 3;

                return new MerchantOffer(
                        new ItemCost(ModItemsRegistry.DEMON_HORN.get(), cost),
                        new ItemStack(item),
                        maxUses,
                        5,
                        .05f
                );
            });
        }
    }

    public static class FocusSellTrade extends AdditionalWanderingTrades.SimpleTrade {
        public FocusSellTrade() {
            super((trader, random) -> {
                List<Item> focuses = List.of(
                        ItemRegistry.BLOOD_VIAL.get(),
                        ItemRegistry.LIGHTNING_BOTTLE.get(),
                        ItemRegistry.DIVINE_PEARL.get(),
                        Items.BLAZE_ROD,
                        Items.POISONOUS_POTATO,
                        Items.ENDER_PEARL,
                        ItemRegistry.FROZEN_BONE_SHARD.get()
                );

                Item selectedFocus = focuses.get(random.nextInt(focuses.size()));
                int hornCost = random.nextIntBetweenInclusive(1, 2);
                return new MerchantOffer(
                        new ItemCost(ModItemsRegistry.DEMON_HORN.get(), hornCost),
                        new ItemStack(selectedFocus, 1),
                        12,
                        5,
                        .05f
                );
            });
        }
    }

    public static class RuneSellTrade extends AdditionalWanderingTrades.SimpleTrade {
        public RuneSellTrade() {
            super((trader, random) -> {
                List<Item> focuses = List.of(
                        ItemRegistry.BLANK_RUNE.get(),
                        ItemRegistry.BLOOD_RUNE.get(),
                        ItemRegistry.COOLDOWN_RUNE.get(),
                        ItemRegistry.ENDER_RUNE.get(),
                        ItemRegistry.EVOCATION_RUNE.get(),
                        ItemRegistry.FIRE_RUNE.get(),
                        ItemRegistry.HOLY_RUNE.get(),
                        ItemRegistry.ICE_RUNE.get(),
                        ItemRegistry.LIGHTNING_RUNE.get(),
                        ItemRegistry.MANA_RUNE.get(),
                        ItemRegistry.NATURE_RUNE.get(),
                        ItemRegistry.PROTECTION_RUNE.get()
                );

                Item selectedFocus = focuses.get(random.nextInt(focuses.size()));
                int hornCost = random.nextIntBetweenInclusive(7, 12);
                return new MerchantOffer(
                        new ItemCost(ModItemsRegistry.DEMON_HORN.get(), hornCost),
                        new ItemStack(selectedFocus, 1),
                        3,
                        5,
                        .05f
                );
            });
        }
    }

    public static class MiscSellTrade implements VillagerTrades.ItemListing {
        public static final List<MiscEntry> ENTRIES = List.of(
                new MiscEntry(Items.FIRE_CHARGE, 3, 1, 12),
                new MiscEntry(Items.AMETHYST_SHARD, 4, 1, 16),
                new MiscEntry(Items.RABBIT_FOOT, 1, 1, 4),
                new MiscEntry(Items.PHANTOM_MEMBRANE, 1, 2, 6),
                new MiscEntry(Items.PUFFERFISH, 1, 1, 8),
                new MiscEntry(Items.OMINOUS_BOTTLE, 1, 4, 2),
                new MiscEntry(Items.GOLDEN_APPLE, 1, 5, 3),
                new MiscEntry(Items.SLIME_BALL, 4, 1, 12),
                new MiscEntry(Items.ARMADILLO_SCUTE, 2, 4, 6),
                new MiscEntry(Items.DRAGON_BREATH, 3, 8, 2),
                new MiscEntry(Items.WIND_CHARGE, 7, 2, 10),
                new MiscEntry(Items.FERMENTED_SPIDER_EYE, 2, 1, 8),
                new MiscEntry(Items.SKELETON_SKULL, 1, 6, 1),
                new MiscEntry(Items.EXPERIENCE_BOTTLE, 12, 3, 10),
                new MiscEntry(Items.SOUL_LANTERN, 3, 1, 12),
                new MiscEntry(ItemRegistry.ARCANE_ESSENCE.get(), 5, 1, 12),
                new MiscEntry(ItemRegistry.HOGSKIN.get(), 1, 1, 8),
                new MiscEntry(ItemRegistry.ARCANE_INGOT.get(), 1, 2, 1),
                new MiscEntry(Items.MAGMA_CREAM, 2, 1, 10),
                new MiscEntry(Items.GHAST_TEAR, 1, 3, 4)
        );

        @Override
        public MerchantOffer getOffer(Entity trader, RandomSource random) {
            MiscEntry selected = ENTRIES.get(random.nextInt(ENTRIES.size()));
            return createOffer(selected);
        }

        public static MerchantOffer createOffer(MiscEntry entry) {
            return new MerchantOffer(
                    new ItemCost(ModItemsRegistry.DEMON_HORN.get(), entry.cost),
                    new ItemStack(entry.item, entry.count),
                    entry.maxUses,
                    5,
                    .05f
            );
        }
        public record MiscEntry(Item item, int count, int cost, int maxUses) {}
    }

    public static class ExilirSellTrade extends SimpleTrade {
        public ExilirSellTrade(boolean onlyLesser, boolean onlyGreater) {
            super((trader, random) -> {
                List<Item> lesser = List.of(ItemRegistry.EVASION_ELIXIR.get(), ItemRegistry.OAKSKIN_ELIXIR.get(), ItemRegistry.INVISIBILITY_ELIXIR.get());
                List<Item> greater = List.of(ItemRegistry.GREATER_EVASION_ELIXIR.get(), ItemRegistry.GREATER_OAKSKIN_ELIXIR.get(), ItemRegistry.GREATER_INVISIBILITY_ELIXIR.get(), ItemRegistry.GREATER_HEALING_POTION.get());
                Item item;
                boolean isGreater = onlyGreater || (!onlyLesser && random.nextBoolean());
                item = isGreater ? greater.get(random.nextInt(greater.size())) : lesser.get(random.nextInt(lesser.size()));

                int hornCost = isGreater ? random.nextIntBetweenInclusive(3, 4) : random.nextIntBetweenInclusive(2, 3);

                return new MerchantOffer(
                        new ItemCost(ModItemsRegistry.DEMON_HORN.get(), hornCost),
                        new ItemStack(item),
                        3,
                        1,
                        .05f
                );
            });
        }
    }

    public static class PotionSellTrade extends SimpleTrade {
        public PotionSellTrade(@Nullable Potion potion) {
            super((trader, random) -> {
                var potion1 = potion;
                if (potion1 == null) {
                    var potions = BuiltInRegistries.POTION.stream().filter(p -> !p.getEffects().isEmpty()).toList();
                    if (!potions.isEmpty()) {
                        potion1 = potions.get(random.nextInt(potions.size()));
                    }
                }
                if (potion1 == null) potion1 = Potions.AWKWARD.value();

                int amplifier = 0;
                int duration = 0;
                var effects = potion1.getEffects();
                if (!effects.isEmpty()) {
                    var effect = effects.getFirst();
                    amplifier = effect.getAmplifier();
                    duration = effect.getDuration() / (20 * 60);
                }

                var potionStack = new ItemStack(Items.POTION);
                potionStack.set(DataComponents.POTION_CONTENTS, new PotionContents(BuiltInRegistries.POTION.wrapAsHolder(potion1)));

                int hornCost = random.nextIntBetweenInclusive(3, 4) + amplifier * 2 + duration;

                return new MerchantOffer(
                        new ItemCost(ModItemsRegistry.DEMON_HORN.get(), hornCost),
                        potionStack,
                        3,
                        1,
                        .05f
                );
            });
        }
    }

    public static class RandomCurioTrade extends SimpleTrade {
        public RandomCurioTrade() {
            super((trader, random) -> {
                if (!trader.level().isClientSide) {
                    LootTable loottable = trader.level().getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, IronsSpellbooks.id("magic_items/basic_curios")));
                    var context = new LootParams.Builder((ServerLevel) trader.level()).create(LootContextParamSets.EMPTY);
                    var items = loottable.getRandomItems(context);
                    if (!items.isEmpty()) {
                        ItemStack forSale = items.get(0);
                        var cost = new ItemCost(ModItemsRegistry.DEMON_HORN.get(), random.nextIntBetweenInclusive(18, 23));
                        return new MerchantOffer(cost, forSale, 1, 15, 0.5f);
                    }
                }
                return null;
            });
        }
    }

    public static class WeaponsSellTrade extends SimpleTrade {
        public WeaponsSellTrade() {
            super((trader, random) -> {
                List<MiscEntry> entries = List.of(
                        new MiscEntry(ItemRegistry.MAGEHUNTER.get(), 1, 16, 1),
                        new MiscEntry(ItemRegistry.SPELLBREAKER.get(), 1, 25, 1),
                        new MiscEntry(ItemRegistry.AMETHYST_RAPIER.get(), 1, 23, 1),
                        new MiscEntry(ItemRegistry.ICE_STAFF.get(), 1, 20, 1),
                        new MiscEntry(ItemRegistry.LIGHTNING_ROD_STAFF.get(), 1, 23, 1),
                        new MiscEntry(ItemRegistry.ARTIFICER_STAFF.get(), 1, 18, 1),
                        new MiscEntry(ItemRegistry.GRAYBEARD_STAFF.get(), 1, 15, 1),
                        new MiscEntry(ItemRegistry.ROTTEN_SPELL_BOOK.get(), 1, 22, 1),
                        new MiscEntry(ItemRegistry.GOLD_SPELL_BOOK.get(), 1, 15, 1),
                        new MiscEntry(ItemRegistry.IRON_SPELL_BOOK.get(), 1, 11, 1),
                        new MiscEntry(ItemRegistry.COPPER_SPELL_BOOK.get(), 1, 8, 1)
                );

                MiscEntry selected = entries.get(random.nextInt(entries.size()));

                return new MerchantOffer(
                        new ItemCost(ModItemsRegistry.DEMON_HORN.get(), selected.cost),
                        new ItemStack(selected.item, selected.count),
                        selected.maxUses,
                        10,
                        .05f
                );
            });
        }
        private record MiscEntry(Item item, int count, int cost, int maxUses) {}
    }

    public static class RandomScrollTrade implements VillagerTrades.ItemListing {
        protected final Optional<ItemCost> price2;
        protected final ItemStack forSale;
        protected final int maxTrades;
        protected final int xp;
        protected final float priceMult;
        protected final SpellFilter spellFilter;
        protected float minQuality, maxQuality;

        public RandomScrollTrade(SpellFilter spellFilter) {
            this.spellFilter = spellFilter;
            this.price2 = Optional.empty();
            this.forSale = new ItemStack(ItemRegistry.SCROLL.get());
            this.maxTrades = 1;
            this.xp = 5;
            this.priceMult = .05f;
            this.minQuality = 0f;
            this.maxQuality = 1f;
        }

        public RandomScrollTrade(SpellFilter filter, float minQuality, float maxQuality) {
            this(filter);
            this.minQuality = minQuality;
            this.maxQuality = maxQuality;
        }

        @Nullable
        @Override
        public MerchantOffer getOffer(Entity pTrader, RandomSource random) {
            AbstractSpell spell = spellFilter.getRandomSpell(random);
            if (spell == SpellRegistry.none()) return null;

            int level = random.nextIntBetweenInclusive(1 + (int) (spell.getMaxLevel() * minQuality), (int) ((spell.getMaxLevel() - 1) * maxQuality) + 1);
            ISpellContainer.createScrollContainer(spell, level, forSale);

            int hornCost = spell.getRarity(level).getValue() * 3 + random.nextIntBetweenInclusive(3, 4);

            return new MerchantOffer(new ItemCost(ModItemsRegistry.DEMON_HORN.get(), hornCost), price2, forSale, maxTrades, xp, priceMult);
        }
    }

    public static class OreSellTrade extends SimpleTrade {
        private record OreEntry(Item item, int cost, int maxUses) {}

        public OreSellTrade() {
            super((trader, random) -> {
                List<OreEntry> entries = List.of(
                        new OreEntry(Items.NETHERITE_INGOT, 1, 3),
                        new OreEntry(ItemRegistry.MITHRIL_SCRAP.get(), 1, 5),
                        new OreEntry(ItemRegistry.MITHRIL_INGOT.get(), 3, 3)
                );
                OreEntry selected = entries.get(random.nextInt(entries.size()));
                return new MerchantOffer(
                        new ItemCost(ModItemsRegistry.DARK_HORN.get(), selected.cost),
                        new ItemStack(selected.item),
                        selected.maxUses,
                        10,
                        .05f
                );
            });
        }
    }

    public static class UpgradeOrbSellTrade extends SimpleTrade {
        public UpgradeOrbSellTrade() {
            super((trader, random) -> {
                List<Item> orbs = List.of(
                        ItemRegistry.UPGRADE_ORB.get(),
                        ItemRegistry.FIRE_UPGRADE_ORB.get(),
                        ItemRegistry.ICE_UPGRADE_ORB.get(),
                        ItemRegistry.LIGHTNING_UPGRADE_ORB.get(),
                        ItemRegistry.HOLY_UPGRADE_ORB.get(),
                        ItemRegistry.ENDER_UPGRADE_ORB.get(),
                        ItemRegistry.BLOOD_UPGRADE_ORB.get(),
                        ItemRegistry.EVOCATION_UPGRADE_ORB.get(),
                        ItemRegistry.NATURE_UPGRADE_ORB.get(),
                        ItemRegistry.MANA_UPGRADE_ORB.get(),
                        ItemRegistry.COOLDOWN_UPGRADE_ORB.get(),
                        ItemRegistry.PROTECTION_UPGRADE_ORB.get()
                );
                Item selected = orbs.get(random.nextInt(orbs.size()));
                int cost = random.nextIntBetweenInclusive(3, 4);
                return new MerchantOffer(
                        new ItemCost(ModItemsRegistry.DARK_HORN.get(), cost),
                        new ItemStack(selected),
                        2,
                        10,
                        .05f
                );
            });
        }
    }
}
