package ru.astemir.skillsmobs.common.entity;


import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.astemir.api.client.display.IDisplayArgument;
import org.astemir.api.common.action.Action;
import org.astemir.api.common.action.ActionController;
import org.astemir.api.common.action.ActionStateMachine;
import org.astemir.api.common.animation.Animation;
import org.astemir.api.common.animation.AnimationFactory;
import org.astemir.api.common.entity.ISkillsMob;
import org.astemir.api.common.entity.utils.EntityUtils;
import org.astemir.api.common.handler.CustomEvent;
import org.astemir.api.common.handler.CustomEventMap;
import org.astemir.api.math.components.Vector2;
import org.astemir.api.math.components.Vector3;
import org.astemir.api.math.random.RandomUtils;
import org.jetbrains.annotations.Nullable;
import ru.astemir.skillsmobs.common.entity.ai.EntityTask;
import ru.astemir.skillsmobs.common.registry.SMEntities;
import ru.astemir.skillsmobs.common.registry.SMItems;
import ru.astemir.skillsmobs.common.registry.SMSounds;

public class EntityPiglinShaman extends Monster implements ISkillsMob,RangedAttackMob {

    @OnlyIn(Dist.CLIENT)
    public CustomEventMap clientEventMap = new CustomEventMap().
            registerEvent(EVENT_SUMMON_PARTICLES,(entity,level,args)->{
                Vec3 pos = getPosition(0).add(getViewVector(2));
                for (int i = 0; i < 32; ++i) {
                    level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, pos.x + RandomUtils.randomFloat(-1, 1), pos.y + RandomUtils.randomFloat(-1, 1), pos.z + RandomUtils.randomFloat(-1, 1), 0.0D, 0.0D, 0D);
                }
            }).
            registerEvent(EVENT_ANGRY_PARTICLES,(pos,level,args)->{
                for (int i = 0; i < 8; ++i) {
                    level.addParticle(ParticleTypes.ANGRY_VILLAGER, getX() + RandomUtils.randomFloat(-1, 1), getEyeY(), getZ() + RandomUtils.randomFloat(-1, 1), 0.0D, 0.0D, 0D);
                }
            });
    public static final CustomEvent EVENT_SUMMON_PARTICLES = CustomEventMap.createEvent();
    public static final CustomEvent EVENT_ANGRY_PARTICLES = CustomEventMap.createEvent();


    public AnimationFactory animationFactory = new AnimationFactory(this,ANIMATION_IDLE,ANIMATION_WALK,ANIMATION_RUN,ANIMATION_SUMMON,ANIMATION_ATTACK);
    public static Animation ANIMATION_IDLE = new Animation("animation.model.idle",2.08f).loop().layer(0).smoothness(1.5f);
    public static Animation ANIMATION_WALK = new Animation("animation.model.walk",2.08f).loop().layer(0).smoothness(1.15f);
    public static Animation ANIMATION_RUN = new Animation("animation.model.run",0.6f).loop().layer(0).smoothness(1.15f);
    public static Animation ANIMATION_SUMMON = new Animation("animation.model.summon",0.96f).priority(1).layer(1).smoothness(1.15f);
    public static Animation ANIMATION_ATTACK = new Animation("animation.model.attack",0.52f).priority(1).layer(1).smoothness(1.15f);
    public ActionController controller = ActionController.create(this,"actionController",ACTION_THROW_POTION,ACTION_USE_MAGIC);
    public static final Action ACTION_USE_MAGIC = new Action(0,"use_magic",0.76f);
    public static final Action ACTION_THROW_POTION = new Action(1,"throw_potion",0.62f);
    public ActionStateMachine stateMachine = ActionStateMachine.loadControllers(controller);

    private static final EntityDataAccessor<Boolean> ACCELERATED = SynchedEntityData.defineId(EntityPiglinShaman.class,EntityDataSerializers.BOOLEAN);

    private EntityTask hoglinTask = new EntityTask(this,300) {
        @Override
        public void run() {
            playSound(SMSounds.PIGLIN_SHAMAN_SUMMON.get(), 2, 1);
            controller.playAction(ACTION_USE_MAGIC);
        }
    };
    
    private EntityTask accelerationTask = new EntityTask(this,100) {
        @Override
        public void run() {
            playClientEvent(EVENT_ANGRY_PARTICLES);
            playSound(SMSounds.PIGLIN_SHAMAN_ANGRY.get(), 2, 1);
            addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 4, true, true));
        }
    };

    public EntityPiglinShaman(EntityType<? extends Monster> p_33002_, Level p_33003_) {
        super(p_33002_, p_33003_);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1, new RangedAttackGoal(this, 0.6D, 60, 7.0F));
        goalSelector.addGoal(4,new WaterAvoidingRandomStrollGoal(this,0.6D));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return SMSounds.PIGLIN_SHAMAN_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SMSounds.PIGLIN_SHAMAN_IDLE.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SMSounds.PIGLIN_SHAMAN_DEATH.get();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ACCELERATED,false);
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor p_21686_, MobSpawnType p_21687_) {
        return SMEntities.rollSpawn(8,random,p_21687_);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource p_21385_, int p_21386_, boolean p_21387_) {
        super.dropCustomDeathLoot(p_21385_, p_21386_, p_21387_);
        ItemEntity itementity = this.spawnAtLocation(SMItems.STAFF_OF_VENGEANCE.get());
        if (itementity != null) {
            itementity.setExtendedLifetime();
        }

        ItemEntity itementity2 = this.spawnAtLocation(new ItemStack(SMItems.NETHER_MANA.get(), RandomUtils.randomInt(4,8)));
        if (itementity2 != null) {
            itementity2.setExtendedLifetime();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (EntityUtils.isMoving(this,-0.1f,0.1f)){
            if (entityData.get(ACCELERATED)){
                animationFactory.play(ANIMATION_RUN);
            }else{
                animationFactory.play(ANIMATION_WALK);
            }
        }else{
            animationFactory.play(ANIMATION_IDLE);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (getTarget() != null) {
            accelerationTask.update();
            hoglinTask.update();
        }
    }

    @Override
    public void onActionBegin(Action state) {
        if (state == ACTION_THROW_POTION){
            animationFactory.play(ANIMATION_ATTACK);
        }else
        if (state == ACTION_USE_MAGIC){
            animationFactory.play(ANIMATION_SUMMON);
        }
    }

    @Override
    public void onActionEnd(Action state) {
        if (state == ACTION_THROW_POTION){
            Vec3 vec3 = getTarget().getDeltaMovement();
            double d0 = getTarget().getX() + vec3.x - this.getX();
            double d1 = getTarget().getEyeY() - (double)1.25F - this.getY();
            double d2 = getTarget().getZ() + vec3.z - this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            ThrownPotion thrownpotion = new ThrownPotion(this.level, this);
            thrownpotion.setItem(getItemInHand(InteractionHand.MAIN_HAND));
            thrownpotion.setXRot(thrownpotion.getXRot() - -20.0F);
            thrownpotion.shoot(d0, d1 + d3 * 0.2D, d2, 0.75F, 8.0F);
            if (!this.isSilent()) {
                this.level.playSound((Player) null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW, this.getSoundSource(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
            }
            this.level.addFreshEntity(thrownpotion);
            setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }else
        if (state == ACTION_USE_MAGIC){
            playClientEvent(EVENT_SUMMON_PARTICLES);
            playSound(SMSounds.PIGLIN_SHAMAN_HOGLIN_APPEAR.get(), 2, 1);
            EntityGhostHoglin hoglin = (EntityGhostHoglin) SMEntities.GHOST_HOGLIN.get().create(level);
            Vec3 pos = getPosition(0).add(getViewVector(2));
            Vec3 dir = getPosition(0).subtract(getTarget().getPosition(0)).multiply(-0.75f, 0, -0.75f).normalize();
            Vector2 yawPitch = new Vector3(dir.x, dir.y, dir.z).yawPitchDeg();
            hoglin.setDirection(dir);
            hoglin.moveTo(pos.x, pos.y, pos.z, -yawPitch.x, 0);
            level.addFreshEntity(hoglin);
        }
    }

    @Override
    public void performRangedAttack(LivingEntity p_34143_, float p_34144_) {
        if (controller.isNoAction() && getTarget() != null) {
            Vec3 vec3 = getTarget().getDeltaMovement();
            double d0 = getTarget().getX() + vec3.x - this.getX();
            double d2 = getTarget().getZ() + vec3.z - this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            Potion potion = Potions.STRONG_HARMING;
            if (d3 >= 8.0D && !p_34143_.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                potion = Potions.SLOWNESS;
            } else if (p_34143_.getHealth() >= 8.0F && !p_34143_.hasEffect(MobEffects.POISON)) {
                potion = Potions.POISON;
            } else if (d3 <= 3.0D && !p_34143_.hasEffect(MobEffects.WEAKNESS) && this.random.nextFloat() < 0.25F) {
                potion = Potions.WEAKNESS;
            }
            setItemInHand(InteractionHand.MAIN_HAND, PotionUtils.setPotion(Items.SPLASH_POTION.getDefaultInstance(), potion));
            controller.playAction(ACTION_THROW_POTION);
        }
    }

    @Override
    public boolean addEffect(MobEffectInstance p_147208_, @Nullable Entity p_147209_) {
        if (p_147208_.getEffect().isBeneficial()) {
            return super.addEffect(p_147208_, p_147209_);
        }else{
            return false;
        }
    }

    @Override
    public CustomEventMap clientEventMap() {
        return clientEventMap;
    }

    public void setAccelerated(boolean b){
        entityData.set(ACCELERATED,b);
    }

    @Override
    public ActionStateMachine getActionStateMachine() {
        return stateMachine;
    }

    @Override
    public <K extends IDisplayArgument> AnimationFactory getAnimationFactory(K argument) {
        return animationFactory;
    }
}
