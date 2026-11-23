package net.darkunity.newerafoundation.block.entity;

import net.darkunity.newerafoundation.block.TieredShulkerBlock;
import net.darkunity.newerafoundation.init.ModBlockEntities;
import net.darkunity.newerafoundation.inventory.TieredShulkerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class TieredShulkerBlockEntity extends BaseContainerBlockEntity {
    public enum AnimationStatus { CLOSED, OPENING, OPENED, CLOSING }

    /** Lid travels half a block, same as vanilla. */
    private static final float MAX_LID_EXTENSION = 0.5F;
    private static final float ANIMATION_SPEED = 0.1F;

    private NonNullList<ItemStack> items;
    private final FurnaceTier tier;

    private int openCount;
    private AnimationStatus animationStatus = AnimationStatus.CLOSED;
    private float progress;
    private float progressOld;

    private final ContainerOpenersCounter openers = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.SHULKER_BOX_OPEN, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
            level.gameEvent(null, GameEvent.CONTAINER_OPEN, pos);
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    SoundEvents.SHULKER_BOX_CLOSE, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
            level.gameEvent(null, GameEvent.CONTAINER_CLOSE, pos);
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int oldCount, int newCount) {
            level.blockEvent(pos, state.getBlock(), 1, newCount);
        }

        @Override
        protected boolean isOwnContainer(Player player) {
            return player.containerMenu instanceof TieredShulkerMenu menu && menu.getContainer() == TieredShulkerBlockEntity.this;
        }
    };

    public TieredShulkerBlockEntity(BlockPos pos, BlockState state, FurnaceTier tier) {
        super(ModBlockEntities.TIERED_SHULKER_TYPE.get(), pos, state);
        this.tier = tier;
        this.items = NonNullList.withSize(tier.getSlotCount(), ItemStack.EMPTY);
    }

    // ---------------------------------------------------------------- animation

    public static void tick(Level level, BlockPos pos, BlockState state, TieredShulkerBlockEntity box) {
        box.updateAnimation(level, pos, state);
    }

    private void updateAnimation(Level level, BlockPos pos, BlockState state) {
        progressOld = progress;
        switch (animationStatus) {
            case CLOSED -> progress = 0.0F;
            case OPENING -> {
                progress += ANIMATION_SPEED;
                if (progressOld == 0.0F) {
                    doNeighborUpdates(level, pos, state);
                }
                if (progress >= 1.0F) {
                    animationStatus = AnimationStatus.OPENED;
                    progress = 1.0F;
                    doNeighborUpdates(level, pos, state);
                }
                moveCollidedEntities(level, pos, state);
            }
            case OPENED -> progress = 1.0F;
            case CLOSING -> {
                progress -= ANIMATION_SPEED;
                if (progressOld == 1.0F) {
                    doNeighborUpdates(level, pos, state);
                }
                if (progress <= 0.0F) {
                    animationStatus = AnimationStatus.CLOSED;
                    progress = 0.0F;
                    doNeighborUpdates(level, pos, state);
                }
            }
        }
    }

    /** Entities standing on (or inside the way of) the rising lid are carried along, like vanilla. */
    private void moveCollidedEntities(Level level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof TieredShulkerBlock)) {
            return;
        }
        Direction dir = state.getValue(TieredShulkerBlock.FACING);
        float from = MAX_LID_EXTENSION * progressOld;
        float to = MAX_LID_EXTENSION * progress;
        float delta = to - from;
        // slightly thicker than the real slab so that entities resting exactly on the lid are caught
        AABB area = lidSlab(dir, from - 0.01F, to).move(pos);
        List<Entity> entities = level.getEntities(null, area);
        for (Entity entity : entities) {
            if (entity.getPistonPushReaction() != PushReaction.IGNORE) {
                entity.move(MoverType.SHULKER_BOX, new Vec3(
                        (delta + 0.01) * dir.getStepX(),
                        (delta + 0.01) * dir.getStepY(),
                        (delta + 0.01) * dir.getStepZ()));
            }
        }
    }

    /** Slab (local coordinates) in front of the face looking at {@code dir}, between two lid extensions. */
    public static AABB lidSlab(Direction dir, float fromExt, float toExt) {
        double minX = 0, minY = 0, minZ = 0, maxX = 1, maxY = 1, maxZ = 1;
        switch (dir.getAxis()) {
            case X -> {
                minX = dir.getStepX() > 0 ? 1 + fromExt : -toExt;
                maxX = dir.getStepX() > 0 ? 1 + toExt : -fromExt;
            }
            case Y -> {
                minY = dir.getStepY() > 0 ? 1 + fromExt : -toExt;
                maxY = dir.getStepY() > 0 ? 1 + toExt : -fromExt;
            }
            case Z -> {
                minZ = dir.getStepZ() > 0 ? 1 + fromExt : -toExt;
                maxZ = dir.getStepZ() > 0 ? 1 + toExt : -fromExt;
            }
        }
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    /** Local bounding box: the block plus the lid extension. */
    public AABB getBoundingBox(BlockState state) {
        Direction dir = state.getValue(TieredShulkerBlock.FACING);
        double ext = MAX_LID_EXTENSION * progress;
        return new AABB(0, 0, 0, 1, 1, 1).expandTowards(dir.getStepX() * ext, dir.getStepY() * ext, dir.getStepZ() * ext);
    }

    private void doNeighborUpdates(Level level, BlockPos pos, BlockState state) {
        state.updateNeighbourShapes(level, pos, 3);
        level.updateNeighborsAt(pos, state.getBlock());
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            openCount = type;
            if (type == 0) {
                animationStatus = AnimationStatus.CLOSING;
                doNeighborUpdates(level, worldPosition, getBlockState());
            }
            if (type > 0 && animationStatus != AnimationStatus.OPENED && animationStatus != AnimationStatus.OPENING) {
                animationStatus = AnimationStatus.OPENING;
                doNeighborUpdates(level, worldPosition, getBlockState());
            }
            return true;
        }
        return super.triggerEvent(id, type);
    }

    public AnimationStatus getAnimationStatus() {
        return animationStatus;
    }

    public boolean isClosed() {
        return animationStatus == AnimationStatus.CLOSED;
    }

    public float getProgress(float partialTick) {
        return Mth.lerp(partialTick, progressOld, progress);
    }

    // ---------------------------------------------------------------- container

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> value) {
        items = value;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.newerafoundation." + tier.getSerializedName() + "_shulker");
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new TieredShulkerMenu(id, inventory, this, tier);
    }

    @Override
    public int getContainerSize() {
        return tier.getSlotCount();
    }

    /** Shulker boxes can not be put inside shulker boxes (vanilla rule). */
    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        Block block = Block.byItem(stack.getItem());
        return !(block instanceof ShulkerBoxBlock) && !(block instanceof TieredShulkerBlock);
    }

    @Override
    public boolean stillValid(Player player) {
        return !isRemoved() && player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64;
    }

    @Override
    public void startOpen(Player player) {
        if (!remove && !player.isSpectator() && level != null) {
            openers.incrementOpeners(player, level, worldPosition, getBlockState());
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (!remove && !player.isSpectator() && level != null) {
            openers.decrementOpeners(player, level, worldPosition, getBlockState());
        }
    }

    public FurnaceTier getTier() {
        return tier;
    }

    // ---------------------------------------------------------------- data

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
    }

    /** The item stack carries the contents in the vanilla container component (same as vanilla shulker boxes). */
    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput input) {
        super.applyImplicitComponents(input);
        input.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove("Items");
    }
}
