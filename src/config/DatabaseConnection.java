package config; 

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection { 

    // Adoptar System.getProperty() para la configuración
    private static final String URL = System.getProperty("db.url", "jdbc:mysql://localhost:3306/tp_empleados");
    private static final String USER = System.getProperty("db.user", "root");
    private static final String PASSWORD = System.getProperty("db.password", "");

    // Carga de Drive
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            validateConfiguration(); // Valida la configuración al inicio
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("No se encontró el driver JDBC de MySQL: " + e.getMessage());
        } catch (IllegalStateException e) {
             throw new ExceptionInInitializerError("Error en la configuración de la base de datos: " + e.getMessage());
        }
    }

    private DatabaseConnection() {
    }

    // Método principal para obtener la conexión
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    // Verficiación de configuración de datos
    private static void validateConfiguration() {
        if (URL == null || URL.trim().isEmpty()) {
            throw new IllegalStateException("Url no configurada");
        }
        if (USER == null || USER.trim().isEmpty()) {
            throw new IllegalStateException("Usuario no configurado");
        }
        if (PASSWORD == null) { 
             throw new IllegalStateException("Verificar configuración de contraseña.");
        }
    }
}