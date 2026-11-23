package net.darkunity.newerafoundation.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.darkunity.newerafoundation.block.entity.CustomChestBlockEntity;
import net.darkunity.newerafoundation.block.CustomChestBlock; // ДОБАВИЛ ИМПОРТ
import net.darkunity.newerafoundation.client.ChestMaterials;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CustomChestRenderer implements BlockEntityRenderer<CustomChestBlockEntity> {
    private final ModelPart lid;
    private final ModelPart bottom;
    private final ModelPart lock;

    public CustomChestRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart modelpart = context.bakeLayer(ModelLayers.CHEST);
        this.bottom = modelpart.getChild("bottom");
        this.lid = modelpart.getChild("lid");
        this.lock = modelpart.getChild("lock");
    }

   @Override
    public void render(CustomChestBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        Level level = entity.getLevel();
        BlockState state = level != null ? entity.getBlockState() : null;
        
        Direction direction = Direction.SOUTH;
        if (state != null && state.hasProperty(CustomChestBlock.FACING)) {
            direction = state.getValue(CustomChestBlock.FACING);
        }

        // ПЕРВЫЙ PUSH (Общий для всего блока)
        poseStack.pushPose();
        
        poseStack.translate(0.5D, 0.5D, 0.5D);
        float f = direction.toYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(-f));
        poseStack.translate(-0.5D, -0.5D, -0.5D);

        Material material = ChestMaterials.byTier(entity.getTier());
        VertexConsumer vertexConsumer = material.buffer(buffer, RenderType::entityCutout);

        float lidAngle = entity.getOpenNess(partialTick);
        lidAngle = 1.0F - lidAngle;
        lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
        float finalLidAngle = -(lidAngle * ((float) Math.PI / 2F));

        // 1. Рендерим основание
        this.bottom.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);

        // ВТОРОЙ PUSH (Только для крышки и замка)
        poseStack.pushPose();
        
        // Микро-сдвиг и микро-скейл для убийства полоски на всех гранях
        poseStack.translate(0, 0.0001D, 0); 
        poseStack.scale(1.0001F, 1.0001F, 1.0001F);
        // Небольшая компенсация центра после скейла
        poseStack.translate(-0.00005D, 0, -0.00005D);

        this.lid.xRot = finalLidAngle;
        this.lock.xRot = finalLidAngle;
        
        this.lid.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);
        this.lock.render(poseStack, vertexConsumer, combinedLight, combinedOverlay);
        
        // ЗАКРЫВАЕМ ВТОРОЙ PUSH
        poseStack.popPose();

        // ЗАКРЫВАЕМ ПЕРВЫЙ PUSH
        poseStack.popPose();
    }
}