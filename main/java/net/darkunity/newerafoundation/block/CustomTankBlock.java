package net.darkunity.newerafoundation.block;

import com.mojang.serialization.MapCodec;
import net.darkunity.newerafoundation.block.entity.CustomTankBlockEntity;
import net.darkunity.newerafoundation.block.entity.FurnaceTier;
import net.darkunity.newerafoundation.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

public class CustomTankBlock extends BaseEntityBlock {
    // ВАЖНО: CODEC должен использовать правильный тир, если у тебя разные классы для разных металлов, 
    // но обычно это делается через регистрацию. Оставляю медь как дефолт.
    public static final MapCodec<CustomTankBlock> CODEC = simpleCodec(properties -> new CustomTankBlock(properties, FurnaceTier.COPPER));
    
    public static final BooleanProperty UP = BlockStateProperties.UP;

    private final FurnaceTier tier;

    public CustomTankBlock(Properties properties, FurnaceTier tier) {
        super(properties);
        this.tier = tier;
        this.registerDefaultState(this.stateDefinition.any().setValue(UP, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UP);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(UP, context.getLevel().getBlockState(context.getClickedPos().above()).getBlock() instanceof CustomTankBlock);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP) {
            return state.setValue(UP, neighborState.getBlock() instanceof CustomTankBlock);
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public FurnaceTier getTier() {
        return this.tier;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CustomTankBlockEntity tankBE) {
            var fluidHandlerItem = stack.getCapability(Capabilities.FluidHandler.ITEM);
            
            if (fluidHandlerItem != null) {
                IFluidHandler targetHandler;
                
                // Если ведро пустое — забираем воду из САМОГО ВЕРХНЕГО бака в столбе
                if (FluidUtil.getFluidContained(stack).isEmpty()) {
                    targetHandler = tankBE.getFluidHandlerForInteraction();
                } 
                // Если ведро полное — заливаем воду в САМЫЙ НИЖНИЙ свободный бак (начиная от того, куда тыкнули и выше)
                else {
                    targetHandler = tankBE.getFluidHandlerForFilling();
                }

                if (FluidUtil.interactWithFluidHandler(player, hand, targetHandler)) {
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }
        
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CustomTankBlockEntity(pos, state, tier);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.TANK_TYPE.get(), CustomTankBlockEntity::tick);
    }
}