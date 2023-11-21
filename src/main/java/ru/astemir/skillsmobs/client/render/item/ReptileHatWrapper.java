package ru.astemir.skillsmobs.client.render.item;

import net.minecraft.resources.ResourceLocation;
import org.astemir.api.client.display.DisplayArgumentArmor;
import org.astemir.api.client.model.SkillsModel;
import org.astemir.api.client.wrapper.SkillsWrapperArmor;
import ru.astemir.skillsmobs.SkillsMobs;
import ru.astemir.skillsmobs.common.items.ItemReptileHat;

public class ReptileHatWrapper extends SkillsWrapperArmor<ItemReptileHat> {

    private ReptileHatModel model = new ReptileHatModel();

    @Override
    public SkillsModel<ItemReptileHat, DisplayArgumentArmor> getModel(ItemReptileHat target) {
        return model;
    }

    public class ReptileHatModel extends SkillsModel<ItemReptileHat, DisplayArgumentArmor> {
        private static final ResourceLocation TEXTURE = new ResourceLocation(SkillsMobs.MOD_ID,"textures/entity/reptile/hat.png");
        private static final ResourceLocation MODEL = new ResourceLocation(SkillsMobs.MOD_ID,"geo/item/reptile_hat.geo.json");
        public ReptileHatModel() {
            super(MODEL);
        }

        @Override
        public ResourceLocation getTexture(ItemReptileHat target) {
            return TEXTURE;
        }
    }
}
