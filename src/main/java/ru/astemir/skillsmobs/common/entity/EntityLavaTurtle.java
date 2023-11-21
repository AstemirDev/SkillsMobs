package ru.astemir.skillsmobs.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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
import org.astemir.api.common.world.WorldUtils;
import org.astemir.api.math.components.Vector3;
import org.astemir.api.math.random.RandomUtils;
import org.jetbrains.annotations.Nullable;
import ru.astemir.skillsmobs.common.entity.ai.goals.ConditionalMeleeAttackGoal;
import ru.astemir.skillsmobs.common.entity.ai.goals.ConditionalWAStrollGoal;
import ru.astemir.skillsmobs.common.entity.ai.goals.IrritatingTargetGoal;
import ru.astemir.skillsmobs.common.registry.SMEntities;
import ru.astemir.skillsmobs.common.registry.SMSounds;


import java.util.List;

public class EntityLavaTurtle extends Animal implements ICustomRendered, IAnimatedEntity, IActionListener {


    public AnimationFactory animationFactory = new AnimationFactory(this,ANIMATION_IDLE,ANIMATION_WALK,ANIMATION_IDLE_INSIDE,ANIMATION_ATTACK,ANIMATION_HIDE,ANIMATION_GET_OUT);
    public static Animation ANIMATION_IDLE = new Animation("animation.model.idle",3.12f).loop().layer(0).smoothness(2);
    public static Animation ANIMATION_WALK = new Animation("animation.model.walk",2.08f).loop().layer(0).smoothness(1.25f);
    public static Animation ANIMATION_IDLE_INSIDE = new Animation("animation.model.idle_inside",0.8f).loop().layer(0).smoothness(2);
    public static Animation ANIMATION_ATTACK = new Animation("animation.model.attack",0.4f).layer(1).priority(1).smoothness(1.25f).speed(0.75f);
    public static Animation ANIMATION_HIDE = new Animation("animation.model.hide",0.44f).layer(0).loop(Animation.Loop.HOLD_ON_LAST_FRAME).smoothness(1);
    public static Animation ANIMATION_GET_OUT = new Animation("animation.model.getout",0.4f).layer(0).smoothness(1);
    public ActionController controller = ActionController.create(this,"actionController",ACTION_HIDE,ACTION_SHOW,ACTION_SPIN,ACTION_SPIN_FAST,ACTION_SPIN_VERY_FAST,ACTION_ATTACK);
    public static final Action ACTION_HIDE = new Action(0,"hide",0.44f);
    public static final Action ACTION_SHOW = new Action(1,"show",0.4f);
    public static final Action ACTION_SPIN = new Action(2,"spin",50);
    public static final Action ACTION_SPIN_FAST = new Action(3,"spin_fast",50);
    public static final Action ACTION_SPIN_VERY_FAST = new Action(4,"spin_very_fast",50);
    public static final Action ACTION_ATTACK = new Action(5,"attack",0.4f);

    public ActionStateMachine stateMachine = ActionStateMachine.loadControllers(controller);

    private static final EntityDataAccessor<Boolean> IN_SHELL = SynchedEntityData.defineId(EntityLavaTurtle.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CAN_SPIN = SynchedEntityData.defineId(EntityLavaTurtle.class, EntityDataSerializers.BOOLEAN);


    public EntityLavaTurtle(EntityType<? extends Animal> p_27557_, Level p_27558_) {
        super(p_27557_, p_27558_);
        moveControl = new MoveControl(this){
            @Override
            public double getSpeedModifier() {
                if (isInShell()){
                    return 0;
                }
                return super.getSpeedModifier();
            }
        };
    }

    @Override
    public float getStepHeight() {
        return 1;
    }


    protected void registerGoals() {
        this.goalSelector.addGoal(1, new ConditionalMeleeAttackGoal(this, 0.5D, true,()->!isInShell()));
        this.goalSelector.addGoal(7, new ConditionalWAStrollGoal(this, 0.4,()->!isInShell()));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new IrritatingTargetGoal(this,LivingEntity.class,true,(e)->!(e instanceof EntityLavaTurtle)));
    }

    public static boolean checkSpawnRules(EntityType<EntityLavaTurtle> p_34450_, ServerLevelAccessor p_34451_, MobSpawnType p_34452_, BlockPos p_34453_, RandomSource p_34454_) {
        return p_34451_.getDifficulty() != Difficulty.PEACEFUL && Monster.isDarkEnoughToSpawn(p_34451_, p_34453_, p_34454_) && checkMobSpawnRules(p_34450_,p_34451_,p_34452_,p_34453_,p_34454_);
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor p_21686_, MobSpawnType p_21687_) {
        return SMEntities.rollSpawn(8,random,p_21687_);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(IN_SHELL,false);
        entityData.define(CAN_SPIN,true);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return null;
    }

