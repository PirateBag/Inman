package com.inman.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class EntityMaster {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY )
    protected long id;

    public EntityMaster() {
    }

    public void setId(Long id) {
        this.id = id;
    }
    public long getId() {
        return this.id;
    }
}
