package net.darkunity.newerafoundation.block;

import net.darkunity.newerafoundation.init.ModBlockEntities;
import net.darkunity.newerafoundation.block.entity.CustomHopperBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CustomHopperBlock extends HopperBlock {
    private final int cooldown;
    private final int itemsPerMove;

    public CustomHopperBlock(Properties properties, int cooldown, int itemsPerMove) {
        super(properties);
        this.cooldown = cooldown;
        this.itemsPerMove = itemsPerMove;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CustomHopperBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(type, ModBlockEntities.CUSTOM_HOPPER.get(), 
            (lvl, pos, st, be) -> CustomHopperBlockEntity.pushItemsTick(lvl, pos, st, be, this.cooldown, this.itemsPerMove));
    }
}