package ru.astemir.skillsmobs.common.items;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.astemir.api.client.render.SkillsRendererItem;
import org.astemir.api.common.misc.ICustomRendered;
import org.astemir.api.math.components.Vector2;
import org.astemir.api.math.components.Vector3;
import org.astemir.api.math.random.RandomUtils;
import ru.astemir.skillsmobs.common.entity.EntityGhostHoglin;
import ru.astemir.skillsmobs.common.registry.SMEntities;
import ru.astemir.skillsmobs.common.registry.SMItems;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ItemStaffOfVengeance extends ProjectileWeaponItem implements ICustomRendered {

    public ItemStaffOfVengeance() {
        super(new Properties().tab(CreativeModeTab.TAB_COMBAT).stacksTo(1).durability(30));
    }


    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return SkillsRendererItem.INSTANCE;
            }
        });
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level p_43142_, Player p_43143_, InteractionHand p_43144_) {
        ItemStack itemstack = p_43143_.getItemInHand(p_43144_);
        boolean flag = !p_43143_.getProjectile(itemstack).isEmpty() || p_43143_.isCreative();
        if (!p_43143_.getCooldowns().isOnCooldown(this) && flag) {
            ItemStack ammo = p_43143_.getProjectile(itemstack);
            if (!ammo.isEmpty()){
                ammo.shrink(1);
            }
            EntityGhostHoglin hoglin = SMEntities.GHOST_HOGLIN.get().create(p_43142_);
            Vec3 pos = p_43143_.getPosition(0).add(p_43143_.getViewVector(2));
            Vec3 dir = p_43143_.getViewVector(0).multiply(0.75f, 0, 0.75f).normalize();
            Vector2 yawPitch = new Vector3(dir.x, dir.y, dir.z).yawPitchDeg();
            hoglin.setDirection(dir);
            hoglin.setRider(p_43143_.getUUID());
            hoglin.moveTo(pos.x, pos.y, pos.z, -yawPitch.x, 0);
            p_43142_.addFreshEntity(hoglin);
            p_43143_.startRiding(hoglin, true);
            p_43143_.getCooldowns().addCooldown(this,400);
            for (int i = 0; i < 32; ++i) {
                p_43142_.addParticle(ParticleTypes.SOUL_FIRE_FLAME, pos.x + RandomUtils.randomFloat(-2,2), pos.y + RandomUtils.randomFloat(-2,2), pos.z + RandomUtils.randomFloat(-2,2), 0.0D, 0.0D, 0D);
            }
            itemstack.hurtAndBreak(1,p_43143_,(player)->player.broadcastBreakEvent(p_43144_));
            p_43142_.playSound(null,pos.x,pos.y,pos.z, SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.PLAYERS,1,1);
            return InteractionResultHolder.sidedSuccess(itemstack, p_43142_.isClientSide());
        }
        return InteractionResultHolder.fail(itemstack);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return (e)->e.is(SMItems.NETHER_MANA.get());
    }

    @Override
    public int getDefaultProjectileRange() {
        return 0;
    }
}
