package config; 

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
public final class DatabaseConnection { 

    // Adoptar System.getProperty() para la configuración
    private static final String URL_DB = System.getProperty("db.url", "jdbc:mysql://localhost:3306/tp_empleados");
    private static final String URL = System.getProperty("db.url", "jdbc:mysql://localhost:3306");
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

    private DatabaseConnection() {}

    // Método principal para obtener la conexión
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL_DB, USER, PASSWORD);
    }
    
    // Iniciar DATA BASE
    public static void initializeDatabase() throws Exception {
    
        // Leer archivo completo a un String
        String sqlContent = new String(Files.readAllBytes(Paths.get("src/sql/init.sql")));

        // Separar cada sentencia por ';'
        String[] statements = sqlContent.split(";");
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try (Statement stmt = connection.createStatement()) {
                for (String raw : statements) {
                    String sql = raw.trim();

                    if (sql.isEmpty() || sql.startsWith("--")) {
                        continue; // ignorar líneas vacías o comentarios
                    }

                    stmt.execute(sql);
                }
        }
    }
    
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