package net.darkunity.newerafoundation.block.entity;

import net.darkunity.newerafoundation.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BlastFurnaceMenu;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CustomBlastFurnaceBlockEntity extends AbstractFurnaceBlockEntity {
    private final FurnaceTier tier;

    public CustomBlastFurnaceBlockEntity(BlockPos pos, BlockState state, FurnaceTier tier) {
        super(ModBlockEntities.BLAST_FURNACE_TYPE.get(), pos, state, RecipeType.BLASTING);
        this.tier = tier;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.newerafoundation." + tier.getSerializedName() + "_blast_furnace");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inv) {
        return new BlastFurnaceMenu(id, inv, this, this.dataAccess);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, CustomBlastFurnaceBlockEntity be) {
        FurnaceHeat.tick(level, pos, state, be, be.dataAccess, be.tier, RecipeType.BLASTING);
    }
}
