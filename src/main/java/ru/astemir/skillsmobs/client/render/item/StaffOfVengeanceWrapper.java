package ru.astemir.skillsmobs.client.render.item;


import net.minecraft.resources.ResourceLocation;
import org.astemir.api.client.display.DisplayArgumentItem;
import org.astemir.api.client.model.SkillsModel;
import org.astemir.api.client.wrapper.SkillsWrapperItem;
import ru.astemir.skillsmobs.SkillsMobs;
import ru.astemir.skillsmobs.common.items.ItemStaffOfVengeance;

public class StaffOfVengeanceWrapper extends SkillsWrapperItem<ItemStaffOfVengeance> {

    private StaffOfVengeanceModel model = new StaffOfVengeanceModel();


    @Override
    public SkillsModel<ItemStaffOfVengeance, DisplayArgumentItem> getModel(ItemStaffOfVengeance target) {
        return model;
    }

    public class StaffOfVengeanceModel extends SkillsModel<ItemStaffOfVengeance, DisplayArgumentItem> {

        private static final ResourceLocation TEXTURE = new ResourceLocation(SkillsMobs.MOD_ID,"textures/entity/piglin_shaman.png");
        private static final ResourceLocation MODEL = new ResourceLocation(SkillsMobs.MOD_ID,"geo/item/staff_of_vengeance.geo.json");

        public StaffOfVengeanceModel() {
            super(MODEL);
        }

        @Override
        public ResourceLocation getTexture(ItemStaffOfVengeance target) {
            return TEXTURE;
        }
    }
}
