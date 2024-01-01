package com.inman.integration;

import com.inman.business.EntityUtility;
import com.inman.entity.ActivityState;
import com.inman.entity.Bom;
import com.inman.entity.EntityMaster;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntityMergeTest {

    private EntityMaster[] emptybaseline = new Bom[0];
    private EntityMaster[] emptyChanges = new Bom[0];

    @Test
    public void noData() {
        EntityMaster[] expected = new Bom[0], actual;

        actual = EntityUtility.mergeUpdate(emptybaseline, emptyChanges);
        assertTrue(EntityUtility.isEqual(emptybaseline, actual));
    }

    @Test
    public void baselineWithDataEmptyChanges() {

        EntityMaster[] baseLineWith1 = new Bom[1];
        baseLineWith1[0] = new Bom( 1L, 2L, 1.0);
        baseLineWith1[0].setId( 1L );
        EntityMaster[] expected = baseLineWith1;

        EntityMaster[] actual = EntityUtility.mergeUpdate(baseLineWith1, emptyChanges);
        assertTrue( EntityUtility.isEqual( expected, actual));
    }

    @Test
    public void baselineEmptyWithInserts() {

        EntityMaster[] inserts = new Bom[1];
        inserts[0] = new Bom( 1L, 2L, 1.0);
        inserts[0].setId( 1L );
        inserts[0].setActivityState( ActivityState.INSERT );

        EntityMaster[] expected = new Bom[1];
        System.arraycopy( inserts, 0, expected, 0, inserts.length );

        EntityMaster[] actual = EntityUtility.mergeUpdate( emptybaseline, inserts );
        assertTrue( EntityUtility.isEqual( expected, actual));
        assertTrue( EntityUtility.allAreActivityNone( actual ));
    }

    @Test
    public void NonEmptyBaselineWithInserts() {

        EntityMaster[] nonEmptyBaseLine = new Bom[2];
        nonEmptyBaseLine[ 0 ] = new Bom( 1, 1, 2, 1.0, ActivityState.NONE);
        nonEmptyBaseLine[ 1 ] = new Bom( 2, 1, 2, 2.0, ActivityState.NONE  );


        EntityMaster[] inserts = new Bom[2];
        inserts[0] = new Bom( 100,1L, 3L, 10.0, ActivityState.INSERT );
        inserts[1] = new Bom( 100,1L, 3L, 10.0, ActivityState.INSERT );

        EntityMaster[] expected = new EntityMaster[ nonEmptyBaseLine.length + inserts.length ];

        System.arraycopy( nonEmptyBaseLine, 0, expected, 0, nonEmptyBaseLine.length );
        System.arraycopy( inserts, 0, expected, nonEmptyBaseLine.length, inserts.length );

        EntityMaster[] actual = EntityUtility.mergeUpdate( nonEmptyBaseLine, inserts );
        assertTrue( EntityUtility.isEqual( expected, actual));
        assertTrue( EntityUtility.allAreActivityNone( actual ));
    }

    @Test
    public void NonEmptyBaselineWithChangeAndInsert() {

        EntityMaster[] nonEmptyBaseLine = new Bom[2];
        nonEmptyBaseLine[ 0 ] = new Bom( 1, 1, 2, 1.0, ActivityState.NONE);
        nonEmptyBaseLine[ 1 ] = new Bom( 2, 1, 2, 2.0, ActivityState.NONE  );


        EntityMaster[] changes = new Bom[2];
        changes[0] = new Bom( 100,1L, 3L, 10.0, ActivityState.INSERT );
        changes[1] = new Bom( 1,1L, 3L, 10.0, ActivityState.CHANGE );

        EntityMaster[] expected = new EntityMaster[ nonEmptyBaseLine.length + changes.length - 1 ];
        expected[ 0 ] = changes[ 1 ];
        expected[ 1 ] = nonEmptyBaseLine[ 1 ];
        expected[ 2 ] = changes[ 0 ];

        EntityMaster[] actual = EntityUtility.mergeUpdate( nonEmptyBaseLine, changes );
        assertTrue( EntityUtility.isEqual( expected, actual));
        assertTrue( EntityUtility.allAreActivityNone( actual ));
    }

    @Test
    public void NonEmptyBaselineWithDelete() {

        EntityMaster[] originals = new Bom[3];
        originals[ 0 ] = new Bom( 1, 1, 2, 1.0, ActivityState.NONE);
        originals[ 1 ] = new Bom( 2, 1, 3, 2.0, ActivityState.NONE  );
        originals[ 2] = new Bom( 3, 1, 4, 3.0, ActivityState.NONE  );

        /*
        Id, parent/child, qty
        1   1,2             1
        2   1,3             2
        3   1,4             3

        Id, parent/child, qty
        1   1,2             10
        3   1,4             3
        100, 1,5            50
         */

        EntityMaster[] changes = new Bom[3];
        changes[0] = new Bom( 100,1L, 5L, 50.0, ActivityState.INSERT );
        changes[1] = new Bom( 1,1L, 2L, 10.0, ActivityState.CHANGE );
        changes[2] = new Bom( 2,2L, 2L, 20.0, ActivityState.DELETE );

        EntityMaster[] actual = EntityUtility.mergeUpdate( originals, changes );

        EntityMaster[] expected = new EntityMaster[ 3 ];
        EntityUtility.setAllActivityStateToNone( changes );
        expected[ 0 ] = changes[ 1 ];
        expected[ 1 ] = originals[ 2 ];
        expected[ 2 ] = changes[ 0 ];

        assertTrue( EntityUtility.isEqual( expected, actual));
        assertTrue( EntityUtility.allAreActivityNone( actual ));
    }
}
