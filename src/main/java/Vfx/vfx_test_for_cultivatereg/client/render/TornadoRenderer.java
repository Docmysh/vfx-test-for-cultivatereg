package Vfx.vfx_test_for_cultivatereg.client.render;

import Vfx.vfx_test_for_cultivatereg.Vfx_test_for_cultivatereg;
import Vfx.vfx_test_for_cultivatereg.entity.TornadoEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class TornadoRenderer extends EntityRenderer<TornadoEntity> {
    private static final ResourceLocation RIBBON = ResourceLocation.fromNamespaceAndPath(
            Vfx_test_for_cultivatereg.MODID, "textures/effect/tornado_ribbon.png");
    private static final ResourceLocation STREAK = ResourceLocation.fromNamespaceAndPath(
            Vfx_test_for_cultivatereg.MODID, "textures/effect/tornado_streak.png");

    public TornadoRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(TornadoEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        float time = entity.tickCount + partialTicks;
        float wobble = 3.0F * Mth.sin(time * 0.05F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(wobble));

        drawSpiralLayer(poseStack, buffer, entity, partialTicks, RIBBON,
                1.00F, 0.95F, 0.86F, 0.95F, 1.0F, 1.6F, 32, 0.55F, 3.6F);
        drawSpiralLayer(poseStack, buffer, entity, partialTicks, STREAK,
                1.00F, 0.75F, 0.25F, 0.90F, -1.1F, 1.9F, 28, 0.45F, 3.2F);
        drawSpiralLayer(poseStack, buffer, entity, partialTicks, STREAK,
                0.95F, 0.55F, 0.20F, 0.85F, 1.6F, 2.4F, 24, 0.30F, 4.4F);

        drawHalo(poseStack, buffer, entity, partialTicks, STREAK, 0.85F, 0.15F);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void drawSpiralLayer(PoseStack poseStack, MultiBufferSource buffer, TornadoEntity entity, float partialTicks,
                                 ResourceLocation texture,
                                 float cr, float cg, float cb, float ca,
                                 float swirlDir, float uvScrollSpeed,
                                 int verticalSegments, float widthScale, float coils) {
        float radius = entity.getRadius();
        float height = entity.getHeight();
        float angularSpeed = entity.getAngularSpeed();

        float time = (entity.tickCount + partialTicks) * angularSpeed;
        float uOffset = ((entity.tickCount + partialTicks) % 200F) / 200F * uvScrollSpeed;
        float vOffset = ((entity.tickCount + partialTicks) % 160F) / 160F * (uvScrollSpeed * 0.7F);

        RenderType renderType = RenderType.energySwirl(texture, uOffset, vOffset);
        VertexConsumer consumer = buffer.getBuffer(renderType);
        PoseStack.Pose pose = poseStack.last();

        float ribbonHalf = 0.45F * widthScale;

        for (int i = 0; i < verticalSegments; i++) {
            float v0 = i / (float) verticalSegments;
            float v1 = (i + 1) / (float) verticalSegments;

            float y0 = v0 * height;
            float y1 = v1 * height;

            float flare0 = 0.55F + 0.45F * v0;
            float flare1 = 0.55F + 0.45F * v1;

            float r0 = radius * flare0;
            float r1 = radius * flare1;

            float angleOffset = swirlDir * (time * 12.0F);
            float angle0 = coils * Mth.TWO_PI * v0 + angleOffset;
            float angle1 = coils * Mth.TWO_PI * v1 + angleOffset;

            float cx0 = r0 * Mth.cos(angle0);
            float cz0 = r0 * Mth.sin(angle0);
            float cx1 = r1 * Mth.cos(angle1);
            float cz1 = r1 * Mth.sin(angle1);

            float nx0 = -Mth.sin(angle0);
            float nz0 = Mth.cos(angle0);
            float nx1 = -Mth.sin(angle1);
            float nz1 = Mth.cos(angle1);

            float lx0 = cx0 + nx0 * ribbonHalf;
            float lz0 = cz0 + nz0 * ribbonHalf;
            float rx0 = cx0 - nx0 * ribbonHalf;
            float rz0 = cz0 - nz0 * ribbonHalf;

            float lx1 = cx1 + nx1 * ribbonHalf;
            float lz1 = cz1 + nz1 * ribbonHalf;
            float rx1 = cx1 - nx1 * ribbonHalf;
            float rz1 = cz1 - nz1 * ribbonHalf;

            int light = 0xF000F0;

            consumer.vertex(pose.pose(), lx0, y0, lz0).color(cr, cg, cb, ca).uv(0.0F, v0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
            consumer.vertex(pose.pose(), rx0, y0, rz0).color(cr, cg, cb, ca).uv(1.0F, v0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
            consumer.vertex(pose.pose(), lx1, y1, lz1).color(cr, cg, cb, ca).uv(0.0F, v1)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();

            consumer.vertex(pose.pose(), rx0, y0, rz0).color(cr, cg, cb, ca).uv(1.0F, v0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
            consumer.vertex(pose.pose(), rx1, y1, rz1).color(cr, cg, cb, ca).uv(1.0F, v1)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
            consumer.vertex(pose.pose(), lx1, y1, lz1).color(cr, cg, cb, ca).uv(0.0F, v1)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
        }
    }

    private void drawHalo(PoseStack poseStack, MultiBufferSource buffer, TornadoEntity entity, float partialTicks,
                          ResourceLocation texture, float radiusScale, float thicknessScale) {
        float radius = entity.getRadius() * radiusScale;
        float height = entity.getHeight();
        float angularSpeed = entity.getAngularSpeed();

        float time = (entity.tickCount + partialTicks) * angularSpeed;
        float uOffset = ((entity.tickCount + partialTicks) % 120F) / 120F;
        float vOffset = ((entity.tickCount + partialTicks) % 90F) / 90F;

        RenderType renderType = RenderType.energySwirl(texture, uOffset, vOffset);
        VertexConsumer consumer = buffer.getBuffer(renderType);
        PoseStack.Pose pose = poseStack.last();

        float inner = radius * (1.0F - thicknessScale * 0.5F);
        float outer = radius * (1.0F + thicknessScale * 0.5F);
        int segments = 32;
        int light = 0xF000F0;

        for (int i = 0; i < segments; i++) {
            float progress0 = i / (float) segments;
            float progress1 = (i + 1) / (float) segments;
            float angle0 = progress0 * Mth.TWO_PI + time * 4.0F;
            float angle1 = progress1 * Mth.TWO_PI + time * 4.0F;

            float ix0 = inner * Mth.cos(angle0);
            float iz0 = inner * Mth.sin(angle0);
            float ox0 = outer * Mth.cos(angle0);
            float oz0 = outer * Mth.sin(angle0);

            float ix1 = inner * Mth.cos(angle1);
            float iz1 = inner * Mth.sin(angle1);
            float ox1 = outer * Mth.cos(angle1);
            float oz1 = outer * Mth.sin(angle1);

            float v0 = progress0;
            float v1 = progress1;

            consumer.vertex(pose.pose(), ox0, height, oz0).color(1.0F, 0.85F, 0.45F, 0.85F).uv(0.0F, v0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
            consumer.vertex(pose.pose(), ix0, height + 0.1F, iz0).color(1.0F, 0.95F, 0.65F, 0.65F).uv(1.0F, v0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
            consumer.vertex(pose.pose(), ox1, height, oz1).color(1.0F, 0.85F, 0.45F, 0.85F).uv(0.0F, v1)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();

            consumer.vertex(pose.pose(), ix0, height + 0.1F, iz0).color(1.0F, 0.95F, 0.65F, 0.65F).uv(1.0F, v0)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
            consumer.vertex(pose.pose(), ix1, height + 0.1F, iz1).color(1.0F, 0.95F, 0.65F, 0.65F).uv(1.0F, v1)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
            consumer.vertex(pose.pose(), ox1, height, oz1).color(1.0F, 0.85F, 0.45F, 0.85F).uv(0.0F, v1)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(TornadoEntity entity) {
        return RIBBON;
    }
}