package ru.astemir.skillsmobs.common.event;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.astemir.api.common.event.BiomeModifyEvent;
import ru.astemir.skillsmobs.common.items.ItemScaleShield;
import ru.astemir.skillsmobs.common.registry.SMEntities;
import ru.astemir.skillsmobs.common.registry.SMItems;
import ru.astemir.skillsmobs.utils.WorldExtraUtils;

public class EntityEvents {


    @SubscribeEvent
    public static void onBiomeLoad(BiomeModifyEvent e){
        Biome biome = e.getBiomeHolder().get();
        if (WorldExtraUtils.isBiomes(biome, Biomes.BADLANDS,Biomes.ERODED_BADLANDS,Biomes.WOODED_BADLANDS)){
            e.getBuilder().getMobSpawnSettings().addSpawn(MobCategory.MONSTER,new MobSpawnSettings.SpawnerData(SMEntities.REPTILE.get(),100,1,1));
        }
        if (WorldExtraUtils.isBiomes(biome,Biomes.FROZEN_OCEAN,Biomes.SNOWY_BEACH)){
            e.getBuilder().getMobSpawnSettings().addSpawn(MobCategory.CREATURE,new MobSpawnSettings.SpawnerData(SMEntities.PENGUIN.get(),100,4,8));
        }
        if (WorldExtraUtils.isBiomes(biome, Biomes.NETHER_WASTES)){
            e.getBuilder().getMobSpawnSettings().addSpawn(MobCategory.MONSTER,new MobSpawnSettings.SpawnerData(SMEntities.PIGLIN_SHAMAN.get(),100,1,1));
            e.getBuilder().getMobSpawnSettings().addSpawn(MobCategory.MONSTER,new MobSpawnSettings.SpawnerData(SMEntities.LAVA_TURTLE.get(),100,1,1));
        }
    }

    @SubscribeEvent
    public static void onEntityDamage(LivingHurtEvent e){
        if (e.getSource().isExplosion()){
            if (e.getEntity() != null && e.getSource().getEntity() != null) {
                if (e.getEntity().getUUID().equals(e.getSource().getEntity().getUUID())) {
                    ItemStack helmet = e.getEntity().getItemBySlot(EquipmentSlot.HEAD);
                    if (!helmet.isEmpty()) {
                        if (helmet.is(SMItems.REPTILE_HAT.get())) {
                            e.setCanceled(true);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityVisibility(LivingEvent.LivingVisibilityEvent e){
        if (e.getEntity() instanceof Player) {
            Player player = (Player)e.getEntity();
            ItemStack usedStack = player.getUseItem();
            if (!usedStack.isEmpty()) {
                if (usedStack.getItem() instanceof ItemScaleShield) {
                    e.modifyVisibility(0.125f);
                }
            }
        }
    }
}
