package Vfx.vfx_test_for_cultivatereg.network;

import Vfx.vfx_test_for_cultivatereg.Vfx_test_for_cultivatereg;
import Vfx.vfx_test_for_cultivatereg.entity.ModEntities;
import Vfx.vfx_test_for_cultivatereg.entity.TornadoEntity;
import Vfx.vfx_test_for_cultivatereg.player.FirePowersData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UseFireAbilityPacket {
    private final FireAbility ability;

    public UseFireAbilityPacket(FireAbility ability) {
        this.ability = ability;
    }

    public static UseFireAbilityPacket decode(FriendlyByteBuf buf) {
        return new UseFireAbilityPacket(buf.readEnum(FireAbility.class));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(this.ability);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Player player = context.getSender();
            if (!(player instanceof ServerPlayer serverPlayer)) {
                return;
            }

            if (!FirePowersData.hasFirePowers(serverPlayer)) {
                serverPlayer.displayClientMessage(Component.translatable("message.vfx_test_for_cultivatereg.fire_powers.locked"), true);
                return;
            }

            switch (this.ability) {
                case FIRE_TORNADO -> castFireTornado(serverPlayer);
                default -> {
                }
            }
        });
        context.setPacketHandled(true);
    }

    private void castFireTornado(ServerPlayer player) {
        if (player.getCooldowns().isOnCooldown(Vfx_test_for_cultivatereg.FIRE_POWERS.get())) {
            player.displayClientMessage(Component.translatable("message.vfx_test_for_cultivatereg.fire_powers.cooldown"), true);
            return;
        }

        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        TornadoEntity tornado = ModEntities.FIRE_TORNADO.get().create(serverLevel);
        if (tornado == null) {
            return;
        }

        Vec3 spawnPos = findGroundTarget(player);
        tornado.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, player.getYRot(), player.getXRot());
        tornado.configure(player, 3.5F, 12.0F, 200, 0.25F);
        serverLevel.addFreshEntity(tornado);
        player.getCooldowns().addCooldown(Vfx_test_for_cultivatereg.FIRE_POWERS.get(), 200);
    }

    private Vec3 findGroundTarget(ServerPlayer player) {
        Vec3 eyePosition = player.getEyePosition();
        Vec3 viewVector = player.getViewVector(1.0F);
        double maxDistance = 32.0D;
        Vec3 end = eyePosition.add(viewVector.scale(maxDistance));
        ClipContext context = new ClipContext(eyePosition, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player);
        BlockHitResult result = player.level().clip(context);
        if (result.getType() == HitResult.Type.BLOCK) {
            Vec3 location = result.getLocation();
            return new Vec3(location.x, location.y + 0.05D, location.z);
        }

        Vec3 position = player.position().add(viewVector.scale(2.5D));
        return new Vec3(position.x, player.getY(), position.z);
    }

    public enum FireAbility {
        FIRE_TORNADO,
        FIRE_Z,
        FIRE_X,
        FIRE_V
    }
}