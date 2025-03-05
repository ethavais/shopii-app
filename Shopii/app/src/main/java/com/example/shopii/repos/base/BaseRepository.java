package com.example.shopii.repos.base;

import java.util.List;

public interface BaseRepository<T, ID> {
    T findById(ID id);

    List<T> findAll();

    long insert(T entity);

    int update(T entity);

    int delete(ID id);
}