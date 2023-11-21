package ru.astemir.skillsmobs.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.astemir.example.common.entity.EntityExampleMinotaur;
import ru.astemir.skillsmobs.SkillsMobs;
import ru.astemir.skillsmobs.client.RenderMaterials;
import ru.astemir.skillsmobs.common.items.ItemScaleShield;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = SkillsMobs.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ShieldItemRenderer extends BlockEntityWithoutLevelRenderer {

    public static ShieldItemRenderer instance;

    private BlockEntityRenderDispatcher dispatcher;

    private EntityModelSet models;

    private ShieldModel shieldModel;

    public ShieldItemRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet models) {
        super(dispatcher,models);
        this.dispatcher = dispatcher;
        this.models = models;
    }

    @Override
    public void onResourceManagerReload(ResourceManager p_172555_) {
        this.shieldModel = new ShieldModel(this.models.bakeLayer(ModelLayers.SHIELD));
    }

    @SubscribeEvent
    public static void onRegisterReloadListener(RegisterClientReloadListenersEvent event) {
        instance = new ShieldItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
        event.registerReloadListener(instance);
    }




    @Override
    public void renderByItem(ItemStack p_108830_, ItemTransforms.TransformType p_108831_, PoseStack p_108832_, MultiBufferSource p_108833_, int p_108834_, int p_108835_) {
        if (p_108830_.getItem() instanceof ItemScaleShield) {
            p_108832_.pushPose();
            p_108832_.scale(1.0F, -1.0F, -1.0F);
            Material material = RenderMaterials.LOCATION_SCALE_SHIELD;
            VertexConsumer vertexconsumer = material.sprite().wrap(ItemRenderer.getFoilBufferDirect(p_108833_, this.shieldModel.renderType(material.atlasLocation()), true, p_108830_.hasFoil()));
            this.shieldModel.handle().render(p_108832_, vertexconsumer, p_108834_, p_108835_, 1.0F, 1.0F, 1.0F, 1.0F);
            this.shieldModel.plate().render(p_108832_, vertexconsumer, p_108834_, p_108835_, 1.0F, 1.0F, 1.0F, 1.0F);
            p_108832_.popPose();
        }
    }

}
