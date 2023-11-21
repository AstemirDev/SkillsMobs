package ru.astemir.skillsmobs.client;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import ru.astemir.skillsmobs.SkillsMobs;

public class RenderMaterials {

    public static final Material LOCATION_SCALE_SHIELD = material("item/scale_shield");
    public static final Material LOCATION_STAFF_OF_VENGEANCE = material("item/staff_of_vengeance_icon");


    @SuppressWarnings("deprecation")
    private static Material material(String path) {
        return new Material(
                TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(SkillsMobs.MOD_ID, path));
    }
}