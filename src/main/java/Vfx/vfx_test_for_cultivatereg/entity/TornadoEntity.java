package Vfx.vfx_test_for_cultivatereg.entity;

import Vfx.vfx_test_for_cultivatereg.network.ModNetwork;
import Vfx.vfx_test_for_cultivatereg.network.SpawnTornadoParticlesPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.CommandFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TornadoEntity extends Entity {
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_HEIGHT = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_LIFETIME = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_SPEED = SynchedEntityData.defineId(TornadoEntity.class, EntityDataSerializers.FLOAT);
    private static final ResourceLocation TORNADO_ANIMATION_FUNCTION = new ResourceLocation("test", "demo/l0/l0_0");

    private UUID ownerUUID;
    private LivingEntity cachedOwner;

    public TornadoEntity(EntityType<? extends TornadoEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public void configure(@Nullable LivingEntity owner, float radius, float height, int lifetimeTicks, float angularSpeed) {
        if (owner != null) {
            this.ownerUUID = owner.getUUID();
            this.cachedOwner = owner;
        }
        this.entityData.set(DATA_RADIUS, radius);
        this.entityData.set(DATA_HEIGHT, height);
        this.entityData.set(DATA_LIFETIME, lifetimeTicks);
        this.entityData.set(DATA_SPEED, angularSpeed);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_RADIUS, 3.5F);
        this.entityData.define(DATA_HEIGHT, 12.0F);
        this.entityData.define(DATA_LIFETIME, 200);
        this.entityData.define(DATA_SPEED, 0.25F);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            this.applyForces();
            if (this.level() instanceof ServerLevel serverLevel) {
                this.runAnimationFunction(serverLevel);
            }
        }

        if (this.tickCount > this.getLifetime()) {
            this.discard();
        }
    }

    private void applyForces() {
        double radius = this.getRadius();
        double height = this.getHeight();
        AABB area = new AABB(this.getX() - radius, this.getY(), this.getZ() - radius,
                this.getX() + radius, this.getY() + height, this.getZ() + radius);
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, area, this::canAffect)) {
            Vec3 toCenter = new Vec3(this.getX(), clamp(entity.getY(), this.getY(), this.getY() + height), this.getZ())
                    .subtract(entity.position());
            double horizontalDist = Math.max(0.25, Math.hypot(toCenter.x, toCenter.z));
            Vec3 inward = new Vec3(toCenter.x / horizontalDist, 0, toCenter.z / horizontalDist).scale(0.08);
            Vec3 swirl = new Vec3(-inward.z, 0, inward.x).scale(0.25);
            Vec3 updraft = new Vec3(0, 0.10, 0);
            Vec3 boost = inward.add(swirl).add(updraft);

            double heightFactor = clamp((entity.getY() - this.getY()) / height, 0.0, 1.0);
            double taper = clamp((radius - horizontalDist) / radius, 0.0, 1.0);
            boost = boost.scale(0.4 + 0.6 * taper).scale(0.6 + 0.4 * heightFactor);

            entity.setDeltaMovement(entity.getDeltaMovement().add(boost));
            entity.hurtMarked = true;

            if (this.tickCount % 10 == 0) {
                entity.setSecondsOnFire(2);
                DamageSource damageSource = this.damageSources().inFire();
                entity.hurt(damageSource, 1.0F);
            }
        }
    }

    private void runAnimationFunction(ServerLevel serverLevel) {
        MinecraftServer server = serverLevel.getServer();
        ServerFunctionManager functionManager = server.getFunctions();
        functionManager.get(TORNADO_ANIMATION_FUNCTION)
                .ifPresentOrElse(function -> executeAnimation(functionManager, function),
                        () -> spawnLegacyParticles());
    }

    private void executeAnimation(ServerFunctionManager functionManager, CommandFunction function) {
        CommandSourceStack source = this.createCommandSourceStack()
                .withSuppressedOutput()
                .withPermission(2)
                .withRotation(Vec2.ZERO);
        functionManager.execute(function, source);
    }

    private void spawnLegacyParticles() {
        if (this.tickCount % 2 != 0) {
            return;
        }
        ModNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> this),
                new SpawnTornadoParticlesPacket(this.getX(), this.getY(), this.getZ(),
                        this.getRadius(), this.getHeight(), this.getAngularSpeed(),
                        4, this.tickCount));
    }

    private boolean canAffect(LivingEntity entity) {
        if (!entity.isAlive()) {
            return false;
        }
        LivingEntity owner = this.getOwner();
        return owner == null || entity != owner;
    }

    @Nullable
    private LivingEntity getOwner() {
        if (this.cachedOwner != null && this.cachedOwner.isAlive()) {
            return this.cachedOwner;
        }
        if (this.ownerUUID != null && this.level() instanceof ServerLevel serverLevel) {
            Entity found = serverLevel.getEntity(this.ownerUUID);
            if (found instanceof LivingEntity living) {
                this.cachedOwner = living;
                return living;
            }
        }
        return null;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public float getRadius() {
        return this.entityData.get(DATA_RADIUS);
    }

    public float getHeight() {
        return this.entityData.get(DATA_HEIGHT);
    }

    public int getLifetime() {
        return this.entityData.get(DATA_LIFETIME);
    }

    public float getAngularSpeed() {
        return this.entityData.get(DATA_SPEED);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.entityData.set(DATA_RADIUS, tag.getFloat("Radius"));
        this.entityData.set(DATA_HEIGHT, tag.getFloat("Height"));
        this.entityData.set(DATA_LIFETIME, tag.getInt("Lifetime"));
        this.entityData.set(DATA_SPEED, tag.getFloat("AngularSpeed"));
        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putFloat("Radius", this.getRadius());
        tag.putFloat("Height", this.getHeight());
        tag.putInt("Lifetime", this.getLifetime());
        tag.putFloat("AngularSpeed", this.getAngularSpeed());
        if (this.ownerUUID != null) {
            tag.putUUID("Owner", this.ownerUUID);
        }
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }


}