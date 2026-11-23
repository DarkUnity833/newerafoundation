package net.darkunity.newerafoundation.block.entity;

import net.darkunity.newerafoundation.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.chat.Component;

public class CustomFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
    private final FurnaceTier tier;

    public CustomFurnaceBlockEntity(BlockPos pos, BlockState state, FurnaceTier tier) {
        super(ModBlockEntities.FURNACE_TYPE.get(), pos, state, RecipeType.SMELTING);
        this.tier = tier;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.newerafoundation." + tier.name().toLowerCase() + "_furnace");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inv) {
        return new FurnaceMenu(id, inv, this, this.dataAccess);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CustomFurnaceBlockEntity be) {
        FurnaceHeat.tick(level, pos, state, be, be.dataAccess, be.tier, RecipeType.SMELTING);
    }
}
