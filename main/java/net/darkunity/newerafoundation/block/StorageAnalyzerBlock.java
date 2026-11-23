package net.darkunity.newerafoundation.block;

import com.mojang.serialization.MapCodec;
import net.darkunity.newerafoundation.storage.StorageComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/** Mandatory gateway between the storage core and external inventories. */
public class StorageAnalyzerBlock extends Block implements StorageComponent {
    public static final MapCodec<StorageAnalyzerBlock> CODEC = simpleCodec(StorageAnalyzerBlock::new);
    public StorageAnalyzerBlock(Properties properties) { super(properties); }
    @Override protected MapCodec<? extends Block> codec() { return CODEC; }
}
