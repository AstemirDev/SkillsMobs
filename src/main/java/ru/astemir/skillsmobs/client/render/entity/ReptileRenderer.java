package ru.astemir.skillsmobs.client.render.entity;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.astemir.api.client.display.IDisplayArgument;
import org.astemir.api.client.model.SkillsAnimatedModel;
import org.astemir.api.client.model.SkillsModel;
import org.astemir.api.client.render.SkillsRendererLivingEntity;
import org.astemir.api.client.render.cube.ModelElement;
import org.astemir.api.client.wrapper.SkillsWrapperEntity;
import ru.astemir.skillsmobs.SkillsMobs;
import ru.astemir.skillsmobs.common.entity.EntityReptile;

public class ReptileRenderer extends SkillsRendererLivingEntity<EntityReptile, ReptileRenderer.ReptileWrapper> {


    public ReptileRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ReptileWrapper());
    }

    public static class ReptileWrapper extends SkillsWrapperEntity<EntityReptile> {

        private ReptileModel model = new ReptileModel();

        @Override
        public SkillsModel<EntityReptile, IDisplayArgument> getModel(EntityReptile target) {
            return model;
        }

        @Override
        public RenderType getRenderType() {
            EntityReptile entityReptile = getRenderTarget();
            if (entityReptile != null) {
                if (entityReptile.isReptileInvisible()) {
                    return RenderType.entityTranslucent(model.getTexture(entityReptile));
                } else {
                    return super.getRenderType();
                }
            }else{
                return super.getRenderType();
            }
        }
    }

    public static class ReptileModel extends SkillsAnimatedModel<EntityReptile,IDisplayArgument> {

        private static final ResourceLocation TEXTURE = new ResourceLocation(SkillsMobs.MOD_ID, "textures/entity/reptile/reptile.png");
        private static final ResourceLocation TEXTURE_INVISIBLE = new ResourceLocation(SkillsMobs.MOD_ID, "textures/entity/reptile/reptile_invisible.png");
        private static final ResourceLocation MODEL = new ResourceLocation(SkillsMobs.MOD_ID,"geo/mob/reptile.geo.json");
        private static final ResourceLocation ANIMATION = new ResourceLocation(SkillsMobs.MOD_ID,"animations/mob/reptile.animation.json");

        public ReptileModel() {
            super(MODEL, ANIMATION);
        }


        @Override
        public void customAnimate(EntityReptile animated, IDisplayArgument argument, float limbSwing, float limbSwingAmount, float ticks, float delta, float headYaw, float headPitch) {
            ModelElement head = getModelElement("head");
            if (head != null){
                lookAt(head,headPitch,headYaw);
            }
        }

        @Override
        public ResourceLocation getTexture(EntityReptile target) {
            if (target.isReptileInvisible()){
                return TEXTURE_INVISIBLE;
            }else{
                return TEXTURE;
            }
        }
    }
}
