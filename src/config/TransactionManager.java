package config; 

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Gestor de Transacciones.
 * Implementa AutoCloseable para permitir el uso de try-with-resources.
 * Asegura que la conexión se cierre y que se ejecute un rollback si no hubo commit.
 */
public class TransactionManager implements AutoCloseable {
    
    // Establece conexión 
    private final Connection conn;
    private boolean transactionActive;

    /**
     * Constructor del gestor.
     * @param conn La conexión obtenida de DatabaseConnection.getConnection().
     * @throws IllegalArgumentException Si la conexión es null.
     */
    public TransactionManager(Connection conn) {
        //Verificamos que haya conexión
        if (conn == null) {
            throw new IllegalArgumentException("La conexión no puede ser null");
        }
        //Establecemos la conexión. 
        this.conn = conn;
        this.transactionActive = false;
    }

    /**
     * Retorna la conexión JDBC envuelta, lista para ser pasada a los DAOs.
     * @return La conexión activa.
     */
    public Connection getConnection() {
        return conn;
    }

    /**
     * Inicia la transacción: desactiva el autocommit.
     * @throws SQLException Si hay un error de conexión o si ya está cerrada.
     */
    public void startTransaction() throws SQLException {
        if (conn == null || conn.isClosed()) {
             throw new SQLException("No se puede iniciar la transacción: conexión no disponible o cerrada");
        }
        conn.setAutoCommit(false);
        transactionActive = true;
    }

    /**
     * Confirma los cambios en la base de datos y cierra la transacción. 
     * @throws SQLException Si el commit falla o si no hay una transacción activa.
     */
    public void commit() throws SQLException {
        if (!transactionActive) {
            throw new SQLException("No hay una transacción activa para hacer commit");
        }
        conn.commit();
        transactionActive = false;
    }

    /**
     * Revierte los cambios si la transacción sigue activa.
     * No lanza excepción, solo imprime el error si el rollback falla.
     */
    public void rollback() {
        if (conn != null && transactionActive) {
            try {
                conn.rollback();
                transactionActive = false;
            } catch (SQLException e) {
                System.err.println("Error durante el rollback: " + e.getMessage());
            }
        }
    }

    /**
     * Implementación del método close() de AutoCloseable.
     * Se llama automáticamente al salir de un bloque try-with-resources.
     * 1. Llama a rollback() si no se hizo commit previamente.
     * 2. Vuelve a activar el autocommit.
     * 3. Cierra la conexión.
     */
    @Override
    public void close() {
        if (conn != null) {
            try {
                // 1. Rollback si la transacción no se completó (commit)
                if (transactionActive) {
                    rollback();
                }
                // 2. Vuelve la conexión a su estado original (autocommit=true)
                conn.setAutoCommit(true);
                // 3. Cierra la conexión
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    //Consular si la trasacción esta activa
    public boolean isTransactionActive() {
        return transactionActive;
    }
}