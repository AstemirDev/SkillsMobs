package ru.astemir.skillsmobs.common.items;


import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import ru.astemir.skillsmobs.client.render.item.ShieldItemRenderer;

import java.util.function.Consumer;

public class ItemScaleShield extends ShieldItem {

    public ItemScaleShield() {
        super(new Properties().durability(136).tab(CreativeModeTab.TAB_COMBAT));
    }


    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return ShieldItemRenderer.instance;
            }
        });
    }
}
