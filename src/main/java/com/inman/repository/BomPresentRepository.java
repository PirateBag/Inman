package com.inman.repository;

import com.inman.entity.BomPresent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BomPresentRepository extends JpaRepository<BomPresent, Long> {

    @Query("select b from BomPresent b where b.parentId = :xParentId")
    BomPresent[] byParentId(@Param("xParentId") long xParentId);

    BomPresent findById(long id);

    @Query("select b from BomPresent b where b.parentId = :xParentId and b.childId = :xChildId")
    BomPresent byParentIdChildId(
            @Param("xParentId") long xParentId,
            @Param("xChildId") long xChildId);

    /*  @Query( "select BomPresent.* from BomPresent" )
    BomPresent[] findAllBomForYou( );
*/
}