    @Override
    public void onActionEnd(Action state) {
        if (state == ACTION_HIDE){
            setInShell(true);
        }else
        if (state == ACTION_SHOW){
            setInShell(false);
        }else
        if (state == ACTION_SPIN_FAST){
            controller.playAction(ACTION_SPIN);
        }else
        if (state == ACTION_SPIN_VERY_FAST){
            controller.playAction(ACTION_SHOW);
            setCanSpin(false);
        }
    }


    @Override
    public boolean doHurtTarget(Entity p_21372_) {
        if (controller.isNoAction() && !isInShell()){
            controller.playAction(ACTION_ATTACK);
            playSound(SMSounds.LAVA_TURTLE_ATTACK.get(),1, RandomUtils.randomFloat(0.9f,1.25f));
        }
        return super.doHurtTarget(p_21372_);
    }

    @Override
    public boolean hurt(DamageSource p_27567_, float p_27568_) {
        if (p_27567_.getDirectEntity() instanceof  AbstractArrow || p_27567_.isFall() || p_27567_.equals(DamageSource.CRAMMING) || p_27567_.equals(DamageSource.IN_WALL) || p_27567_.equals(DamageSource.IN_FIRE) || p_27567_.equals(DamageSource.ON_FIRE) || p_27567_.equals(DamageSource.LAVA) || p_27567_.equals(DamageSource.HOT_FLOOR)){
            return false;
        }
        if (isInShell()){
            if (p_27567_.getEntity() != null) {
                p_27568_ = 1;
                if (isCanSpin()) {
                    if (controller.isNoAction()){
                        spin(SpinPower.WEAK, 200);
                    }else
                    if (controller.is(ACTION_SPIN)){
                        spin(SpinPower.NORMAL, 200);
                    }else
                    if (controller.is(ACTION_SPIN_FAST)){
                        spin(SpinPower.STRONG, 100);
                        Vec3 dir = getViewVector(1).multiply(-2, 0, 2).normalize();
                        setDeltaMovement(dir.x, 0, dir.y);
                    }
                }
            }
        }
        if (getHealth() <= getMaxHealth()/2){
            if (controller.isNoAction() || controller.is(ACTION_ATTACK)) {
                if (!isInShell()) {
                    controller.playAction(ACTION_HIDE);
                }
            }
        }
        return super.hurt(p_27567_, p_27568_);
    }

