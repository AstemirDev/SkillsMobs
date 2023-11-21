package ru.astemir.skillsmobs.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
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
import org.astemir.api.math.random.RandomUtils;
import org.astemir.api.math.random.WeightedRandom;
import org.jetbrains.annotations.Nullable;
import ru.astemir.skillsmobs.common.entity.ai.EntityTask;
import ru.astemir.skillsmobs.common.registry.SMEntities;
import ru.astemir.skillsmobs.common.registry.SMItems;
import ru.astemir.skillsmobs.common.registry.SMSounds;
import ru.astemir.skillsmobs.utils.WorldExtraUtils;

import java.util.List;
import java.util.Random;

public class EntityPenguin extends Animal implements IAnimatedEntity, IActionListener, ICustomRendered {

    public AnimationFactory animationFactory = new AnimationFactory(this,ANIMATION_IDLE,ANIMATION_WALK,ANIMATION_SLIDE,ANIMATION_SWIM,ANIMATION_HELLO,ANIMATION_HELLO_SMILE);
    public static Animation ANIMATION_IDLE = new Animation("animation.model.idle",2.08f).loop().layer(0).smoothness(1.5f);
    public static Animation ANIMATION_WALK = new Animation("animation.model.walk",1.88f).loop().layer(0).smoothness(1f);
    public static Animation ANIMATION_SLIDE = new Animation("animation.model.slide",0.4f).loop().layer(0).smoothness(1.25f);
    public static Animation ANIMATION_SWIM = new Animation("animation.model.swim",2.08f).loop().layer(0).smoothness(1.15f);
    public static Animation ANIMATION_HELLO = new Animation("animation.model.hello",1.36f).layer(1).priority(1).smoothness(1.15f);
    public static Animation ANIMATION_HELLO_SMILE = new Animation("animation.model.hello_smile",1.36f).layer(1).priority(1).smoothness(1.15f);
    public ActionController controller = ActionController.create(this,"actionController",ACTION_SWING,ACTION_SLIDE,ACTION_BULLSHIT);
    public static final Action ACTION_SWING = new Action(0,"swing",1.36f);
    public static final Action ACTION_SLIDE = new Action(1,"slide",20);
    public static final Action ACTION_BULLSHIT = new Action(2,"bullshit",1.36f);

    public ActionStateMachine stateMachine = ActionStateMachine.loadControllers(controller);
    private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(EntityPenguin.class, EntityDataSerializers.INT);
    private boolean isLandController = true;
    private boolean improvedFishing = false;
    private static final Ingredient FOOD_ITEMS = Ingredient.of(Items.TROPICAL_FISH);
    private Player targetPlayer;

    private int loseInterestTicks = 0;

    public static final WeightedRandom<ItemStack> PENGUIN_LOOT = new WeightedRandom<>().
            add(50,Items.FLINT.getDefaultInstance()).
            add(40,Items.STICK.getDefaultInstance()).
            add(20,Items.IRON_NUGGET.getDefaultInstance()).
            add(10,Items.GOLD_NUGGET.getDefaultInstance()).
            add(5,Items.PRISMARINE_SHARD.getDefaultInstance()).
            add(5,Items.PRISMARINE_CRYSTALS.getDefaultInstance()).
            add(5,Items.NAUTILUS_SHELL.getDefaultInstance()).
            add(5,Items.SCUTE.getDefaultInstance()).
            add(4, SMItems.HOWLING_SEASHELL.get().getDefaultInstance()).
            add(2,Items.DIAMOND.getDefaultInstance()).
            build();


    private EntityTask treasureTask = new EntityTask(this,500) {
        @Override
        public void run() {
            if (isInWater()) {
                loseInterestTicks = 400;
                if (level instanceof ServerLevel) {
                    if (!improvedFishing) {
                        setHeldItem(PENGUIN_LOOT.random());
                    }else{
                        BlockPos blockPos = WorldExtraUtils.getNearestAirBlockAbove(level,blockPosition());
                        LootContext.Builder contextBuilder = (new LootContext.Builder((ServerLevel) level)).withParameter(LootContextParams.ORIGIN, new Vec3(blockPos.getX(),blockPos.getY(),blockPos.getZ())).withParameter(LootContextParams.TOOL, Items.FISHING_ROD.getDefaultInstance()).withParameter(LootContextParams.THIS_ENTITY, EntityPenguin.this).withLuck(20).withRandom(random);
                        contextBuilder.withParameter(LootContextParams.KILLER_ENTITY, EntityPenguin.this).withParameter(LootContextParams.THIS_ENTITY, EntityPenguin.this);
                        ResourceLocation builtInLootTable = BuiltInLootTables.FISHING;
                        if (RandomUtils.doWithChance(5)){
                            builtInLootTable = BuiltInLootTables.FISHING_TREASURE;
                        }
                        LootTable loottable = level.getServer().getLootTables().get(builtInLootTable);
                        List<ItemStack> list = loottable.getRandomItems(contextBuilder.create(LootContextParamSets.FISHING));
                        if (list.size() > 0) {
                            setHeldItem(list.get(0));
                        }
                    }
                }
            }
        }
    }.selfCancel().cancelledAtBeginning();

