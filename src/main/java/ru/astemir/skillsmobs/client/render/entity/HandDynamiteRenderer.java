package ru.astemir.skillsmobs.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.astemir.api.client.display.IDisplayArgument;
import org.astemir.api.client.model.SkillsModel;
import org.astemir.api.client.render.SkillsRendererEntity;
import org.astemir.api.client.render.cube.ModelElement;
import org.astemir.api.client.wrapper.SkillsWrapperEntity;
import org.astemir.api.math.MathUtils;
import ru.astemir.skillsmobs.SkillsMobs;
import ru.astemir.skillsmobs.common.entity.EntityHandDynamite;

public class HandDynamiteRenderer extends SkillsRendererEntity<EntityHandDynamite, HandDynamiteRenderer.HandDynamiteWrapper> {

    public HandDynamiteRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HandDynamiteWrapper());
    }

    public static class HandDynamiteWrapper extends SkillsWrapperEntity<EntityHandDynamite> {

        private HandDynamiteModel model = new HandDynamiteModel();

        @Override
        public SkillsModel<EntityHandDynamite, IDisplayArgument> getModel(EntityHandDynamite target) {
            return model;
        }
    }

    public static class HandDynamiteModel extends SkillsModel<EntityHandDynamite, IDisplayArgument> {

        private static final ResourceLocation TEXTURE = new ResourceLocation(SkillsMobs.MOD_ID,"textures/entity/reptile/reptile.png");
        private static final ResourceLocation MODEL = new ResourceLocation(SkillsMobs.MOD_ID,"geo/entity/hand_dynamite.geo.json");

        public HandDynamiteModel() {
            super(MODEL);
        }

        @Override
        public void customAnimate(EntityHandDynamite animated, IDisplayArgument argument, float limbSwing, float limbSwingAmount, float ticks, float delta, float headYaw, float headPitch) {
            ModelElement head = getModelElement("Tnt");
            if (head != null) {
                lookAt(head, animated.xRotO+90, animated.yRotO);
            }
        }

        @Override
        public ResourceLocation getTexture(EntityHandDynamite target) {
            return TEXTURE;
        }
    }

}
