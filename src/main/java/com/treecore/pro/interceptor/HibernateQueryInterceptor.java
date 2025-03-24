package com.treecore.pro.interceptor;

import java.io.Serializable;
import java.util.Iterator;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.treecore.pro.util.SessionUtil;

/**
 * Hibernate 쿼리 인터셉터
 * SQL 쿼리를 동적으로 수정하는 인터셉터
 */
@Component
public class HibernateQueryInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(HibernateQueryInterceptor.class);
    private static final String DEFAULT_TABLE_NAME = "tree_node";
    private static final String TABLE_NAME_KEY = "treeNodeTableName";

    @Override
    public String onPrepareStatement(String sql) {
        if (sql != null && sql.contains(DEFAULT_TABLE_NAME)) {
            String tableName = (String) SessionUtil.getSessionValue(TABLE_NAME_KEY);
            logger.debug("동적 테이블 매핑: {} -> {}", DEFAULT_TABLE_NAME, 
                         tableName != null ? tableName : DEFAULT_TABLE_NAME);
            return sql.replace(DEFAULT_TABLE_NAME, tableName != null ? tableName : DEFAULT_TABLE_NAME);
        }
        logger.debug("SQL: {}", sql);
        return sql;
    }
    
    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        logger.debug("Entity updated: {}, ID: {}", entity.getClass().getName(), id);
        return false;
    }
    
    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        logger.debug("Entity saved: {}, ID: {}", entity.getClass().getName(), id);
        return false;
    }
    
    @Override
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
        logger.debug("Entity deleted: {}, ID: {}", entity.getClass().getName(), id);
    }
    
    @Override
    public void postFlush(Iterator entities) {
        logger.debug("Post flush completed");
    }
} 