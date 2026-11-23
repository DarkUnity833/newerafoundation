package net.darkunity.newerafoundation.block;

import net.darkunity.newerafoundation.block.entity.CustomBlastFurnaceBlockEntity;
import net.darkunity.newerafoundation.block.entity.FurnaceTier;
import net.darkunity.newerafoundation.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BlastFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CustomBlastFurnaceBlock extends BlastFurnaceBlock {
    private final FurnaceTier tier;

    public CustomBlastFurnaceBlock(Properties props, FurnaceTier tier) {
        super(props);
        this.tier = tier;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CustomBlastFurnaceBlockEntity(pos, state, tier);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CustomBlastFurnaceBlockEntity blast) player.openMenu(blast);
        return InteractionResult.CONSUME;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // Вызываем тикер только на сервере через встроенный хелпер
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.BLAST_FURNACE_TYPE.get(), 
            (lvl, pos, st, blockEntity) -> {
                if (blockEntity instanceof CustomBlastFurnaceBlockEntity blast) {
                    CustomBlastFurnaceBlockEntity.serverTick(lvl, pos, st, blast);
                }
            });
    }
}