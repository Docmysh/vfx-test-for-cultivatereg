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

        if (currentTick % 2 != 0) {
            return;
        }

        RandomSource random = RandomSource.create(entity.getId() * 3129871L + currentTick);
        double time = level.getGameTime() + partialTicks;
        double rotation = (entity.getId() * 0.15D) + time * entity.getAngularSpeed() * 0.25D;

        float distanceFade = 1.0F;
        if (minecraft.player != null) {
            float distance = (float) minecraft.player.distanceToSqr(entity.getX(), entity.getY(), entity.getZ());
            distanceFade = Mth.clamp(1.0F - distance / 512.0F, 0.35F, 1.0F);
        }

        float radius = entity.getRadius();
        float height = entity.getHeight();

        for (TornadoShape.TornadoPoint point : TornadoShape.POINTS) {
            Vec3 world = TornadoShape.toWorld(point, entity.getX(), entity.getY(), entity.getZ(), radius, height, rotation);
            double upwardSpeed = 0.008D + random.nextDouble() * 0.02D;
            double spread = 0.0025D * distanceFade;
            level.addParticle(ParticleTypes.FLAME, world.x, world.y, world.z,
                    (random.nextDouble() - 0.5D) * spread, upwardSpeed,
                    (random.nextDouble() - 0.5D) * spread);
            if (random.nextFloat() < 0.35F) {
                level.addParticle(ParticleTypes.SMALL_FLAME, world.x, world.y, world.z,
                        0.0D, upwardSpeed * 0.6D, 0.0D);
            }
            if (random.nextFloat() < 0.25F) {
                level.addParticle(ParticleTypes.ASH, world.x, world.y, world.z,
                        0.0D, 0.002D + random.nextDouble() * 0.01D, 0.0D);
            }
        }

        int emberCount = Mth.ceil(30 * distanceFade);
        for (int i = 0; i < emberCount; i++) {
            double offsetX = (random.nextDouble() * 2 - 1) * (radius * 0.35D);
            double offsetZ = (random.nextDouble() * 2 - 1) * (radius * 0.35D);
            double y = entity.getY() + random.nextDouble() * height;
            level.addParticle(ParticleTypes.LAVA, entity.getX() + offsetX, y, entity.getZ() + offsetZ,
                    0.0D, 0.035D + random.nextDouble() * 0.015D, 0.0D);
        }

        int smokeCount = Mth.ceil(18 * distanceFade);
        for (int i = 0; i < smokeCount; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = radius * (0.65D + 0.45D * random.nextDouble());
            level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    entity.getX() + distance * Math.cos(angle), entity.getY() + height,
                    entity.getZ() + distance * Math.sin(angle),
                    0.0D, 0.02D + random.nextDouble() * 0.02D, 0.0D);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(TornadoEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
