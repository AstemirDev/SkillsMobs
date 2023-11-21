package ru.astemir.skillsmobs.common.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.astemir.api.client.display.IDisplayArgument;
import org.astemir.api.common.animation.Animation;
import org.astemir.api.common.animation.AnimationFactory;
import org.astemir.api.common.animation.objects.IAnimatedEntity;
import org.astemir.api.common.misc.ICustomRendered;
import org.astemir.api.math.components.Vector3;

import java.util.EnumSet;
import java.util.UUID;

public class EntityGhostHoglin extends PathfinderMob implements ICustomRendered,IAnimatedEntity {

    public static final Animation ANIMATION_IDLE = new Animation("animation.model.run",0.48f).loop().layer(0).smoothness(1);
    private AnimationFactory factory = new AnimationFactory(this,ANIMATION_IDLE);
    private int limitedLifeTicks = 100;
    private Vec3 direction;
    private int hurtDelay = 0;
    private UUID rider;


    public EntityGhostHoglin(EntityType<? extends PathfinderMob> p_21683_, Level p_21684_) {
        super(p_21683_, p_21684_);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(0,new RunGoal());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (direction != null) {
            CompoundTag rushDirNbt = new CompoundTag();
            rushDirNbt.putDouble("X", direction.x);
            rushDirNbt.putDouble("Y", direction.y);
            rushDirNbt.putDouble("Z", direction.z);
            tag.put("RushDirection",rushDirNbt);
        }
        if (rider != null){
            tag.putUUID("Rider",uuid);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("RushDirection")) {
            CompoundTag rushDirNbt = tag.getCompound("RushDirection");
            direction = new Vec3(rushDirNbt.getDouble("X"),rushDirNbt.getDouble("Y"),rushDirNbt.getDouble("Z"));
        }
        if (tag.contains("Rider")){
            rider = tag.getUUID("Rider");
        }
    }

    @Override
    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
        factory.play(ANIMATION_IDLE);
        if (--this.limitedLifeTicks <= 0) {
            this.limitedLifeTicks = 20;
            remove(RemovalReason.KILLED);
        }
        if (hurtDelay > 0){
            hurtDelay--;
        }
        if (getFirstPassenger() != null) {
            BlockHitResult res = getHitResult(level, ClipContext.Fluid.NONE);
            BlockPos pos = res.getBlockPos();
            if (pos != null) {
                if (level.getBlockState(res.getBlockPos()).isSolidRender(level, pos)) {
                    getFirstPassenger().stopRiding();
                }
            }
        }
        level.getEntitiesOfClass(LivingEntity.class,getBoundingBox(),(e)->{
           if (!(e instanceof EntityPiglinShaman) && !(e instanceof EntityGhostHoglin) && !e.getUUID().equals(rider)){
               if (hurtDelay <= 0) {
                   e.hurt(DamageSource.mobAttack(this), 10);
                   strongKnockback(e);
                   hurtDelay = 20;
               }
           }
           return false;
        });
    }


    private BlockHitResult getHitResult(Level p_41436_, ClipContext.Fluid p_41438_) {
        float f = getXRot();
        float f1 = getYRot();
        Vec3 vec3 = getEyePosition();
        float f2 = Mth.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f3 = Mth.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
        float f4 = -Mth.cos(-f * ((float)Math.PI / 180F));
        float f5 = Mth.sin(-f * ((float)Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = 2;
        Vec3 vec31 = vec3.add((double)f6 * d0, (double)f5 * d0, (double)f7 * d0);
        return p_41436_.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, p_41438_, this));
    }

    private void strongKnockback(Entity entity) {
        Vector3 vector = new Vector3(getX(),getY(),getZ()).direction(new Vector3(entity.getX(),entity.getY(),entity.getZ()));
        entity.push(vector.x, 0.2D,vector.z);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity p_20303_) {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }


    public UUID getRider() {
        return rider;
    }

    public void setRider(UUID rider) {
        this.rider = rider;
    }

    public void setDirection(Vec3 direction) {
        this.direction = direction;
    }

    @Override
    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        return false;
    }

    @Override
    public <K extends IDisplayArgument> AnimationFactory getAnimationFactory(K argument) {
        return factory;
    }

    private class RunGoal extends Goal {

        public RunGoal() {
            setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return true;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (direction != null) {
                setDeltaMovement(direction);
            }
        }
    }
}
