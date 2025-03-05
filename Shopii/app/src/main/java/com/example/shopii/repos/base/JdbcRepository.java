package com.example.shopii.repos.base;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import com.example.shopii.datas.DatabaseHelper;

public class JdbcRepository<T, ID> implements BaseRepository<T, ID> {
    private final DatabaseHelper dbHelper;
    private final Class<T> entityClass;

    public JdbcRepository(DatabaseHelper dbHelper, Class<T> entityClass) {
        this.dbHelper = dbHelper;
        this.entityClass = entityClass;
    }

    private String getTableName(Class<?> entityClass) {
        Entity entity = entityClass.getAnnotation(Entity.class);
        return entity.name();
    }

    private Field getIdField(Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No @Id field found"));
    }

    @Override
    public T findById(ID id) {
return null;    }

    @Override
    public List<T> findAll() {
return null;    }

    @Override
    public long insert(T entity) {
        try (Connection conn = dbHelper.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(generateInsertSQL(), Statement.RETURN_GENERATED_KEYS)) {

            setInsertParameters(pstmt, entity);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next())
                        return rs.getLong(1);
                }
            }
            return -1;
        } catch (Exception e) {
            throw new RuntimeException("Insert failed", e);
        }
    }

    private String generateInsertSQL() {
return null;    }

    private void setInsertParameters(PreparedStatement pstmt, T entity) {
        // Set parameters using reflection
    }

    @Override
    public int update(T entity) {
        // TODO: Implement update logic
        throw new UnsupportedOperationException("Update not implemented");
    }

    @Override
    public int delete(ID id) {
        // TODO: Implement delete logic
        throw new UnsupportedOperationException("Delete not implemented");
    }
}
