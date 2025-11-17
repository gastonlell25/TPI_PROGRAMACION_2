
public final class MenuDisplay {

    private MenuDisplay() {
        throw new AssertionError("MenuDisplay no debe ser instanciada");

    }

    public static void mostrarMenuPrincipal() {
       System.out.println("\n========= MENU =========");
    System.out.println("1. Crear empleado");
    System.out.println("2. Listar empleados");
    System.out.println("3. Actualizar empleado");
    System.out.println("4. Eliminar empleado (baja lógica)");
    System.out.println("5. Crear legajo");
    System.out.println("6. Listar legajos");
    System.out.println("7. Actualizar legajo por ID");
    System.out.println("8. Eliminar legajo por ID");
    System.out.println("9. Actualizar legajo por ID de empleado");
    System.out.println("10. Eliminar legajo por ID de empleado");
    System.out.println("11. Buscar empleado por ID");
    System.out.println("12. Buscar legajo por ID"); 
    System.out.println("0. Salir");
    System.out.print("Ingrese una opcion: ");
    }
}
