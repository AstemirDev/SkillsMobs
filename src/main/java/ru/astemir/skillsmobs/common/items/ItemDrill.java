package ru.astemir.skillsmobs.common.items;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent;
import org.astemir.api.common.entity.utils.EntityUtils;
import org.astemir.api.common.world.WorldUtils;
import org.astemir.api.math.components.Vector3;
import org.astemir.api.math.random.RandomUtils;
import ru.astemir.skillsmobs.common.entity.EntityHandDynamite;
import ru.astemir.skillsmobs.common.registry.SMEntities;

public class ItemDrill extends Item {


    public ItemDrill() {
        super(new Properties().stacksTo(1).tab(CreativeModeTab.TAB_TOOLS));
    }

    public InteractionResultHolder<ItemStack> use(Level p_40703_, Player p_40704_, InteractionHand p_40705_) {
        ItemStack itemstack = p_40704_.getItemInHand(p_40705_);
        BlockHitResult blockhitresult = getPlayerPOVHitResult(p_40703_, p_40704_, ClipContext.Fluid.NONE);
        InteractionResultHolder<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(p_40704_, p_40703_, itemstack, blockhitresult);
        if (ret != null) return ret;
        if (blockhitresult.getType() == HitResult.Type.MISS) {
            if (!p_40704_.getCooldowns().isOnCooldown(itemstack.getItem())) {
                CompoundTag tag = itemstack.getTag();
                if (tag == null) {
                    tag = new CompoundTag();
                }
                int power = 0;
                if (tag.contains("Power")) {
                    power = tag.getInt("Power")+1;
                }
                if (power > 10){
                    power = 1;
                }
                tag.putInt("Power", power);
                itemstack.setTag(tag);
                p_40704_.displayClientMessage(Component.literal("Power is: " + power),true);
                p_40704_.getCooldowns().addCooldown(this,40);
            }
            return InteractionResultHolder.pass(itemstack);
        } else if (blockhitresult.getType() == HitResult.Type.BLOCK) {
            CompoundTag tag = itemstack.getTag();
            if (tag == null){
                tag = new CompoundTag();
            }
            int power = 0;
            if (tag.contains("Power")){
                power = tag.getInt("Power");
            }
            if (!p_40704_.level.isClientSide) {
                int j1 = Mth.floor(blockhitresult.getBlockPos().getY());
                int i2 = Mth.floor(blockhitresult.getBlockPos().getX());
                int j2 = Mth.floor(blockhitresult.getBlockPos().getZ());
                boolean flag = false;
                for (int j = 0; j <= power; ++j) {
                    for (int k2 = (int) 0; k2 <= power; ++k2) {
                        for (int k = 0; k <= power; ++k) {
                            int l2 = i2 + j;
                            int l = j1 + k;
                            int i1 = j2 + k2;
                            BlockPos blockpos = new BlockPos(l2, l, i1);
                            BlockState state = p_40703_.getBlockState(blockpos);
                            flag = p_40704_.level.destroyBlock(blockpos, true, p_40704_) || flag;
                            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(p_40703_, blockpos, state, p_40704_);
                            MinecraftForge.EVENT_BUS.post(event);
                        }
                    }
                }
                if (flag) {
                    p_40704_.level.levelEvent((Player) null, 1022, p_40704_.blockPosition(), 0);
                }
                return InteractionResultHolder.pass(itemstack);
            }
        }
        return InteractionResultHolder.fail(itemstack);
    }

}
