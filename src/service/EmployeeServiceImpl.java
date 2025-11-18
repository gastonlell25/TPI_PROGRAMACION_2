package service;

import config.DatabaseConnection;
import config.TransactionManager;
import Models.Employee;
import utils.ValidationHelper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import dao.EmployeeDAO;

/**
 * Capa de servicio para la entidad Employee. Es responsable de aplicar y
 * realizar las validaciones y gestionar las transacción (conexión, commit y
 * rollback) para garantizar la atomicidad de las operaciones.
 */
public class EmployeeServiceImpl implements GenericService<Employee> {

    private final EmployeeDAO employeeDAO;

    // constructor del servicio. Inicializa las instancias de los DAOs necesarios.
    public EmployeeServiceImpl(EmployeeDAO employeeDAO) {
        if (employeeDAO == null) {
            throw new IllegalArgumentException("EmployeeDAO must not be null");
        }
        this.employeeDAO = employeeDAO;
    }

    private Connection getNonTransactionalConnection() throws Exception {
        return DatabaseConnection.getConnection();
    }

    /**
     * Inserta un nuevo empleado en la base de datos. Realiza validaciones
     * Gestiona una transacción atómica.
     *
     * @param employee El objeto Employee a insertar.
     * @throws SQLException Si ocurre un error de base de datos durante la
     * transacción.
     * @throws IllegalArgumentException Si la validacion falla.
     */
    @Override
    public void insert(Employee employee) throws Exception {
        try (TransactionManager tm = new TransactionManager(getNonTransactionalConnection())) {

            tm.startTransaction();
            Connection conn = tm.getConnection();

            ValidationHelper.validateEmployeeFormat(employee);

            employeeDAO.insert(employee, conn);

            tm.commit();
        }
    }

    /**
     * Actualiza los datos de un empleado existente. Realiza validaciones de
     * formato, existencia del ID y estado de baja lógica. Gestiona una
     * transacción atómica.
     *
     * @param employee El objeto Employee con los datos actualizados.
     * @throws SQLException Si ocurre un error de base de datos.
     * @throws IllegalArgumentException Si el ID es inválido o la validacion
     * flla.
     */
    @Override
    public void update(Employee employee) throws Exception {
        try (TransactionManager tm = new TransactionManager(getNonTransactionalConnection())) {

            tm.startTransaction();
            Connection conn = tm.getConnection();

            ValidationHelper.validateEmployeeFormat(employee);

            if (!ValidationHelper.isValidId(employee.getId())) {
                throw new IllegalArgumentException("Employee ID is invalid");
            }

            Employee existing = employeeDAO.getById(employee.getId(), conn);
            if (existing == null) {
                throw new IllegalArgumentException("Employee with ID " + employee.getId() + " not found");
            }
            if (existing.isDeleted()) {
                throw new IllegalArgumentException("Cannot update a deleted employee");
            }

            employeeDAO.update(employee, conn);
            tm.commit();
        }
    }

    @Override
    public void delete(Long id) throws Exception {
        try (TransactionManager tm = new TransactionManager(getNonTransactionalConnection())) {

            tm.startTransaction();
            Connection conn = tm.getConnection();

            if (!ValidationHelper.isValidId(id)) {
                throw new IllegalArgumentException("El ID es inválido");
            }

            Employee existing = employeeDAO.getById(id, conn);
            if (existing == null) {
                throw new IllegalArgumentException("Empleado con el ID " + id + " no se encontró");
            }
            if (existing.isDeleted()) {
                throw new IllegalArgumentException("El empleado ya fue eliminado");
            }

            employeeDAO.delete(id, conn);
            tm.commit();
        }
    }

    @Override
    public Employee getById(Long id) throws Exception {
        if (!ValidationHelper.isValidId(id)) {
            throw new IllegalArgumentException("El ID es inválido");
        }
        //Se establece y cierra conexión automaticamente
        try (Connection conn = getNonTransactionalConnection()) {
            return employeeDAO.getById(id, conn);
        }
    }

    @Override
    public List<Employee> getAll() throws Exception {
        //Se establece y cierra conexión automaticamente
        try (Connection conn = getNonTransactionalConnection()) {
            return employeeDAO.getAll(conn);
        }
    }

    public Employee findByLegalId(String legalId) throws Exception {
        if (!ValidationHelper.isValidString(legalId)) {
            throw new IllegalArgumentException("El DNI no puede estar vacío");
        }
        //Se establece y cierra conexión automaticamente
        try (Connection conn = getNonTransactionalConnection()) {
            return employeeDAO.findByLegalId(legalId, conn);
        }
    }
}