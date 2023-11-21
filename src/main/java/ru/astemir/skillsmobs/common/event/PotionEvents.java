package ru.astemir.skillsmobs.common.event;


import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import ru.astemir.skillsmobs.common.entity.EntityPiglinShaman;

public class PotionEvents {

    @SubscribeEvent
    public static void onAddEffect(MobEffectEvent.Added e){
        if (e.getEntity() instanceof EntityPiglinShaman){
            ((EntityPiglinShaman)e.getEntity()).setAccelerated(true);
        }
    }

    @SubscribeEvent
    public static void onExpireEffect(MobEffectEvent.Expired e){
        if (e.getEntity() instanceof EntityPiglinShaman){
            ((EntityPiglinShaman)e.getEntity()).setAccelerated(false);
        }
    }

    @SubscribeEvent
    public void onRemoveEffect(MobEffectEvent.Remove e){
        if (e.getEntity() instanceof EntityPiglinShaman){
            ((EntityPiglinShaman)e.getEntity()).setAccelerated(false);
        }
    }
}