    @Override
    public InteractionResult mobInteract(Player p_27584_, InteractionHand p_27585_) {
        if (isDeadOrDying()){
            return InteractionResult.PASS;
        }
        ItemStack item = p_27584_.getItemInHand(p_27585_);
        if (!item.isEmpty() && item.is(Items.WARPED_FUNGUS) || item.is(Items.CRIMSON_FUNGUS)) {
            if (getHealth() < getMaxHealth()) {
                ParticleOptions particleoptions = ParticleTypes.HAPPY_VILLAGER;
                for (int i = 0; i < 7; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    this.level.addParticle(particleoptions, this.getRandomX(0.5D), this.getRandomY() + 0.5D, this.getRandomZ(0.5D), d0, d1, d2);
                }
                setHealth(getHealth()+2);
                item.shrink(1);
                this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }else{
                if (!isCanSpin()){
                    ParticleOptions particleoptions = ParticleTypes.HEART;
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        this.level.addParticle(particleoptions, this.getRandomX(0.5D), this.getRandomY() + 0.5D, this.getRandomZ(0.5D), d0, d1, d2);
                    }
                    setCanSpin(true);
                    item.shrink(1);
                    setHealth(getHealth()+2);
                    this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                    return InteractionResult.sidedSuccess(this.level.isClientSide);
                }
            }
        }
        return super.mobInteract(p_27584_, p_27585_);
    }

    @Override
    public void tick() {
        super.tick();
        if (!animationFactory.isPlaying(ANIMATION_HIDE,ANIMATION_GET_OUT)) {
            if (isInShell()) {
                if (!controller.is(ACTION_SPIN,ACTION_SPIN_FAST,ACTION_SPIN_VERY_FAST)){
                    animationFactory.play(ANIMATION_IDLE);
                }
            } else {
                if (EntityUtils.isMoving(this, -0.01f, 0.01f)) {
                    animationFactory.play(ANIMATION_WALK);
                }else{
                    animationFactory.play(ANIMATION_IDLE);
                }
            }
        }
    }

    @Override
    public void onActionBegin(Action state) {
        if (state == ACTION_HIDE){
            animationFactory.play(ANIMATION_HIDE);
        }else
        if (state == ACTION_SHOW){
            animationFactory.stop(ANIMATION_HIDE);
            animationFactory.play(ANIMATION_IDLE);
        }else
        if (state == ACTION_ATTACK){
            animationFactory.play(ANIMATION_ATTACK);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        Action state = controller.getActionState();
        if (isInWater()){
            hurt(DamageSource.GENERIC,5);
        }
        if (tickCount % 40 == 0) {
            if (this.level.isClientSide) {
                for(int i = 0; i < 2; ++i) {
                    this.level.addParticle(ParticleTypes.LAVA, this.getRandomX(0.5D), this.getRandomY() - 0.25D, this.getRandomZ(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
                }
            }
        }
        if (state == ACTION_SPIN || state == ACTION_SPIN_FAST || state == ACTION_SPIN_VERY_FAST){
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(0.5f), (e) -> {
                if (!(e instanceof EntityLavaTurtle)){
                    return true;
                }
                return false;
            });
            if (!entities.isEmpty()) {
                for (LivingEntity entity : entities) {
                    strongHit(entity);
                }
            }
        }
        if (controller.isNoAction()) {
            if (isInShell()) {
                if (getHealth() > getMaxHealth() / 2) {
                    controller.playAction(ACTION_SHOW);
                    if (getHealth() > 15) {
                        setHealth(15);
                    }
                }
            }
        }
        if (state == ACTION_SPIN){
            if (tickCount % 10 == 0) {
                playSound(SMSounds.LAVA_TURTLE_SPIN.get(), 0.75f, 1);
            }
        }
        if (state == ACTION_SPIN_FAST){
            if (tickCount % 5 == 0) {
                playSound(SMSounds.LAVA_TURTLE_SPIN.get(), 0.75f, 1.25f);
            }
        }
        if (state == ACTION_SPIN_VERY_FAST) {
            if (tickCount % 2 == 0) {
                playSound(SMSounds.LAVA_TURTLE_SPIN.get(), 0.75f, 1.5f);
            }
            if (tickCount % 5 == 0) {
                Vec3 dir = getViewVector(0).multiply(-1,0,-1).add(RandomUtils.randomFloat(-0.15f, 0.15f),0,RandomUtils.randomFloat(-0.15f, 0.15f)).normalize();
                setDeltaMovement(dir.x, 0, dir.z);
                if (net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(level, this) && !this.level.isClientSide) {
                    int j1 = Mth.floor(this.getY());
                    int i2 = Mth.floor(this.getX());
                    int j2 = Mth.floor(this.getZ());
                    boolean flag = false;
                    for (int j = (int) -2; j <= 2; ++j) {
                        for (int k2 = (int) -2; k2 <= 2; ++k2) {
                            for (int k = 0; k <= 1; ++k) {
                                int l2 = i2 + j;
                                int l = j1 + k;
                                int i1 = j2 + k2;
                                BlockPos blockpos = new BlockPos(l2, l, i1);
                                BlockState blockstate = this.level.getBlockState(blockpos);
                                if (!blockstate.isAir() && !blockstate.is(BlockTags.WITHER_IMMUNE) && blockstate.getFluidState().isEmpty() && net.minecraftforge.event.ForgeEventFactory.onEntityDestroyBlock(this, blockpos, blockstate)) {
                                    if (RandomUtils.doWithChance(75)) {
                                        flag = this.level.destroyBlock(blockpos, true, this) || flag;
                                    }
                                }
                            }
                        }
                    }
                    if (flag) {
                        this.level.levelEvent((Player) null, 1022, this.blockPosition(), 0);
                    }
                }
                //EntityUtils.breakNearbyBlocks(this,new Vector3(2,2,2),0,75);
            }
        }
    }


    private void strongHit(Entity entity) {
        Vector3 vector = new Vector3(RandomUtils.randomFloat(-1f,1f),0,RandomUtils.randomFloat(-1f,1f));
        entity.push(vector.x, 0.8D,vector.z);
        entity.hurt(DamageSource.mobAttack(this),RandomUtils.randomFloat(10,15));
    }

    public void spin(SpinPower power, int ticks){
        switch (power){
            case WEAK -> controller.playAction(ACTION_SPIN,ticks);
            case NORMAL -> controller.playAction(ACTION_SPIN_FAST,ticks);
            case STRONG -> controller.playAction(ACTION_SPIN_VERY_FAST,ticks);
        }
    }

    public void setCanSpin(boolean b){
        entityData.set(CAN_SPIN,b);
    }

    public void setInShell(boolean b){
        entityData.set(IN_SHELL,b);
    }

    public boolean isInShell(){
        return entityData.get(IN_SHELL);
    }

    public boolean isCanSpin(){
        return entityData.get(CAN_SPIN);
    }


    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SMSounds.LAVA_TURTLE_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        if (isInShell()) {
            return SMSounds.LAVA_TURTLE_SHELL_HIT.get();
        }
        return SMSounds.LAVA_TURTLE_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SMSounds.LAVA_TURTLE_DEATH.get();
    }

    @Override
    public ActionStateMachine getActionStateMachine() {
        return stateMachine;
    }

    @Override
    public <K extends IDisplayArgument> AnimationFactory getAnimationFactory(K argument) {
        return animationFactory;
    }


    public enum SpinPower{
        WEAK,NORMAL,STRONG
    }
}
