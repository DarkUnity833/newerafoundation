package net.darkunity.newerafoundation.block.entity;

import net.darkunity.newerafoundation.block.FluidPumpBlock;
import net.darkunity.newerafoundation.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

/**
 * Насос: один фронт редстоун-сигнала (0 -> 1) = одно срабатывание = PULSE_AMOUNT mB.
 * Источник: любая жидкость позади (блок-источник в мире или ёмкость с capability).
 * Выход: лицевая сторона (дырка) - труба или бак.
 */
public class FluidPumpBlockEntity extends BlockEntity {
    public static final int PULSE_AMOUNT = 100;
    public static final int BUFFER = 1000;
    /** Радиус области поиска источников и лимит просматриваемых блоков за один забор. */
    public static final int RANGE = 8;
    private static final int MAX_SCAN = 600;

    private final FluidTank buffer = new FluidTank(BUFFER);
    private boolean wasPowered = false;

    public FluidPumpBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PUMP_TYPE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FluidPumpBlockEntity be) {
        if (level.isClientSide) return;
        boolean powered = level.hasNeighborSignal(pos);
        boolean rising = powered && !be.wasPowered;
        if (powered != be.wasPowered) { be.wasPowered = powered; be.setChanged(); }
        if (!rising) return;

        Direction f = state.getValue(FluidPumpBlock.FACING);
        IFluidHandler dst = level.getCapability(Capabilities.FluidHandler.BLOCK, pos.relative(f), f.getOpposite());
        if (dst == null) return;

        // 1. добрать в буфер, если не хватает на импульс
        if (be.buffer.getFluidAmount() < PULSE_AMOUNT) be.pullFromSource(level, pos.relative(f.getOpposite()), f);

        // 2. выдать импульс
        FluidStack out = be.buffer.drain(PULSE_AMOUNT, IFluidHandler.FluidAction.SIMULATE);
        if (out.isEmpty()) return;
        int accepted = dst.fill(out, IFluidHandler.FluidAction.EXECUTE);
        if (accepted > 0) be.buffer.drain(accepted, IFluidHandler.FluidAction.EXECUTE);
    }

    private void pullFromSource(Level level, BlockPos srcPos, Direction f) {
        // ёмкость с capability (бак, модовая цистерна)
        IFluidHandler src = level.getCapability(Capabilities.FluidHandler.BLOCK, srcPos, f);
        if (src != null) {
            FluidStack want = src.drain(BUFFER - buffer.getFluidAmount(), IFluidHandler.FluidAction.SIMULATE);
            if (!want.isEmpty()) {
                int ok = buffer.fill(want, IFluidHandler.FluidAction.SIMULATE);
                if (ok > 0) buffer.fill(src.drain(ok, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
            }
            return;
        }
        // жидкость в мире (вода, лава, любые модовые жидкости): ищем источник по области вокруг точки позади насоса
        BlockPos best = findSource(level, srcPos);
        if (best == null) return;
        BlockState st = level.getBlockState(best);
        FluidState fs = st.getFluidState();
        if (st.getBlock() instanceof BucketPickup pickup
                && buffer.fill(new FluidStack(fs.getType(), 1000), IFluidHandler.FluidAction.SIMULATE) == 1000) {
            if (!pickup.pickupBlock(null, level, best, st).isEmpty())
                buffer.fill(new FluidStack(fs.getType(), 1000), IFluidHandler.FluidAction.EXECUTE);
        }
    }

    /**
     * Область: обходим связную жидкость того же типа в радиусе RANGE от точки позади насоса
     * и берём самый высокий блок-источник (при равной высоте - ближайший), поэтому озеро вычерпывается сверху вниз.
     */
    private BlockPos findSource(Level level, BlockPos origin) {
        FluidState first = level.getFluidState(origin);
        if (first.isEmpty()) return null;
        net.minecraft.world.level.material.Fluid type = first.getType();
        java.util.ArrayDeque<BlockPos> queue = new java.util.ArrayDeque<>();
        java.util.HashSet<BlockPos> seen = new java.util.HashSet<>();
        queue.add(origin);
        seen.add(origin);
        BlockPos best = null;
        int visited = 0;
        while (!queue.isEmpty() && visited++ < MAX_SCAN) {
            BlockPos p = queue.poll();
            FluidState fs = level.getFluidState(p);
            if (fs.isSource() && level.getBlockState(p).getBlock() instanceof BucketPickup) {
                if (best == null || p.getY() > best.getY()
                        || (p.getY() == best.getY() && p.distSqr(origin) < best.distSqr(origin))) best = p;
            }
            for (Direction d : Direction.values()) {
                BlockPos n = p.relative(d);
                if (seen.contains(n)) continue;
                if (Math.abs(n.getX() - origin.getX()) > RANGE || Math.abs(n.getY() - origin.getY()) > RANGE
                        || Math.abs(n.getZ() - origin.getZ()) > RANGE) continue;
                seen.add(n);
                if (level.getFluidState(n).getType().isSame(type)) queue.add(n);
            }
        }
        return best;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Buffer", buffer.writeToNBT(registries, new CompoundTag()));
        tag.putBoolean("Powered", wasPowered);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        buffer.readFromNBT(registries, tag.getCompound("Buffer"));
        wasPowered = tag.getBoolean("Powered");
    }
}
