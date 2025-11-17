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
public class EmployeeFile extends BaseEntity {
    
    private String fileNumber;
    private String category;
    private FileStatus status;
    private LocalDate dateCreated;
    private String observation;
    private Long employeeId; // CAMPO DE RELACION CON EMPLEADO

    // CONSTRUCTOR VACIO CON CALORES POR DEFECTOS
    public EmployeeFile() {
        super();
        this.status = FileStatus.ACTIVO;
        this.dateCreated = LocalDate.now();
    }

    // CONSTRUCTOR SIN ID, PARA QUENERACION AUTOMATICA POR DB
    public EmployeeFile(String fileNumber, String category, FileStatus status, LocalDate dateCreated, String observation, Long employeeId) {
        super();
        this.fileNumber = fileNumber;
        this.category = category;
        this.status = status;
        this.dateCreated = dateCreated;
        this.observation = observation;
        this.employeeId = employeeId;
    }

    // CONSTRUCTOR CON TODOS LOS CAMPOS COMPLETOS
    public EmployeeFile(Long id, boolean deleted, String fileNumber, String category, FileStatus status, LocalDate dateCreated, String observation, Long employeeId) {
        super(id, deleted);
        this.fileNumber = fileNumber;
        this.category = category;
        this.status = status;
        this.dateCreated = dateCreated;
        this.observation = observation;
        this.employeeId = employeeId;
    }

    //GETTERS Y SETTERS
    public String getFileNumber() {
        return fileNumber;

    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public FileStatus getStatus() {
        return status;
    }

    public void setStatus(FileStatus status) {
        this.status = status;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeedId) {
        this.employeeId = employeedId;
    }

    @Override
    public String toString() {
        return "EmployeeFile "
                + "id=" + getId()
                + ", fileNumber=" + getFileNumber()
                + ", category='" + category + '\''
                + ", status='" + status + '\''
                + ", dateCreated='" + dateCreated + '\''
                + ", observations='" + observation + '\''
                + ", employeeId='" + employeeId + '\''
                + ", deleted='" + isDeleted()
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EmployeeFile that = (EmployeeFile) o;

        // Verifica que no haya legajos iguales
        return Objects.equals(fileNumber, that.fileNumber);
    }

    @Override
    public int hashCode() {
        // Genera el hash code unico para fileNumber.
        return Objects.hash(fileNumber);
    }
}