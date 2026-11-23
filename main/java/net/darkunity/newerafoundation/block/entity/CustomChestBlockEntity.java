package net.darkunity.newerafoundation.block.entity;

import net.darkunity.newerafoundation.inventory.CustomChestMenu;
import net.darkunity.newerafoundation.init.ModBlockEntities;
import net.darkunity.newerafoundation.block.CustomChestBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;

public class CustomChestBlockEntity extends ChestBlockEntity {

    private NonNullList<ItemStack> items;
    private FurnaceTier tier; 

    // Добавляем свой счетчик, так как родительский может быть недоступен напрямую
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            CustomChestBlockEntity.this.playSound(state, net.minecraft.sounds.SoundEvents.CHEST_OPEN);
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            CustomChestBlockEntity.this.playSound(state, net.minecraft.sounds.SoundEvents.CHEST_CLOSE);
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int count, int openCount) {
            CustomChestBlockEntity.this.signalOpenCount(level, pos, state, count, openCount);
        }

        @Override
        protected boolean isOwnContainer(Player player) {
            if (!(player.containerMenu instanceof CustomChestMenu menu)) {
                return false;
            } else {
                return menu.getContainer() == CustomChestBlockEntity.this;
            }
        }
    };

    public CustomChestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHEST_TYPE.get(), pos, state);
        
        if (state.getBlock() instanceof CustomChestBlock customBlock) {
            this.tier = customBlock.tier; 
        } else {
            this.tier = FurnaceTier.IRON; 
        }
        
        this.items = NonNullList.withSize(this.tier.getSlotCount(), ItemStack.EMPTY);
    }

    public CustomChestBlockEntity(BlockPos pos, BlockState state, FurnaceTier tier) {
        super(ModBlockEntities.CHEST_TYPE.get(), pos, state);
        this.tier = tier;
        this.items = NonNullList.withSize(tier.getSlotCount(), ItemStack.EMPTY);
    }

    // ---------- OPEN/CLOSE LOGIC (FIXED) ----------

    @Override
    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    @Override
    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    // ---------- INVENTORY ----------

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.newerafoundation." + (tier != null ? tier.getSerializedName() : "iron") + "_chest");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inv) {
        return new CustomChestMenu(id, inv, this, this.tier);
    }

    public FurnaceTier getTier() {
        return this.tier;
    }

    @Override
    public int getContainerSize() {
        return this.tier != null ? this.tier.getSlotCount() : 27;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        if (this.items != null) this.items.clear();
    }

    // ---------- TICK ----------

    public static void tick(Level level, BlockPos pos, BlockState state, CustomChestBlockEntity chest) {
        // Мы используем openersCounter для получения количества игроков, чтобы анимация знала, что делать
        chest.openersCounter.recheckOpeners(level, pos, state);
        ChestBlockEntity.lidAnimateTick(level, pos, state, chest);
    }

    // ---------- NBT ----------

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider reg) {
        super.loadAdditional(tag, reg);
        this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items, reg);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider reg) {
        super.saveAdditional(tag, reg);
        ContainerHelper.saveAllItems(tag, this.items, reg);
    }

    // Вспомогательный метод для звука
    private void playSound(BlockState state, net.minecraft.sounds.SoundEvent sound) {
        double d0 = (double)this.worldPosition.getX() + 0.5D;
        double d1 = (double)this.worldPosition.getY() + 0.5D;
        double d2 = (double)this.worldPosition.getZ() + 0.5D;
        this.level.playSound((Player)null, d0, d1, d2, sound, net.minecraft.sounds.SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat() * 0.1F + 0.9F);
    }
}