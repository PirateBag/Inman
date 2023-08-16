package com.inman.model.response;

import com.inman.entity.ActivityState;
import com.inman.entity.EntityMaster;
import com.inman.model.rest.ErrorLine;

import java.util.ArrayList;
import java.util.Arrays;

public class ResponsePackage<T> {
    public ResponseType getResponseType() {
        return responseType;
    }

    protected ResponseType responseType;
    private ArrayList<ErrorLine> errors = new ArrayList<ErrorLine>();
    private T[] data = (T[]) new EntityMaster[0];

    public ResponsePackage(ArrayList<ErrorLine> errors, T[] data, ResponseType xResponseType) {
        this.errors = errors;
        this.data = data;
        this.responseType = xResponseType;
    }

    public ResponsePackage( T[] data, ResponseType xResponseType) {
        this.data = data;
        this.responseType = xResponseType;
    }

    public ResponsePackage() {
    }

    public void addError(ErrorLine error) {
        if (error.getKey() == 0) {
            error.setKey(this.errors.size() + 1);
        }
        errors.add(error);
    }

    public ResponsePackage<T> mergeAnotherResponse(ResponsePackage xIncrementalChanges) {
        ResponsePackage<T> rValue;
        EntityMaster[] newData = null;
        var originalNumberOfRows = this.getData().length;
        var numberOfIncrementalRows = xIncrementalChanges.getData().length;

        int sourceIndex = 0;


        switch (xIncrementalChanges.getResponseType()) {
            case QUERY:
                throw new IllegalArgumentException("Query is not a legal Incremental Change type");
            case ADD:
                var numberOfRowsAfterAdd = originalNumberOfRows + numberOfIncrementalRows;
                newData = (EntityMaster[]) Arrays.copyOf(this.getData(), originalNumberOfRows + xIncrementalChanges.getData().length);

                int destinationIndex = originalNumberOfRows;
                while (sourceIndex < numberOfIncrementalRows) {
                    newData[destinationIndex] = (EntityMaster) xIncrementalChanges.getData()[sourceIndex];
                    sourceIndex++;
                    destinationIndex++;
                }

                break;
            case DELETE:
                int numberOfUpdatedRows = originalNumberOfRows - numberOfIncrementalRows;
                newData = new EntityMaster[ numberOfUpdatedRows ];
                int destationIndex = 0;

                /*  Make sure that all the items to be deleted can be found in the original list.  */
                for ( int incrementalIndex = 0; incrementalIndex < numberOfIncrementalRows; incrementalIndex++ ) {
                    var incrementalEntity = ((EntityMaster) xIncrementalChanges.getData()[incrementalIndex]);
                    if (null == findEntityWithId(incrementalEntity.getId(), (EntityMaster[]) this.getData())) {
                        throw new IllegalStateException("EntityId " + incrementalEntity.getId() + " can't be found to be deleted");
                    }
                }

                /*  Verified that every item to be deleted exists, copy the ones to not be deleted into the destination.  */
               while ( sourceIndex < originalNumberOfRows ) {
                    //  If we don't find the entity in the delete list, copy it into the destination...
                    var sourceEntity = ((EntityMaster) this.getData()[ sourceIndex ]);
                    if ( null == findEntityWithId( sourceEntity.getId(), (EntityMaster[]) xIncrementalChanges.getData())  ) {
                        newData[ destationIndex ] = sourceEntity;
                        destationIndex++;
                    }
                    sourceIndex++;

                }
                break;
            case CHANGE:
                newData = new EntityMaster[ originalNumberOfRows ];
                for ( destationIndex = 0 ; destationIndex < originalNumberOfRows; destationIndex++) {
                    var originalEntity = (EntityMaster) this.getData()[ destationIndex ];
                    var updatedEntity = findEntityWithId( originalEntity.getId(), (EntityMaster[]) xIncrementalChanges.getData());
                    newData[ destationIndex ] = updatedEntity == null ? originalEntity : updatedEntity;
                    }
                break;
        }

        rValue = new ResponsePackage<T>( );
        rValue.setErrors( this.errors );
        rValue.setData((T[]) newData);
        rValue.setResponseType( this.responseType );
        return rValue;

    }

    public void setResponseType(ResponseType xResponseType) {
        responseType = xResponseType;
    }

    public ArrayList<ErrorLine> getErrors() {
        return errors;
    }

    public void setErrors(ArrayList<ErrorLine> errors) {
        this.errors = errors;
    }

    public T[] getData() {
        return data;
    }

    public void setData(T[] data) {
        this.data = data;
    }
    private EntityMaster findEntityWithId( long xEntityToSearch, EntityMaster[]  entitiesToSearch ) {
        for( EntityMaster entity : entitiesToSearch ) {
            if ( entity.getId() == xEntityToSearch ) {
                return entity;
            }
        }
        return null;
    }

    public EntityMaster [] getArrayOfUpdatedComponents(ActivityState xStateToSearchFor ) {
        var rValue = new ArrayList<T>();
        for( T entity : data ) {
            EntityMaster entityToSearch = (EntityMaster) entity;
            if ( entityToSearch.getActivityState() == xStateToSearchFor ) {
                rValue.add((T) entityToSearch);
            }
        }

        EntityMaster[] temp = new EntityMaster[ rValue.size() ];
        temp = rValue.toArray( temp );
        return temp;
    }



}
