package br.edu.ifmt.liberacampus.dto;

import br.edu.ifmt.liberacampus.model.Course;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistrationForm {

    @NotBlank(message = "Informe seu nome completo.")
    @Size(max = 140, message = "O nome deve ter até 140 caracteres.")
    private String fullName;

    @NotBlank(message = "Informe seu código de matrícula.")
    @Pattern(regexp = "[A-Za-z0-9.-]{5,20}", message = "Use de 5 a 20 caracteres alfanuméricos.")
    private String matricula;

    @Email(message = "Informe um e-mail válido.")
    @Size(max = 160)
    private String email;

    @NotNull(message = "Selecione seu curso técnico.")
    private Course course;

    @NotBlank(message = "Informe sua turma.")
    @Size(max = 80)
    private String className;

    @NotBlank(message = "Crie uma senha.")
    @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres.")
    private String password;

    @NotBlank(message = "Confirme sua senha.")
    private String confirmPassword;

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public String getMatricula() {
        return matricula;
    }
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Course getCourse() {
        return course;
    }
    public void setCourse(Course course) {
        this.course = course;
    }
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
