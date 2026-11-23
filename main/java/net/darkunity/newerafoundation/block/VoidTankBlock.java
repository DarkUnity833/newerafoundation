package net.darkunity.newerafoundation.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;

/** Бак-void: принимает любую жидкость и уничтожает её (через capability FluidHandler). */
public class VoidTankBlock extends Block {
    public static final MapCodec<VoidTankBlock> CODEC = simpleCodec(VoidTankBlock::new);

    public VoidTankBlock(Properties properties) { super(properties); }

    @Override
    protected MapCodec<? extends Block> codec() { return CODEC; }
}
