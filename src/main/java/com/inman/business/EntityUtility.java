package com.inman.business;

import com.inman.entity.ActivityState;
import com.inman.entity.EntityMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EntityUtility {

    static Logger logger = LoggerFactory.getLogger("EntityUtility");

    public static EntityMaster[] mergeUpdate(EntityMaster[] baseline, EntityMaster[] changes) {
        List<EntityMaster> listOfMergedEntities = new LinkedList<>();
        String message;

        Map<Long, EntityMaster> mappedChanges = new HashMap<>();
        for (var change : changes) {
            mappedChanges.put(change.getId(), change);
        }

        for (var baselineEntity : baseline) {
            // Copy all baseline entries without corresponding changes.
            var correspondingChange = mappedChanges.get(baselineEntity.getId());
            if (correspondingChange == null) {
                logger.info( "No changes to baseline entity:  " + baselineEntity.getId() );
                listOfMergedEntities.add( baselineEntity.copy( baselineEntity ) );
                continue;
            }


            switch (correspondingChange.getActivityState()) {
                default:
                    message = "Found a baseline entity with an unexpected activityState: " + baselineEntity.getId();
                    logger.error(message);
                    throw new RuntimeException(message);
                case NONE:
                    break;
                case CHANGE:
                    logger.info( "Changing existing entity: " + baselineEntity.getId() );
                    listOfMergedEntities.add(correspondingChange);
                    break;
                case INSERT:
                    message = "Found a corresponding insert that matched the entity:" + baselineEntity.getId();
                    logger.error(message);
                    throw new RuntimeException(message);
                case DELETE:
                    //  Do not copy an Entity with a corresponding change which is DELETE.
                    message = "Deleting entity:" + baselineEntity.getId();
                    logger.info(message);
                    break;
            }
        }

        //  Add in the changes that are inserts.
        for ( var change : changes ) {
            var copy = change.copy( change );
            if ( change.getActivityState() == ActivityState.INSERT ) {
                logger.info( "Inserting :" + copy.getId() );
                listOfMergedEntities.add( change.copy( change ) );
            }
        }


        var rValue = listOfMergedEntities.toArray(new EntityMaster[listOfMergedEntities.size()]);
        setAllActivityStateToNone( rValue );
        return rValue;
    }

    public static void setAllActivityStateToNone( EntityMaster[] entityMasters ) {
        for (var entityMaster : entityMasters ) {
            entityMaster.setActivityState(ActivityState.NONE);
        }
    }

    public static Boolean isEqual( EntityMaster[] a, EntityMaster[] b) {
        if ( a.length != b.length ) return false;

        for ( int index = 0; index < a.length ; index++ ) {
            if ( a[index].getId() != b[index].getId() ) {
                return false;
            }
        }
        return true;
    }


    public static boolean allAreActivityNone(EntityMaster[] source) {
        for ( var sourceElement : source ) {
            if ( sourceElement.getActivityState() != ActivityState.NONE )
                return false;
        }
    return true;
    }
}
