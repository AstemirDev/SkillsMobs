package ru.astemir.skillsmobs.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
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
import ru.astemir.skillsmobs.SkillsMobs;
import ru.astemir.skillsmobs.common.entity.EntityPenguin;
import ru.astemir.skillsmobs.common.entity.EntityPiglinShaman;

public class PiglinShamanRenderer extends SkillsRendererLivingEntity<EntityPiglinShaman, PiglinShamanRenderer.PiglinShamanWrapper> {

    public PiglinShamanRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PiglinShamanWrapper());
    }

    public static class PiglinShamanWrapper extends SkillsWrapperEntity<EntityPiglinShaman> {

        private PiglinShamanModel model = new PiglinShamanModel();


        @Override
        public SkillsModel<EntityPiglinShaman, IDisplayArgument> getModel(EntityPiglinShaman target) {
            return model;
        }
    }

    private static class PiglinShamanModel extends SkillsAnimatedModel<EntityPiglinShaman, IDisplayArgument> {

        private static final ResourceLocation TEXTURE = new ResourceLocation(SkillsMobs.MOD_ID,"textures/entity/piglin_shaman.png");
        private static final ResourceLocation MODEL = new ResourceLocation(SkillsMobs.MOD_ID,"geo/mob/piglin_shaman.geo.json");
        private static final ResourceLocation ANIMATION = new ResourceLocation(SkillsMobs.MOD_ID,"animations/mob/piglin_shaman.animation.json");

        public PiglinShamanModel() {
            super(MODEL, ANIMATION);
        }

        @Override
        public void customAnimate(EntityPiglinShaman animated, IDisplayArgument argument, float limbSwing, float limbSwingAmount, float ticks, float delta, float headYaw, float headPitch) {
            ModelElement head = getModelElement("Head");
            if (animated.animationFactory.isPlaying(EntityPenguin.ANIMATION_IDLE,EntityPenguin.ANIMATION_WALK,EntityPenguin.ANIMATION_SWIM)) {
                if (head != null) {
                    lookAt(head, headPitch, headYaw);
                }
            }
        }

        @Override
        public void onRenderModelCube(ModelElement cube, PoseStack matrixStackIn, VertexConsumer bufferIn, RenderCall renderCall, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
            if (renderCall == RenderCall.MODEL) {
                if (cube.getName().equals("Potion") && getRenderTarget() != null) {
                    ItemStack stack = getRenderTarget().getItemInHand(InteractionHand.MAIN_HAND);
                    if (stack != null) {
                        matrixStackIn.pushPose();
                        matrixStackIn.mulPose(Vector3f.XN.rotationDegrees(240));
                        renderItem(stack, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, matrixStackIn, packedLightIn);
                        bufferIn = returnDefaultBuffer();
                        matrixStackIn.popPose();
                    }
                }
            }
        }

        @Override
        public ResourceLocation getTexture(EntityPiglinShaman target) {
            return TEXTURE;
        }
    }

}
