package net.darkunity.newerafoundation.block;

import com.mojang.serialization.MapCodec;
import net.darkunity.newerafoundation.block.entity.FluidPipeBlockEntity;
import net.darkunity.newerafoundation.block.entity.PipeTier;
import net.darkunity.newerafoundation.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

/** Жидкостная труба. Тир определяет пропускную способность. */
public class FluidPipeBlock extends BaseEntityBlock {
    public static final MapCodec<FluidPipeBlock> CODEC = simpleCodec(p -> new FluidPipeBlock(p, PipeTier.COPPER));

    public static final Map<Direction, BooleanProperty> PROPS = new EnumMap<>(Direction.class);
    static {
        PROPS.put(Direction.NORTH, BlockStateProperties.NORTH);
        PROPS.put(Direction.EAST, BlockStateProperties.EAST);
        PROPS.put(Direction.SOUTH, BlockStateProperties.SOUTH);
        PROPS.put(Direction.WEST, BlockStateProperties.WEST);
        PROPS.put(Direction.UP, BlockStateProperties.UP);
        PROPS.put(Direction.DOWN, BlockStateProperties.DOWN);
    }

    private static final VoxelShape CORE = Block.box(5, 5, 5, 11, 11, 11);
    private static final Map<Direction, VoxelShape> ARMS = new EnumMap<>(Direction.class);
    static {
        ARMS.put(Direction.DOWN, Block.box(5, 0, 5, 11, 5, 11));
        ARMS.put(Direction.UP, Block.box(5, 11, 5, 11, 16, 11));
        ARMS.put(Direction.NORTH, Block.box(5, 5, 0, 11, 11, 5));
        ARMS.put(Direction.SOUTH, Block.box(5, 5, 11, 11, 11, 16));
        ARMS.put(Direction.WEST, Block.box(0, 5, 5, 5, 11, 11));
        ARMS.put(Direction.EAST, Block.box(11, 5, 5, 16, 11, 11));
    }

    private final PipeTier tier;

    public FluidPipeBlock(Properties properties, PipeTier tier) {
        super(properties);
        this.tier = tier;
        BlockState s = this.stateDefinition.any();
        for (BooleanProperty p : PROPS.values()) s = s.setValue(p, false);
        this.registerDefaultState(s);
    }

    public PipeTier getTier() { return tier; }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        for (BooleanProperty p : PROPS.values()) builder.add(p);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        VoxelShape shape = CORE;
        for (Map.Entry<Direction, BooleanProperty> e : PROPS.entrySet()) {
            if (state.getValue(e.getValue())) shape = Shapes.or(shape, ARMS.get(e.getKey()));
        }
        return shape;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return computeState(ctx.getLevel(), ctx.getClickedPos(), this.defaultBlockState());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return state.setValue(PROPS.get(direction), connectsTo(level, pos, direction));
    }

    private static BlockState computeState(LevelAccessor level, BlockPos pos, BlockState state) {
        for (Direction d : Direction.values()) {
            state = state.setValue(PROPS.get(d), connectsTo(level, pos, d));
        }
        return state;
    }

    private static boolean connectsTo(LevelAccessor level, BlockPos pos, Direction dir) {
        BlockPos np = pos.relative(dir);
        BlockState ns = level.getBlockState(np);
        Block b = ns.getBlock();
        if (b instanceof FluidPipeBlock) return true;
        if (b instanceof FluidPumpBlock) {
            Direction f = ns.getValue(FluidPumpBlock.FACING);
            return f == dir || f == dir.getOpposite();
        }
        if (b instanceof LavaFeederBlock || b instanceof CustomTankBlock || b instanceof VoidTankBlock) return true;
        if (level instanceof Level l) {
            return l.getCapability(Capabilities.FluidHandler.BLOCK, np, dir.getOpposite()) != null;
        }
        return false;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FluidPipeBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.PIPE_TYPE.get(), FluidPipeBlockEntity::tick);
    }
}
