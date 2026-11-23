package net.darkunity.newerafoundation.block.entity;

import net.darkunity.newerafoundation.inventory.CustomBarrelMenu;
import net.darkunity.newerafoundation.init.ModBlockEntities;
import net.darkunity.newerafoundation.block.CustomBarrelBlock; // Импорт нашего блока для доступа к OPEN
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;

public class CustomBarrelBlockEntity extends BaseContainerBlockEntity {
    private NonNullList<ItemStack> items;
    private final FurnaceTier tier;

    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            // Звук открытия
            level.playSound(null, pos, SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
            // ПЕРЕКЛЮЧЕНИЕ МОДЕЛИ: Устанавливаем OPEN = true
            level.setBlock(pos, state.setValue(CustomBarrelBlock.OPEN, true), 3);
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            // Звук закрытия
            level.playSound(null, pos, SoundEvents.BARREL_CLOSE, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
            // ПЕРЕКЛЮЧЕНИЕ МОДЕЛИ: Устанавливаем OPEN = false
            level.setBlock(pos, state.setValue(CustomBarrelBlock.OPEN, false), 3);
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int count, int openCount) {
            // Оставляем пустым, так как логика переключения уже в onOpen/onClose
        }

        @Override
        protected boolean isOwnContainer(Player player) {
            if (player.containerMenu instanceof CustomBarrelMenu menu) {
                return menu.getContainer() == CustomBarrelBlockEntity.this;
            }
            return false;
        }
    };

    public CustomBarrelBlockEntity(BlockPos pos, BlockState state, FurnaceTier tier) {
        super(ModBlockEntities.BARREL_TYPE.get(), pos, state);
        this.tier = tier;
        this.items = NonNullList.withSize(tier.getSlotCount(), ItemStack.EMPTY);
    }

    @Override
    protected NonNullList<ItemStack> getItems() { return this.items; }

    @Override
    protected void setItems(NonNullList<ItemStack> items) { this.items = items; }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.newerafoundation." + tier.getSerializedName() + "_barrel");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory playerInv) {
        return new CustomBarrelMenu(id, playerInv, this, this.tier);
    }

    @Override
    public int getContainerSize() { return this.tier.getSlotCount(); }

    @Override
    public boolean stillValid(Player player) { return true; }

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

    // Метод для тикера, чтобы проверять закрытие меню
    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public FurnaceTier getTier() { return this.tier; }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        // Важно переинициализировать список под размер тира перед загрузкой предметов
        this.items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
    }
}