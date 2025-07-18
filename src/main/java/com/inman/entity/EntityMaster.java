package com.inman.entity;

import jakarta.persistence.*;


@MappedSuperclass
public abstract class EntityMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Transient
    ActivityState activityState = ActivityState.NONE;

    public EntityMaster() {
    }

    public void setId( long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public void setActivityState(ActivityState xNewState) {
        this.activityState = xNewState;
    }

    public ActivityState getActivityState() {
        return this.activityState;
    }

    public abstract EntityMaster copy( EntityMaster oldValue);
}
