package main;
import Models.Employee;
import Models.EmployeeFile;
import Models.FileStatus;
import service.EmployeeServiceImpl;
import service.EmployeeFileServiceImpl;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class MenuHandler {

    private final Scanner scanner;
    private final EmployeeServiceImpl employeeService;
    private final EmployeeFileServiceImpl employeeFileService;

    public MenuHandler(Scanner scanner,
            EmployeeServiceImpl employeeService,
            EmployeeFileServiceImpl employeeFileService) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner no puede ser null");
        }
        if (employeeService == null) {
            throw new IllegalArgumentException("EmployeeServiceImpl no puede ser null");
        }
        if (employeeFileService == null) {
            throw new IllegalArgumentException("EmployeeFileServiceImpl no puede ser null");
        }
        this.scanner = scanner;
        this.employeeService = employeeService;
        this.employeeFileService = employeeFileService;
    }

    // 1. Crear empleado
    public void crearEmpleado() {
        try {
            System.out.print("Nombre: ");
            String firstName = scanner.nextLine().trim();

            System.out.print("Apellido: ");
            String lastName = scanner.nextLine().trim();

            System.out.print("DNI / Legal ID: ");
            String legalId = scanner.nextLine().trim();

            System.out.print("Email: ");
            String email = scanner.nextLine().trim();

            System.out.print("Fecha de ingreso (yyyy-MM-dd, Enter para dejar null): ");
            String hireDateStr = scanner.nextLine().trim();
            LocalDate hireDate = null;
            if (!hireDateStr.isEmpty()) {
                try {
                    hireDate = LocalDate.parse(hireDateStr);
                } catch (DateTimeParseException e) {
                    System.out.println("Formato de fecha inválido, se deja null.");
                }
            }

            System.out.print("Área: ");
            String area = scanner.nextLine().trim();

            Employee employee = new Employee();
            employee.setDeleted(false);
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setLegalId(legalId);
            employee.setEmail(email);
            employee.setHireDate(hireDate);
            employee.setArea(area);

            // 1) Crear empleado (service maneja transacción y validaciones)
            employeeService.insert(employee);
            System.out.println("Empleado creado exitosamente con ID: " + employee.getId());

            // 2) Preguntar si quiere crear legajo
            System.out.print("¿Desea crear también un legajo para este empleado? (s/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
                EmployeeFile file = crearLegajoInteractivo();
                file.setEmployeeId(employee.getId());
                employeeFileService.insert(file);
                employee.setEmployeeFile(file); // solo en memoria
                System.out.println("Legajo creado con ID: " + file.getId());
            }

        } catch (Exception e) {
            System.err.println("Error al crear empleado: " + e.getMessage());
        }
    }

    // 2. Listar empleados
    public void listarEmpleados() {
        try {
            List<Employee> employees = employeeService.getAll();

            if (employees.isEmpty()) {
                System.out.println("No se encontraron empleados.");
                return;
            }

            for (Employee e : employees) {
                System.out.println("----------------------------------------");
                System.out.println("ID: " + e.getId());
                System.out.println("Nombre: " + e.getFirstName() + " " + e.getLastName());
                System.out.println("Legal ID: " + e.getLegalId());
                System.out.println("Email: " + e.getEmail());
                System.out.println("Fecha ingreso: " + e.getHireDate());
                System.out.println("Área: " + e.getArea());
                System.out.println("Eliminado (soft): " + e.isDeleted());

                EmployeeFile f = e.getEmployeeFile();
                if (f != null) {
                    System.out.println("  Legajo ID: " + f.getId());
                    System.out.println("  N° legajo: " + f.getFileNumber());
                    System.out.println("  Categoría: " + f.getCategory());
                    System.out.println("  Estado: " + f.getStatus());
                    System.out.println("  Fecha creación: " + f.getDateCreated());
                    System.out.println("  Observación: " + f.getObservation());
                } else {
                    System.out.println("  (Sin legajo asociado)");
                }
            }
        } catch (Exception e) {
            System.err.println("Error al listar empleados: " + e.getMessage());
        }
    }

    // 3. Actualizar empleado
    public void actualizarEmpleado() {
        try {
            System.out.print("ID del empleado a actualizar: ");
            Long id = Long.parseLong(scanner.nextLine());

            Employee e = employeeService.getById(id);
            if (e == null || e.isDeleted()) {
                System.out.println("Empleado no encontrado o dado de baja.");
                return;
            }

            System.out.print("Nuevo nombre (actual: " + e.getFirstName() + ", Enter para mantener): ");
            String firstName = scanner.nextLine().trim();
            if (!firstName.isEmpty()) {
                e.setFirstName(firstName);
            }

            System.out.print("Nuevo apellido (actual: " + e.getLastName() + ", Enter para mantener): ");
            String lastName = scanner.nextLine().trim();
            if (!lastName.isEmpty()) {
                e.setLastName(lastName);
            }

            System.out.print("Nuevo Legal ID (actual: " + e.getLegalId() + ", Enter para mantener): ");
            String legalId = scanner.nextLine().trim();
            if (!legalId.isEmpty()) {
                e.setLegalId(legalId);
            }

            System.out.print("Nuevo email (actual: " + e.getEmail() + ", Enter para mantener): ");
            String email = scanner.nextLine().trim();
            if (!email.isEmpty()) {
                e.setEmail(email);
            }

            System.out.print("Nueva fecha ingreso (actual: " + e.getHireDate()
                    + ", formato yyyy-MM-dd, Enter para mantener): ");
            String hireDateStr = scanner.nextLine().trim();
            if (!hireDateStr.isEmpty()) {
                try {
                    e.setHireDate(LocalDate.parse(hireDateStr));
                } catch (DateTimeParseException ex) {
                    System.out.println("Formato de fecha inválido, se mantiene valor actual.");
                }
            }

            System.out.print("Nueva área (actual: " + e.getArea() + ", Enter para mantener): ");
            String area = scanner.nextLine().trim();
            if (!area.isEmpty()) {
                e.setArea(area);
            }

            // Manejar legajo asociado (crear / actualizar)
            actualizarLegajoDeEmpleado(e);

            employeeService.update(e);
            System.out.println("Empleado actualizado exitosamente.");
        } catch (Exception e) {
            System.err.println("Error al actualizar empleado: " + e.getMessage());
        }
    }

    // 4. Eliminar empleado logicamente
    public void eliminarEmpleado() {
        try {
            System.out.print("ID del empleado a eliminar (baja lógica): ");
            Long id = Long.parseLong(scanner.nextLine());

            employeeService.delete(id);
            System.out.println("Empleado eliminado (baja lógica) exitosamente.");
        } catch (Exception e) {
            System.err.println("Error al eliminar empleado: " + e.getMessage());
        }
    }

    // 5. Crear legajo (asociado a empleado)
    public void crearLegajoIndependiente() {
        try {
            EmployeeFile file = crearLegajoInteractivo();

            System.out.print("ID de empleado para asociar este legajo (Enter para dejar sin asociar): ");
            String empIdStr = scanner.nextLine().trim();
            if (!empIdStr.isEmpty()) {
                Long empId = Long.parseLong(empIdStr);
                file.setEmployeeId(empId);
            }

            employeeFileService.insert(file);
            System.out.println("Legajo creado exitosamente con ID: " + file.getId());
        } catch (Exception e) {
            System.err.println("Error al crear legajo: " + e.getMessage());
        }
    }

    // 6. Listar legajos
    public void listarLegajos() {
        try {
            List<EmployeeFile> files = employeeFileService.getAll();

            if (files.isEmpty()) {
                System.out.println("No se encontraron legajos.");
                return;
            }

            for (EmployeeFile f : files) {
                System.out.println("----------------------------------------");
                System.out.println("Legajo ID: " + f.getId());
                System.out.println("N° legajo: " + f.getFileNumber());
                System.out.println("Categoría: " + f.getCategory());
                System.out.println("Estado: " + f.getStatus());
                System.out.println("Fecha creación: " + f.getDateCreated());
                System.out.println("Observación: " + f.getObservation());
                System.out.println("Empleado ID asociado: " + f.getEmployeeId());
                System.out.println("Eliminado (soft): " + f.isDeleted());
            }
        } catch (Exception e) {
            System.err.println("Error al listar legajos: " + e.getMessage());
        }
    }

    // 7. Actualizar legajo por ID
    public void actualizarLegajoPorId() {
        try {
            System.out.print("ID del legajo a actualizar: ");
            Long id = Long.parseLong(scanner.nextLine());

            EmployeeFile f = employeeFileService.getById(id);
            if (f == null || f.isDeleted()) {
                System.out.println("Legajo no encontrado o dado de baja.");
                return;
            }

            actualizarLegajoPorIdInterno(f);
            employeeFileService.update(f);
            System.out.println("Legajo actualizado exitosamente.");
        } catch (Exception e) {
            System.err.println("Error al actualizar legajo: " + e.getMessage());
        }
    }

    // 8. Eliminar logicamente legajo por ID 
    // =========================
    public void eliminarLegajoPorId() {
        try {
            System.out.print("ID del legajo a eliminar (baja lógica): ");
            Long id = Long.parseLong(scanner.nextLine());

            employeeFileService.delete(id);
            System.out.println("Legajo eliminado (baja lógica) exitosamente.");
        } catch (Exception e) {
            System.err.println("Error al eliminar legajo: " + e.getMessage());
        }
    }

    // 9. Actualizar legajo por ID de empleado
    public void actualizarLegajoPorEmpleado() {
        try {
            System.out.print("ID del empleado cuyo legajo desea actualizar: ");
            Long empId = Long.parseLong(scanner.nextLine());

            EmployeeFile f = employeeFileService.findByEmployeeId(empId);
            if (f == null || f.isDeleted()) {
                System.out.println("No se encontró legajo activo para este empleado.");
                return;
            }

            System.out.println("Actualizando legajo ID: " + f.getId());
            actualizarLegajoPorIdInterno(f);
            employeeFileService.update(f);
            System.out.println("Legajo actualizado exitosamente.");
        } catch (Exception e) {
            System.err.println("Error al actualizar legajo por empleado: " + e.getMessage());
        }
    }

    // 10. Eliminar legajo por ID de empleado
    public void eliminarLegajoPorEmpleado() {
        try {
            System.out.print("ID del empleado cuyo legajo desea eliminar: ");
            Long empId = Long.parseLong(scanner.nextLine());

            EmployeeFile f = employeeFileService.findByEmployeeId(empId);
            if (f == null || f.isDeleted()) {
                System.out.println("No se encontró legajo activo para este empleado.");
                return;
            }

            employeeFileService.delete(f.getId());
            System.out.println("Legajo eliminado exitosamente (baja lógica).");
        } catch (Exception e) {
            System.err.println("Error al eliminar legajo por empleado: " + e.getMessage());
        }
    }

    // Métodos auxiliares privados
    private EmployeeFile crearLegajoInteractivo() {
        EmployeeFile file = new EmployeeFile();
        file.setDeleted(false);
        file.setStatus(FileStatus.ACTIVO); // valor por defecto

        System.out.print("Número de legajo: ");
        file.setFileNumber(scanner.nextLine().trim());

        System.out.print("Categoría: ");
        file.setCategory(scanner.nextLine().trim());

        System.out.print("Fecha de creación (yyyy-MM-dd, Enter para dejar null): ");
        String dateStr = scanner.nextLine().trim();
        if (!dateStr.isEmpty()) {
            try {
                file.setDateCreated(LocalDate.parse(dateStr));
            } catch (DateTimeParseException e) {
                System.out.println("Formato de fecha inválido, se deja null.");
            }
        }

        System.out.print("Observación (Enter para vacío): ");
        String obs = scanner.nextLine().trim();
        if (!obs.isEmpty()) {
            file.setObservation(obs);
        }

        return file;
    }

    private void actualizarLegajoDeEmpleado(Employee e) throws Exception {
        EmployeeFile f = e.getEmployeeFile();

        if (f != null) {
            System.out.print("El empleado tiene legajo. ¿Desea actualizarlo? (s/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
                actualizarLegajoPorIdInterno(f);
                employeeFileService.update(f);
            }
        } else {
            System.out.print("El empleado no tiene legajo. ¿Desea crear uno? (s/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("s")) {
                EmployeeFile nuevo = crearLegajoInteractivo();
                nuevo.setEmployeeId(e.getId());
                employeeFileService.insert(nuevo);
                e.setEmployeeFile(nuevo);
            }
        }
    }

    private void actualizarLegajoPorIdInterno(EmployeeFile f) {
        System.out.print("Nuevo número de legajo (actual: " + f.getFileNumber() + ", Enter para mantener): ");
        String fileNumber = scanner.nextLine().trim();
        if (!fileNumber.isEmpty()) {
            f.setFileNumber(fileNumber);
        }

        System.out.print("Nueva categoría (actual: " + f.getCategory() + ", Enter para mantener): ");
        String category = scanner.nextLine().trim();
        if (!category.isEmpty()) {
            f.setCategory(category);
        }

        System.out.print("Nuevo estado (actual: " + f.getStatus()
                + ", valores enum, Enter para mantener): ");
        String statusStr = scanner.nextLine().trim();
        if (!statusStr.isEmpty()) {
            try {
                f.setStatus(FileStatus.valueOf(statusStr.toUpperCase()));
            } catch (IllegalArgumentException ex) {
                System.out.println("Estado inválido, se mantiene valor actual.");
            }
        }

        System.out.print("Nueva fecha creación (actual: " + f.getDateCreated()
                + ", formato yyyy-MM-dd, Enter para mantener): ");
        String dateStr = scanner.nextLine().trim();
        if (!dateStr.isEmpty()) {
            try {
                f.setDateCreated(LocalDate.parse(dateStr));
            } catch (DateTimeParseException ex) {
                System.out.println("Formato de fecha inválido, se mantiene valor actual.");
            }
        }

        System.out.print("Nueva observación (actual: " + f.getObservation() + ", Enter para mantener): ");
        String obs = scanner.nextLine().trim();
        if (!obs.isEmpty()) {
            f.setObservation(obs);
        }
    }

    public void listarEmpleadoPorId() {
        try {
            System.out.print("Ingrese el ID del empleado: ");
            Long id = Long.parseLong(scanner.nextLine().trim());

            Employee e = employeeService.getById(id);

            if (e == null) {
                System.out.println("No se encontró un empleado con ID " + id);
                return;
            }

            System.out.println("----------------------------------------");
            System.out.println("ID: " + e.getId());
            System.out.println("Nombre: " + e.getFirstName() + " " + e.getLastName());
            System.out.println("Legal ID: " + e.getLegalId());
            System.out.println("Email: " + e.getEmail());
            System.out.println("Fecha ingreso: " + e.getHireDate());
            System.out.println("Área: " + e.getArea());
            System.out.println("Eliminado (soft): " + e.isDeleted());

            EmployeeFile f = e.getEmployeeFile();
            if (f != null) {
                System.out.println("  Legajo ID: " + f.getId());
                System.out.println("  N° legajo: " + f.getFileNumber());
                System.out.println("  Categoría: " + f.getCategory());
                System.out.println("  Estado: " + f.getStatus());
                System.out.println("  Fecha creación: " + f.getDateCreated());
                System.out.println("  Observación: " + f.getObservation());
            } else {
                System.out.println("  (Sin legajo asociado)");
            }

        } catch (NumberFormatException ex) {
            System.out.println("ID inválido. Debe ser numérico.");
        } catch (Exception e) {
            System.err.println("Error al buscar empleado por ID: " + e.getMessage());
        }
    }

    public void listarLegajoPorId() {
        try {
            System.out.print("Ingrese el ID del legajo: ");
            Long id = Long.parseLong(scanner.nextLine().trim());

            EmployeeFile f = employeeFileService.getById(id);

            if (f == null) {
                System.out.println("No se encontró un legajo con ID " + id);
                return;
            }

            System.out.println("----------------------------------------");
            System.out.println("Legajo ID: " + f.getId());
            System.out.println("N° legajo: " + f.getFileNumber());
            System.out.println("Categoría: " + f.getCategory());
            System.out.println("Estado: " + f.getStatus());
            System.out.println("Fecha creación: " + f.getDateCreated());
            System.out.println("Observación: " + f.getObservation());
            System.out.println("Empleado ID asociado: " + f.getEmployeeId());
            System.out.println("Eliminado (soft): " + f.isDeleted());

        } catch (NumberFormatException ex) {
            System.out.println("ID inválido. Debe ser numérico.");
        } catch (Exception e) {
            System.err.println("Error al buscar legajo por ID: " + e.getMessage());
        }
    }
}
