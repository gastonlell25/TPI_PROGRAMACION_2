/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import Models.Employee;
import Models.EmployeeFile;

public final class ValidationHelper {

    private ValidationHelper() {
    }

    // VALIDACION DE STRING
    public static boolean isValidString(String s) {
        return s != null && !s.trim().isEmpty();
    }

    // VALIDACION DE LONGITUD DE STRING
    public static boolean hasLength(String s, int min, int max) {
        if (s == null) {
            return false;
        }
        int length = s.trim().length();
        return length >= min && length <= max;
    }

    //VALIDACION DE EMIAL Y FORMADO
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    //VALIDACION DE DNI 
    public static boolean isValidId(Long id) {
        return id != null && id > 0;
    }

    // VALIDACION DE FORMATO DE DNI 
    public static boolean isValidDNI(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            return false;
        }
        // VALIDACION DE FORMATO NUMERICO
        return dni.matches("^[0-9]{7,8}$");
    }

    // VALIDACIONES DE FORMATO PARA EMPLOYEE ENTITY
    public static void validateEmployeeFormat(Employee employee) {
        if (employee == null) {
            throw new IllegalArgumentException("El empleado no puede ser null");
        }

        if (!isValidString(employee.getFirstName())) {
            throw new IllegalArgumentException("El nombre es requerido");
        }

        if (!hasLength(employee.getFirstName(), 2, 50)) {
            throw new IllegalArgumentException("El nombre debe tener entre 2 y 50 caracteres");
        }

        if (!isValidString(employee.getLastName())) {
            throw new IllegalArgumentException("El apellido es requerido");
        }

        if (!hasLength(employee.getLastName(), 2, 50)) {
            throw new IllegalArgumentException("El apellido debe tener entre 2 y 50 caracteres");
        }

        if (!isValidString(employee.getLegalId())) {
            throw new IllegalArgumentException("El DNI es requerido");
        }

        if (!isValidDNI(employee.getLegalId())) {
            throw new IllegalArgumentException("El DNI debe tener 7-8 dígitos numéricos");
        }

        if (!isValidString(employee.getEmail())) {
            throw new IllegalArgumentException("El email es requerido");
        }

        if (!isValidEmail(employee.getEmail())) {
            throw new IllegalArgumentException("El formato del email es inválido");
        }
    }

    //  VALIDACIONES DE FORMATO PARA EMPLOYEE FILE 
    public static void validateEmployeeFileFormat(EmployeeFile file, boolean validateEmployeeId) {
        if (file == null) {
            throw new IllegalArgumentException("El legajo no puede ser null");
        }

        if (!isValidString(file.getFileNumber())) {
            throw new IllegalArgumentException("El número de legajo es requerido");
        }

        if (!hasLength(file.getFileNumber(), 5, 20)) {
            throw new IllegalArgumentException("El número de legajo debe tener entre 5 y 20 caracteres");
        }

        if (!isValidString(file.getCategory())) {
            throw new IllegalArgumentException("La categoría es requerida");
        }

        if (file.getStatus() == null) {
            throw new IllegalArgumentException("El estado es requerido");
        }

        if (file.getDateCreated() == null) {
            throw new IllegalArgumentException("La fecha de creación es requerida");
        }

        if (validateEmployeeId && !isValidId(file.getEmployeeId())) {
            throw new IllegalArgumentException("El ID de empleado es requerido y debe ser válido");
        }

    }

    public static void validateEmployeeFileFormat(EmployeeFile file) {
        validateEmployeeFileFormat(file, true);
    }

}