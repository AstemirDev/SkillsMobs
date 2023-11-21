package ru.astemir.skillsmobs.common.entity.ai;

import net.minecraft.world.entity.Entity;

public abstract class EntityTask {

    private Entity entity;
    private int delay = 0;
    private int ticksBegin = 0;
    private boolean selfCancel = false;
    private boolean cancelled = false;


    public EntityTask(Entity entity, int delay) {
        this.entity = entity;
        this.delay = delay;
        this.ticksBegin = entity.tickCount;
    }

    public void update(){
        if (entity.tickCount >= ticksBegin+delay && !cancelled){
            ticksBegin = entity.tickCount;
            run();
            if (selfCancel){
                cancelled = true;
            }
        }
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void restart(){
        cancelled = false;
        this.ticksBegin = entity.tickCount;
    }



    public EntityTask cancelledAtBeginning(){
        cancelled = true;
        return this;
    }


    public EntityTask selfCancel(){
        selfCancel = true;
        return this;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public abstract void run();
}
