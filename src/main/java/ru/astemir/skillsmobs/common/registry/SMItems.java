package ru.astemir.skillsmobs.common.registry;


import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.astemir.skillsmobs.SkillsMobs;
import ru.astemir.skillsmobs.common.items.*;


public class SMItems {


    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SkillsMobs.MOD_ID);
    public static final RegistryObject<Item> HOWLING_SEASHELL = ITEMS.register("howling_seashell",()->new ItemHowlingSeashell());
    public static final RegistryObject<Item> DRILL = ITEMS.register("drill",()->new ItemDrill());
    public static final RegistryObject<Item> HAND_DYNAMITE = ITEMS.register("hand_dynamite",()->new ItemHandDynamite());
    public static final RegistryObject<Item> SCALE_SHIELD = ITEMS.register("scale_shield",()->new ItemScaleShield());
    public static final RegistryObject<Item> REPTILE_HAT = ITEMS.register("reptile_hat",()->new ItemReptileHat());
    public static final RegistryObject<Item> STAFF_OF_VENGEANCE = ITEMS.register("staff_of_vengeance",()->new ItemStaffOfVengeance());
    public static final RegistryObject<Item> REPTILE_SKIN = ITEMS.register("reptile_skin",()->new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> NETHER_MANA = ITEMS.register("nether_mana",()->new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> PIGLIN_SHAMAN_SPAWN_EGG = ITEMS.register("piglin_shaman_spawn_egg",()->new ForgeSpawnEggItem(()-> (EntityType<? extends Mob>) SMEntities.PIGLIN_SHAMAN.get(),0x00fbd2,0xfb4500,new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> PENGUIN_SPAWN_EGG = ITEMS.register("penguin_spawn_egg",()->new ForgeSpawnEggItem(()-> (EntityType<? extends Mob>) SMEntities.PENGUIN.get(),0x181a48,0xc6c7db,new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> REPTILE_SPAWN_EGG = ITEMS.register("reptile_spawn_egg",()->new ForgeSpawnEggItem(()-> (EntityType<? extends Mob>) SMEntities.REPTILE.get(),0x7bae5f,0x6b5030,new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<Item> LAVA_TURTLE_SPAWN_EGG = ITEMS.register("lava_turtle_spawn_egg",()->new ForgeSpawnEggItem(()-> (EntityType<? extends Mob>) SMEntities.LAVA_TURTLE.get(),0xf50000,0xa95c0a,new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

}
