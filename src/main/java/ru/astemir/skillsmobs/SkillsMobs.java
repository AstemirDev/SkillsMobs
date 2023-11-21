package ru.astemir.skillsmobs;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.astemir.api.IClientLoader;
import org.astemir.api.SkillsForgeMod;
import org.astemir.api.client.registry.TESRModelsRegistry;
import org.astemir.example.common.item.ItemExampleMace;
import ru.astemir.skillsmobs.client.ISMClientLoader;
import ru.astemir.skillsmobs.common.event.EntityEvents;
import ru.astemir.skillsmobs.common.event.PotionEvents;
import ru.astemir.skillsmobs.common.registry.SMEntities;
import ru.astemir.skillsmobs.common.registry.SMItems;
import ru.astemir.skillsmobs.common.registry.SMSounds;


@Mod(SkillsMobs.MOD_ID)
public class SkillsMobs extends SkillsForgeMod {
    public static final String MOD_ID = "skillsmobs";


    public SkillsMobs() {
        MinecraftForge.EVENT_BUS.register(PotionEvents.class);
        MinecraftForge.EVENT_BUS.register(EntityEvents.class);
        MinecraftForge.EVENT_BUS.register(this);
        SMSounds.SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
        SMEntities.ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        SMItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @Override
    protected void onUnsafeClientSetup() {
        TESRModelsRegistry.addModelReplacement("skillsmobs:staff_of_vengeance", "skillsmobs:staff_of_vengeance_in_hand");
        TESRModelsRegistry.addModelReplacement("skillsmobs:hand_dynamite", "skillsmobs:hand_dynamite_in_hand");
    }

    @Override
    public IClientLoader getClientLoader() {
        return new ISMClientLoader();
    }
}
