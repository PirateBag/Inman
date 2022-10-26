package com.inman.entity;

import javax.persistence.*;
import java.util.LinkedList;

@MappedSuperclass
public class EntityMaster {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY )
    protected long id;

    @Transient
    protected ActivityState activityState = ActivityState.NONE;

    public EntityMaster() {
    }

    public void setId(Long id) {
        this.id = id;
    }
    public long getId() {
        return this.id;
    }

    public void setActivityState( ActivityState xNewState ) {
        this.activityState = xNewState;
    }
    public ActivityState getActivityState( ) { return this.activityState; }

}
