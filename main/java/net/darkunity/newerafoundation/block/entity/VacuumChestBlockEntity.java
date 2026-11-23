package net.darkunity.newerafoundation.block.entity;

import net.darkunity.newerafoundation.block.VacuumChestBlock;
import net.darkunity.newerafoundation.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class VacuumChestBlockEntity extends BaseContainerBlockEntity {
    private final int rows;
    private final int radius;
    private NonNullList<ItemStack> items;
    private int counter = 0;

    public VacuumChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.VACUUM_CHEST_TYPE.get(), pos, state);
        if (state.getBlock() instanceof VacuumChestBlock b) {
            this.rows = b.rows;
            this.radius = b.radius;
        } else {
            this.rows = 3;
            this.radius = 5;
        }
        this.items = NonNullList.withSize(rows * 9, ItemStack.EMPTY);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inv) {
        return new ChestMenu(rows == 3 ? MenuType.GENERIC_9x3 : MenuType.GENERIC_9x6, id, inv, this, rows);
    }

    @Override
    protected NonNullList<ItemStack> getItems() { return items; }

    @Override
    protected void setItems(NonNullList<ItemStack> newItems) { this.items = newItems; }

    @Override
    public int getContainerSize() { return rows * 9; }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
    }

    /** Кладёт стак в сундук, возвращает остаток. */
    private ItemStack insert(ItemStack stack) {
        for (int i = 0; i < items.size() && !stack.isEmpty(); i++) {
            ItemStack slot = items.get(i);
            if (!slot.isEmpty() && ItemStack.isSameItemSameComponents(slot, stack)) {
                int space = Math.min(slot.getMaxStackSize(), getMaxStackSize()) - slot.getCount();
                if (space > 0) {
                    int move = Math.min(space, stack.getCount());
                    slot.grow(move);
                    stack.shrink(move);
                }
            }
        }
        for (int i = 0; i < items.size() && !stack.isEmpty(); i++) {
            if (items.get(i).isEmpty()) {
                int move = Math.min(stack.getMaxStackSize(), stack.getCount());
                items.set(i, stack.copyWithCount(move));
                stack.shrink(move);
            }
        }
        setChanged();
        return stack;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VacuumChestBlockEntity be) {
        if (level.isClientSide) return;
        if (++be.counter < 5) return;
        be.counter = 0;
        AABB box = new AABB(pos).inflate(be.radius);
        // tickCount >= 10: не хватаем предмет сразу после броска игроком
        for (ItemEntity ie : level.getEntitiesOfClass(ItemEntity.class, box, e -> e.isAlive() && e.tickCount >= 10)) {
            ItemStack rest = be.insert(ie.getItem().copy());
            if (rest.isEmpty()) ie.discard();
            else ie.setItem(rest);
        }
    }
}
