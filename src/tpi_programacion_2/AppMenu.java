package tpi_programacion_2;
import dao.EmployeeDAO;
import dao.EmployeeFileDAO;
import service.EmployeeServiceImpl;
import service.EmployeeFileServiceImpl;

import java.util.Scanner;


/**
 * Código para interactuar con el usuario que gestiona la aplicación por la terminal.
 */
public class AppMenu {

    private final Scanner scanner;
    private final MenuHandler handler;

    // Constructor: arma DAOs, Services y el handler
    public AppMenu() {
        this.scanner = new Scanner(System.in);

        // DAOs
        EmployeeDAO employeeDAO = new EmployeeDAO();
        EmployeeFileDAO employeeFileDAO = new EmployeeFileDAO();

        // Services
        EmployeeServiceImpl employeeService = new EmployeeServiceImpl(employeeDAO);
        EmployeeFileServiceImpl employeeFileService = new EmployeeFileServiceImpl(employeeFileDAO, employeeDAO);

        // Handler del menú (usa ambos servicios)
        this.handler = new MenuHandler(scanner, employeeService, employeeFileService);
    }

    // Método que corre el loop del menú
    public void run() {
        boolean salir = false;
        while (!salir) {
            MenuDisplay.mostrarMenuPrincipal();
            String opcionStr = scanner.nextLine().trim();

            switch (opcionStr) {
                case "1" -> handler.crearEmpleado();
                case "2" -> handler.listarEmpleados();
                case "3" -> handler.actualizarEmpleado();
                case "4" -> handler.eliminarEmpleado();
                case "5" -> handler.crearLegajoIndependiente();
                case "6" -> handler.listarLegajos();
                case "7" -> handler.actualizarLegajoPorId();
                case "8" -> handler.eliminarLegajoPorId();
                case "9" -> handler.actualizarLegajoPorEmpleado();
                case "10" -> handler.eliminarLegajoPorEmpleado();
                case "11" -> handler.listarEmpleadoPorId(); 
                case "12" -> handler.listarLegajoPorId(); 
                case "0" -> salir = true;
                default -> System.out.println("Opción inválida");
            }
        }

        System.out.println("Aplicación finalizada.");
        scanner.close();
    }
}
