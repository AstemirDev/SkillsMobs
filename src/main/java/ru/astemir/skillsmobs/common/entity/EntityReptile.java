package ru.astemir.skillsmobs.common.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.network.NetworkHooks;
import org.astemir.api.client.display.IDisplayArgument;
import org.astemir.api.common.action.Action;
import org.astemir.api.common.action.ActionController;
import org.astemir.api.common.action.ActionStateMachine;
import org.astemir.api.common.action.IActionListener;
import org.astemir.api.common.animation.Animation;
import org.astemir.api.common.animation.AnimationFactory;
import org.astemir.api.common.animation.objects.IAnimatedEntity;
import org.astemir.api.common.entity.utils.EntityUtils;
import org.astemir.api.common.misc.ICustomRendered;
import org.astemir.api.math.random.RandomUtils;
import org.jetbrains.annotations.Nullable;
import ru.astemir.skillsmobs.common.entity.ai.EntityTask;
import ru.astemir.skillsmobs.common.registry.SMEntities;
import ru.astemir.skillsmobs.common.registry.SMItems;
import ru.astemir.skillsmobs.common.registry.SMSounds;

import java.util.EnumSet;


public class EntityReptile extends Monster implements IAnimatedEntity, IActionListener, ICustomRendered {

    public AnimationFactory animationFactory = new AnimationFactory(this,ANIMATION_IDLE,ANIMATION_WALK,ANIMATION_RUN,ANIMATION_ATTACK);
    public static Animation ANIMATION_IDLE = new Animation("animation.model.idle",3.12f).loop().layer(0).smoothness(1.25f);
    public static Animation ANIMATION_WALK = new Animation("animation.model.walk",2.08f).loop().layer(0).smoothness(1);
    public static Animation ANIMATION_RUN = new Animation("animation.model.run",0.64f).loop().layer(0).smoothness(1);
    public static Animation ANIMATION_ATTACK = new Animation("animation.model.attack",0.88f).layer(0).smoothness(1);
    public ActionController controller = ActionController.create(this,"actionController",ACTION_ATTACK);
    public static final Action ACTION_ATTACK = new Action(0,"swing",0.75f);
    private ActionStateMachine stateMachine = ActionStateMachine.loadControllers(controller);

    private static final EntityDataAccessor<Boolean> INVISIBLE = SynchedEntityData.defineId(EntityReptile.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ACCELERATED = SynchedEntityData.defineId(EntityReptile.class,EntityDataSerializers.BOOLEAN);

    public EntityReptile(EntityType<? extends Monster> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1,new WhirlAroundPlayer());
        goalSelector.addGoal(3,new WaterAvoidingRandomStrollGoal(this,0.4D));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        ANIMATION_WALK.smoothness(1.25f);
        if (!controller.is(ACTION_ATTACK)) {
            if (EntityUtils.isMoving(this, -0.1f, 0.1f)) {
                if (entityData.get(ACCELERATED)) {
                    animationFactory.play(ANIMATION_RUN);
                } else {
                    animationFactory.play(ANIMATION_WALK);
                }
            } else {
                animationFactory.play(ANIMATION_IDLE);
            }
        }
    }

    @Override
    public void onActionBegin(Action state) {
        if (state == ACTION_ATTACK){
            animationFactory.play(ANIMATION_ATTACK);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(INVISIBLE,false);
        entityData.define(ACCELERATED,false);
    }


    @Override
    public boolean checkSpawnRules(LevelAccessor p_21686_, MobSpawnType p_21687_) {
        return SMEntities.rollSpawn(8,getRandom(),p_21687_);
    }

    public static boolean checkSpawnRules(EntityType<EntityReptile> p_32896_, ServerLevelAccessor p_32897_, MobSpawnType p_32898_, BlockPos p_32899_, RandomSource p_32900_) {
        return p_32897_.canSeeSky(p_32899_);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource p_21385_, int p_21386_, boolean p_21387_) {
        super.dropCustomDeathLoot(p_21385_, p_21386_, p_21387_);
        if (random.nextInt(8) == 0) {
            ItemEntity itementity = this.spawnAtLocation(SMItems.REPTILE_HAT.get());
            if (itementity != null) {
                itementity.setExtendedLifetime();
            }
        }

        ItemEntity itementity = this.spawnAtLocation(SMItems.REPTILE_SKIN.get());
        if (itementity != null) {
            itementity.setExtendedLifetime();
        }

        ItemEntity itementity2 = this.spawnAtLocation(new ItemStack(SMItems.HAND_DYNAMITE.get(), RandomUtils.randomInt(2,4)));
        if (itementity2 != null) {
            itementity2.setExtendedLifetime();
        }
    }


    @Override
    public void onActionEnd(Action state) {
        if (state == ACTION_ATTACK && getTarget() != null){
            playAmbientSound();
            EntityHandDynamite snowball = new EntityHandDynamite(SMEntities.HAND_DYNAMITE.get(), EntityReptile.this,level);
            double d0 = getTarget().getEyeY() - (double)1.1F;
            double d1 = getTarget().getX() - getX();
            double d2 = d0 - snowball.getY();
            double d3 = getTarget().getZ() - getZ();
            double d4 = Math.sqrt(d1 * d1 + d3 * d3) * (double)0.2F;
            snowball.shoot(d1, d2 + d4, d3, 1.2F, 12.0F);
            playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 0.4F / (getRandom().nextFloat() * 0.4F + 0.8F));
            level.addFreshEntity(snowball);
        }
    }

