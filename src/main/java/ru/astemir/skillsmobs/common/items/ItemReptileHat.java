package ru.astemir.skillsmobs.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.astemir.api.common.item.SkillsArmorItem;

import javax.annotation.Nullable;
import java.util.List;

public class ItemReptileHat extends SkillsArmorItem{

    public ItemReptileHat() {
        super(ArmorMaterials.LEATHER, EquipmentSlot.HEAD, new Properties().tab(CreativeModeTab.TAB_COMBAT).stacksTo(1).durability(400));
    }

    @Override
    public void appendHoverText(ItemStack p_43043_, @Nullable Level p_43044_, List<Component> p_43045_, TooltipFlag p_43046_) {
        p_43045_.add(Component.literal("Даёт иммунитет к взрывам от своего динамита.").withStyle(ChatFormatting.GRAY));
    }
}
