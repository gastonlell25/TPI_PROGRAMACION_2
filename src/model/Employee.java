/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models;
import java.time.LocalDate;
import java.util.Objects;
/**
 *
 * @author gastonlell
 */
public class Employee extends BaseEntity {
    
    private String firstName;
    private String lastName;
    private String legalId;
    private String email;
    private LocalDate hireDate;
    private String area;
    private EmployeeFile employeeFile;

    //CONSTRUCTOR VACIO 
    public Employee() {
        super();
    }

    // CONSTRUCTOR CON PARAMETROS
    public Employee(String firstName, String lastName, String legalId, String email, LocalDate hireDate, String area) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.legalId = legalId;
        this.email = email;
        this.hireDate = hireDate;
        this.area = area;
        this.employeeFile = null;  // CAMPO DE LA RELACION 
    }

    // CONSTRUCTOR PARA GENERAR NUEVOS EMPLEADOS EN LA BASE DE DATOS
    public Employee(Long id, boolean deleted, String firstName, String lastName, String legalId, String email, LocalDate hireDate, String area, EmployeeFile employeeFile) {
        super(id, deleted);
        this.firstName = firstName;
        this.lastName = lastName;
        this.legalId = legalId;
        this.email = email;
        this.hireDate = hireDate;
        this.area = area;
        this.employeeFile = employeeFile;
    }

    // GETTERS Y SETTERS
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLegalId() {
        return legalId;
    }

    public void setLegalId(String legalId) {
        this.legalId = legalId;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public EmployeeFile getEmployeeFile() {
        return employeeFile;
    }

    public void setEmployeeFile(EmployeeFile employeeFile) {
        this.employeeFile = employeeFile;
    }

    @Override
    public String toString() {
        return "Employee{"
                + "id=" + getId()
                + ", deleted=" + isDeleted()
                + ", firstName='" + firstName + '\''
                + ", lastName='" + lastName + '\''
                + ", legalId='" + legalId + '\''
                + ", email='" + email + '\''
                + ", hireDate='" + hireDate + '\''
                + ", area='" + area + '\''
                + ", employeeFile='" + (employeeFile != null ? employeeFile.getFileNumber() : "No tiene Legajo")
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        // Verifica Ids iguales 
        if (this == o) {
            return true;
        }

        // Verifica si es es null o no es la misma clase
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Employee employee = (Employee) o;

        // Verifica unicidad 
        return Objects.equals(this.legalId, employee.legalId);
    }

    @Override
    public int hashCode() {
        // Genera el hash basado solo en el legal_id unico
        return Objects.hash(legalId);
    }
}
