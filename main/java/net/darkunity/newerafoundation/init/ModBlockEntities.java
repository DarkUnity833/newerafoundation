package net.darkunity.newerafoundation.init;

import net.darkunity.newerafoundation.block.CustomFurnaceBlock;
import net.darkunity.newerafoundation.block.CustomSmokerBlock;
import net.darkunity.newerafoundation.block.CustomBlastFurnaceBlock;
import net.darkunity.newerafoundation.block.CustomTankBlock;
import net.darkunity.newerafoundation.block.entity.CustomFurnaceBlockEntity;
import net.darkunity.newerafoundation.block.entity.CustomSmokerBlockEntity;
import net.darkunity.newerafoundation.block.entity.CustomBlastFurnaceBlockEntity;
import net.darkunity.newerafoundation.block.entity.CustomBarrelBlockEntity;
import net.darkunity.newerafoundation.block.entity.CustomChestBlockEntity;
import net.darkunity.newerafoundation.block.entity.CustomHopperBlockEntity;
import net.darkunity.newerafoundation.block.entity.CustomTankBlockEntity;
import net.darkunity.newerafoundation.block.entity.FurnaceTier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "newerafoundation");

    // ПЕЧИ
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CustomFurnaceBlockEntity>> FURNACE_TYPE = 
            BLOCK_ENTITIES.register("custom_furnace", () -> 
                    BlockEntityType.Builder.of(
                            (pos, state) -> new CustomFurnaceBlockEntity(pos, state, getTierFromBlock(state.getBlock())),
                            ModBlocks.COPPER_FURNACE.get(), ModBlocks.IRON_FURNACE.get(),
                            ModBlocks.GOLD_FURNACE.get(), ModBlocks.EMERALD_FURNACE.get(),
                            ModBlocks.DIAMOND_FURNACE.get(), ModBlocks.CRYSTAL_FURNACE.get(),
                            ModBlocks.OBSIDIAN_FURNACE.get(), ModBlocks.NETHERITE_FURNACE.get(),
                            ModBlocks.MAGMA_FURNACE.get()
                    ).build(null)
            );

    // КОПТИЛЬНИ
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CustomSmokerBlockEntity>> SMOKER_TYPE = 
            BLOCK_ENTITIES.register("custom_smoker", () -> 
                    BlockEntityType.Builder.of(
                            (pos, state) -> new CustomSmokerBlockEntity(pos, state, getTierFromBlock(state.getBlock())),
                            ModBlocks.COPPER_SMOKER.get(), ModBlocks.IRON_SMOKER.get(),
                            ModBlocks.GOLD_SMOKER.get(), ModBlocks.EMERALD_SMOKER.get(),
                            ModBlocks.DIAMOND_SMOKER.get(), ModBlocks.CRYSTAL_SMOKER.get(),
                            ModBlocks.OBSIDIAN_SMOKER.get(), ModBlocks.NETHERITE_SMOKER.get(),
                            ModBlocks.MAGMA_SMOKER.get()
                    ).build(null)
            );

    // ПЛАВИЛЬНИ
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CustomBlastFurnaceBlockEntity>> BLAST_FURNACE_TYPE = 
            BLOCK_ENTITIES.register("custom_blast_furnace", () -> 
                    BlockEntityType.Builder.of(
                            (pos, state) -> new CustomBlastFurnaceBlockEntity(pos, state, getTierFromBlock(state.getBlock())),
                            ModBlocks.COPPER_BLAST_FURNACE.get(), ModBlocks.IRON_BLAST_FURNACE.get(),
                            ModBlocks.GOLD_BLAST_FURNACE.get(), 
                            ModBlocks.EMERALD_BLAST_FURNACE.get(),
                            ModBlocks.DIAMOND_BLAST_FURNACE.get(),
                            ModBlocks.CRYSTAL_BLAST_FURNACE.get(),
                            ModBlocks.OBSIDIAN_BLAST_FURNACE.get(), 
                            ModBlocks.NETHERITE_BLAST_FURNACE.get(),
                            ModBlocks.MAGMA_BLAST_FURNACE.get()
                    ).build(null)
            );

    // ТИП БОЧЕК
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CustomBarrelBlockEntity>> BARREL_TYPE = 
            BLOCK_ENTITIES.register("custom_barrel", () -> 
                    BlockEntityType.Builder.of(
                            (pos, state) -> new CustomBarrelBlockEntity(pos, state, getTierFromBlock(state.getBlock())),
                            ModBlocks.COPPER_BARREL.get(), ModBlocks.IRON_BARREL.get(),
                            ModBlocks.GOLD_BARREL.get(), ModBlocks.EMERALD_BARREL.get(),
                            ModBlocks.DIAMOND_BARREL.get(), ModBlocks.CRYSTAL_BARREL.get(),
                            ModBlocks.OBSIDIAN_BARREL.get(), ModBlocks.NETHERITE_BARREL.get()
                    ).build(null)
            );

    // ТИП СУНДУКОВ
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CustomChestBlockEntity>> CHEST_TYPE = 
            BLOCK_ENTITIES.register("custom_chest", () -> 
                    BlockEntityType.Builder.of(
                            (pos, state) -> new CustomChestBlockEntity(pos, state, getTierFromBlock(state.getBlock())),
                            ModBlocks.COPPER_CHEST.get(), ModBlocks.IRON_CHEST.get(),
                            ModBlocks.GOLD_CHEST.get(), ModBlocks.EMERALD_CHEST.get(),
                            ModBlocks.DIAMOND_CHEST.get(), ModBlocks.CRYSTAL_CHEST.get(),
                            ModBlocks.OBSIDIAN_CHEST.get(), ModBlocks.NETHERITE_CHEST.get()
                    ).build(null)
            );

    // ТИП ВОРОНОК (ОБНОВЛЕНО ДО 8 ТИРОВ)
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CustomHopperBlockEntity>> CUSTOM_HOPPER = 
            BLOCK_ENTITIES.register("custom_hopper", () -> 
                    BlockEntityType.Builder.of(
                            CustomHopperBlockEntity::new,
                            ModBlocks.COPPER_HOPPER.get(), 
                            ModBlocks.IRON_HOPPER.get(),
                            ModBlocks.GOLD_HOPPER.get(), 
                            ModBlocks.DIAMOND_HOPPER.get(),
                            ModBlocks.EMERALD_HOPPER.get(),
                            ModBlocks.CRYSTAL_HOPPER.get(),
                            ModBlocks.OBSIDIAN_HOPPER.get(),
                            ModBlocks.NETHERITE_HOPPER.get()
                    ).build(null)
            );

    // ТИП БАКОВ (TANKS)
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CustomTankBlockEntity>> TANK_TYPE = 
            BLOCK_ENTITIES.register("custom_tank", () -> 
                    BlockEntityType.Builder.of(
                            (pos, state) -> new CustomTankBlockEntity(pos, state, getTierFromBlock(state.getBlock())),
                            ModBlocks.COPPER_TANK.get(), ModBlocks.IRON_TANK.get(),
                            ModBlocks.GOLD_TANK.get(), ModBlocks.EMERALD_TANK.get(),
                            ModBlocks.DIAMOND_TANK.get(), ModBlocks.CRYSTAL_TANK.get(),
                            ModBlocks.OBSIDIAN_TANK.get(), ModBlocks.NETHERITE_TANK.get(),
                            ModBlocks.MAGMA_TANK.get()
                    ).build(null)
            );

    // --- НОВЫЕ ТИПЫ ---
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<net.darkunity.newerafoundation.block.entity.VacuumChestBlockEntity>> VACUUM_CHEST_TYPE =
            BLOCK_ENTITIES.register("vacuum_chest", () ->
                    BlockEntityType.Builder.of(net.darkunity.newerafoundation.block.entity.VacuumChestBlockEntity::new,
                            ModBlocks.VACUUM_CHEST.get(), ModBlocks.DIAMOND_VACUUM_CHEST.get(), ModBlocks.NETHERITE_VACUUM_CHEST.get()
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<net.darkunity.newerafoundation.block.entity.VoidBarrelBlockEntity>> VOID_BARREL_TYPE =
            BLOCK_ENTITIES.register("void_barrel", () ->
                    BlockEntityType.Builder.of(net.darkunity.newerafoundation.block.entity.VoidBarrelBlockEntity::new,
                            ModBlocks.VOID_BARREL.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<net.darkunity.newerafoundation.block.entity.StorageCoreBlockEntity>> STORAGE_CORE_TYPE =
            BLOCK_ENTITIES.register("storage_core", () ->
                    BlockEntityType.Builder.of(net.darkunity.newerafoundation.block.entity.StorageCoreBlockEntity::new,
                            ModBlocks.STORAGE_CORE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<net.darkunity.newerafoundation.block.entity.FluidPipeBlockEntity>> PIPE_TYPE =
            BLOCK_ENTITIES.register("fluid_pipe", () ->
                    BlockEntityType.Builder.of(net.darkunity.newerafoundation.block.entity.FluidPipeBlockEntity::new,
                            ModBlocks.COPPER_PIPE.get(), ModBlocks.IRON_PIPE.get(), ModBlocks.GOLD_PIPE.get(),
                            ModBlocks.DIAMOND_PIPE.get(), ModBlocks.NETHERITE_PIPE.get()
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<net.darkunity.newerafoundation.block.entity.FluidPumpBlockEntity>> PUMP_TYPE =
            BLOCK_ENTITIES.register("fluid_pump", () ->
                    BlockEntityType.Builder.of(net.darkunity.newerafoundation.block.entity.FluidPumpBlockEntity::new,
                            ModBlocks.FLUID_PUMP.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<net.darkunity.newerafoundation.block.entity.LavaFeederBlockEntity>> LAVA_FEEDER_TYPE =
            BLOCK_ENTITIES.register("lava_feeder", () ->
                    BlockEntityType.Builder.of(net.darkunity.newerafoundation.block.entity.LavaFeederBlockEntity::new,
                            ModBlocks.LAVA_FEEDER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<net.darkunity.newerafoundation.block.entity.TieredShulkerBlockEntity>> TIERED_SHULKER_TYPE =
            BLOCK_ENTITIES.register("tiered_shulker", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new net.darkunity.newerafoundation.block.entity.TieredShulkerBlockEntity(pos, state, ((net.darkunity.newerafoundation.block.TieredShulkerBlock) state.getBlock()).tier),
                    ModBlocks.COPPER_SHULKER.get(), ModBlocks.IRON_SHULKER.get(), ModBlocks.GOLD_SHULKER.get(), ModBlocks.DIAMOND_SHULKER.get(),
                    ModBlocks.CRYSTAL_SHULKER.get(), ModBlocks.EMERALD_SHULKER.get(), ModBlocks.OBSIDIAN_SHULKER.get(), ModBlocks.NETHERITE_SHULKER.get()).build(null));

    /**
     * Универсальный метод для определения тира.
     */
    private static FurnaceTier getTierFromBlock(Block block) {
        if (isTier(block, ModBlocks.COPPER_FURNACE, ModBlocks.COPPER_SMOKER, ModBlocks.COPPER_BLAST_FURNACE, ModBlocks.COPPER_BARREL, ModBlocks.COPPER_CHEST, ModBlocks.COPPER_TANK, ModBlocks.COPPER_SHULKER)) return FurnaceTier.COPPER;
        if (isTier(block, ModBlocks.IRON_FURNACE, ModBlocks.IRON_SMOKER, ModBlocks.IRON_BLAST_FURNACE, ModBlocks.IRON_BARREL, ModBlocks.IRON_CHEST, ModBlocks.IRON_TANK, ModBlocks.IRON_SHULKER)) return FurnaceTier.IRON;
        if (isTier(block, ModBlocks.GOLD_FURNACE, ModBlocks.GOLD_SMOKER, ModBlocks.GOLD_BLAST_FURNACE, ModBlocks.GOLD_BARREL, ModBlocks.GOLD_CHEST, ModBlocks.GOLD_TANK, ModBlocks.GOLD_SHULKER)) return FurnaceTier.GOLD;
        if (isTier(block, ModBlocks.EMERALD_FURNACE, ModBlocks.EMERALD_SMOKER, ModBlocks.EMERALD_BLAST_FURNACE, ModBlocks.EMERALD_BARREL, ModBlocks.EMERALD_CHEST, ModBlocks.EMERALD_TANK, ModBlocks.EMERALD_SHULKER)) return FurnaceTier.EMERALD;
        if (isTier(block, ModBlocks.DIAMOND_FURNACE, ModBlocks.DIAMOND_SMOKER, ModBlocks.DIAMOND_BLAST_FURNACE, ModBlocks.DIAMOND_BARREL, ModBlocks.DIAMOND_CHEST, ModBlocks.DIAMOND_TANK, ModBlocks.DIAMOND_SHULKER)) return FurnaceTier.DIAMOND;
        if (isTier(block, ModBlocks.CRYSTAL_FURNACE, ModBlocks.CRYSTAL_SMOKER, ModBlocks.CRYSTAL_BLAST_FURNACE, ModBlocks.CRYSTAL_BARREL, ModBlocks.CRYSTAL_CHEST, ModBlocks.CRYSTAL_TANK, ModBlocks.CRYSTAL_SHULKER)) return FurnaceTier.CRYSTAL;
        if (isTier(block, ModBlocks.OBSIDIAN_FURNACE, ModBlocks.OBSIDIAN_SMOKER, ModBlocks.OBSIDIAN_BLAST_FURNACE, ModBlocks.OBSIDIAN_BARREL, ModBlocks.OBSIDIAN_CHEST, ModBlocks.OBSIDIAN_TANK, ModBlocks.OBSIDIAN_SHULKER)) return FurnaceTier.OBSIDIAN;
        if (isTier(block, ModBlocks.NETHERITE_FURNACE, ModBlocks.NETHERITE_SMOKER, ModBlocks.NETHERITE_BLAST_FURNACE, ModBlocks.NETHERITE_BARREL, ModBlocks.NETHERITE_CHEST, ModBlocks.NETHERITE_TANK, ModBlocks.NETHERITE_SHULKER)) return FurnaceTier.NETHERITE;
        if (isTier(block, ModBlocks.MAGMA_FURNACE, ModBlocks.MAGMA_SMOKER, ModBlocks.MAGMA_BLAST_FURNACE, ModBlocks.MAGMA_TANK)) return FurnaceTier.MAGMA;
        return FurnaceTier.COPPER;
    }

    @SafeVarargs
    private static boolean isTier(Block block, DeferredHolder<Block, ?>... holders) {
        for (DeferredHolder<Block, ?> holder : holders) {
            if (block == holder.get()) return true;
        }
        return false;
    }
}