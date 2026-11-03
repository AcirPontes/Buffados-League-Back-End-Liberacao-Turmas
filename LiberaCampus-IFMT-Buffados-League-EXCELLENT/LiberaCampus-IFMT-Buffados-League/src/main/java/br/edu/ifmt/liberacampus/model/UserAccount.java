package br.edu.ifmt.liberacampus.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Locale;

@Entity
@Table(name = "usuarios", uniqueConstraints = {
        @UniqueConstraint(name = "uk_usuario_matricula", columnNames = "matricula")
})
public class UserAccount implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String matricula;

    @Column(nullable = false, length = 140)
    private String fullName;

    @Column(length = 160)
    private String email;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private Course course;

    @Column(length = 80)
    private String className;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "administrador_departamentos",
            joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "departamento", nullable = false, length = 10)
    private Set<Department> assignedDepartments = new HashSet<>();

    protected UserAccount() {
    }

    public UserAccount(String matricula, String fullName, String email,
                       String passwordHash, Role role, Course course,
                       String className, Set<Department> assignedDepartments) {
        this.matricula = matricula.trim().toUpperCase(Locale.ROOT);
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.course = course;
        this.className = className;
        if (assignedDepartments != null) {
            this.assignedDepartments = new HashSet<>(assignedDepartments);
        }
    }

    public Long getId() {
        return id;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public Course getCourse() {
        return course;
    }

    public String getClassName() {
        return className;
    }

    public Set<Department> getAssignedDepartments() {
        return assignedDepartments;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return matricula;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
