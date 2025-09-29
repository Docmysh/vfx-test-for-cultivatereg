package Vfx.vfx_test_for_cultivatereg.item;

import Vfx.vfx_test_for_cultivatereg.player.FirePowersData;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FirePowersItem extends Item {
    public FirePowersItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!FirePowersData.hasFirePowers(player)) {
            if (!level.isClientSide) {
                FirePowersData.unlock(player);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BLAZE_AMBIENT, SoundSource.PLAYERS, 1.0F, 1.25F);
                player.displayClientMessage(Component.translatable("item.vfx_test_for_cultivatereg.fire_powers.unlocked"), true);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        if (!level.isClientSide) {
            player.displayClientMessage(Component.translatable("item.vfx_test_for_cultivatereg.fire_powers.already"), true);
        }
        return InteractionResultHolder.fail(stack);
    }
}