    private EntityTask slideTask = new EntityTask(this,400) {
        @Override
        public void run() {
            if (RandomUtils.doWithChance(10)){
                if (isOnGround() && !isInWater() && WorldExtraUtils.isIceBlock(level.getBlockState(blockPosition().below()))) {
                    controller.playAction(ACTION_SLIDE);
                    Vec3 viewVec = getViewVector(1).multiply(2,0,2);
                    setYRot((float) Math.atan2(viewVec.x,viewVec.z));
                    setDeltaMovement(getDeltaMovement().scale(0.85).add(viewVec));
                }
            }
        }
    };

    private EntityTask swingTask = new EntityTask(this,300) {
        @Override
        public void run() {
            if (RandomUtils.doWithChance(5) && controller.isNoAction()) {
                List<Player> players = level.getEntitiesOfClass(Player.class, getBoundingBox().inflate(4.0D), (e) -> true);
                if (!players.isEmpty()) {
                    Player player = players.get(0);
                    if (player != null) {
                        swingToPlayer(player);
                    }
                }
            }
        }
    };

    @Override
    protected void dropCustomDeathLoot(DamageSource p_21385_, int p_21386_, boolean p_21387_) {
        super.dropCustomDeathLoot(p_21385_, p_21386_, p_21387_);
        if (!isBaby()) {
            ItemEntity itementity2 = this.spawnAtLocation(new ItemStack(Items.FEATHER, RandomUtils.randomInt(1, 2)));
            if (itementity2 != null) {
                itementity2.setExtendedLifetime();
            }
        }
    }

    public EntityPenguin(EntityType<? extends Animal> p_27557_, Level p_27558_) {
        super(p_27557_, p_27558_);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        selectController();
    }

    @Override
    public float getStepHeight() {
        if (treasureTask.isCancelled() || isInWater()) {
            return 1;
        }else{
            return 0;
        }
    }

