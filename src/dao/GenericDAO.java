/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.util.List;
import java.sql.Connection;

/**
 * INTERFAZ GENERICA CON OPERACION CRUD PARA IMPLEMENTAR EN DAOs
 * <T> Entidad a insertar conn Conexion con la BD para el uso de las
 * transacciones throws SQLException para capturar los errores con la BD
 */
public interface GenericDAO<T> {

    void insert(T entity, Connection conn) throws Exception;

    void update(T entity, Connection conn) throws Exception;

    T getById(Long id, Connection conn) throws Exception;

    List<T> getAll(Connection conn) throws Exception;

    void delete(Long id, Connection conn) throws Exception;
}
