package ru.astemir.skillsmobs.common.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import ru.astemir.skillsmobs.common.entity.EntityHandDynamite;
import ru.astemir.skillsmobs.common.registry.SMEntities;

public class ItemHandDynamite extends Item {


    public ItemHandDynamite() {
        super(new Properties().stacksTo(16).tab(CreativeModeTab.TAB_COMBAT));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_43142_, Player p_43143_, InteractionHand p_43144_) {
        ItemStack itemstack = p_43143_.getItemInHand(p_43144_);
        if (!p_43143_.getCooldowns().isOnCooldown(itemstack.getItem())) {
            p_43143_.getCooldowns().addCooldown(itemstack.getItem(),20);
            p_43142_.playSound((Player) null, p_43143_.getX(), p_43143_.getY(), p_43143_.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (p_43142_.getRandom().nextFloat() * 0.4F + 0.8F));
            if (!p_43142_.isClientSide) {
                EntityHandDynamite dynamite = new EntityHandDynamite(SMEntities.HAND_DYNAMITE.get(),p_43143_,p_43142_);
                dynamite.setOwner(p_43143_);
                dynamite.shootFromRotation(p_43143_, p_43143_.getXRot(), p_43143_.getYRot(), 0.0F, 1.25F, 1.0F);
                p_43142_.addFreshEntity(dynamite);
            }

            p_43143_.awardStat(Stats.ITEM_USED.get(this));
            if (!p_43143_.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            return InteractionResultHolder.sidedSuccess(itemstack, p_43142_.isClientSide());
        }
        return InteractionResultHolder.fail(itemstack);
    }
}
