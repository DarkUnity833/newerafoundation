package net.darkunity.newerafoundation.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.darkunity.newerafoundation.init.ModItems;
import net.darkunity.newerafoundation.init.ModBlocks;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "newerafoundation");

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = 
            CREATIVE_MODE_TABS.register("foundation_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.newerafoundation.main"))
                    .icon(() -> new ItemStack(ModBlocks.NETHERITE_FURNACE.get()))
                    .displayItems((parameters, output) -> {

                        // Starter
                        output.accept(ModItems.WOOL_BACKPACK.get());
                        output.accept(ModItems.LEATHER_BACKPACK.get());
                        
                        // COPPER
                        output.accept(ModBlocks.COPPER_FURNACE.get());
                        output.accept(ModBlocks.COPPER_SMOKER.get());
                        output.accept(ModBlocks.COPPER_BLAST_FURNACE.get());
                        output.accept(ModBlocks.COPPER_BARREL.get());
                        output.accept(ModBlocks.COPPER_CHEST.get());
                        output.accept(ModBlocks.COPPER_SHULKER.get());
                        output.accept(ModBlocks.COPPER_TANK.get()); // Добавлено
                        output.accept(ModBlocks.COPPER_HOPPER.get());
                        output.accept(ModItems.COPPER_BACKPACK.get());

                        // IRON
                        output.accept(ModBlocks.IRON_FURNACE.get());
                        output.accept(ModBlocks.IRON_SMOKER.get());
                        output.accept(ModBlocks.IRON_BLAST_FURNACE.get());
                        output.accept(ModBlocks.IRON_BARREL.get());
                        output.accept(ModBlocks.IRON_CHEST.get());
                        output.accept(ModBlocks.IRON_SHULKER.get());
                        output.accept(ModBlocks.IRON_TANK.get()); // Добавлено
                        output.accept(ModBlocks.IRON_HOPPER.get());
                        output.accept(ModItems.IRON_BACKPACK.get());

                        // GOLD
                        output.accept(ModBlocks.GOLD_FURNACE.get());
                        output.accept(ModBlocks.GOLD_SMOKER.get());
                        output.accept(ModBlocks.GOLD_BLAST_FURNACE.get());
                        output.accept(ModBlocks.GOLD_BARREL.get());
                        output.accept(ModBlocks.GOLD_CHEST.get());
                        output.accept(ModBlocks.GOLD_SHULKER.get());
                        output.accept(ModBlocks.GOLD_TANK.get()); // Добавлено
                        output.accept(ModBlocks.GOLD_HOPPER.get());
                        output.accept(ModItems.GOLD_BACKPACK.get());

                        // EMERALD
                        output.accept(ModBlocks.EMERALD_FURNACE.get());
                        output.accept(ModBlocks.EMERALD_SMOKER.get());
                        output.accept(ModBlocks.EMERALD_BLAST_FURNACE.get());
                        output.accept(ModBlocks.EMERALD_BARREL.get());
                        output.accept(ModBlocks.EMERALD_CHEST.get());
                        output.accept(ModBlocks.EMERALD_SHULKER.get());
                        output.accept(ModBlocks.EMERALD_TANK.get()); // Добавлено
                        output.accept(ModBlocks.EMERALD_HOPPER.get());
                        output.accept(ModItems.EMERALD_BACKPACK.get());

                        // DIAMOND
                        output.accept(ModBlocks.DIAMOND_FURNACE.get());
                        output.accept(ModBlocks.DIAMOND_SMOKER.get());
                        output.accept(ModBlocks.DIAMOND_BLAST_FURNACE.get());
                        output.accept(ModBlocks.DIAMOND_BARREL.get());
                        output.accept(ModBlocks.DIAMOND_CHEST.get());
                        output.accept(ModBlocks.DIAMOND_SHULKER.get());
                        output.accept(ModBlocks.DIAMOND_TANK.get()); // Добавлено
                        output.accept(ModBlocks.DIAMOND_HOPPER.get());
                        output.accept(ModItems.DIAMOND_BACKPACK.get());

                        // CRYSTAL (AMETHYST)
                        output.accept(ModBlocks.CRYSTAL_FURNACE.get());
                        output.accept(ModBlocks.CRYSTAL_SMOKER.get());
                        output.accept(ModBlocks.CRYSTAL_BLAST_FURNACE.get());
                        output.accept(ModBlocks.CRYSTAL_BARREL.get());
                        output.accept(ModBlocks.CRYSTAL_CHEST.get());
                        output.accept(ModBlocks.CRYSTAL_SHULKER.get());
                        output.accept(ModBlocks.CRYSTAL_TANK.get()); // Добавлено
                        output.accept(ModBlocks.CRYSTAL_HOPPER.get());
                        output.accept(ModItems.CRYSTAL_BACKPACK.get());

                        // OBSIDIAN
                        output.accept(ModBlocks.OBSIDIAN_FURNACE.get());
                        output.accept(ModBlocks.OBSIDIAN_SMOKER.get());
                        output.accept(ModBlocks.OBSIDIAN_BLAST_FURNACE.get());
                        output.accept(ModBlocks.OBSIDIAN_BARREL.get());
                        output.accept(ModBlocks.OBSIDIAN_CHEST.get());
                        output.accept(ModBlocks.OBSIDIAN_SHULKER.get());
                        output.accept(ModBlocks.OBSIDIAN_TANK.get()); // Добавлено
                        output.accept(ModBlocks.OBSIDIAN_HOPPER.get());
                        output.accept(ModItems.OBSIDIAN_BACKPACK.get());

                        // NETHERITE
                        output.accept(ModBlocks.NETHERITE_FURNACE.get());
                        output.accept(ModBlocks.NETHERITE_SMOKER.get());
                        output.accept(ModBlocks.NETHERITE_BLAST_FURNACE.get());
                        output.accept(ModBlocks.NETHERITE_BARREL.get());
                        output.accept(ModBlocks.NETHERITE_CHEST.get());
                        output.accept(ModBlocks.NETHERITE_SHULKER.get());
                        output.accept(ModBlocks.NETHERITE_TANK.get()); // Добавлено
                        output.accept(ModBlocks.NETHERITE_HOPPER.get());
                        output.accept(ModItems.NETHERITE_BACKPACK.get());

                        // MAGMA (Печи и Баки)
                        output.accept(ModBlocks.MAGMA_FURNACE.get());
                        output.accept(ModBlocks.MAGMA_SMOKER.get());
                        output.accept(ModBlocks.MAGMA_BLAST_FURNACE.get());
                        output.accept(ModBlocks.MAGMA_TANK.get()); // Добавлено

                        // STORAGE / FLUIDS
                        output.accept(ModBlocks.VOID_BARREL.get());
                        output.accept(ModBlocks.VOID_TANK.get());
                        output.accept(net.darkunity.newerafoundation.init.ModItems.ENDER_BACKPACK.get());
                        output.accept(ModBlocks.STORAGE_CORE.get());
                        output.accept(ModBlocks.STORAGE_ANALYZER.get());
                        output.accept(ModBlocks.STORAGE_CABLE.get());
                        output.accept(ModBlocks.STORAGE_TERMINAL.get());
                        output.accept(ModBlocks.VACUUM_CHEST.get());
                        output.accept(ModBlocks.DIAMOND_VACUUM_CHEST.get());
                        output.accept(ModBlocks.NETHERITE_VACUUM_CHEST.get());
                        output.accept(ModBlocks.COPPER_PIPE.get());
                        output.accept(ModBlocks.IRON_PIPE.get());
                        output.accept(ModBlocks.GOLD_PIPE.get());
                        output.accept(ModBlocks.DIAMOND_PIPE.get());
                        output.accept(ModBlocks.NETHERITE_PIPE.get());
                        output.accept(ModBlocks.FLUID_PUMP.get());
                        output.accept(ModBlocks.LAVA_FEEDER.get());

                        // UPGRADES
                        output.accept(ModItems.COPPER_UPGRADE.get());
                        output.accept(ModItems.IRON_UPGRADE.get());
                        output.accept(ModItems.GOLD_UPGRADE.get());
                        output.accept(ModItems.DIAMOND_UPGRADE.get());
                        output.accept(ModItems.CRYSTAL_UPGRADE.get());
                        output.accept(ModItems.EMERALD_UPGRADE.get());
                        output.accept(ModItems.OBSIDIAN_UPGRADE.get());
                        output.accept(ModItems.NETHERITE_UPGRADE.get());

                    }).build());
}