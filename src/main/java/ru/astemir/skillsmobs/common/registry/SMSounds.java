package ru.astemir.skillsmobs.common.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.astemir.skillsmobs.SkillsMobs;

public class SMSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, SkillsMobs.MOD_ID);

    public static final RegistryObject<SoundEvent> PIGLIN_SHAMAN_IDLE = register("entity.piglin_shaman.idle");
    public static final RegistryObject<SoundEvent> PIGLIN_SHAMAN_ANGRY = register("entity.piglin_shaman.angry");
    public static final RegistryObject<SoundEvent> PIGLIN_SHAMAN_DEATH = register("entity.piglin_shaman.death");
    public static final RegistryObject<SoundEvent> PIGLIN_SHAMAN_HURT = register("entity.piglin_shaman.hurt");
    public static final RegistryObject<SoundEvent> PIGLIN_SHAMAN_SUMMON = register("entity.piglin_shaman.summon");
    public static final RegistryObject<SoundEvent> PIGLIN_SHAMAN_HOGLIN_APPEAR = register("entity.piglin_shaman.hoglin_appear");

    public static final RegistryObject<SoundEvent> PENGUIN_IDLE = register("entity.penguin.idle");
    public static final RegistryObject<SoundEvent> PENGUIN_SWING = register("entity.penguin.swing");
    public static final RegistryObject<SoundEvent> PENGUIN_HURT = register("entity.penguin.hurt");
    public static final RegistryObject<SoundEvent> PENGUIN_DEATH = register("entity.penguin.death");

    public static final RegistryObject<SoundEvent> REPTILE_IDLE = register("entity.reptile.idle");
    public static final RegistryObject<SoundEvent> REPTILE_ABILITY = register("entity.reptile.ability");
    public static final RegistryObject<SoundEvent> REPTILE_HURT = register("entity.reptile.hurt");
    public static final RegistryObject<SoundEvent> REPTILE_DEATH = register("entity.reptile.death");


    public static final RegistryObject<SoundEvent> LAVA_TURTLE_IDLE = register("entity.lava_turtle.idle");
    public static final RegistryObject<SoundEvent> LAVA_TURTLE_ATTACK = register("entity.lava_turtle.attack");
    public static final RegistryObject<SoundEvent> LAVA_TURTLE_DEATH = register("entity.lava_turtle.death");
    public static final RegistryObject<SoundEvent> LAVA_TURTLE_HURT = register("entity.lava_turtle.hurt");
    public static final RegistryObject<SoundEvent> LAVA_TURTLE_SHELL_HIT = register("entity.lava_turtle.shell_hit");
    public static final RegistryObject<SoundEvent> LAVA_TURTLE_SPIN = register("entity.lava_turtle.spin");
    public static final RegistryObject<SoundEvent> HOWLING_SEASHELL = register("item.howling_seashell.use");

    public static RegistryObject<SoundEvent> register(String path){
        return SOUNDS.register(path,()->new SoundEvent(new ResourceLocation(SkillsMobs.MOD_ID,path)));
    }
}
