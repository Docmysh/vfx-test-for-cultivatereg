package Vfx.vfx_test_for_cultivatereg.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
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
        return new SpawnTornadoParticlesPacket(buf.readDouble(), buf.readDouble(), buf.readDouble(),
                buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readInt(), buf.readInt());
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
        context.enqueueWork(() -> handleClient());
        context.setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClient() {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        Random random = new Random(this.seed);
        int stepsPerBand = Mth.clamp(24 - (int) (Minecraft.getInstance().player != null ?
                Minecraft.getInstance().player.distanceToSqr(this.centerX, this.centerY, this.centerZ) / 32.0 : 0), 12, 24);
        double time = (level.getGameTime() % 20) / 20.0;
        for (int band = 0; band < this.bands; band++) {
            double theta0 = (2 * Math.PI * band / this.bands) + random.nextDouble() * 0.5;
            for (int step = 0; step < stepsPerBand; step++) {
                double t = step / (double) stepsPerBand;
                double y = this.centerY + t * this.height;
                double r = this.radius * (0.6 + 0.4 * t);
                double theta = theta0 + 6.0 * t + this.angularSpeed * time;
                double x = this.centerX + r * Math.cos(theta);
                double z = this.centerZ + r * Math.sin(theta);

                level.addParticle(ParticleTypes.FLAME, x, y, z, 0.0, 0.02, 0.0);
                if (random.nextFloat() < 0.4f) {
                    level.addParticle(ParticleTypes.ASH, x, y, z, 0.0, 0.005, 0.0);
                }
                if (random.nextFloat() < 0.25f) {
                    level.addParticle(ParticleTypes.SMALL_FLAME, x, y, z, 0.0, 0.01, 0.0);
                }
            }
        }

        for (int i = 0; i < 40; i++) {
            double offsetX = (random.nextDouble() * 2 - 1) * (this.radius * 0.3);
            double offsetZ = (random.nextDouble() * 2 - 1) * (this.radius * 0.3);
            double y = this.centerY + random.nextDouble() * this.height;
            level.addParticle(ParticleTypes.LAVA, this.centerX + offsetX, y, this.centerZ + offsetZ, 0.0, 0.04, 0.0);
        }

        for (int i = 0; i < 20; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = this.radius * (0.7 + 0.5 * random.nextDouble());
            level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    this.centerX + distance * Math.cos(angle), this.centerY + this.height,
                    this.centerZ + distance * Math.sin(angle), 0.0, 0.02 + random.nextDouble() * 0.02, 0.0);
        }
    }
}