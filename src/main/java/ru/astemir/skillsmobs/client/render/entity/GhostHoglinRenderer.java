package ru.astemir.skillsmobs.client.render.entity;

import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.astemir.api.client.ResourceArray;
import org.astemir.api.client.SkillsRenderTypes;
import org.astemir.api.client.display.IDisplayArgument;
import org.astemir.api.client.model.SkillsAnimatedModel;
import org.astemir.api.client.model.SkillsModel;
import org.astemir.api.client.render.SkillsRendererLivingEntity;
import org.astemir.api.client.wrapper.SkillsWrapperEntity;
import org.astemir.api.lib.shimmer.ShimmerLib;
import ru.astemir.skillsmobs.SkillsMobs;
import ru.astemir.skillsmobs.common.entity.EntityGhostHoglin;

import javax.swing.*;

public class GhostHoglinRenderer extends SkillsRendererLivingEntity<EntityGhostHoglin, GhostHoglinRenderer.GhostHoglinWrapper> {

    public GhostHoglinRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new GhostHoglinWrapper());
    }

    public static class GhostHoglinWrapper extends SkillsWrapperEntity<EntityGhostHoglin> {

        private GhostHoglinModel model = new GhostHoglinModel();

        @Override
        public SkillsModel<EntityGhostHoglin, IDisplayArgument> getModel(EntityGhostHoglin target) {
            return model;
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer bufferSource, int packedLight, int packedOverlay, float r, float g, float b, float a) {
            super.renderToBuffer(poseStack, bufferSource, packedLight, packedOverlay, r, g, b, a);
            if (ShimmerLib.isLoaded()){
                ShimmerLib.postModelWrapperForce(poseStack,this, SkillsRenderTypes.eyesTransparent(model.getTexture(getRenderTarget())),getRenderTarget(),ShimmerLib.LIGHT_UNSHADED, OverlayTexture.NO_OVERLAY,1,1,1,1);
                ShimmerLib.renderEntityPost();
            }
        }

        @Override
        public RenderType getRenderType() {
            if (getRenderTarget() != null) {
                return RenderType.entityTranslucent(model.getTexture(getRenderTarget()));
            }else{
                return super.getRenderType();
            }
        }
    }

    public static class GhostHoglinModel extends SkillsAnimatedModel<EntityGhostHoglin,IDisplayArgument> {

        private static final ResourceArray TEXTURES = new ResourceArray(SkillsMobs.MOD_ID,"entity/hoglin/hoglin_ghost_%s.png",6,0.5);
        private static final  ResourceLocation MODEL = new ResourceLocation(SkillsMobs.MOD_ID,"geo/mob/hoglin_ghost.geo.json");
        private static final  ResourceLocation ANIMATION = new ResourceLocation(SkillsMobs.MOD_ID,"animations/mob/hoglin_ghost.animation.json");

        public GhostHoglinModel() {
            super(MODEL, ANIMATION);
        }

        @Override
        public ResourceLocation getTexture(EntityGhostHoglin target) {
            return TEXTURES.getResourceLocation(target.tickCount);
        }

    }

}
