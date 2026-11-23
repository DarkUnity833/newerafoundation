package net.darkunity.newerafoundation.block.entity;

import net.darkunity.newerafoundation.block.FluidPipeBlock;
import net.darkunity.newerafoundation.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

public class FluidPipeBlockEntity extends BlockEntity {
    private final PipeTier tier;
    public final FluidTank tank;
    private int startDir = 0;

    public FluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PIPE_TYPE.get(), pos, state);
        this.tier = state.getBlock() instanceof FluidPipeBlock p ? p.getTier() : PipeTier.COPPER;
        this.tank = new FluidTank(tier.capacity()) {
            @Override
            protected void onContentsChanged() { setChanged(); }
        };
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FluidPipeBlockEntity be) {
        if (level.isClientSide || be.tank.isEmpty()) return;
        int budget = be.tier.throughput;
        Direction[] dirs = Direction.values();
        be.startDir = (be.startDir + 1) % dirs.length;

        // 1. сначала отдаём в приёмники (баки, feeder, void), они важнее выравнивания между трубами
        for (int i = 0; i < dirs.length && budget > 0 && !be.tank.isEmpty(); i++) {
            Direction d = dirs[(be.startDir + i) % dirs.length];
            BlockPos np = pos.relative(d);
            if (level.getBlockEntity(np) instanceof FluidPipeBlockEntity) continue;
            if (level.getBlockState(np).getBlock() instanceof net.darkunity.newerafoundation.block.FluidPumpBlock) continue;
            IFluidHandler h = level.getCapability(Capabilities.FluidHandler.BLOCK, np, d.getOpposite());
            if (h == null) continue;
            FluidStack out = be.tank.drain(budget, IFluidHandler.FluidAction.SIMULATE);
            if (out.isEmpty()) break;
            int filled = h.fill(out, IFluidHandler.FluidAction.EXECUTE);
            if (filled > 0) {
                be.tank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                budget -= filled;
            }
        }

        // 2. выравнивание с соседними трубами (только если жидкость совместима)
        for (int i = 0; i < dirs.length && budget > 0 && !be.tank.isEmpty(); i++) {
            Direction d = dirs[(be.startDir + i) % dirs.length];
            if (!(level.getBlockEntity(pos.relative(d)) instanceof FluidPipeBlockEntity other)) continue;
            int mine = be.tank.getFluidAmount();
            int theirs = other.tank.getFluidAmount();
            if (mine <= theirs) continue;
            int want = Math.min(budget, Math.max(1, (mine - theirs + 1) / 2));
            FluidStack out = be.tank.drain(want, IFluidHandler.FluidAction.SIMULATE);
            int filled = other.tank.fill(out, IFluidHandler.FluidAction.EXECUTE);
            if (filled > 0) {
                be.tank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                budget -= filled;
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("FluidData", tank.writeToNBT(registries, new CompoundTag()));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        tank.readFromNBT(registries, tag.getCompound("FluidData"));
    }
}
