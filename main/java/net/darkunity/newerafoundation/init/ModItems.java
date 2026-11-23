package net.darkunity.newerafoundation.init;

import net.darkunity.newerafoundation.item.BackpackItem;
import net.darkunity.newerafoundation.item.UpgradeItem;
import net.darkunity.newerafoundation.block.entity.FurnaceTier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = 
            DeferredRegister.create(Registries.ITEM, "newerafoundation");

    // РЮКЗАКИ
    public static final DeferredHolder<Item, Item> ENDER_BACKPACK = ITEMS.register("ender_backpack", () -> new net.darkunity.newerafoundation.item.EnderBackpackItem(new Item.Properties()));
    public static final DeferredHolder<Item, Item> WOOL_BACKPACK = ITEMS.register("wool_backpack", () -> new BackpackItem(new Item.Properties(), FurnaceTier.WOOL));
    public static final DeferredHolder<Item, Item> LEATHER_BACKPACK = ITEMS.register("leather_backpack", () -> new BackpackItem(new Item.Properties(), FurnaceTier.LEATHER));
    public static final DeferredHolder<Item, Item> COPPER_BACKPACK = ITEMS.register("copper_backpack", () -> new BackpackItem(new Item.Properties(), FurnaceTier.COPPER));
    public static final DeferredHolder<Item, Item> IRON_BACKPACK = ITEMS.register("iron_backpack", () -> new BackpackItem(new Item.Properties(), FurnaceTier.IRON));
    public static final DeferredHolder<Item, Item> GOLD_BACKPACK = ITEMS.register("gold_backpack", () -> new BackpackItem(new Item.Properties(), FurnaceTier.GOLD));
    public static final DeferredHolder<Item, Item> EMERALD_BACKPACK = ITEMS.register("emerald_backpack", () -> new BackpackItem(new Item.Properties(), FurnaceTier.EMERALD));
    public static final DeferredHolder<Item, Item> DIAMOND_BACKPACK = ITEMS.register("diamond_backpack", () -> new BackpackItem(new Item.Properties(), FurnaceTier.DIAMOND));
    public static final DeferredHolder<Item, Item> CRYSTAL_BACKPACK = ITEMS.register("crystal_backpack", () -> new BackpackItem(new Item.Properties(), FurnaceTier.CRYSTAL));
    public static final DeferredHolder<Item, Item> OBSIDIAN_BACKPACK = ITEMS.register("obsidian_backpack", () -> new BackpackItem(new Item.Properties(), FurnaceTier.OBSIDIAN));
    public static final DeferredHolder<Item, Item> NETHERITE_BACKPACK = ITEMS.register("netherite_backpack", () -> new BackpackItem(new Item.Properties(), FurnaceTier.NETHERITE));

   // УНИВЕРСАЛЬНЫЕ АПГРЕЙДЫ
    public static final DeferredHolder<Item, Item> COPPER_UPGRADE = ITEMS.register("copper_upgrade",
            () -> new UpgradeItem(new Item.Properties(), "copper"));

    public static final DeferredHolder<Item, Item> IRON_UPGRADE = ITEMS.register("iron_upgrade",
            () -> new UpgradeItem(new Item.Properties(), "iron"));

    public static final DeferredHolder<Item, Item> GOLD_UPGRADE = ITEMS.register("gold_upgrade",
            () -> new UpgradeItem(new Item.Properties(), "gold"));

    public static final DeferredHolder<Item, Item> DIAMOND_UPGRADE = ITEMS.register("diamond_upgrade",
            () -> new UpgradeItem(new Item.Properties(), "diamond"));

    public static final DeferredHolder<Item, Item> CRYSTAL_UPGRADE = ITEMS.register("crystal_upgrade",
            () -> new UpgradeItem(new Item.Properties(), "crystal"));

    public static final DeferredHolder<Item, Item> EMERALD_UPGRADE = ITEMS.register("emerald_upgrade",
            () -> new UpgradeItem(new Item.Properties(), "emerald"));

    public static final DeferredHolder<Item, Item> OBSIDIAN_UPGRADE = ITEMS.register("obsidian_upgrade",
            () -> new UpgradeItem(new Item.Properties(), "obsidian"));

    public static final DeferredHolder<Item, Item> NETHERITE_UPGRADE = ITEMS.register("netherite_upgrade",
            () -> new UpgradeItem(new Item.Properties(), "netherite"));
}