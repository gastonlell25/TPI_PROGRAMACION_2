package dao;

import Models.EmployeeFile;
import Models.FileStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

/**
 * DAO para la entidad EmployeeFile. Implementa las operaciones CRUD para la
 * tabla employee_files Utiliza una Connection compartida para las operaciones
 * transaccionales.
 */
public class EmployeeFileDAO implements GenericDAO<EmployeeFile> {

    //Se establece nombre de la tabla a utilizar en la base de datos.
    private static final String TABLE_NAME = "employee_files";

    /**
     * Inserta un nuevo objeto EmployeeFile en la base de datos.
     *
     * @param file El objeto EmployeeFile a insertar.
     * @param conn es la conexión JDBC a utilizar.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    @Override
    public void insert(EmployeeFile file, Connection conn) throws Exception {
        //Prepara la query para ejecutar la operacion
        String sql = "INSERT INTO " + TABLE_NAME
                + " (file_number, category, status, date_created, observation, deleted, employee_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        //Se signan los valores a insertar 
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, file.getFileNumber());
            stmt.setString(2, file.getCategory());
            stmt.setString(3, file.getStatus().name());
            stmt.setDate(4, file.getDateCreated() != null ? Date.valueOf(file.getDateCreated()) : null);
            stmt.setString(5, file.getObservation());
            stmt.setBoolean(6, file.isDeleted());
            stmt.setLong(7, file.getEmployeeId());

            //Se ejecuta la consulta con los valores cargados 
            int rows = stmt.executeUpdate();

            //Verificamos si la insercción fue exitosa.
            if (rows == 0) {
                throw new SQLException("Fallo la inserción");
            }

            //Capturamos los valores autogenerados.
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    file.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Fallo en la creación de legajo");
                }
            }
        } catch (SQLException e) { //Capturamos errorees. 
            throw new Exception("Error al insertar Legajo: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza un objeto EmployeeFile existente en la base de datos. Solo
     * actualiza legajos que no hayan sido dados de baja lógicamente.
     *
     * @param file El objeto EmployeeFile con los datos actualizados .
     * @param conn es la conexión JDBC a utilizar.
     * @throws SQLException Si ocurre un error de base de datos o si no se
     * encuentra el legajo.
     */
    @Override
    public void update(EmployeeFile file, Connection conn) throws Exception {
        String sql = "UPDATE " + TABLE_NAME
                + " SET file_number = ?, category = ?, status = ?, date_created = ?, "
                + "observation = ?, employee_id = ? WHERE id = ? AND deleted = FALSE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, file.getFileNumber());
            stmt.setString(2, file.getCategory());
            stmt.setString(3, file.getStatus().name());
            stmt.setDate(4, file.getDateCreated() != null ? Date.valueOf(file.getDateCreated()) : null);
            stmt.setString(5, file.getObservation());
            stmt.setLong(6, file.getEmployeeId());
            stmt.setLong(7, file.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("El legajo a actualizar no existe.");
            }
        } catch (SQLException e) {
            throw new Exception("Error al actualizar Legajo: " + e.getMessage(), e);
        }
    }

    /**
     * Busca un legajo por su ID primario. Solo retorna legajos que no hayan
     * sido dados de baja lógicamente
     *
     * @param id El ID del legajo a buscar.
     * @param conn es la conexión JDBC a utilizar.
     * @return El objeto EmployeeFile encontrado, o null si no se encuentra.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    @Override
    public EmployeeFile getById(Long id, Connection conn) throws Exception {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ? AND deleted = FALSE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Setea la  fecha
                    Date dateCreatedSql = rs.getDate("date_created");
                    LocalDate dateCreated = dateCreatedSql != null ? dateCreatedSql.toLocalDate() : null;

                    // Setea el enum
                    String statusStr = rs.getString("status");
                    FileStatus status = statusStr != null ? FileStatus.valueOf(statusStr) : FileStatus.ACTIVO;

                    // Crear y retornar EmployeeFile
                    EmployeeFile file = new EmployeeFile();
                    file.setId(rs.getLong("id"));
                    file.setDeleted(rs.getBoolean("deleted"));
                    file.setFileNumber(rs.getString("file_number"));
                    file.setCategory(rs.getString("category"));
                    file.setStatus(status);
                    file.setDateCreated(dateCreated);
                    file.setObservation(rs.getString("observation"));
                    file.setEmployeeId(rs.getLong("employee_id")); // 🔴 clave

                    return file;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener Legajo por ID: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Recupera todos los legajos activos de la base de datos. Solo lista
     * legajos que no hayan sido dados de baja lógicamente.
     *
     * @param conn es la conexión JDBC a utilizar.
     * @return Una lista de objetos EmployeeFile.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    @Override
    public List<EmployeeFile> getAll(Connection conn) throws Exception {
        List<EmployeeFile> files = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE deleted = FALSE ORDER BY id";

        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Setea fecha
                Date dateCreatedSql = rs.getDate("date_created");
                LocalDate dateCreated = dateCreatedSql != null ? dateCreatedSql.toLocalDate() : null;

                // Setea enum
                String statusStr = rs.getString("status");
                FileStatus status = statusStr != null ? FileStatus.valueOf(statusStr) : FileStatus.ACTIVO;

                // Crear EmployeeFile
                EmployeeFile file = new EmployeeFile();
                file.setId(rs.getLong("id"));
                file.setDeleted(rs.getBoolean("deleted"));
                file.setFileNumber(rs.getString("file_number"));
                file.setCategory(rs.getString("category"));
                file.setStatus(status);
                file.setDateCreated(dateCreated);
                file.setObservation(rs.getString("observation"));
                file.setEmployeeId(rs.getLong("employee_id"));

                // Agrega los datos a la lista
                files.add(file);
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener todos los Legajos: " + e.getMessage(), e);
        }

        //Retorna la lista de los datos encontrados. 
        return files;
    }

    /**
     * Realiza una eliminacion de baja logica del legajo Esto significa que el
     * registro se marca como eliminado estableciendo el campo 'deleted' a TRUE.
     *
     * @param id El ID del legajo a marcar como eliminado.
     * @param conn es la conexión JDBC a utilizar.
     * @throws SQLException Si ocurre un error de base de datos o si no se
     * encuentra el legajo.
     */
    @Override
    public void delete(Long id, Connection conn) throws Exception {
        String sql = "UPDATE " + TABLE_NAME + " SET deleted = TRUE WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No se encontró el legado a eliminar con id: " + id);
            }
        } catch (SQLException e) {
            throw new Exception("Error al realizar baja lógica de Legajo: " + e.getMessage(), e);
        }
    }

    /**
     * Busca un legajo por el ID del empleado al que está asociado. Este metodo
     * se usa para verificar la relacion desde EmployeeDAO. Solo retorna legajos
     * activos.
     *
     * @param employeeId El ID del empleado asociado al legajo.
     * @param conn es la conexión JDBC a utilizar.
     * @return El objeto EmployeeFile activo, o null si no se encuentra o está
     * eliminado.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public EmployeeFile findByEmployeeId(Long employeeId, Connection conn) throws Exception {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE employee_id = ? AND deleted = FALSE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, employeeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Setea fecha
                    Date dateCreatedSql = rs.getDate("date_created");
                    LocalDate dateCreated = dateCreatedSql != null ? dateCreatedSql.toLocalDate() : null;

                    // Setea enum
                    String statusStr = rs.getString("status");
                    FileStatus status = statusStr != null ? FileStatus.valueOf(statusStr) : FileStatus.ACTIVO;

                    // Crear y retornar EmployeeFile
                    EmployeeFile file = new EmployeeFile();
                    file.setId(rs.getLong("id"));
                    file.setDeleted(rs.getBoolean("deleted"));
                    file.setFileNumber(rs.getString("file_number"));
                    file.setCategory(rs.getString("category"));
                    file.setStatus(status);
                    file.setDateCreated(dateCreated);
                    file.setObservation(rs.getString("observation"));
                    file.setEmployeeId(rs.getLong("employee_id"));

                    return file;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar Legajo por ID de Empleado: " + e.getMessage(), e);
        }

        return null;
    }

    /**
     * Busca un legajo por su número único de legajo. Solo retorna legajos
     * activos.
     *
     * @param fileNumber El número de legajo a buscar.
     * @param conn es la conexión JDBC a utilizar.
     * @return El objeto EmployeeFile activo, o null si no se encuentra o está
     * eliminado.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public EmployeeFile findByFileNumber(String fileNumber, Connection conn) throws Exception {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE file_number = ? AND deleted = FALSE";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fileNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Setea fecha
                    Date dateCreatedSql = rs.getDate("date_created");
                    LocalDate dateCreated = dateCreatedSql != null ? dateCreatedSql.toLocalDate() : null;

                    // Setea enum
                    String statusStr = rs.getString("status");
                    FileStatus status = statusStr != null ? FileStatus.valueOf(statusStr) : FileStatus.ACTIVO;

                    // Crear y retornar EmployeeFile
                    EmployeeFile file = new EmployeeFile();
                    file.setId(rs.getLong("id"));
                    file.setDeleted(rs.getBoolean("deleted"));
                    file.setFileNumber(rs.getString("file_number"));
                    file.setCategory(rs.getString("category"));
                    file.setStatus(status);
                    file.setDateCreated(dateCreated);
                    file.setObservation(rs.getString("observation"));
                    file.setEmployeeId(rs.getLong("employee_id"));

                    return file;
                }
            }
        } catch (SQLException e) {
            throw new Exception("Error al buscar Legajo por Número de Legajo: " + e.getMessage(), e);
        }

        return null;
    }
}