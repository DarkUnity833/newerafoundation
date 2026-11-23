package net.darkunity.newerafoundation.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.darkunity.newerafoundation.block.entity.FurnaceTier;
import net.darkunity.newerafoundation.block.entity.TieredShulkerBlockEntity;
import net.darkunity.newerafoundation.init.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Tiered shulker box. Behaves like the vanilla one: keeps its contents when broken
 * (loot table copies the container component), can be placed in any of 6 directions,
 * the lid animates and carries entities standing on it.
 */
public class TieredShulkerBlock extends BaseEntityBlock {
    public static final MapCodec<TieredShulkerBlock> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            propertiesCodec(),
            FurnaceTier.CODEC.fieldOf("tier").forGetter(b -> b.tier)
    ).apply(i, TieredShulkerBlock::new));

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public final FurnaceTier tier;

    public TieredShulkerBlock(Properties properties, FurnaceTier tier) {
        super(properties);
        this.tier = tier;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    /** The box is drawn by TieredShulkerRenderer (animated lid), the block model is only used for particles. */
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TieredShulkerBlockEntity(pos, state, tier);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.TIERED_SHULKER_TYPE.get(), TieredShulkerBlockEntity::tick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (player.isSpectator()) {
            return InteractionResult.CONSUME;
        }
        if (level.getBlockEntity(pos) instanceof TieredShulkerBlockEntity box) {
            if (canOpen(state, level, pos, box)) {
                player.openMenu(box, pos);
                player.awardStat(Stats.OPEN_SHULKER_BOX);
                PiglinAi.angerNearbyPiglins(player, true);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    /** Same rule as vanilla: the lid needs free space (half a block) in front of the opening side. */
    private static boolean canOpen(BlockState state, Level level, BlockPos pos, TieredShulkerBlockEntity box) {
        if (box.getAnimationStatus() != TieredShulkerBlockEntity.AnimationStatus.CLOSED) {
            return true;
        }
        var slab = TieredShulkerBlockEntity.lidSlab(state.getValue(FACING), 0.0F, 0.5F).move(pos).deflate(1.0E-6);
        return level.noCollision(slab);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return level.getBlockEntity(pos) instanceof TieredShulkerBlockEntity box
                ? Shapes.create(box.getBoundingBox(state))
                : Shapes.block();
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return level.getBlockEntity(pos) instanceof TieredShulkerBlockEntity box
                ? Shapes.create(box.getBoundingBox(state))
                : Shapes.block();
    }

    /**
     * Survival drops come from the loot table (copy_components: container/custom_name),
     * so the contents also survive explosions, fluids etc. Like vanilla, creative players
     * only drop a non-empty box.
     */
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (level.getBlockEntity(pos) instanceof TieredShulkerBlockEntity box) {
            if (!level.isClientSide && player.isCreative() && !box.isEmpty()) {
                ItemStack stack = new ItemStack(this);
                stack.applyComponents(box.collectComponents());
                ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
                entity.setDefaultPickUpDelay();
                level.addFreshEntity(entity);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
        if (contents == null) {
            return;
        }
        int shown = 0;
        int total = 0;
        for (ItemStack s : contents.nonEmptyItems()) {
            total++;
            if (shown < 5) {
                shown++;
                tooltip.add(Component.translatable("container.shulkerBox.itemCount", s.getHoverName(), s.getCount()));
            }
        }
        if (total - shown > 0) {
            tooltip.add(Component.translatable("container.shulkerBox.more", total - shown).withStyle(ChatFormatting.ITALIC));
        }
    }
}
