package Vfx.vfx_test_for_cultivatereg.network;

import Vfx.vfx_test_for_cultivatereg.Vfx_test_for_cultivatereg;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Vfx_test_for_cultivatereg.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    private ModNetwork() {
    }

    public static void register() {
        CHANNEL.registerMessage(packetId++, UseFireAbilityPacket.class,
                UseFireAbilityPacket::encode, UseFireAbilityPacket::decode,
                UseFireAbilityPacket::handle, NetworkDirection.PLAY_TO_SERVER);
        CHANNEL.registerMessage(packetId++, SpawnTornadoParticlesPacket.class,
                SpawnTornadoParticlesPacket::encode, SpawnTornadoParticlesPacket::decode,
                SpawnTornadoParticlesPacket::handle, NetworkDirection.PLAY_TO_CLIENT);
    }
}