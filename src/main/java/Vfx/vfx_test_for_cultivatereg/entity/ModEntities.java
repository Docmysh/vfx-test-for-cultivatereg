package Vfx.vfx_test_for_cultivatereg.entity;

import Vfx.vfx_test_for_cultivatereg.Vfx_test_for_cultivatereg;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Vfx_test_for_cultivatereg.MODID);

    public static final RegistryObject<EntityType<TornadoEntity>> FIRE_TORNADO = ENTITY_TYPES.register(
            "fire_tornado",
            () -> EntityType.Builder.<TornadoEntity>of(TornadoEntity::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .clientTrackingRange(96)
                    .updateInterval(1)
                    .build(new ResourceLocation(Vfx_test_for_cultivatereg.MODID, "fire_tornado").toString())
    );

    private ModEntities() {
    }
}