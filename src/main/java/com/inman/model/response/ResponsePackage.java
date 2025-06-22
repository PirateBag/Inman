package com.inman.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.inman.entity.EntityMaster;
import com.inman.model.rest.ErrorLine;

import java.util.ArrayList;

public class ResponsePackage<T> {
    public ResponsePackage() {
    }

    public ResponseType getResponseType() {
        return responseType;
    }

    protected ResponseType responseType;
    private ArrayList<ErrorLine> errors = new ArrayList<ErrorLine>();
    private ArrayList<T> data = new ArrayList<>();

    public ResponsePackage(ResponseType xResponseType) {
        this.responseType = xResponseType;
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
        var originalNumberOfRows = this.getData().size();
        var numberOfIncrementalRows = xIncrementalChanges.getData().size();

        int sourceIndex = 0;


        switch (xIncrementalChanges.getResponseType()) {
            case QUERY:
                throw new IllegalArgumentException("Query is not a legal Incremental Change type");
            case ADD:
                var numberOfRowsAfterAdd = originalNumberOfRows + numberOfIncrementalRows;
                /*
                newData = (EntityMaster[]) Arrays.copyOf(this.getData(), originalNumberOfRows + xIncrementalChanges.getData().size());

                int destinationIndex = originalNumberOfRows;
                while (sourceIndex < numberOfIncrementalRows) {
                    newData[destinationIndex] = (EntityMaster) xIncrementalChanges.getData()[sourceIndex];
                    sourceIndex++;
                    destinationIndex++;
                }  */

                break;
            case DELETE:
                int numberOfUpdatedRows = originalNumberOfRows - numberOfIncrementalRows;
                newData = new EntityMaster[ numberOfUpdatedRows ];
                int destationIndex = 0;

                /*  Make sure that all the items to be deleted can be found in the original list.  */
                for ( int incrementalIndex = 0; incrementalIndex < numberOfIncrementalRows; incrementalIndex++ ) {
                    var incrementalEntity = ((EntityMaster) xIncrementalChanges.getData().get(incrementalIndex));
                    /* if (null == findEntityWithId(incrementalEntity.getId(), (EntityMaster[]) this.getData())) {
                        throw new IllegalStateException("EntityId " + incrementalEntity.getId() + " can't be found to be deleted");
                    }*/
                }

                /*  Verified that every item to be deleted exists, copy the ones to not be deleted into the destination.  */
               while ( sourceIndex < originalNumberOfRows ) {
                    //  If we don't find the entity in the delete list, copy it into the destination...
                    var sourceEntity = ((EntityMaster) this.getData().get( sourceIndex ));
                    /*  if ( null == findEntityWithId( sourceEntity.getId(), (EntityMaster[]) xIncrementalChanges.getData())  ) {
                        newData[ destationIndex ] = sourceEntity;
                        destationIndex++;
                    }  */
                    sourceIndex++;

                }
                break;
            case CHANGE:
                newData = new EntityMaster[ originalNumberOfRows ];
                for ( destationIndex = 0 ; destationIndex < originalNumberOfRows; destationIndex++) {
                    /*  var originalEntity = (EntityMaster) this.getData()[ destationIndex ];
                    /*  var updatedEntity = findEntityWithId( originalEntity.getId(), (EntityMaster[]) xIncrementalChanges.getData());  */
                    /*  newData[ destationIndex ] = updatedEntity == null ? originalEntity : updatedEntity;  */
                    }
                break;
        }

        rValue = new ResponsePackage<T>();
        rValue.setErrors( this.errors );
          ///     rValue.setData((T[]) newData);
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

    public ArrayList<T> getData() {
        return data;
    }
    public void setData(ArrayList<T> data) { this.data = data;  }

    @JsonIgnore
    public String getErrorsTextAsString( ) {
        StringBuilder responseMessages = new StringBuilder();
        for (ErrorLine errorLine : this.errors) {
            responseMessages.append(String.format( "%-3d  %s\n", errorLine.getKey(), errorLine.getMessage()));
        }
        return responseMessages.toString();
    }
    }
