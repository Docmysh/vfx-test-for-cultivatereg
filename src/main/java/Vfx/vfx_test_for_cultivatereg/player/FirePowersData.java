package Vfx.vfx_test_for_cultivatereg.player;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public final class FirePowersData {
    private static final String ROOT_TAG = "VfxFirePowers";
    private static final String UNLOCKED_TAG = "Unlocked";

    private FirePowersData() {
    }

    public static boolean hasFirePowers(Player player) {
        return getRootTag(player).getBoolean(UNLOCKED_TAG);
    }

    public static void unlock(Player player) {
        CompoundTag root = getOrCreateRoot(player);
        root.putBoolean(UNLOCKED_TAG, true);
    }

    public static void copy(Player original, Player clone) {
        CompoundTag originalRoot = original.getPersistentData().getCompound(ROOT_TAG);
        CompoundTag cloneRoot = clone.getPersistentData().getCompound(ROOT_TAG);
        cloneRoot.merge(originalRoot);
        clone.getPersistentData().put(ROOT_TAG, cloneRoot);
    }

    private static CompoundTag getRootTag(Player player) {
        return player.getPersistentData().getCompound(ROOT_TAG);
    }

    private static CompoundTag getOrCreateRoot(Player player) {
        CompoundTag persistentData = player.getPersistentData();
        if (!persistentData.contains(ROOT_TAG)) {
            CompoundTag newTag = new CompoundTag();
            persistentData.put(ROOT_TAG, newTag);
            return newTag;
        }
        return persistentData.getCompound(ROOT_TAG);
    }
}