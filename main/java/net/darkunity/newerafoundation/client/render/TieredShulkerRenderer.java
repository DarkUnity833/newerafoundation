package net.darkunity.newerafoundation.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.darkunity.newerafoundation.block.TieredShulkerBlock;
import net.darkunity.newerafoundation.block.entity.TieredShulkerBlockEntity;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

/**
 * Same geometry and animation as the vanilla shulker box renderer, but with our own
 * per-tier textures (assets/newerafoundation/textures/entity/shulker/<tier>.png, 64x64, vanilla UV layout).
 */
public class TieredShulkerRenderer implements BlockEntityRenderer<TieredShulkerBlockEntity> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath("newerafoundation", "tiered_shulker"), "main");

    private final ModelPart base;
    private final ModelPart lid;

    public TieredShulkerRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart root = context.bakeLayer(LAYER);
        this.base = root.getChild("base");
        this.lid = root.getChild("lid");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("base",
                CubeListBuilder.create().texOffs(0, 28).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 8.0F, 16.0F),
                PartPose.offset(0.0F, 24.0F, 0.0F));
        root.addOrReplaceChild("lid",
                CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -16.0F, -8.0F, 16.0F, 12.0F, 16.0F),
                PartPose.offset(0.0F, 24.0F, 0.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void render(TieredShulkerBlockEntity box, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Direction direction = Direction.UP;
        if (box.hasLevel() && box.getBlockState().hasProperty(TieredShulkerBlock.FACING)) {
            direction = box.getBlockState().getValue(TieredShulkerBlock.FACING);
        }
        float progress = box.getProgress(partialTick);

        poseStack.pushPose();
        poseStack.translate(0.5F, 0.5F, 0.5F);
        poseStack.scale(0.9995F, 0.9995F, 0.9995F);
        poseStack.mulPose(direction.getRotation());
        poseStack.scale(1.0F, -1.0F, -1.0F);
        poseStack.translate(0.0F, -1.0F, 0.0F);

        lid.setPos(0.0F, 24.0F - progress * 0.5F * 16.0F, 0.0F);
        lid.yRot = 270.0F * progress * ((float) Math.PI / 180.0F);

        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("newerafoundation",
                "textures/entity/shulker/" + box.getTier().getSerializedName() + ".png");
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        base.render(poseStack, consumer, packedLight, packedOverlay);
        lid.render(poseStack, consumer, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
