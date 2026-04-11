package com.inman.entity;

import com.inman.model.rest.ErrorLine;
import enums.CrudAction;
import org.jetbrains.annotations.UnknownNullability;

import java.util.ArrayList;

public class Text extends EntityMaster{
    private String message;

    public Text() {};

    public Text(String message) {
        this.message = message;
    }

    public Text(String message, long id ) {
        this.message = message;
        this.id = id;
    }


    public String getMessage() {
        return this.message;
    }
    public void setMessage(String text) {
        this.message = text;
    }

    @Override
    public EntityMaster copy(EntityMaster oldValue) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static ArrayList<Text> convertErrorsToData(@UnknownNullability ArrayList<ErrorLine> errors ) {
        ArrayList<Text> data = new ArrayList<>();
        for (int i = 0; i < errors.size(); i++) {
            data.add( new Text( errors.get(i).getMessage() ) );
            data.get(i).setId(i+1);
            data.get(i).setCrudAction( CrudAction.NONE );
        }
        return data;
    }
}
