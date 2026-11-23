package net.darkunity.newerafoundation.block;

import com.mojang.serialization.MapCodec;
import net.darkunity.newerafoundation.block.entity.StorageCoreBlockEntity;
import net.darkunity.newerafoundation.storage.StorageComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/** Controller/brain of a storage network. Actual items remain inside connected inventories. */
public class StorageCoreBlock extends BaseEntityBlock implements StorageComponent {
    public static final MapCodec<StorageCoreBlock> CODEC = simpleCodec(StorageCoreBlock::new);
    public StorageCoreBlock(Properties p) { super(p); }
    @Override protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Nullable @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new StorageCoreBlockEntity(pos, state); }
}
