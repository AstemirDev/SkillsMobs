package ru.astemir.skillsmobs.common.items;


import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import ru.astemir.skillsmobs.common.entity.EntityPenguin;
import ru.astemir.skillsmobs.common.registry.SMSounds;

import java.util.List;

public class ItemHowlingSeashell extends Item {


    public ItemHowlingSeashell() {
        super(new Properties().tab(CreativeModeTab.TAB_TOOLS).rarity(Rarity.UNCOMMON).stacksTo(1).durability(5).setNoRepair());
    }

    @Override
    public int getUseDuration(ItemStack p_40680_) {
        return 40;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack p_40678_) {
        return UseAnim.BOW;
    }


    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        stack.hurtAndBreak(1, livingEntity, p -> p.broadcastBreakEvent(livingEntity.getUsedItemHand()));
        if (livingEntity instanceof Player){
            ((Player)livingEntity).getCooldowns().addCooldown(this,200);
            List<EntityPenguin> penguins = level.getEntitiesOfClass(EntityPenguin.class, livingEntity.getBoundingBox().inflate(20.0D), (e) -> true);
            if (penguins.size() > 0) {
                for (int i = 0; i < Math.min(penguins.size(), 5); i++) {
                    EntityPenguin penguin = penguins.get(i);
                    penguin.forceGoToFishing((Player)livingEntity,true);
                }
            }
        }
        level.playSound((Player) null,livingEntity.blockPosition(), SMSounds.HOWLING_SEASHELL.get(), SoundSource.PLAYERS,1,1);
        return super.finishUsingItem(stack, level, livingEntity);
    }

    @Override
    public void releaseUsing(ItemStack p_41412_, Level p_41413_, LivingEntity p_41414_, int p_41415_) {
        if (p_41414_ instanceof Player){
            ((Player)p_41414_).getCooldowns().addCooldown(this,200);
        }
        super.releaseUsing(p_41412_, p_41413_, p_41414_, p_41415_);
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (count % 5 == 0){
            Vec3 pos = player.getEyePosition().add(player.getViewVector(0.75f));
            player.level.addParticle(ParticleTypes.BUBBLE,pos.x,pos.y,pos.z,1,1,1);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_40672_, Player p_40673_, InteractionHand p_40674_) {
        ItemStack itemstack = p_40673_.getItemInHand(p_40674_);
        p_40673_.startUsingItem(p_40674_);
        return InteractionResultHolder.consume(itemstack);
    }



}
