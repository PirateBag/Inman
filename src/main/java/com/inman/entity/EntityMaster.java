package com.inman.entity;

import enums.CrudAction;
import jakarta.persistence.*;


@MappedSuperclass
public abstract class EntityMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Transient
    CrudAction  crudAction = CrudAction.NONE;

    public EntityMaster() {
    }

    public void setId( long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public void setCrudAction(CrudAction xNewState) {
        this.crudAction = xNewState;
    }

    public CrudAction getCrudAction() {
        return this.crudAction;
    }

    public abstract EntityMaster copy( EntityMaster oldValue);


}
