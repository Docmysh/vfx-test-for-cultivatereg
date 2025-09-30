package Vfx.vfx_test_for_cultivatereg.client.render;

import Vfx.vfx_test_for_cultivatereg.entity.TornadoEntity;
import Vfx.vfx_test_for_cultivatereg.util.TornadoShape;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class TornadoRenderer extends EntityRenderer<TornadoEntity> {
    private final Map<Integer, Integer> lastParticleTick = new HashMap<>();

    public TornadoRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(TornadoEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        this.spawnParticles(entity, partialTicks);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void spawnParticles(TornadoEntity entity, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || minecraft.isPaused()) {
            return;
        }

        if (entity.isRemoved()) {
            this.lastParticleTick.remove(entity.getId());
            return;
        }

        int currentTick = entity.tickCount;
        Integer lastTick = this.lastParticleTick.get(entity.getId());
        if (lastTick != null && lastTick == currentTick) {
            return;
        }
        this.lastParticleTick.put(entity.getId(), currentTick);

        RandomSource random = RandomSource.create(entity.getId() * 3129871L + currentTick);
        float radius = entity.getRadius();
        float height = entity.getHeight();
        double yawRadians = Math.toRadians(entity.getViewYRot(partialTicks));
        double spinRadians = (entity.tickCount + partialTicks) * entity.getAngularSpeed();
        double rotation = yawRadians + spinRadians + entity.getId() * 0.2617993877991494D;

        float distanceFade = 1.0F;
        if (minecraft.player != null) {
            float distance = (float) minecraft.player.distanceToSqr(entity.getX(), entity.getY(), entity.getZ());
            distanceFade = Mth.clamp(1.0F - distance / 512.0F, 0.35F, 1.0F);
        }

        for (TornadoShape.TornadoPoint point : TornadoShape.POINTS) {
            Vec3 world = TornadoShape.toWorld(point, entity.getX(), entity.getY(), entity.getZ(), radius, height, rotation);
            double upwardSpeed = 0.01D + random.nextDouble() * 0.015D;
            double lateralDrift = (random.nextDouble() - 0.5D) * 0.0025D * distanceFade;
            level.addParticle(ParticleTypes.FLAME, world.x, world.y, world.z,
                    lateralDrift, upwardSpeed, lateralDrift);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(TornadoEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
