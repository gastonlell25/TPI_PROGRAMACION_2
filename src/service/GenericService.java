package service;

import java.util.List;

public interface GenericService<T> {
    
    void insert(T entity) throws Exception;

    void update(T entity) throws Exception;

    void delete(Long id) throws Exception;

    T getById(Long id) throws Exception;

    List<T> getAll() throws Exception;
}