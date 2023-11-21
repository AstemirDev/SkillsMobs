package ru.astemir.skillsmobs.common.registry;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.astemir.skillsmobs.SkillsMobs;
import ru.astemir.skillsmobs.common.entity.*;

@Mod.EventBusSubscriber(modid = SkillsMobs.MOD_ID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class SMEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SkillsMobs.MOD_ID);
    public static final RegistryObject<EntityType<EntityPiglinShaman>> PIGLIN_SHAMAN = ENTITIES.register("piglin_shaman",()->register("piglin_shaman", EntityPiglinShaman::new, MobCategory.MONSTER,0.8f, 1.95F));
    public static final RegistryObject<EntityType<EntityGhostHoglin>> GHOST_HOGLIN = ENTITIES.register("ghost_hoglin",()->register("ghost_hoglin", EntityGhostHoglin::new, MobCategory.MONSTER,1.3964844F, 1.4F));
    public static final RegistryObject<EntityType<EntityPenguin>> PENGUIN = ENTITIES.register("penguin",()->register("penguin", EntityPenguin::new, MobCategory.CREATURE,0.6f, 0.7f));
    public static final RegistryObject<EntityType<EntityReptile>> REPTILE = ENTITIES.register("reptile",()->register("reptile", EntityReptile::new, MobCategory.MONSTER,0.65f, 1.75f));
    public static final RegistryObject<EntityType<EntityHandDynamite>> HAND_DYNAMITE = ENTITIES.register("hand_dynamite", ()->register("hand_dynamite", EntityHandDynamite::new,MobCategory.MISC,0.25f,0.25f,10));
    public static final RegistryObject<EntityType<EntityLavaTurtle>> LAVA_TURTLE = ENTITIES.register("lava_turtle",()->register("lava_turtle", EntityLavaTurtle::new, MobCategory.MONSTER,2f, 2f));


    public static final EntityType register(String name, EntityType.EntityFactory entity, MobCategory classification, float width, float height){
        EntityType<? extends Mob> type = EntityType.Builder.of(entity, classification).sized(width,height).clientTrackingRange(8).build(new ResourceLocation(name).toString());
        return type;
    }


    public static final EntityType register(String name, EntityType.EntityFactory entity, MobCategory classification, float width, float height,int updateInterval){
        EntityType<? extends Mob> type = EntityType.Builder.of(entity, classification).sized(width,height).clientTrackingRange(8).updateInterval(updateInterval).build(new ResourceLocation(name).toString());
        return type;
    }

    public static boolean rollSpawn(int rolls, RandomSource random, MobSpawnType reason){
        if(reason == MobSpawnType.SPAWNER){
            return true;
        }else{
            return rolls <= 0 || random.nextInt(rolls) == 0;
        }
    }

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent e){
        e.put(PENGUIN.get(), Animal.createMobAttributes().add(Attributes.MAX_HEALTH,10).add(Attributes.MOVEMENT_SPEED, 0.2D).add(ForgeMod.SWIM_SPEED.get(),2f).build());
        e.put(PIGLIN_SHAMAN.get(),Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40).add(Attributes.MOVEMENT_SPEED, 0.3D).add(Attributes.FOLLOW_RANGE,40).build());
        e.put(GHOST_HOGLIN.get(),Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 40).add(Attributes.MOVEMENT_SPEED, 0.8D).build());
        e.put(REPTILE.get(),Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20).add(Attributes.MOVEMENT_SPEED, 0.4D).add(Attributes.FOLLOW_RANGE,60).build());
        e.put(LAVA_TURTLE.get(),Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 60).add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.ATTACK_KNOCKBACK,0.8f).add(Attributes.FOLLOW_RANGE,20).add(Attributes.ATTACK_DAMAGE,15).add(Attributes.ARMOR,6).build());
        SpawnPlacements.register(SMEntities.REPTILE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityReptile::checkSpawnRules);
        SpawnPlacements.register(SMEntities.PIGLIN_SHAMAN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules);
        SpawnPlacements.register(SMEntities.PENGUIN.get(), SpawnPlacements.Type.NO_RESTRICTIONS, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityPenguin::checkSpawnRules);
        SpawnPlacements.register(SMEntities.LAVA_TURTLE.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EntityLavaTurtle::checkSpawnRules);
    }

}
