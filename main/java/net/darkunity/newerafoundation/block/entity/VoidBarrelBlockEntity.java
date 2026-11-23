package net.darkunity.newerafoundation.block.entity;

import net.darkunity.newerafoundation.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

/** Void-бочка: принимает всё, но последний удалённый предмет остаётся в 1 слоте (только забрать). */
public class VoidBarrelBlockEntity extends BlockEntity {
    public final SimpleContainer trash = new SimpleContainer(1) {
        @Override public void setChanged() { super.setChanged(); VoidBarrelBlockEntity.this.setChanged(); }
    };

    public VoidBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VOID_BARREL_TYPE.get(), pos, state);
    }

    /** Уничтожает предмет, но запоминает его в слоте (заменяет прошлый). */
    public void voidItem(ItemStack stack) {
        trash.setItem(0, stack.copy());
    }

    public final IItemHandler handler = new IItemHandler() {
        @Override public int getSlots() { return 1; }
        @Override public ItemStack getStackInSlot(int slot) { return ItemStack.EMPTY; } // воронки ничего не вытягивают
        @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!simulate && !stack.isEmpty()) voidItem(stack);
            return ItemStack.EMPTY;
        }
        @Override public ItemStack extractItem(int slot, int amount, boolean simulate) { return ItemStack.EMPTY; }
        @Override public int getSlotLimit(int slot) { return 64; }
        @Override public boolean isItemValid(int slot, ItemStack stack) { return true; }
    };

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Trash", trash.createTag(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        trash.fromTag(tag.getList("Trash", 10), registries);
    }
}
