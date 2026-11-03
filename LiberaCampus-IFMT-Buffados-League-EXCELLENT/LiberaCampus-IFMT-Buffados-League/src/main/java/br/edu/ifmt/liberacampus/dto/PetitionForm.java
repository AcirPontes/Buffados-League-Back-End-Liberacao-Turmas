package br.edu.ifmt.liberacampus.dto;

import br.edu.ifmt.liberacampus.model.RepresentativeRole;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class PetitionForm {

    @NotNull(message = "Informe a data pretendida para a liberação.")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate requestedDate;

    @NotNull(message = "Informe o primeiro horário/aula.")
    @Min(value = 1, message = "O horário mínimo é 1.")
    @Max(value = 12, message = "O horário máximo é 12.")
    private Integer firstPeriod;

    @NotNull(message = "Informe o último horário/aula.")
    @Min(value = 1, message = "O horário mínimo é 1.")
    @Max(value = 12, message = "O horário máximo é 12.")
    private Integer lastPeriod;

    @Size(max = 500, message = "O campo deve ter até 500 caracteres.")
    private String affectedSubjects;

    @NotNull(message = "Informe a quantidade estimada de estudantes.")
    @Min(value = 1, message = "A quantidade deve ser maior que zero.")
    @Max(value = 100, message = "A quantidade máxima para este formulário é 100.")
    private Integer expectedStudents;

    @NotBlank(message = "Descreva o motivo da solicitação.")
    @Size(min = 20, max = 1800, message = "O motivo deve conter entre 20 e 1.800 caracteres.")
    private String reason;

    @NotNull(message = "Informe sua condição de representação.")
    private RepresentativeRole representativeRole;

    private boolean includeDabc;

    public LocalDate getRequestedDate() {
        return requestedDate;
    }
    public void setRequestedDate(LocalDate requestedDate) {
        this.requestedDate = requestedDate;
    }
    public Integer getFirstPeriod() {
        return firstPeriod;
    }
    public void setFirstPeriod(Integer firstPeriod) {
        this.firstPeriod = firstPeriod;
    }
    public Integer getLastPeriod() {
        return lastPeriod;
    }
    public void setLastPeriod(Integer lastPeriod) {
        this.lastPeriod = lastPeriod;
    }
    public String getAffectedSubjects() {
        return affectedSubjects;
    }
    public void setAffectedSubjects(String affectedSubjects) {
        this.affectedSubjects = affectedSubjects;
    }
    public Integer getExpectedStudents() {
        return expectedStudents;
    }
    public void setExpectedStudents(Integer expectedStudents) {
        this.expectedStudents = expectedStudents;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public RepresentativeRole getRepresentativeRole() {
        return representativeRole;
    }
    public void setRepresentativeRole(RepresentativeRole representativeRole) {
        this.representativeRole = representativeRole;
    }
    public boolean isIncludeDabc() {
        return includeDabc;
    }
    public void setIncludeDabc(boolean includeDabc) {
        this.includeDabc = includeDabc;
    }
}
