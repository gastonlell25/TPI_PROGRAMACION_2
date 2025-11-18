package service;

import config.DatabaseConnection;
import config.TransactionManager;
import dao.EmployeeDAO;
import dao.EmployeeFileDAO;
import Models.Employee;
import Models.EmployeeFile;

import java.sql.Connection;
import java.util.List;
import utils.ValidationHelper;

/**
 * Capa de servicio para la entidad EmployeeFile. Se aplica las validaciones. La
 * gestión de transacciones (commit/rollback) para garantizar operaciones
 * atomicas. Centralizar la obtención y el cierre de la conexión (conn).
 */
public class EmployeeFileServiceImpl implements GenericService<EmployeeFile> {

    // Establecemos la referencia de los DAOs.
    private final EmployeeFileDAO employeeFileDAO;
    private final EmployeeDAO employeeDAO;

    // constructor del servicio. Inicializa las instancias de los DAOs necesarios.
    public EmployeeFileServiceImpl(EmployeeFileDAO employeeFileDAO, EmployeeDAO employeeDAO) {
        if (employeeFileDAO == null || employeeDAO == null) {
            throw new IllegalArgumentException("EmployeeFileDAO y EmployeeDAO no deben ser nulos");
        }
        this.employeeFileDAO = employeeFileDAO;
        this.employeeDAO = employeeDAO;
    }

    // Establecemos la conexión a la configuracion de la base de datos. 
    private Connection getNonTransactionalConnection() throws Exception {
        return DatabaseConnection.getConnection();
    }

    /**
     * Crea un nuevo legajo de empleado. Realiza validaciones. Gestiona una
     * transacción atómica (commit/rollback).
     *
     * @param file El objeto EmployeeFile a insertar.
     * @throws SQLException Si ocurre un error de base de datos durante la
     * transacción.
     * @throws IllegalArgumentException Si la validación falla.
     */
    @Override
    public void insert(EmployeeFile file) throws Exception {
        //  Valida el formato correcto
        ValidationHelper.validateEmployeeFileFormat(file);

        // Gestiona e inicia la transación con  el TransactionManager
        try (TransactionManager tm = new TransactionManager(getNonTransactionalConnection())) {
            tm.startTransaction();
            Connection conn = tm.getConnection();

            // Verificamos unicidad referecnial que no haya duplicados.
            EmployeeFile existingByNumber = employeeFileDAO.findByFileNumber(file.getFileNumber(), conn);
            if (existingByNumber != null) {
                throw new IllegalArgumentException("El número de legajo " + file.getFileNumber() + " ya existe.");
            }

            // Verificamos y establecemos realción con la tabla employee
            Employee employee = employeeDAO.getById(file.getEmployeeId(), conn);
            if (employee == null) {
                throw new IllegalArgumentException(
                        "No existe un empleado con ID " + file.getEmployeeId() + " para asociar el legajo."
                );
            }

            // Verificamos que no exista el legajo
            EmployeeFile existingByEmployee = employeeFileDAO.findByEmployeeId(file.getEmployeeId(), conn);
            if (existingByEmployee != null) {
                throw new IllegalArgumentException(
                        "El empleado con ID " + file.getEmployeeId() + " ya tiene un legajo asignado."
                );
            }

            // Se ejecuta el Commit si esta todo ok
            employeeFileDAO.insert(file, conn);
            tm.commit();
        }
    }

    /**
     * Actualiza un legajo de empleado existente y hace las validaciones.
     *
     * @param file El objeto EmployeeFile con los datos actualizados.
     * @throws SQLException Si ocurre un error de base de datos durante la
     * transacción.
     * @throws IllegalArgumentException si la validacion falla.
     */
    @Override
    public void update(EmployeeFile file) throws Exception {
        // Validacion de formato
        ValidationHelper.validateEmployeeFileFormat(file);

        if (!ValidationHelper.isValidId(file.getId())) {
            throw new IllegalArgumentException("El ID de legajo es inválido.");
        }

        // Gestiona la transaccion con TransactionManager
        try (TransactionManager tm = new TransactionManager(getNonTransactionalConnection())) {
            tm.startTransaction();
            Connection conn = tm.getConnection();

            // Verificamos la existencia del id
            EmployeeFile existing = employeeFileDAO.getById(file.getId(), conn);
            if (existing == null) {
                throw new IllegalArgumentException("Legajo con el ID " + file.getId() + " no se encontró.");
            }
            //Verificamos que el legejo no se ecuentre eliminado logicamente. 
            if (existing.isDeleted()) {
                throw new IllegalArgumentException("No se puede actualizar un legajo eliminado.");
            }

            // Verificamos unicida del legajo. 
            EmployeeFile byNumber = employeeFileDAO.findByFileNumber(file.getFileNumber(), conn);
            if (byNumber != null && !byNumber.getId().equals(file.getId())) {
                throw new IllegalArgumentException(
                        "Ya existe otro legajo con el número " + file.getFileNumber()
                );
            }

            // Se ejecuta el Commit si esta todo ok
            employeeFileDAO.update(file, conn);
            tm.commit();
        }
    }

    @Override
    public void delete(Long id) throws Exception {
        //Verifica que el id sea correcto
        if (!ValidationHelper.isValidId(id)) {
            throw new IllegalArgumentException("El ID de legajo es inválido.");
        }

        //Se genera la transacción 
        try (TransactionManager tm = new TransactionManager(getNonTransactionalConnection())) {
            tm.startTransaction();
            Connection conn = tm.getConnection();

            //Verificamos si existe el id
            EmployeeFile existing = employeeFileDAO.getById(id, conn);
            if (existing == null) {
                throw new IllegalArgumentException("Legajo con el ID " + id + " no se encontró.");
            }
            //Verificamos que el id ingresado no haya sido eliminado previamentes. 
            if (existing.isDeleted()) {
                throw new IllegalArgumentException("El legajo ya fue eliminado.");
            }

            //Se ejecuta commit si todo esta ok. 
            employeeFileDAO.delete(id, conn);
            tm.commit();
        }
    }

    @Override
    public EmployeeFile getById(Long id) throws Exception {
        if (!ValidationHelper.isValidId(id)) {
            throw new IllegalArgumentException("El ID de legajo es inválido.");
        }

        try (Connection conn = getNonTransactionalConnection()) {
            return employeeFileDAO.getById(id, conn);
        }
    }

    @Override
    public List<EmployeeFile> getAll() throws Exception {
        try (Connection conn = getNonTransactionalConnection()) {
            return employeeFileDAO.getAll(conn);
        }
    }

    public EmployeeFile findByEmployeeId(Long employeeId) throws Exception {
        if (!ValidationHelper.isValidId(employeeId)) {
            throw new IllegalArgumentException("El ID de empleado es inválido.");
        }

        try (Connection conn = getNonTransactionalConnection()) {
            return employeeFileDAO.findByEmployeeId(employeeId, conn);
        }
    }

    public EmployeeFile findByFileNumber(String fileNumber) throws Exception {
        if (!ValidationHelper.isValidString(fileNumber)) {
            throw new IllegalArgumentException("El número de legajo no puede estar vacío.");
        }

        try (Connection conn = getNonTransactionalConnection()) {
            return employeeFileDAO.findByFileNumber(fileNumber, conn);
        }
    }
}