package net.darkunity.newerafoundation.block.entity;

import net.darkunity.newerafoundation.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.Container;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.item.ItemStack;

public class CustomHopperBlockEntity extends HopperBlockEntity {
    private int customCooldownTicks = 0;

    public CustomHopperBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModBlockEntities.CUSTOM_HOPPER.get();
    }

    // --- ПОЛНОЕ ПОДАВЛЕНИЕ ВАНИЛЬНОЙ ЛОГИКИ ---

    @Override
    public void setCooldown(int cooldown) {
        // Игнорируем ванильные попытки поставить кулдаун
    }

    @Override
    public boolean isOnCustomCooldown() {
        // Мы используем свою переменную, чтобы ванильный тикер нас не трогал
        return this.customCooldownTicks > 0;
    }

    // Этот метод вызывается из твоего Block класса (CustomHopperBlock)
    public static void pushItemsTick(Level level, BlockPos pos, BlockState state, CustomHopperBlockEntity be, int customCooldown, int itemsPerMove) {
        // Ручной декремент нашего собственного кулдауна
        if (be.customCooldownTicks > 0) {
            be.customCooldownTicks--;
            return;
        }

        boolean changed = false;

        for (int i = 0; i < itemsPerMove; i++) {
            boolean moved = false;

            // 1. Пытаемся отдать
            if (ejectItems(level, pos, state, be)) {
                moved = true;
            }

            // 2. Пытаемся всосать
            if (HopperBlockEntity.suckInItems(level, be)) {
                moved = true;
            }

            if (moved) {
                changed = true;
            } else {
                break;
            }
        }

        if (changed) {
            // Устанавливаем наш собственный кулдаун
            be.customCooldownTicks = customCooldown;
            setChanged(level, pos, state);
        } else {
            // Если воронка пуста, проверяем её чуть реже (раз в 4 тика), чтобы не лагало
            be.customCooldownTicks = 4;
        }
    }

    // Кастомный метод перемещения, который не дергает ванильные кулдауны
    private static boolean ejectItems(Level level, BlockPos pos, BlockState state, HopperBlockEntity be) {
        Container container = getAttachedContainer(level, pos, state);
        if (container == null) return ejectToHandler(level, pos, state, be);

        Direction direction = state.getValue(HopperBlock.FACING).getOpposite();
        if (isFullContainer(container, direction)) return false;

        for (int i = 0; i < be.getContainerSize(); ++i) {
            ItemStack stack = be.getItem(i);
            if (!stack.isEmpty()) {
                ItemStack copy = stack.copy();
                ItemStack toAdd = stack.split(1); // Забираем 1 предмет
                ItemStack remaining = HopperBlockEntity.addItem(be, container, toAdd, direction);
                
                if (remaining.isEmpty()) {
                    container.setChanged();
                    return true;
                }
                
                // Если не влезло, возвращаем предмет назад
                stack.grow(1);
            }
        }
        return false;
    }

    /** Запасной путь: цель - не ванильный Container, а блок с capability (void-бочка, модовые хранилища). */
    private static boolean ejectToHandler(Level level, BlockPos pos, BlockState state, HopperBlockEntity be) {
        Direction facing = state.getValue(HopperBlock.FACING);
        net.neoforged.neoforge.items.IItemHandler handler = level.getCapability(
                net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, pos.relative(facing), facing.getOpposite());
        if (handler == null) return false;
        for (int i = 0; i < be.getContainerSize(); ++i) {
            ItemStack stack = be.getItem(i);
            if (stack.isEmpty()) continue;
            ItemStack one = stack.copy();
            one.setCount(1);
            ItemStack rest = net.neoforged.neoforge.items.ItemHandlerHelper.insertItem(handler, one, false);
            if (rest.isEmpty()) {
                stack.shrink(1);
                be.setChanged();
                return true;
            }
        }
        return false;
    }

    private static Container getAttachedContainer(Level level, BlockPos pos, BlockState state) {
        Direction direction = state.getValue(HopperBlock.FACING);
        return HopperBlockEntity.getContainerAt(level, pos.relative(direction));
    }

    private static boolean isFullContainer(Container container, Direction direction) {
        return getSlots(container, direction).allMatch(slot -> {
            ItemStack itemStack = container.getItem(slot);
            return itemStack.getCount() >= itemStack.getMaxStackSize();
        });
    }

    private static java.util.stream.IntStream getSlots(Container container, Direction direction) {
        return container instanceof net.minecraft.world.WorldlyContainer worldly 
            ? java.util.stream.IntStream.of(worldly.getSlotsForFace(direction)) 
            : java.util.stream.IntStream.range(0, container.getContainerSize());
    }
}