/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Services;

import java.util.List;

/**
 *
 * @author gastonlell
 */
public interface GenericService<T> {
    
    void insert(T entity) throws Exception;

    void update(T entity) throws Exception;

    void delete(Long id) throws Exception;

    T getById(Long id) throws Exception;

    List<T> getAll() throws Exception;
}
