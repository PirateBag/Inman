package com.inman.repository;

import com.inman.controller.ItemCrud;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Repository
public class DdlRepository {
    static Logger logger = LoggerFactory.getLogger("controller: " + DdlRepository.class);
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void resetIdForTable(String tableName) {
        String sqlCommand = "ALTER TABLE %s ALTER COLUMN id RESTART WITH 1".formatted(tableName);
        logger.info(sqlCommand);
        entityManager.createNativeQuery(sqlCommand).executeUpdate();
    }
    public static String createUpdateByRowIdStatemet( String tableName, int rowId, Map<String,String> fieldsToUpdate ) {
        StringBuilder sqlCommand = new StringBuilder( "UPDATE " + tableName + " SET "  );
        int numberOfKeys = 0;
        for ( String key : fieldsToUpdate.keySet()) {
            sqlCommand.append( key + "=" + fieldsToUpdate.get(key) + (numberOfKeys < fieldsToUpdate.size() ? "," : "" ) );
        }
        sqlCommand.append( "WHERE id=" + rowId );
        logger.info(sqlCommand.toString());
        return sqlCommand.toString();
    }
}
