package net.darkunity.newerafoundation.data;

import net.darkunity.newerafoundation.init.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.darkunity.newerafoundation.block.TieredShulkerBlock;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Collections;
import java.util.stream.Collectors;

public class ModBlockLoot extends BlockLootSubProvider {

    protected ModBlockLoot(HolderLookup.Provider registries) {
        super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        // Используем DeferredHolder вместо DeferredBlock
        for (DeferredHolder<Block, ? extends Block> holder : ModBlocks.BLOCKS.getEntries()) {
            Block block = holder.value();
            if (block instanceof TieredShulkerBlock) {
                // keeps contents/custom name on the dropped item (same as vanilla shulker boxes)
                this.add(block, this.createShulkerBoxDrop(block));
            } else {
                this.dropSelf(block);
            }
        }
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        // Используем явную лямбду, чтобы компилятор не путался в типах
        return ModBlocks.BLOCKS.getEntries().stream()
                .map(DeferredHolder::value)
                .collect(Collectors.toList());
    }
}