package net.darkunity.newerafoundation.init;

import net.darkunity.newerafoundation.block.*;
import net.darkunity.newerafoundation.block.entity.FurnaceTier;
import net.darkunity.newerafoundation.block.entity.PipeTier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("newerafoundation");
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("newerafoundation");

    // --- ПЕЧИ (FURNACES) ---
    public static final DeferredHolder<Block, CustomFurnaceBlock> COPPER_FURNACE = register("copper_furnace", FurnaceTier.COPPER);
    public static final DeferredHolder<Block, CustomFurnaceBlock> IRON_FURNACE = register("iron_furnace", FurnaceTier.IRON);
    public static final DeferredHolder<Block, CustomFurnaceBlock> GOLD_FURNACE = register("gold_furnace", FurnaceTier.GOLD);
    public static final DeferredHolder<Block, CustomFurnaceBlock> EMERALD_FURNACE = register("emerald_furnace", FurnaceTier.EMERALD);
    public static final DeferredHolder<Block, CustomFurnaceBlock> DIAMOND_FURNACE = register("diamond_furnace", FurnaceTier.DIAMOND);
    public static final DeferredHolder<Block, CustomFurnaceBlock> CRYSTAL_FURNACE = register("crystal_furnace", FurnaceTier.CRYSTAL);
    public static final DeferredHolder<Block, CustomFurnaceBlock> OBSIDIAN_FURNACE = register("obsidian_furnace", FurnaceTier.OBSIDIAN);
    public static final DeferredHolder<Block, CustomFurnaceBlock> NETHERITE_FURNACE = register("netherite_furnace", FurnaceTier.NETHERITE);
    public static final DeferredHolder<Block, CustomFurnaceBlock> MAGMA_FURNACE = register("magma_furnace", FurnaceTier.MAGMA);

    // --- КОПТИЛЬНИ (SMOKERS) ---
    public static final DeferredHolder<Block, CustomSmokerBlock> COPPER_SMOKER = registerSmoker("copper_smoker", FurnaceTier.COPPER);
    public static final DeferredHolder<Block, CustomSmokerBlock> IRON_SMOKER = registerSmoker("iron_smoker", FurnaceTier.IRON);
    public static final DeferredHolder<Block, CustomSmokerBlock> GOLD_SMOKER = registerSmoker("gold_smoker", FurnaceTier.GOLD);
    public static final DeferredHolder<Block, CustomSmokerBlock> EMERALD_SMOKER = registerSmoker("emerald_smoker", FurnaceTier.EMERALD);
    public static final DeferredHolder<Block, CustomSmokerBlock> DIAMOND_SMOKER = registerSmoker("diamond_smoker", FurnaceTier.DIAMOND);
    public static final DeferredHolder<Block, CustomSmokerBlock> CRYSTAL_SMOKER = registerSmoker("crystal_smoker", FurnaceTier.CRYSTAL);
    public static final DeferredHolder<Block, CustomSmokerBlock> OBSIDIAN_SMOKER = registerSmoker("obsidian_smoker", FurnaceTier.OBSIDIAN);
    public static final DeferredHolder<Block, CustomSmokerBlock> NETHERITE_SMOKER = registerSmoker("netherite_smoker", FurnaceTier.NETHERITE);
    public static final DeferredHolder<Block, CustomSmokerBlock> MAGMA_SMOKER = registerSmoker("magma_smoker", FurnaceTier.MAGMA);

    // --- ПЛАВИЛЬНИ (BLAST FURNACES) ---
    public static final DeferredHolder<Block, CustomBlastFurnaceBlock> COPPER_BLAST_FURNACE = registerBlast("copper_blast_furnace", FurnaceTier.COPPER);
    public static final DeferredHolder<Block, CustomBlastFurnaceBlock> IRON_BLAST_FURNACE = registerBlast("iron_blast_furnace", FurnaceTier.IRON);
    public static final DeferredHolder<Block, CustomBlastFurnaceBlock> GOLD_BLAST_FURNACE = registerBlast("gold_blast_furnace", FurnaceTier.GOLD);
    public static final DeferredHolder<Block, CustomBlastFurnaceBlock> EMERALD_BLAST_FURNACE = registerBlast("emerald_blast_furnace", FurnaceTier.EMERALD);
    public static final DeferredHolder<Block, CustomBlastFurnaceBlock> DIAMOND_BLAST_FURNACE = registerBlast("diamond_blast_furnace", FurnaceTier.DIAMOND);
    public static final DeferredHolder<Block, CustomBlastFurnaceBlock> CRYSTAL_BLAST_FURNACE = registerBlast("crystal_blast_furnace", FurnaceTier.CRYSTAL);
    public static final DeferredHolder<Block, CustomBlastFurnaceBlock> OBSIDIAN_BLAST_FURNACE = registerBlast("obsidian_blast_furnace", FurnaceTier.OBSIDIAN);
    public static final DeferredHolder<Block, CustomBlastFurnaceBlock> NETHERITE_BLAST_FURNACE = registerBlast("netherite_blast_furnace", FurnaceTier.NETHERITE);
    public static final DeferredHolder<Block, CustomBlastFurnaceBlock> MAGMA_BLAST_FURNACE = registerBlast("magma_blast_furnace", FurnaceTier.MAGMA);

    // --- БОЧКИ (BARRELS) ---
    public static final DeferredHolder<Block, CustomBarrelBlock> COPPER_BARREL = registerBarrel("copper_barrel", FurnaceTier.COPPER);
    public static final DeferredHolder<Block, CustomBarrelBlock> IRON_BARREL = registerBarrel("iron_barrel", FurnaceTier.IRON);
    public static final DeferredHolder<Block, CustomBarrelBlock> GOLD_BARREL = registerBarrel("gold_barrel", FurnaceTier.GOLD);
    public static final DeferredHolder<Block, CustomBarrelBlock> EMERALD_BARREL = registerBarrel("emerald_barrel", FurnaceTier.EMERALD);
    public static final DeferredHolder<Block, CustomBarrelBlock> DIAMOND_BARREL = registerBarrel("diamond_barrel", FurnaceTier.DIAMOND);
    public static final DeferredHolder<Block, CustomBarrelBlock> CRYSTAL_BARREL = registerBarrel("crystal_barrel", FurnaceTier.CRYSTAL);
    public static final DeferredHolder<Block, CustomBarrelBlock> OBSIDIAN_BARREL = registerBarrel("obsidian_barrel", FurnaceTier.OBSIDIAN);
    public static final DeferredHolder<Block, CustomBarrelBlock> NETHERITE_BARREL = registerBarrel("netherite_barrel", FurnaceTier.NETHERITE);

    // --- СУНДУКИ (CHESTS) ---
    public static final DeferredHolder<Block, CustomChestBlock> COPPER_CHEST = registerChest("copper_chest", FurnaceTier.COPPER);
    public static final DeferredHolder<Block, CustomChestBlock> IRON_CHEST = registerChest("iron_chest", FurnaceTier.IRON);
    public static final DeferredHolder<Block, CustomChestBlock> GOLD_CHEST = registerChest("gold_chest", FurnaceTier.GOLD);
    public static final DeferredHolder<Block, CustomChestBlock> EMERALD_CHEST = registerChest("emerald_chest", FurnaceTier.EMERALD);
    public static final DeferredHolder<Block, CustomChestBlock> DIAMOND_CHEST = registerChest("diamond_chest", FurnaceTier.DIAMOND);
    public static final DeferredHolder<Block, CustomChestBlock> CRYSTAL_CHEST = registerChest("crystal_chest", FurnaceTier.CRYSTAL);
    public static final DeferredHolder<Block, CustomChestBlock> OBSIDIAN_CHEST = registerChest("obsidian_chest", FurnaceTier.OBSIDIAN);
    public static final DeferredHolder<Block, CustomChestBlock> NETHERITE_CHEST = registerChest("netherite_chest", FurnaceTier.NETHERITE);

    // --- ТИРНЫЕ ШАЛКЕР-БОКСЫ ---
    public static final DeferredHolder<Block, TieredShulkerBlock> COPPER_SHULKER = registerShulker("copper_shulker", FurnaceTier.COPPER);
    public static final DeferredHolder<Block, TieredShulkerBlock> IRON_SHULKER = registerShulker("iron_shulker", FurnaceTier.IRON);
    public static final DeferredHolder<Block, TieredShulkerBlock> GOLD_SHULKER = registerShulker("gold_shulker", FurnaceTier.GOLD);
    public static final DeferredHolder<Block, TieredShulkerBlock> DIAMOND_SHULKER = registerShulker("diamond_shulker", FurnaceTier.DIAMOND);
    public static final DeferredHolder<Block, TieredShulkerBlock> CRYSTAL_SHULKER = registerShulker("crystal_shulker", FurnaceTier.CRYSTAL);
    public static final DeferredHolder<Block, TieredShulkerBlock> EMERALD_SHULKER = registerShulker("emerald_shulker", FurnaceTier.EMERALD);
    public static final DeferredHolder<Block, TieredShulkerBlock> OBSIDIAN_SHULKER = registerShulker("obsidian_shulker", FurnaceTier.OBSIDIAN);
    public static final DeferredHolder<Block, TieredShulkerBlock> NETHERITE_SHULKER = registerShulker("netherite_shulker", FurnaceTier.NETHERITE);

    // --- ВОРОНКИ (HOPPERS) ---
    public static final DeferredHolder<Block, CustomHopperBlock> COPPER_HOPPER = registerHopper("copper_hopper", 6, 1);
    public static final DeferredHolder<Block, CustomHopperBlock> IRON_HOPPER = registerHopper("iron_hopper", 4, 1);
    public static final DeferredHolder<Block, CustomHopperBlock> GOLD_HOPPER = registerHopper("gold_hopper", 3, 1);
    public static final DeferredHolder<Block, CustomHopperBlock> DIAMOND_HOPPER = registerHopper("diamond_hopper", 2, 1);
    public static final DeferredHolder<Block, CustomHopperBlock> EMERALD_HOPPER = registerHopper("emerald_hopper", 1, 1);
    public static final DeferredHolder<Block, CustomHopperBlock> CRYSTAL_HOPPER = registerHopper("crystal_hopper", 1, 2);
    public static final DeferredHolder<Block, CustomHopperBlock> OBSIDIAN_HOPPER = registerHopper("obsidian_hopper", 1, 3);
    public static final DeferredHolder<Block, CustomHopperBlock> NETHERITE_HOPPER = registerHopper("netherite_hopper", 1, 4);

    // --- БАКИ (TANKS) ---
    public static final DeferredHolder<Block, CustomTankBlock> COPPER_TANK = registerTank("copper_tank", FurnaceTier.COPPER);
    public static final DeferredHolder<Block, CustomTankBlock> IRON_TANK = registerTank("iron_tank", FurnaceTier.IRON);
    public static final DeferredHolder<Block, CustomTankBlock> GOLD_TANK = registerTank("gold_tank", FurnaceTier.GOLD);
    public static final DeferredHolder<Block, CustomTankBlock> EMERALD_TANK = registerTank("emerald_tank", FurnaceTier.EMERALD);
    public static final DeferredHolder<Block, CustomTankBlock> DIAMOND_TANK = registerTank("diamond_tank", FurnaceTier.DIAMOND);
    public static final DeferredHolder<Block, CustomTankBlock> CRYSTAL_TANK = registerTank("crystal_tank", FurnaceTier.CRYSTAL);
    public static final DeferredHolder<Block, CustomTankBlock> OBSIDIAN_TANK = registerTank("obsidian_tank", FurnaceTier.OBSIDIAN);
    public static final DeferredHolder<Block, CustomTankBlock> NETHERITE_TANK = registerTank("netherite_tank", FurnaceTier.NETHERITE);
    public static final DeferredHolder<Block, CustomTankBlock> MAGMA_TANK = registerTank("magma_tank", FurnaceTier.MAGMA);


    // --- НОВЫЕ БЛОКИ: хранилище ---
    public static final DeferredHolder<Block, VoidBarrelBlock> VOID_BARREL = regBlock("void_barrel",
            () -> new VoidBarrelBlock(solid(MapColor.COLOR_BLACK)));
    public static final DeferredHolder<Block, VoidTankBlock> VOID_TANK = regBlock("void_tank",
            () -> new VoidTankBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).mapColor(MapColor.COLOR_PURPLE)
                    .strength(3.5f, 6.0f).sound(SoundType.METAL).requiresCorrectToolForDrops()));
    public static final DeferredHolder<Block, VacuumChestBlock> VACUUM_CHEST = regBlock("vacuum_chest",
            () -> new VacuumChestBlock(solid(MapColor.METAL), 3, 5));
    public static final DeferredHolder<Block, VacuumChestBlock> DIAMOND_VACUUM_CHEST = regBlock("diamond_vacuum_chest",
            () -> new VacuumChestBlock(solid(MapColor.DIAMOND), 6, 9));
    public static final DeferredHolder<Block, VacuumChestBlock> NETHERITE_VACUUM_CHEST = regBlock("netherite_vacuum_chest",
            () -> new VacuumChestBlock(solid(MapColor.COLOR_BLACK), 6, 16));

    // --- СИСТЕМА ХРАНЕНИЯ (аналог Steve's Simple Storage) ---
    public static final DeferredHolder<Block, StorageCoreBlock> STORAGE_CORE = regBlock("storage_core",
            () -> new StorageCoreBlock(solid(MapColor.COLOR_PURPLE)));
    public static final DeferredHolder<Block, StorageAnalyzerBlock> STORAGE_ANALYZER = regBlock("storage_analyzer",
            () -> new StorageAnalyzerBlock(solid(MapColor.COLOR_CYAN).noOcclusion()));
    public static final DeferredHolder<Block, StorageCableBlock> STORAGE_CABLE = regBlock("storage_cable",
            () -> new StorageCableBlock(solid(MapColor.COLOR_GRAY).noOcclusion()));
    public static final DeferredHolder<Block, StorageTerminalBlock> STORAGE_TERMINAL = regBlock("storage_terminal",
            () -> new StorageTerminalBlock(solid(MapColor.COLOR_CYAN).noOcclusion()));

    // --- НОВЫЕ БЛОКИ: жидкости ---
    public static final DeferredHolder<Block, FluidPipeBlock> COPPER_PIPE = registerPipe("copper_pipe", PipeTier.COPPER);
    public static final DeferredHolder<Block, FluidPipeBlock> IRON_PIPE = registerPipe("iron_pipe", PipeTier.IRON);
    public static final DeferredHolder<Block, FluidPipeBlock> GOLD_PIPE = registerPipe("gold_pipe", PipeTier.GOLD);
    public static final DeferredHolder<Block, FluidPipeBlock> DIAMOND_PIPE = registerPipe("diamond_pipe", PipeTier.DIAMOND);
    public static final DeferredHolder<Block, FluidPipeBlock> NETHERITE_PIPE = registerPipe("netherite_pipe", PipeTier.NETHERITE);
    public static final DeferredHolder<Block, FluidPumpBlock> FLUID_PUMP = regBlock("fluid_pump",
            () -> new FluidPumpBlock(solid(MapColor.METAL)));
    public static final DeferredHolder<Block, LavaFeederBlock> LAVA_FEEDER = regBlock("lava_feeder",
            () -> new LavaFeederBlock(solid(MapColor.COLOR_ORANGE)));


    // --- МЕТОДЫ РЕГИСТРАЦИИ ---

    private static DeferredHolder<Block, CustomTankBlock> registerTank(String name, FurnaceTier tier) {
        DeferredHolder<Block, CustomTankBlock> block = BLOCKS.register(name, 
            () -> new CustomTankBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)
                .mapColor(MapColor.METAL)
                .strength(3.0f, 6.0f)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .requiresCorrectToolForDrops(), tier));
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static DeferredHolder<Block, CustomHopperBlock> registerHopper(String name, int cooldown, int itemsPerMove) {
        DeferredHolder<Block, CustomHopperBlock> block = BLOCKS.register(name, 
            () -> new CustomHopperBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.HOPPER)
                .strength(3.0f, 4.8f)
                .requiresCorrectToolForDrops(), cooldown, itemsPerMove));
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static DeferredHolder<Block, TieredShulkerBlock> registerShulker(String name, FurnaceTier tier) {
        DeferredHolder<Block, TieredShulkerBlock> block = BLOCKS.register(name, () -> new TieredShulkerBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.SHULKER_BOX)
                .mapColor(MapColor.METAL)
                .strength(2.5f, 6.0f)
                .noOcclusion()
                .dynamicShape()
                .isSuffocating((s, l, p) -> !(l.getBlockEntity(p) instanceof net.darkunity.newerafoundation.block.entity.TieredShulkerBlockEntity b) || b.isClosed())
                .isViewBlocking((s, l, p) -> !(l.getBlockEntity(p) instanceof net.darkunity.newerafoundation.block.entity.TieredShulkerBlockEntity b) || b.isClosed()), tier));
        // like vanilla shulker boxes: do not stack
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().stacksTo(1)));
        return block;
    }

    private static DeferredHolder<Block, CustomChestBlock> registerChest(String name, FurnaceTier tier) {
        DeferredHolder<Block, CustomChestBlock> block = BLOCKS.register(name, 
            () -> new CustomChestBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST)
                    .mapColor(MapColor.METAL)
                    .strength(2.5f / tier.getSpeed(), 6.0f) 
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops(), 
                    tier));
        
        ITEMS.register(name, () -> new net.darkunity.newerafoundation.item.CustomChestItem(block.get(), new Item.Properties()));
        return block;
    }
    
    private static DeferredHolder<Block, CustomFurnaceBlock> register(String name, FurnaceTier tier) {
        DeferredHolder<Block, CustomFurnaceBlock> block = BLOCKS.register(name, () -> new CustomFurnaceBlock(
                BlockBehaviour.Properties.ofFullCopy(Blocks.FURNACE) 
                        .mapColor(MapColor.STONE)
                        .requiresCorrectToolForDrops()
                        .strength(3.5F / tier.getSpeed(), 3.5F)
                        .lightLevel(s -> s.getValue(CustomFurnaceBlock.LIT) ? 13 : 0), 
                tier));

        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static DeferredHolder<Block, CustomSmokerBlock> registerSmoker(String name, FurnaceTier tier) {
        DeferredHolder<Block, CustomSmokerBlock> block = BLOCKS.register(name, () -> new CustomSmokerBlock(
                BlockBehaviour.Properties.ofFullCopy(Blocks.SMOKER) 
                        .mapColor(MapColor.STONE)
                        .requiresCorrectToolForDrops()
                        .strength(3.5F / tier.getSpeed(), 3.5F)
                        .lightLevel(s -> s.getValue(CustomSmokerBlock.LIT) ? 13 : 0), 
                tier));

        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static DeferredHolder<Block, CustomBlastFurnaceBlock> registerBlast(String name, FurnaceTier tier) {
        DeferredHolder<Block, CustomBlastFurnaceBlock> block = BLOCKS.register(name, () -> new CustomBlastFurnaceBlock(
                BlockBehaviour.Properties.ofFullCopy(Blocks.BLAST_FURNACE) 
                        .mapColor(MapColor.STONE)
                        .requiresCorrectToolForDrops()
                        .strength(3.5F / tier.getSpeed(), 3.5F)
                        .lightLevel(s -> s.getValue(CustomBlastFurnaceBlock.LIT) ? 13 : 0), 
                tier));

        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static DeferredHolder<Block, CustomBarrelBlock> registerBarrel(String name, FurnaceTier tier) {
        DeferredHolder<Block, CustomBarrelBlock> block = BLOCKS.register(name, () -> new CustomBarrelBlock(
                BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL) 
                        .mapColor(MapColor.WOOD)
                        .strength(2.5F / tier.getSpeed(), 2.5F)
                        .requiresCorrectToolForDrops(), 
                tier));

        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static BlockBehaviour.Properties solid(MapColor color) {
        return BlockBehaviour.Properties.of().mapColor(color).strength(3.5f, 6.0f)
                .sound(SoundType.METAL).requiresCorrectToolForDrops();
    }

    private static <B extends Block> DeferredHolder<Block, B> regBlock(String name, java.util.function.Supplier<B> sup) {
        DeferredHolder<Block, B> block = BLOCKS.register(name, sup);
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static DeferredHolder<Block, FluidPipeBlock> registerPipe(String name, net.darkunity.newerafoundation.block.entity.PipeTier tier) {
        return regBlock(name, () -> new FluidPipeBlock(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL).strength(1.5f, 4.0f).sound(SoundType.METAL)
                .noOcclusion().requiresCorrectToolForDrops(), tier));
    }
}