    public void turnInvisible(){
        setReptileInvisible(true);
        playSound(SMSounds.REPTILE_ABILITY.get(),0.25f, RandomUtils.randomFloat(0.9f,1.25f));
    }

    public void turnVisible(){
        setReptileInvisible(false);
        playSound(SMSounds.REPTILE_ABILITY.get(),0.25f, RandomUtils.randomFloat(0.75f,0.85f));
    }

    public boolean isReptileInvisible(){
        return entityData.get(INVISIBLE);
    }

    public void setReptileInvisible(boolean b){
        this.entityData.set(INVISIBLE,b);
    }

    public void setAccelerated(boolean b){
        entityData.set(ACCELERATED,b);
    }


    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SMSounds.REPTILE_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return SMSounds.REPTILE_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SMSounds.REPTILE_DEATH.get();
    }

    @Override
    public <K extends IDisplayArgument> AnimationFactory getAnimationFactory(K argument) {
        return animationFactory;
    }

    @Override
    public ActionStateMachine getActionStateMachine() {
        return stateMachine;
    }


    public class WhirlAroundPlayer extends Goal{


        private EntityTask attackTask = new EntityTask(EntityReptile.this,120) {
            @Override
            public void run() {
                invisibilityEnableTask.restart();
                controller.playAction(ACTION_ATTACK);
                navigation.stop();
            }
        };

        private EntityTask prepareAttackTask = new EntityTask(EntityReptile.this,100) {
            @Override
            public void run() {
                if (isReptileInvisible()) {
                    turnVisible();
                }
                getLookControl().setLookAt(getTarget());
            }
        };


        private EntityTask invisibilityEnableTask = new EntityTask(EntityReptile.this,20) {
            @Override
            public void run() {
                turnInvisible();
                getLookControl().setLookAt(getTarget());
            }
        }.selfCancel().cancelledAtBeginning();


        private int ticks = 0;

        public WhirlAroundPlayer() {
            setFlags(EnumSet.of(Flag.LOOK,Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return getTarget() != null;
        }

        @Override
        public boolean canContinueToUse() {
            return getTarget() != null;
        }

        @Override
        public void stop() {
            super.stop();
            setAccelerated(false);
            setReptileInvisible(false);
        }



        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = getTarget();
            if (target != null) {
                if (distanceTo(target) > 15) {
                    getNavigation().moveTo(target.getX(), target.getY(), target.getZ(), 0.8f);
                } else {
                    float speed = 0.8f;
                    boolean nearPlayer = distanceTo(target) <= 4;
                    if (nearPlayer) {
                        speed = 0.9f;
                        ticks = 0;
                        getNavigation().stop();
                    }
                    prepareAttackTask.update();
                    attackTask.update();
                    invisibilityEnableTask.update();
                    if (getNavigation().isDone() && controller.isNoAction()) {
                        double angle = ticks * 30;
                        double destX = target.getX() + 8 * Math.cos(angle);
                        double destZ = target.getZ() + 8 * Math.sin(angle);
                        getNavigation().moveTo(destX, target.getY(), destZ, speed);
                        ticks++;
                    }
                }
                setAccelerated(true);
                getLookControl().setLookAt(target);
            }
        }
    }


    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
