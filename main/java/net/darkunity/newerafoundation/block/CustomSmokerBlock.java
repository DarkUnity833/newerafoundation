package net.darkunity.newerafoundation.block;

import net.darkunity.newerafoundation.block.entity.CustomSmokerBlockEntity;
import net.darkunity.newerafoundation.block.entity.FurnaceTier;
import net.darkunity.newerafoundation.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SmokerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class CustomSmokerBlock extends SmokerBlock {
    private final FurnaceTier tier;

    public CustomSmokerBlock(Properties props, FurnaceTier tier) {
        super(props);
        this.tier = tier;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CustomSmokerBlockEntity(pos, state, tier);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof CustomSmokerBlockEntity smoker) player.openMenu(smoker);
        return InteractionResult.CONSUME;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (type == ModBlockEntities.SMOKER_TYPE.get()) {
            return level.isClientSide ? null : (lvl, p, st, be) -> {
                if (be instanceof CustomSmokerBlockEntity smoker) {
                    CustomSmokerBlockEntity.serverTick(lvl, p, st, smoker);
                }
            };
        }
        return null;
    }
}