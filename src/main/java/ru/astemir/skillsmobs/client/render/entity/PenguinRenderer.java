package ru.astemir.skillsmobs.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.astemir.api.client.display.IDisplayArgument;
import org.astemir.api.client.model.SkillsAnimatedModel;
import org.astemir.api.client.model.SkillsModel;
import org.astemir.api.client.render.RenderCall;
import org.astemir.api.client.render.SkillsRendererLivingEntity;
import org.astemir.api.client.render.cube.ModelElement;
import org.astemir.api.client.wrapper.SkillsWrapperEntity;
import org.astemir.api.math.MathUtils;
import org.astemir.api.math.components.Vector3;
import ru.astemir.skillsmobs.SkillsMobs;
import ru.astemir.skillsmobs.common.entity.EntityPenguin;

public class PenguinRenderer extends SkillsRendererLivingEntity<EntityPenguin, PenguinRenderer.PenguinWrapper> {

    public PenguinRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PenguinWrapper());
    }

    public static class PenguinWrapper extends SkillsWrapperEntity<EntityPenguin> {

        public PenguinModel MODEL = new PenguinModel(new ResourceLocation(SkillsMobs.MOD_ID,"geo/mob/penguin.geo.json"));
        public PenguinModel BABY_MODEL = new PenguinModel(new ResourceLocation(SkillsMobs.MOD_ID,"geo/mob/little_penguin.geo.json"));

        @Override
        public void renderWrapper(PoseStack poseStack, VertexConsumer bufferSource, int packedLight, int packedOverlay, float r, float g, float b, float a, RenderCall renderCall, boolean resetBuffer) {
            poseStack.pushPose();
            if (getRenderTarget() != null) {
                if (getRenderTarget().isInWater()) {
                    float ticks = ((float) getRenderTarget().tickCount) + Minecraft.getInstance().getPartialTick();
                    poseStack.translate(0, 0.75f, -0.25f);
                    poseStack.mulPose(Vector3f.XN.rotationDegrees(MathUtils.sin(ticks / 10) * 8));
                    poseStack.translate(0, -0.75f, 0.25f);
                    poseStack.translate(0, MathUtils.sin(ticks / 10) / 10f, 0);
                }
            }
            super.renderWrapper(poseStack, bufferSource, packedLight, packedOverlay, r, g, b, a, renderCall, resetBuffer);
            poseStack.popPose();
        }

        @Override
        public SkillsModel<EntityPenguin, IDisplayArgument> getModel(EntityPenguin target) {
            if (target.isBaby()){
                return BABY_MODEL;
            }else{
                return MODEL;
            }
        }


    }


    public static class PenguinModel extends SkillsAnimatedModel<EntityPenguin, IDisplayArgument> {

        private static final ResourceLocation TEXTURE = new ResourceLocation(SkillsMobs.MOD_ID, "textures/entity/penguin/penguin.png");
        private static final ResourceLocation TEXTURE_KING = new ResourceLocation(SkillsMobs.MOD_ID, "textures/entity/penguin/king_penguin.png");
        private static final ResourceLocation TEXTURE_SMILE = new ResourceLocation(SkillsMobs.MOD_ID, "textures/entity/penguin/penguin_smile.png");
        private static final ResourceLocation TEXTURE_BABY = new ResourceLocation(SkillsMobs.MOD_ID, "textures/entity/penguin/little_penguin.png");
        private static final ResourceLocation TEXTURE_BABY_2 = new ResourceLocation(SkillsMobs.MOD_ID, "textures/entity/penguin/little_penguin2.png");
        private static final ResourceLocation ANIMATION = new ResourceLocation(SkillsMobs.MOD_ID,"animations/mob/penguin.animation.json");

        public PenguinModel(ResourceLocation modelLoc) {
            super(modelLoc, ANIMATION);
        }

        @Override
        public void customAnimate(EntityPenguin animated, IDisplayArgument argument, float limbSwing, float limbSwingAmount, float ticks, float delta, float headYaw, float headPitch) {
            ModelElement head = getModelElement("head");
            if (animated.animationFactory.isPlaying(EntityPenguin.ANIMATION_IDLE,EntityPenguin.ANIMATION_WALK,EntityPenguin.ANIMATION_SWIM)) {
                if (head != null) {
                    lookAt(head, headPitch, headYaw);
                }
            }
        }

        @Override
        public void onRenderModelCube(ModelElement cube, PoseStack matrixStackIn, VertexConsumer bufferIn, RenderCall renderCall, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
            EntityPenguin currentEntity = getRenderTarget();
            if (renderCall == RenderCall.MODEL) {
                if (cube.getName().equals("head") && currentEntity != null){
                    ItemStack stack = getRenderTarget().getHeldItem();
                    if (!stack.isEmpty() && stack != null){
                        matrixStackIn.pushPose();
                        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90));
                        matrixStackIn.translate(0, -0.5, 0.1);
                        renderItem(stack, ItemTransforms.TransformType.GROUND,matrixStackIn,packedLightIn);
                        matrixStackIn.popPose();
                        bufferIn = returnDefaultBuffer();
                     }
                }
            }
        }

        @Override
        public ResourceLocation getTexture(EntityPenguin target) {
            if (target.controller.is(EntityPenguin.ACTION_BULLSHIT)){
                return TEXTURE_SMILE;
            }
            if (target.isBaby()){
                if (target.getSkinType() == 0) {
                    return TEXTURE_BABY;
                }else{
                    return TEXTURE_BABY_2;
                }
            }
            if (target.getSkinType() == 0) {
                return TEXTURE;
            }else
            if (target.getSkinType() == 1){
                return TEXTURE_KING;
            }
            return TEXTURE;
        }
    }
}
