package net.darkunity.newerafoundation.block.entity;

import net.darkunity.newerafoundation.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Persistent anchor for the network. No item stacks are stored in the core itself. */
public class StorageCoreBlockEntity extends BlockEntity {
    public StorageCoreBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.STORAGE_CORE_TYPE.get(), pos, state); }
}
