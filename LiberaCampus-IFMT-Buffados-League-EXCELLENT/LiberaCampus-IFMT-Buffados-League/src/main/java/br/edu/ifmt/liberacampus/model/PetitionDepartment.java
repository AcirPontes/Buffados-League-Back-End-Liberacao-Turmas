package br.edu.ifmt.liberacampus.model;

import br.edu.ifmt.liberacampus.util.CampusTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(name = "peticao_departamentos", uniqueConstraints = {
        @UniqueConstraint(name = "uk_peticao_departamento", columnNames = {"peticao_id", "departamento"})
})
public class PetitionDepartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "peticao_id", nullable = false)
    private Petition petition;

    @Enumerated(EnumType.STRING)
    @Column(name = "departamento", nullable = false, length = 10)
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DepartmentStatus status = DepartmentStatus.PENDING;

    @Column(length = 1800)
    private String decisionNote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decidido_por_id")
    private UserAccount decidedBy;

    @Column(length = 140)
    private String decidedByName;

    private LocalDateTime decidedAt;

    protected PetitionDepartment() {
    }

    public PetitionDepartment(Petition petition, Department department) {
        this.petition = petition;
        this.department = department;
        this.status = DepartmentStatus.PENDING;
    }

    public void updateDecision(DepartmentStatus status, String note, UserAccount administrator) {
        this.status = status;
        this.decisionNote = note;
        this.decidedBy = administrator;
        this.decidedByName = administrator.getFullName();
        this.decidedAt = CampusTime.now();
    }

    public Long getId() {
        return id;
    }
    public Petition getPetition() {
        return petition;
    }
    public Department getDepartment() {
        return department;
    }
    public DepartmentStatus getStatus() {
        return status;
    }
    public String getDecisionNote() {
        return decisionNote;
    }
    public UserAccount getDecidedBy() {
        return decidedBy;
    }

    public String getDecidedByName() {
        return decidedByName;
    }
    public LocalDateTime getDecidedAt() {
        return decidedAt;
    }
}
