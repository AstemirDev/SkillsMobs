package ru.astemir.skillsmobs.client.render.entity;

import com.lowdragmc.shimmer.client.postprocessing.PostProcessing;
import com.lowdragmc.shimmer.client.shader.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.astemir.api.client.ResourceArray;
import org.astemir.api.client.SkillsRenderTypes;
import org.astemir.api.client.display.IDisplayArgument;
import org.astemir.api.client.model.SkillsAnimatedModel;
import org.astemir.api.client.model.SkillsModel;
import org.astemir.api.client.model.SkillsModelLayer;
import org.astemir.api.client.render.RenderCall;
import org.astemir.api.client.render.SkillsRendererLivingEntity;
import org.astemir.api.client.render.cube.ModelElement;
import org.astemir.api.client.wrapper.SkillsWrapperEntity;
import org.astemir.api.lib.shimmer.ShimmerLib;
import ru.astemir.skillsmobs.SkillsMobs;
import ru.astemir.skillsmobs.common.entity.EntityLavaTurtle;

public class LavaTurtleRenderer extends SkillsRendererLivingEntity<EntityLavaTurtle, LavaTurtleRenderer.LavaTurtleWrapper> {

    public LavaTurtleRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new LavaTurtleWrapper());
    }

    @Override
    protected int getBlockLightLevel(EntityLavaTurtle p_114496_, BlockPos p_114497_) {
        return 15;
    }

    public static class LavaTurtleWrapper extends SkillsWrapperEntity<EntityLavaTurtle> {

        private LavaTurtleModel model = new LavaTurtleModel();

        @Override
        public SkillsModel<EntityLavaTurtle, IDisplayArgument> getModel(EntityLavaTurtle target) {
            return model;
        }


        @Override
        public RenderType getRenderType() {
            EntityLavaTurtle lavaTurtle = getRenderTarget();
            if (lavaTurtle != null) {
                return RenderType.entityTranslucent(model.getTexture(lavaTurtle));
            }
            return super.getRenderType();
        }
    }

    public static class LavaTurtleModel extends SkillsAnimatedModel<EntityLavaTurtle,IDisplayArgument> {

        private static final ResourceArray TEXTURES = new ResourceArray(SkillsMobs.MOD_ID,"entity/lava_turtle/lava_turtle_%s.png",8,0.125);
        private static final ResourceArray TEXTURES_LAYER = new ResourceArray(SkillsMobs.MOD_ID,"entity/lava_turtle/lava_turtle_layer_%s.png",8,0.125);

        private static final ResourceLocation MODEL = new ResourceLocation(SkillsMobs.MOD_ID,"geo/mob/lava_turtle.geo.json");
        private static final ResourceLocation ANIMATION = new ResourceLocation(SkillsMobs.MOD_ID,"animations/mob/lava_turtle.animation.json");

        public LavaTurtleModel() {
            super(MODEL, ANIMATION);
            addLayer(new SkillsModelLayer<EntityLavaTurtle, IDisplayArgument, SkillsModel<EntityLavaTurtle, IDisplayArgument>>(this) {
                @Override
                public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, EntityLavaTurtle instance, int pPackedLight, float pPartialTick, float r, float g, float b, float a) {
                    RenderType renderType = SkillsRenderTypes.eyesTransparent(getTexture(instance));
                    renderModel(pPoseStack,pBuffer,renderType , 15728640,1,1,1,1);
                    if (ShimmerLib.isLoaded()){
                        PoseStack copyStack = RenderUtils.copyPoseStack(pPoseStack);
                        PostProcessing.BLOOM_UNREAL.postEntityForce((source) -> {
                            VertexConsumer consumer = source.getBuffer(renderType);
                            getModel().renderModel(copyStack, consumer, ShimmerLib.LIGHT_UNSHADED, OverlayTexture.NO_OVERLAY, 1,1,1,1, RenderCall.LAYER,false);
                        });
                        ShimmerLib.renderEntityPost();
                    }
                }
                @Override
                public ResourceLocation getTexture(EntityLavaTurtle instance) {
                    return TEXTURES_LAYER.getResourceLocation(instance.tickCount);
                }
            });
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
            if (getRenderTarget() != null) {
                EntityLavaTurtle lavaTurtle = getRenderTarget();
                float ticks = ((float)lavaTurtle.tickCount)+Minecraft.getInstance().getPartialTick();
                poseStack.pushPose();
                if (lavaTurtle.controller.is(EntityLavaTurtle.ACTION_SPIN)){
                    poseStack.mulPose(Vector3f.YN.rotationDegrees(ticks*10));
                }else
                if (lavaTurtle.controller.is(EntityLavaTurtle.ACTION_SPIN_FAST)){
                    poseStack.mulPose(Vector3f.YN.rotationDegrees(ticks*30));
                }else
                if (lavaTurtle.controller.is(EntityLavaTurtle.ACTION_SPIN_VERY_FAST)){
                    poseStack.mulPose(Vector3f.YN.rotationDegrees(ticks*60));
                }
                super.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                poseStack.popPose();
            }
        }

        @Override
        public ResourceLocation getTexture(EntityLavaTurtle object) {
            return TEXTURES.getResourceLocation(object.tickCount);
        }

        @Override
        public void customAnimate(EntityLavaTurtle animated, IDisplayArgument argument, float limbSwing, float limbSwingAmount, float ticks, float delta, float headYaw, float headPitch) {
            ModelElement head = getModelElement("head");
            if (head != null){
                lookAt(head,headPitch,headYaw);
            }
        }
    }
}
