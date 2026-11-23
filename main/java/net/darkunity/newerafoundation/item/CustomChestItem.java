package net.darkunity.newerafoundation.item;

import net.darkunity.newerafoundation.init.ModBlockEntities;
import net.darkunity.newerafoundation.block.entity.CustomChestBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.function.Consumer;

public class CustomChestItem extends BlockItem {
    public CustomChestItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    Minecraft mc = Minecraft.getInstance();
                    this.renderer = new BlockEntityWithoutLevelRenderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels()) {
                        private CustomChestBlockEntity dummyEntity;

                        @Override
                        public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
                            if (dummyEntity == null) {
                                dummyEntity = new CustomChestBlockEntity(BlockPos.ZERO, CustomChestItem.this.getBlock().defaultBlockState());
                            }
                            
                            // Вызываем рендерер через диспетчер, передавая нашу пустышку
                            var blockEntityRenderer = mc.getBlockEntityRenderDispatcher().getRenderer(dummyEntity);
                            if (blockEntityRenderer != null) {
                                blockEntityRenderer.render(dummyEntity, 0, poseStack, buffer, light, overlay);
                            }
                        }
                    };
                }
                return this.renderer;
            }
        });
    }
}