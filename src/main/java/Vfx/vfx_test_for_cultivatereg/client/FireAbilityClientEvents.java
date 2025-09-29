package Vfx.vfx_test_for_cultivatereg.client;

import Vfx.vfx_test_for_cultivatereg.Vfx_test_for_cultivatereg;
import Vfx.vfx_test_for_cultivatereg.network.ModNetwork;
import Vfx.vfx_test_for_cultivatereg.network.UseFireAbilityPacket;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Vfx_test_for_cultivatereg.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FireAbilityClientEvents {
    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(FireAbilityKeyMappings.FIRE_KEY_Z);
        event.register(FireAbilityKeyMappings.FIRE_KEY_X);
        event.register(FireAbilityKeyMappings.FIRE_KEY_C);
        event.register(FireAbilityKeyMappings.FIRE_KEY_V);
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Vfx_test_for_cultivatereg.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player == null || minecraft.screen != null) {
                return;
            }
            while (FireAbilityKeyMappings.FIRE_KEY_C.consumeClick()) {
                ModNetwork.CHANNEL.sendToServer(new UseFireAbilityPacket(UseFireAbilityPacket.FireAbility.FIRE_TORNADO));
            }
        }
    }
}