    @Override
    public void onActionBegin(Action state) {
        if (state == ACTION_SWING){
            animationFactory.play(ANIMATION_HELLO);
        }else
        if (state == ACTION_BULLSHIT){
            animationFactory.play(ANIMATION_HELLO_SMILE);
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_146746_, DifficultyInstance p_146747_, MobSpawnType p_146748_, @Nullable SpawnGroupData p_146749_, @Nullable CompoundTag p_146750_) {
        if (RandomUtils.doWithChance(10)){
            setSkinType(1);
        }else{
            setSkinType(0);
        }
        return super.finalizeSpawn(p_146746_, p_146747_, p_146748_, p_146749_, p_146750_);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_27576_) {
        super.readAdditionalSaveData(p_27576_);
        if (p_27576_.contains("TargetPlayer")) {
            targetPlayer = level.getPlayerByUUID(p_27576_.getUUID("TargetPlayer"));
        }
        setSkinType(p_27576_.getInt("Skin"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_27587_) {
        super.addAdditionalSaveData(p_27587_);
        if (targetPlayer != null) {
            p_27587_.putUUID("TargetPlayer", targetPlayer.getUUID());
        }
        p_27587_.putInt("Skin",getSkinType());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(TYPE,0);
    }

    @Override
    protected PathNavigation createNavigation(Level p_21480_) {
        return new PenguinPathNavigation(this,level);
    }

    @Override
    public ActionStateMachine getActionStateMachine() {
        return stateMachine;
    }

    @Override
    public <K extends IDisplayArgument> AnimationFactory getAnimationFactory(K argument) {
        return animationFactory;
    }

    static class PenguinPathNavigation extends WaterBoundPathNavigation {

        PenguinPathNavigation(EntityPenguin p_149218_, Level p_149219_) {
            super(p_149218_, p_149219_);
        }

        protected boolean canUpdatePath() {
            return true;
        }

        protected PathFinder createPathFinder(int p_149222_) {
            this.nodeEvaluator = new AmphibiousNodeEvaluator(false);
            return new PathFinder(this.nodeEvaluator, p_149222_);
        }

        public boolean isStableDestination(BlockPos p_149224_) {
            if (mob.isInWater()){
                return super.isStableDestination(p_149224_);
            }
            return true;
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_146743_, AgeableMob p_146744_) {
        return (AgeableMob) SMEntities.PENGUIN.get().create(p_146743_);
    }


    @Override
    public boolean isFood(ItemStack p_29508_) {
        return FOOD_ITEMS.test(p_29508_);
    }


    protected void spawnTamingParticles() {
        ParticleOptions particleoptions = ParticleTypes.HAPPY_VILLAGER;

        for(int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            this.level.addParticle(particleoptions, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
        }

    }

    public static boolean checkSpawnRules(EntityType<? extends EntityPenguin> p_27578_, LevelAccessor p_27579_, MobSpawnType p_27580_, BlockPos p_27581_, RandomSource p_27582_) {
        return WorldExtraUtils.isIceBlock(p_27579_.getBlockState(p_27581_.below())) && isBrightEnoughToSpawn(p_27579_, p_27581_);
    }

    @Override
    public InteractionResult mobInteract(Player p_27584_, InteractionHand p_27585_) {
        ItemStack item = p_27584_.getItemInHand(p_27585_);
        if (item.isEmpty() && !getHeldItem().isEmpty()){
            dropItemToPlayer();
            swingToPlayer(p_27584_);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        }
        if (!item.isEmpty() && item.is(ItemTags.FISHES)) {
            if (!item.getItem().equals(Items.TROPICAL_FISH)) {
                forceGoToFishing(p_27584_,false);
                spawnTamingParticles();
                item.shrink(1);
                this.playSound(SoundEvents.PARROT_EAT, 1.0F, 1.0F);
                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
        }
        return super.mobInteract(p_27584_, p_27585_);
    }

    public void forceGoToFishing(Player player,boolean improvedFishing){
        if (treasureTask.isCancelled()) {
            targetPlayer = player;
            BlockPos water = findWater();
            float pitch = 0;
            if (isBaby()) {
                pitch += 0.35f;
            }
            if (water != null) {
                setImprovedFishing(improvedFishing);
                playSound(SMSounds.PENGUIN_SWING.get(), 1.0F, pitch + 1.2F);
                getNavigation().moveTo(water.getX(), water.getY(), water.getZ(), 0.8D);
                treasureTask.setDelay(random.nextInt(200, 500));
                treasureTask.restart();
            } else {
                playSound(SMSounds.PENGUIN_SWING.get(), 1.0F, pitch + 0.8F);
            }
        }
    }


    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    private void selectController(){
        if (isInWater() && isLandController){
            isLandController = false;
            this.moveControl = new PenguinInWaterMoveControl(this);
            this.lookControl = new PenguinInWaterLookControl(this,20);
        }else
        if (!isInWater() && !isLandController){
            isLandController = true;

            this.moveControl = new MoveControl(this);
            this.lookControl = new LookControl(this);
        }
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(1,new PanicGoal(this,0.8D));
        goalSelector.addGoal(2, new BreedGoal(this, 0.5D));
        goalSelector.addGoal(3, new TemptGoal(this, 0.5D, FOOD_ITEMS, false));
        goalSelector.addGoal(4,new RandomStrollGoal(this,0.4D));
        goalSelector.addGoal(5, new FollowParentGoal(this, 0.6D));
        goalSelector.addGoal(4,new PenguinRandomSwimmingGoal(this,1.2D,10));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }


    @Override
    public void travel(Vec3 p_149181_) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), p_149181_);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(p_149181_);
        }
    }

    public class PenguinInWaterMoveControl extends SmoothSwimmingMoveControl {

        private final EntityPenguin penguin;

        public PenguinInWaterMoveControl(EntityPenguin p_149215_) {
            super(p_149215_, 85, 10, 0.5F, 0.5F, false);
            this.penguin = p_149215_;
        }

        @Override
        public void tick() {
            super.tick();
        }
    }

    public class PenguinInWaterLookControl extends SmoothSwimmingLookControl {

        public PenguinInWaterLookControl(Mob p_148061_, int p_148062_) {
            super(p_148061_, p_148062_);
        }

        public void tick() {
            super.tick();
        }
    }


    @Override
    public void tick() {
        super.tick();
        if (controller.is(ACTION_SLIDE)){
            animationFactory.play(ANIMATION_SLIDE);
        }else{
            if (isInWater()){
                animationFactory.play(ANIMATION_SWIM);
            }else
            if (EntityUtils.isMoving(this,-0.01f,0.01f)){
                animationFactory.play(ANIMATION_WALK);
            }else{
                animationFactory.play(ANIMATION_IDLE);
            }
        }
    }

    @Override
    public void aiStep() {
        treasureTask.update();
        swingTask.update();
        slideTask.update();
        if (targetPlayer != null){
            if (!treasureTask.isCancelled()){
                if (isInWater()){
                    setDeltaMovement(getDeltaMovement().x,-0.1f,getDeltaMovement().z);
                }else{
                    BlockPos water = findWater();
                    if (water != null) {
                        getNavigation().moveTo(water.getX(), water.getY(), water.getZ(), 0.8D);
                    }
                }
            }
            if (treasureTask.isCancelled() && !getHeldItem().equals(ItemStack.EMPTY)){
                if (distanceTo(targetPlayer) <= 2000 && loseInterestTicks > 0) {
                    loseInterestTicks--;
                    navigation.moveTo(targetPlayer, 1);
                }else {
                    loseInterestTicks = 0;
                    dropItemToPlayer();
                }
            }
        }
        selectController();
        super.aiStep();
    }

    public void dropItemToPlayer(){
        if (targetPlayer != null) {
            ItemEntity itementity = new ItemEntity(this.level, this.getX(), getEyeY(), this.getZ(), getHeldItem());
            itementity.setPickUpDelay(40);
            float f8 = Mth.sin(this.getXRot() * ((float) Math.PI / 180F));
            float f2 = Mth.cos(this.getXRot() * ((float) Math.PI / 180F));
            float f3 = Mth.sin(this.getYRot() * ((float) Math.PI / 180F));
            float f4 = Mth.cos(this.getYRot() * ((float) Math.PI / 180F));
            float f5 = this.random.nextFloat() * ((float) Math.PI * 2F);
            float f6 = 0.02F * this.random.nextFloat();
            setHeldItem(ItemStack.EMPTY);
            itementity.setDeltaMovement((double) (-f3 * f2 * 0.3F) + Math.cos((double) f5) * (double) f6, (double) (-f8 * 0.3F + 0.1F + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F), (double) (f4 * f2 * 0.3F) + Math.sin((double) f5) * (double) f6);
            level.addFreshEntity(itementity);
            targetPlayer = null;
            setImprovedFishing(false);
        }
    }

    public void swingToPlayer(Player player){
        if (player != null && !isInWater()){
            lookAt(player,180,180);
            controller.playAction(ACTION_SWING);
            playSound(SMSounds.PENGUIN_SWING.get(),1,1);
        }
    }


    public BlockPos findWater() {
        BlockPos blockpos = null;
        final Random random = new Random();
        final int range = 30;
        for(int i = 0; i < 15; i++) {
            BlockPos blockPos = blockPosition().offset(random.nextInt(range) - range/2, 3, random.nextInt(range) - range/2);
            while (this.level.isEmptyBlock(blockPos) && blockPos != null) {
                blockPos = blockPos.below();
                if (blockpos != null) {
                    if (blockpos.getY() < level.getMinBuildHeight()) {
                        break;
                    }
                }
            }

            if (this.level.getFluidState(blockPos).is(FluidTags.WATER)) {
                blockpos = blockPos;
            }
        }
        return blockpos;
    }


    private class PenguinRandomSwimmingGoal extends RandomSwimmingGoal{

        public PenguinRandomSwimmingGoal(PathfinderMob p_25753_, double p_25754_, int p_25755_) {
            super(p_25753_, p_25754_, p_25755_);
        }

        @Override
        public boolean canUse() {
            return super.canUse() && isInWater();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && isInWater();
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SMSounds.PENGUIN_IDLE.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return SMSounds.PENGUIN_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SMSounds.PENGUIN_DEATH.get();
    }

    public int getSkinType(){
        return entityData.get(TYPE);
    }

    public void setSkinType(int type){
        this.entityData.set(TYPE,type);
    }


    public void setHeldItem(ItemStack stack){
        if (!level.isClientSide) {
            setItemInHand(InteractionHand.MAIN_HAND, stack);
        }
    }

    public ItemStack getHeldItem(){
        return getItemInHand(InteractionHand.MAIN_HAND);
    }

    public void setImprovedFishing(boolean improvedFishing) {
        this.improvedFishing = improvedFishing;
    }
}
