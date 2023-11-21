package ru.astemir.skillsmobs.common.entity;


import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.astemir.api.common.misc.ICustomRendered;
import org.astemir.api.math.random.RandomUtils;
import ru.astemir.skillsmobs.common.entity.ai.EntityTask;


public class EntityHandDynamite extends ThrowableProjectile implements ICustomRendered {

    private boolean onGround = false;

    private EntityTask explodeTask = new EntityTask(this,10) {
        @Override
        public void run() {
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(4.0D), (e) -> {
                if (e instanceof EntityReptile) {
                    return false;
                }
                return true;
            })) {
                entity.hurt(DamageSource.explosion((LivingEntity) getOwner()),10);
            }
            if (!level.isClientSide){
                level.broadcastEntityEvent(EntityHandDynamite.this, (byte) 3);
                playSound(SoundEvents.GENERIC_EXPLODE,2,1);
                discard();
            }
        }
    }.selfCancel().cancelledAtBeginning();


    @Override
    public void handleEntityEvent(byte p_19882_) {
        if (p_19882_ == 3){
            for (int i = 0;i<8;i++) {
                level.addParticle(ParticleTypes.EXPLOSION, getX()+ RandomUtils.randomFloat(-2,2), getY()+ RandomUtils.randomFloat(0,2), getZ()+ RandomUtils.randomFloat(-2,2), 1, 1, 1);
            }
        }
    }

    public EntityHandDynamite(EntityType<? extends ThrowableProjectile> p_37466_, Level p_37467_) {
        super(p_37466_, p_37467_);
    }

    public EntityHandDynamite(EntityType<? extends ThrowableProjectile> p_37456_, double p_37457_, double p_37458_, double p_37459_, Level p_37460_) {
        super(p_37456_, p_37457_, p_37458_, p_37459_, p_37460_);
    }

    public EntityHandDynamite(EntityType<? extends ThrowableProjectile> p_37462_, LivingEntity p_37463_, Level p_37464_) {
        super(p_37462_, p_37463_, p_37464_);
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 vec3 = this.getDeltaMovement();
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double d0 = vec3.horizontalDistance();
            this.setYRot((float)(Mth.atan2(vec3.x, vec3.z) * (double)(180F / (float)Math.PI)));
            this.setXRot((float)(Mth.atan2(vec3.y, d0) * (double)(180F / (float)Math.PI)));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }
        level.addParticle(ParticleTypes.SMOKE, getX(), getY(), getZ(), 0, 0, 0);
        explodeTask.update();
    }





    @Override
    protected void onHit(HitResult p_37260_) {
        if (p_37260_ instanceof EntityHitResult) {
            EntityHitResult hitResult = (EntityHitResult) p_37260_;
            if (getOwner() != null && hitResult.getEntity() != null) {
                if (hitResult.getEntity().getUUID().equals(getOwner().getUUID())) {
                    return;
                }
            }
        }
        if (!onGround) {
            setDeltaMovement(0,0,0);
        }else{
            setDeltaMovement(0,0,0);
        }
        if (explodeTask.isCancelled()) {
            explodeTask.restart();
        }
    }

    @Override
    protected void defineSynchedData() {}

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
