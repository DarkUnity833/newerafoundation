package net.darkunity.newerafoundation.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.darkunity.newerafoundation.block.entity.CustomTankBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix4f;

public class CustomTankRenderer implements BlockEntityRenderer<CustomTankBlockEntity> {
    public CustomTankRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CustomTankBlockEntity tankBE, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        FluidStack fluidStack = tankBE.getFluid();
        if (fluidStack.isEmpty()) return;

        Fluid fluid = fluidStack.getFluid();
        IClientFluidTypeExtensions fluidResources = IClientFluidTypeExtensions.of(fluid);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidResources.getStillTexture(fluidStack));
        
        int color = fluidResources.getTintColor(fluidStack);
        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;
        float a = (float)(color >> 24 & 255) / 255.0F;

        float capacity = (float) tankBE.getCapacity();
        float amount = (float) fluidStack.getAmount();
        
        // Математически точные границы, чтобы избежать Z-fighting с моделью блока
        float min = 0.132f;
        float max = 0.868f;
        float yMin = 0.063f; // Чуть выше дна бака
        float yMax = yMin + (amount / capacity) * 0.874f; // Максимальная высота внутри рамок

        VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
        Matrix4f matrix = poseStack.last().pose();

        // Порядок вершин важен для Culling (видимости сторон):
        
        // NORTH (Z-)
        addVertex(builder, matrix, min, yMin, min, sprite.getU0(), sprite.getV1(), r, g, b, a, combinedLight, 0, 0, -1);
        addVertex(builder, matrix, min, yMax, min, sprite.getU0(), sprite.getV0(), r, g, b, a, combinedLight, 0, 0, -1);
        addVertex(builder, matrix, max, yMax, min, sprite.getU1(), sprite.getV0(), r, g, b, a, combinedLight, 0, 0, -1);
        addVertex(builder, matrix, max, yMin, min, sprite.getU1(), sprite.getV1(), r, g, b, a, combinedLight, 0, 0, -1);

        // SOUTH (Z+)
        addVertex(builder, matrix, max, yMin, max, sprite.getU1(), sprite.getV1(), r, g, b, a, combinedLight, 0, 0, 1);
        addVertex(builder, matrix, max, yMax, max, sprite.getU1(), sprite.getV0(), r, g, b, a, combinedLight, 0, 0, 1);
        addVertex(builder, matrix, min, yMax, max, sprite.getU0(), sprite.getV0(), r, g, b, a, combinedLight, 0, 0, 1);
        addVertex(builder, matrix, min, yMin, max, sprite.getU0(), sprite.getV1(), r, g, b, a, combinedLight, 0, 0, 1);

        // WEST (X-)
        addVertex(builder, matrix, min, yMin, max, sprite.getU1(), sprite.getV1(), r, g, b, a, combinedLight, -1, 0, 0);
        addVertex(builder, matrix, min, yMax, max, sprite.getU1(), sprite.getV0(), r, g, b, a, combinedLight, -1, 0, 0);
        addVertex(builder, matrix, min, yMax, min, sprite.getU0(), sprite.getV0(), r, g, b, a, combinedLight, -1, 0, 0);
        addVertex(builder, matrix, min, yMin, min, sprite.getU0(), sprite.getV1(), r, g, b, a, combinedLight, -1, 0, 0);

        // EAST (X+)
        addVertex(builder, matrix, max, yMin, min, sprite.getU0(), sprite.getV1(), r, g, b, a, combinedLight, 1, 0, 0);
        addVertex(builder, matrix, max, yMax, min, sprite.getU0(), sprite.getV0(), r, g, b, a, combinedLight, 1, 0, 0);
        addVertex(builder, matrix, max, yMax, max, sprite.getU1(), sprite.getV0(), r, g, b, a, combinedLight, 1, 0, 0);
        addVertex(builder, matrix, max, yMin, max, sprite.getU1(), sprite.getV1(), r, g, b, a, combinedLight, 1, 0, 0);

        // TOP (Y+)
        addVertex(builder, matrix, min, yMax, min, sprite.getU0(), sprite.getV0(), r, g, b, a, combinedLight, 0, 1, 0);
        addVertex(builder, matrix, min, yMax, max, sprite.getU0(), sprite.getV1(), r, g, b, a, combinedLight, 0, 1, 0);
        addVertex(builder, matrix, max, yMax, max, sprite.getU1(), sprite.getV1(), r, g, b, a, combinedLight, 0, 1, 0);
        addVertex(builder, matrix, max, yMax, min, sprite.getU1(), sprite.getV0(), r, g, b, a, combinedLight, 0, 1, 0);
        
        // BOTTOM (Y-) - Добавил на случай, если бак стоит на прозрачном блоке
        addVertex(builder, matrix, min, yMin, min, sprite.getU0(), sprite.getV0(), r, g, b, a, combinedLight, 0, -1, 0);
        addVertex(builder, matrix, max, yMin, min, sprite.getU1(), sprite.getV0(), r, g, b, a, combinedLight, 0, -1, 0);
        addVertex(builder, matrix, max, yMin, max, sprite.getU1(), sprite.getV1(), r, g, b, a, combinedLight, 0, -1, 0);
        addVertex(builder, matrix, min, yMin, max, sprite.getU0(), sprite.getV1(), r, g, b, a, combinedLight, 0, -1, 0);
    }

    private void addVertex(VertexConsumer builder, Matrix4f matrix, float x, float y, float z, float u, float v, float r, float g, float b, float a, int light, float nx, float ny, float nz) {
        builder.addVertex(matrix, x, y, z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(nx, ny, nz);
    }
}