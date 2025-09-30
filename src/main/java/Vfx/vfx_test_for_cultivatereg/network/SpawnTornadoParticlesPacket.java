package Vfx.vfx_test_for_cultivatereg.network;

import Vfx.vfx_test_for_cultivatereg.util.TornadoShape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.Random;
import java.util.function.Supplier;

public class SpawnTornadoParticlesPacket {
    private final double centerX;
    private final double centerY;
    private final double centerZ;
    private final float radius;
    private final float height;
    private final float angularSpeed;
    private final int bands;
    private final int seed;

    public SpawnTornadoParticlesPacket(double centerX, double centerY, double centerZ, float radius,
                                       float height, float angularSpeed, int bands, int seed) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
        this.radius = radius;
        this.height = height;
        this.angularSpeed = angularSpeed;
        this.bands = bands;
        this.seed = seed;
    }

    public static SpawnTornadoParticlesPacket decode(FriendlyByteBuf buf) {
        double centerX = buf.readDouble();
        double centerY = buf.readDouble();
        double centerZ = buf.readDouble();
        float radius = buf.readFloat();
        float height = buf.readFloat();
        float angularSpeed = buf.readFloat();
        int bands = buf.readInt();
        int seed = buf.readInt();
        return new SpawnTornadoParticlesPacket(centerX, centerY, centerZ, radius, height, angularSpeed, bands, seed);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeDouble(this.centerX);
        buf.writeDouble(this.centerY);
        buf.writeDouble(this.centerZ);
        buf.writeFloat(this.radius);
        buf.writeFloat(this.height);
        buf.writeFloat(this.angularSpeed);
        buf.writeInt(this.bands);
        buf.writeInt(this.seed);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(this::handleClient);
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClient() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        Random random = new Random(this.seed);
        double time = level.getGameTime() + Minecraft.getInstance().getFrameTime();
        double rotation = (this.seed * 0.15D) + time * this.angularSpeed * 0.25D;

        float distanceFade = 1.0F;
        if (Minecraft.getInstance().player != null) {
            float distance = (float) Minecraft.getInstance().player.distanceToSqr(this.centerX, this.centerY, this.centerZ);
            distanceFade = Mth.clamp(1.0F - distance / 512.0F, 0.35F, 1.0F);
        }

        for (TornadoShape.TornadoPoint point : TornadoShape.POINTS) {
            Vec3 world = TornadoShape.toWorld(point, this.centerX, this.centerY, this.centerZ, this.radius, this.height, rotation);
            double upwardSpeed = 0.008D + random.nextDouble() * 0.02D;
            double spread = 0.0025D * distanceFade;
            level.addParticle(ParticleTypes.FLAME, world.x, world.y, world.z,
                    (random.nextDouble() - 0.5D) * spread, upwardSpeed, (random.nextDouble() - 0.5D) * spread);
            if (random.nextFloat() < 0.35F) {
                level.addParticle(ParticleTypes.SMALL_FLAME, world.x, world.y, world.z,
                        0.0D, upwardSpeed * 0.6D, 0.0D);
            }
            if (random.nextFloat() < 0.25F) {
                level.addParticle(ParticleTypes.ASH, world.x, world.y, world.z,
                        0.0D, 0.002D + random.nextDouble() * 0.01D, 0.0D);
            }
        }

        int emberCount = (int) (30 * distanceFade);
        for (int i = 0; i < emberCount; i++) {
            double offsetX = (random.nextDouble() * 2 - 1) * (this.radius * 0.35);
            double offsetZ = (random.nextDouble() * 2 - 1) * (this.radius * 0.35);
            double y = this.centerY + random.nextDouble() * this.height;
            level.addParticle(ParticleTypes.LAVA, this.centerX + offsetX, y, this.centerZ + offsetZ,
                    0.0D, 0.035D + random.nextDouble() * 0.015D, 0.0D);
        }

        int smokeCount = (int) (18 * distanceFade);
        for (int i = 0; i < smokeCount; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = this.radius * (0.65 + 0.45 * random.nextDouble());
            level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    this.centerX + distance * Math.cos(angle), this.centerY + this.height,
                    this.centerZ + distance * Math.sin(angle),
                    0.0D, 0.02D + random.nextDouble() * 0.02D, 0.0D);
        }
    }
}