package ru.astemir.skillsmobs.client;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import org.astemir.api.IClientLoader;
import org.astemir.api.client.registry.ArmorModelsRegistry;
import org.astemir.api.client.render.SkillsRendererItem;
import org.astemir.api.client.wrapper.SkillsWrapperItem;
import ru.astemir.skillsmobs.SkillsMobs;
import ru.astemir.skillsmobs.client.render.entity.*;
import ru.astemir.skillsmobs.client.render.item.ReptileHatWrapper;
import ru.astemir.skillsmobs.client.render.item.StaffOfVengeanceWrapper;
import ru.astemir.skillsmobs.common.registry.SMEntities;
import ru.astemir.skillsmobs.common.registry.SMItems;

public class ISMClientLoader implements IClientLoader {

    @Override
    public void load() {
        EntityRenderers.register(SMEntities.PIGLIN_SHAMAN.get(), PiglinShamanRenderer::new);
        EntityRenderers.register(SMEntities.GHOST_HOGLIN.get(), GhostHoglinRenderer::new);
        EntityRenderers.register(SMEntities.PENGUIN.get(), PenguinRenderer::new);
        EntityRenderers.register(SMEntities.REPTILE.get(), ReptileRenderer::new);
        EntityRenderers.register(SMEntities.LAVA_TURTLE.get(), LavaTurtleRenderer::new);
        EntityRenderers.register(SMEntities.HAND_DYNAMITE.get(), HandDynamiteRenderer::new);
        ArmorModelsRegistry.addModel(SMItems.REPTILE_HAT.get(), new ReptileHatWrapper());
        SkillsRendererItem.addModel(SMItems.STAFF_OF_VENGEANCE.get(),new StaffOfVengeanceWrapper());

        ItemProperties.register(SMItems.SCALE_SHIELD.get(), new ResourceLocation("blocking"), (p_174590_, p_174591_, p_174592_, p_174593_) -> p_174592_ != null && p_174592_.isUsingItem() && p_174592_.getUseItem() == p_174590_ ? 1.0F : 0.0F);
        ItemProperties.register(SMItems.HOWLING_SEASHELL.get(), new ResourceLocation(SkillsMobs.MOD_ID,"using"), (p_174590_, p_174591_, p_174592_, p_174593_) -> p_174592_ != null && p_174592_.isUsingItem() && p_174592_.getUseItem() == p_174590_ ? 1.0F : 0.0F);
    }
}
