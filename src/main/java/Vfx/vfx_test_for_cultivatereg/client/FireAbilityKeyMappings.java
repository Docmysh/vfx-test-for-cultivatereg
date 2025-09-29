package Vfx.vfx_test_for_cultivatereg.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public final class FireAbilityKeyMappings {
    public static final String CATEGORY = "key.categories.vfx_test_for_cultivatereg.fire";

    public static final KeyMapping FIRE_KEY_Z = new KeyMapping("key.vfx_test_for_cultivatereg.fire.z",
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_Z), CATEGORY);
    public static final KeyMapping FIRE_KEY_X = new KeyMapping("key.vfx_test_for_cultivatereg.fire.x",
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_X), CATEGORY);
    public static final KeyMapping FIRE_KEY_C = new KeyMapping("key.vfx_test_for_cultivatereg.fire.c",
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_C), CATEGORY);
    public static final KeyMapping FIRE_KEY_V = new KeyMapping("key.vfx_test_for_cultivatereg.fire.v",
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_V), CATEGORY);

    private FireAbilityKeyMappings() {
    }
}
