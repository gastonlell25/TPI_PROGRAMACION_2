package dao;

import Models.Employee;
import Models.EmployeeFile;
import Models.FileStatus;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.time.LocalDate;

/**
 * DAO para la entidad Employee. Implementa las operaciones CRUD para la tabla
 * employee Esta implementación utiliza una Connection compartida para las
 * transacciones
 */
public class EmployeeDAO implements GenericDAO<Employee> {

    // Se establece nombre de la tabla para usar en las operaciones 
    private static final String TABLE_NAME = "employees";
    //Intaciamos employeeFileDAO para interactuar con las operaciones CRUD relacionales 
    private EmployeeFileDAO employeeFileDAO = new EmployeeFileDAO();

    /**
     * Inserta un nuevo objeto Employee en la base de datos.
     *
     * @param employee el objeto Employee a insertar.
     * @param conn La conexión JDBC a utilizar.
     * @throws SQLException Si ocurre un error de base de datos durante la
     * inserción.
     */
    @Override
    public Employee insert(Employee employee, Connection conn) throws Exception {
        //Se construye la consulta sql para interactuar con la base de datos.
        String sql = "INSERT INTO " + TABLE_NAME
                + " (first_name, last_name, legal_id, email, hire_date, area, deleted) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        //Se cargan los datos para ejecutar la consulta
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setString(3, employee.getLegalId());
            stmt.setString(4, employee.getEmail());
            stmt.setDate(5, employee.getHireDate() != null ? Date.valueOf(employee.getHireDate()) : null);
            stmt.setString(6, employee.getArea());
            stmt.setBoolean(7, employee.isDeleted());

            //Ejecutamos la consulta con los datos establecidos
            int rows = stmt.executeUpdate();

            if (rows == 0) {
                throw new SQLException("Fallo la inserción");
            }
            //Retornamos la clave autogenerada.
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    employee.setId(generatedKeys.getLong(1));
                    return employee;
                } else {
                    throw new SQLException("Fallo la inserción no se obtuvo id");
                }
            }
        } catch (SQLException e) { // Se captura cualquier error 
            throw new Exception("Error al insertar empleado: " + e.getMessage(), e);
        }
    }

    /**
     * Actualiza un objeto Employee existente en la base de datos. Solo
     * actualiza empleados que NO hayan sido borrados logicamente.
     *
     * @param employee El objeto Employee con los datos actualizados (debe tener
     * un ID válido).
     * @param conn es la conexión JDBC a utilizar.
     * @throws SQLException Si ocurre un error de base de datos o si no se
     * encuentra el empleado.
     */
    @Override
    public void update(Employee employee, Connection conn) throws Exception {
        String sql = "UPDATE employees SET first_name = ?, last_name = ?, legal_id = ?, email = ?, " + "hire_date = ?, area = ? WHERE id = ? AND deleted = FALSE";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setString(3, employee.getLegalId());
            stmt.setString(4, employee.getEmail());
            stmt.setDate(5, employee.getHireDate() != null ? Date.valueOf(employee.getHireDate()) : null);
            stmt.setString(6, employee.getArea());
            stmt.setLong(7, employee.getId());
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                throw new SQLException("Error:: No se actualizó nninguna fila");
            }

        } catch (SQLException e) {
            throw new Exception("Error al actualizar empleado: " + e.getMessage(), e);
        }
    }

    /**
     * Busca un empleado por su ID primario. Solo retorna empleados que NO hayan
     * sido dados de baja lógicamente (deleted = FALSE).
     *
     * @param id El ID del empleado a buscar.
     * @param conn es la conexión JDBC a utilizar.
     * @return El objeto Employee encontrado, incluyendo su EmployeeFile
     * asociado (relación 1:1), o null si no se encuentra.
     * @throws SQLException Si ocurre un error de base de datos.
     */
