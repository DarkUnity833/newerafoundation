package net.darkunity.newerafoundation.block;

import com.mojang.serialization.MapCodec;
import net.darkunity.newerafoundation.storage.StorageComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.EnumMap;
import java.util.Map;

/**
 * Cable for the storage network. Not a full block: its hitbox/collision is a 4x4x4 core plus an arm per connection.
 * Each side is NONE, CABLE (plain tube to another cable) or BLOCK (tube + connector plate to any other block).
 */
public class StorageCableBlock extends Block implements StorageComponent {
    public static final MapCodec<StorageCableBlock> CODEC = simpleCodec(StorageCableBlock::new);

    public enum Connection implements StringRepresentable {
        NONE("none"), CABLE("cable"), BLOCK("block");
        private final String name;
        Connection(String name) { this.name = name; }
        @Override public String getSerializedName() { return name; }
        public boolean connected() { return this != NONE; }
    }

    public static final EnumProperty<Connection> NORTH = EnumProperty.create("north", Connection.class);
    public static final EnumProperty<Connection> EAST = EnumProperty.create("east", Connection.class);
    public static final EnumProperty<Connection> SOUTH = EnumProperty.create("south", Connection.class);
    public static final EnumProperty<Connection> WEST = EnumProperty.create("west", Connection.class);
    public static final EnumProperty<Connection> UP = EnumProperty.create("up", Connection.class);
    public static final EnumProperty<Connection> DOWN = EnumProperty.create("down", Connection.class);

    private static final Map<Direction, EnumProperty<Connection>> PROPS = new EnumMap<>(Direction.class);
    static {
        PROPS.put(Direction.NORTH, NORTH); PROPS.put(Direction.EAST, EAST); PROPS.put(Direction.SOUTH, SOUTH);
        PROPS.put(Direction.WEST, WEST); PROPS.put(Direction.UP, UP); PROPS.put(Direction.DOWN, DOWN);
    }

    private static final VoxelShape CORE = Block.box(6, 6, 6, 10, 10, 10);
    private static final Map<Direction, VoxelShape> ARMS = new EnumMap<>(Direction.class);
    static {
        ARMS.put(Direction.NORTH, Block.box(6, 6, 0, 10, 10, 6));
        ARMS.put(Direction.SOUTH, Block.box(6, 6, 10, 10, 10, 16));
        ARMS.put(Direction.WEST, Block.box(0, 6, 6, 6, 10, 10));
        ARMS.put(Direction.EAST, Block.box(10, 6, 6, 16, 10, 10));
        ARMS.put(Direction.DOWN, Block.box(6, 0, 6, 10, 6, 10));
        ARMS.put(Direction.UP, Block.box(6, 10, 6, 10, 16, 10));
    }
    /** Connector plates flush with the neighbour's face (only for BLOCK connections). */
    private static final Map<Direction, VoxelShape> PLATES = new EnumMap<>(Direction.class);
    static {
        PLATES.put(Direction.NORTH, Block.box(4, 4, 0, 12, 12, 2));
        PLATES.put(Direction.SOUTH, Block.box(4, 4, 14, 12, 12, 16));
        PLATES.put(Direction.WEST, Block.box(0, 4, 4, 2, 12, 12));
        PLATES.put(Direction.EAST, Block.box(14, 4, 4, 16, 12, 12));
        PLATES.put(Direction.DOWN, Block.box(4, 0, 4, 12, 2, 12));
        PLATES.put(Direction.UP, Block.box(4, 14, 4, 12, 16, 12));
    }
    private static final VoxelShape[] SHAPE_CACHE = new VoxelShape[729]; // 3^6 states

    public StorageCableBlock(Properties p) {
        super(p);
        BlockState s = stateDefinition.any();
        for (EnumProperty<Connection> prop : PROPS.values()) s = s.setValue(prop, Connection.NONE);
        registerDefaultState(s);
    }

    @Override protected MapCodec<? extends Block> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) { b.add(NORTH, EAST, SOUTH, WEST, UP, DOWN); }

    private static VoxelShape shapeOf(BlockState state) {
        int key = 0;
        for (Direction d : Direction.values()) key = key * 3 + state.getValue(PROPS.get(d)).ordinal();
        VoxelShape cached = SHAPE_CACHE[key];
        if (cached != null) return cached;
        VoxelShape shape = CORE;
        for (Direction d : Direction.values()) {
            Connection c = state.getValue(PROPS.get(d));
            if (c == Connection.NONE) continue;
            shape = Shapes.or(shape, ARMS.get(d));
            if (c == Connection.BLOCK) shape = Shapes.or(shape, PLATES.get(d));
        }
        return SHAPE_CACHE[key] = shape.optimize();
    }

    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) { return shapeOf(state); }
    @Override protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) { return shapeOf(state); }
    @Override protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) { return Shapes.empty(); }
    @Override protected boolean useShapeForLightOcclusion(BlockState state) { return true; }

    private Connection connection(LevelAccessor level, BlockPos pos, Direction dir) {
        BlockPos np = pos.relative(dir);
        BlockState s = level.getBlockState(np);
        Block b = s.getBlock();
        if (b instanceof StorageCableBlock) return Connection.CABLE;
        if (b instanceof StorageCoreBlock || b instanceof StorageAnalyzerBlock || b instanceof StorageTerminalBlock) return Connection.BLOCK;
        // any other block: only if it actually exposes an inventory (that is what the network uses)
        if (level instanceof Level l) return l.getCapability(Capabilities.ItemHandler.BLOCK, np, dir.getOpposite()) != null ? Connection.BLOCK : Connection.NONE;
        return level.getBlockEntity(np) != null ? Connection.BLOCK : Connection.NONE;
    }

    private BlockState connections(BlockState state, LevelAccessor level, BlockPos pos) {
        for (Direction d : Direction.values()) state = state.setValue(PROPS.get(d), connection(level, pos, d));
        return state;
    }

    @Override public BlockState getStateForPlacement(BlockPlaceContext ctx) { return connections(defaultBlockState(), ctx.getLevel(), ctx.getClickedPos()); }

    @Override
    public BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return state.setValue(PROPS.get(dir), connection(level, pos, dir));
    }
}
