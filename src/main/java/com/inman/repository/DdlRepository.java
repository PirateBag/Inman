package com.inman.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

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
    public static String createUpdateByRowIdStatement( String tableName, long rowId, Map<String,String> fieldsToUpdate ) {
        StringBuilder sqlCommand = new StringBuilder( "UPDATE " + tableName + " SET "  );
        int numberOfKeys = 0;
        for ( String key : fieldsToUpdate.keySet()) {
            sqlCommand.append(key).append("=").append(fieldsToUpdate.get(key)).append(++numberOfKeys < fieldsToUpdate.size() ? ", " : " ");
        }
        sqlCommand.append(" WHERE id=").append(rowId);
        logger.info(sqlCommand.toString());
        return sqlCommand.toString();
    }

    @Transactional
    public void executeDynamicDML( @NotNull String sqlCommand)    {
        logger.info(sqlCommand);
        entityManager.createNativeQuery(sqlCommand).executeUpdate();

    }
}
