package net.darkunity.newerafoundation.block;

import net.darkunity.newerafoundation.block.entity.StorageCoreBlockEntity;
import net.darkunity.newerafoundation.inventory.StorageMenu;
import net.darkunity.newerafoundation.storage.StorageComponent;
import net.darkunity.newerafoundation.storage.StorageNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** Терминал: открывает хранилище сети из любой её точки. */
public class StorageTerminalBlock extends HorizontalDirectionalBlock implements StorageComponent {
    public static final com.mojang.serialization.MapCodec<StorageTerminalBlock> CODEC = simpleCodec(StorageTerminalBlock::new);
    public StorageTerminalBlock(Properties p) { super(p); registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH)); }
    @Override protected com.mojang.serialization.MapCodec<? extends HorizontalDirectionalBlock> codec() { return CODEC; }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) { builder.add(FACING); }
    @Override public BlockState getStateForPlacement(BlockPlaceContext context) { return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()); }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        StorageCoreBlockEntity core = StorageNetwork.findCore(level, pos);
        if (core == null) {
            player.displayClientMessage(Component.translatable("message.newerafoundation.no_core"), true);
            return InteractionResult.CONSUME;
        }
        if (!StorageNetwork.hasAnalyzer(level, core.getBlockPos())) {
            player.displayClientMessage(Component.translatable("message.newerafoundation.no_analyzer"), true);
            return InteractionResult.CONSUME;
        }
        BlockPos corePos = core.getBlockPos();
        player.openMenu(new SimpleMenuProvider((id, inv, p) -> new StorageMenu(id, inv, core),
                Component.translatable("block.newerafoundation.storage_terminal")), buf -> buf.writeBlockPos(corePos));
        return InteractionResult.CONSUME;
    }
}
