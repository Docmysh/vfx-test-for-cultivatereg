package Vfx.vfx_test_for_cultivatereg.player;

import Vfx.vfx_test_for_cultivatereg.Vfx_test_for_cultivatereg;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Vfx_test_for_cultivatereg.MODID)
public class FirePowersEvents {
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) {
            return;
        }
        FirePowersData.copy(event.getOriginal(), event.getEntity());
    }
}