@Override
public Employee getById(Long id, Connection conn) throws SQLException {

    String sql = """
        SELECT 
            e.id AS e_id,
            e.deleted AS e_deleted,
            e.first_name,
            e.last_name,
            e.legal_id,
            e.email,
            e.hire_date,
            e.area,
            f.id as f_id,
            f.file_number AS f_file_number,
            f.category AS f_category,
            f.status AS f_status,
            f.date_created AS f_date_created,
            f.observation AS f_observation,
            f.employee_id AS f_employee_id,
            f.deleted AS f_deleted
            FROM employees e
            INNER JOIN employee_files f ON f.employee_id = e.id
            WHERE e.id = ?
              AND e.deleted = FALSE
        """;
    
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setLong(1, id);

        try (ResultSet rs = stmt.executeQuery()) {

            if (!rs.next()) {
                return null;
            }

            // ==========================
            // MAPEO DE EmployeeFile (si existe)
            // ==========================

            EmployeeFile employeeFile = null;

            String fileNumber = rs.getString("f_file_number");
            if (fileNumber != null) {  
                // Si file_number no es NULL, entonces existe un file asociado
                
                employeeFile = new EmployeeFile(
                rs.getLong("f_id"),
                rs.getBoolean("f_deleted"),
                fileNumber,
                rs.getString("f_category"),
                FileStatus.valueOf(rs.getString("f_status")),
                rs.getDate("f_date_created").toLocalDate(),
                rs.getString("f_observation"),
                rs.getLong("e_id")
            );
            }

            // ==========================
            // MAPEO DE Employee
            // ==========================
            LocalDate hireDateFormat = rs.getObject("hire_date", LocalDate.class);
            Employee employee = new Employee(
                rs.getLong("e_id"),
                rs.getBoolean("e_deleted"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("legal_id"),
                rs.getString("email"),
                hireDateFormat,
                rs.getString("area"),
                employeeFile
            );

            return employee;
        }
    }
}

    /**
     * Recupera todos los empleados activos de la base de datos. Solo lista
     * empleados que no hayan sido eliminados logicamente.
     *
     * @param conn es la conexión JDBC a utilizar.
     * @return Una lista de objetos Employee, cada uno con su EmployeeFile
     * asociado.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    @Override
    public List<Employee> getAll(Connection conn) throws Exception {
        List<Employee> employees = new ArrayList<>();
        String sql = """
            SELECT 
                e.id AS e_id,
                e.deleted AS e_deleted,
                e.first_name,
                e.last_name,
                e.legal_id,
                e.email,
                e.hire_date,
                e.area,
                f.id as f_id,
                f.file_number AS f_file_number,
                f.category AS f_category,
                f.status AS f_status,
                f.date_created AS f_date_created,
                f.observation AS f_observation,
                f.employee_id AS f_employee_id,
                f.deleted AS f_deleted
            FROM employees e
            INNER JOIN employee_files f ON f.employee_id = e.id
            WHERE
              e.deleted = FALSE
        """;
        
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                // ==========================
                // MAPEO DE EmployeeFile (si existe)
                // ==========================

                EmployeeFile employeeFile = null;

                String fileNumber = rs.getString("f_file_number");
                if (fileNumber != null) {  
                    // Si file_number no es NULL, entonces existe un file asociado

                    employeeFile = new EmployeeFile(
                        rs.getLong("f_id"),
                        rs.getBoolean("f_deleted"),
                        fileNumber,
                        rs.getString("f_category"),
                        FileStatus.valueOf(rs.getString("f_status")),
                        rs.getDate("f_date_created").toLocalDate(),
                        rs.getString("f_observation"),
                        rs.getLong("e_id")
                    );
                }

                // ==========================
                // MAPEO DE Employee
                // ==========================
                LocalDate hireDateFormat = rs.getObject("hire_date", LocalDate.class);
                Employee employee = new Employee(
                    rs.getLong("e_id"),
                    rs.getBoolean("e_deleted"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("legal_id"),
                    rs.getString("email"),
                    hireDateFormat,
                    rs.getString("area"),
                    employeeFile
                );


                employees.add(employee);
            }
        } catch (SQLException e) {
            throw new Exception("Error al obtener todos los empleados: " + e.getMessage(), e);
        }

        return employees;
    }

    /**
     * Busca un empleado por su número de identificación legal (DNI). Solo
     * retorna empleados que no hayan sido borrados logicamente.
     *
     * @param legalId El DNI/ID legal del empleado a buscar.
     * @param conn es la conexión JDBC a utilizar.
     * @return El objeto Employee encontrado, incluyendo su EmployeeFile
     * asociado, o null si no se encuentra.
     * @throws SQLException Si ocurre un error de base de datos.
     */
    public Employee findByLegalId(String legalId, Connection conn) throws Exception {
        String sql = """
                SELECT 
                    e.id AS e_id,
                    e.deleted AS e_deleted,
                    e.first_name,
                    e.last_name,
                    e.legal_id,
                    e.email,
                    e.hire_date,
                    e.area,
                    f.id as f_id,
                    f.file_number AS f_file_number,
                    f.category AS f_category,
                    f.status AS f_status,
                    f.date_created AS f_date_created,
                    f.observation AS f_observation,
                    f.employee_id AS f_employee_id,
                    f.deleted AS f_deleted
                    FROM employees e
                    INNER JOIN employee_files f ON f.employee_id = e.id
                    WHERE e.legal_id = ?
                      AND e.deleted = FALSE
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, legalId);

            try (ResultSet rs = stmt.executeQuery()) {

                if (!rs.next()) {
                    return null;
                }

                // ==========================
                // MAPEO DE EmployeeFile (si existe)
                // ==========================

                EmployeeFile employeeFile = null;

                String fileNumber = rs.getString("f_file_number");
                if (fileNumber != null) {  
                    // Si file_number no es NULL, entonces existe un file asociado

                    employeeFile = new EmployeeFile(
                    rs.getLong("f_id"),
                    rs.getBoolean("f_deleted"),
                    fileNumber,
                    rs.getString("f_category"),
                    FileStatus.valueOf(rs.getString("f_status")),
                    rs.getDate("f_date_created").toLocalDate(),
                    rs.getString("f_observation"),
                    rs.getLong("e_id")
                );
                }

                // ==========================
                // MAPEO DE Employee
                // ==========================
                LocalDate hireDateFormat = rs.getObject("hire_date", LocalDate.class);
                Employee employee = new Employee(
                    rs.getLong("e_id"),
                    rs.getBoolean("e_deleted"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("legal_id"),
                    rs.getString("email"),
                    hireDateFormat,
                    rs.getString("area"),
                    employeeFile
                );

                return employee;
            }
        }
    }

    /**
     * Realiza una eliminacion de baja logica del empleado. Esto significa que
     * el registro no se elimina físicamente, sino que se marca como eliminado
     * estableciendo el campo 'deleted' a TRUE.
     *
     * @param id es el ID del empleado a marcar como eliminado.
     * @param conn es la conexión JDBC a utilizar.
     * @throws SQLException Si ocurre un error de base de datos o si no se
     * encuentra el empleado.
     */
    @Override
    public void delete(Long id, Connection conn) throws Exception {
        String sql = "UPDATE " + TABLE_NAME + " SET deleted = TRUE WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated == 0) {
                throw new SQLException("No se pudo borrar porque no se encotró empleado con este id: " + id);
            }
        } catch (SQLException e) {
            throw new Exception("Error al realizar baja lógica de empleado: " + e.getMessage(), e);
        }
    }
}