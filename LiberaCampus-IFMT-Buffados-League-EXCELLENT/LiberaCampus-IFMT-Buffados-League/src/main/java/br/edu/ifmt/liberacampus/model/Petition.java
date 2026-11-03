package br.edu.ifmt.liberacampus.model;

import br.edu.ifmt.liberacampus.util.CampusTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "peticoes", indexes = {
        @Index(name = "idx_peticao_protocolo", columnList = "protocolo"),
        @Index(name = "idx_peticao_status", columnList = "status"),
        @Index(name = "idx_peticao_data", columnList = "created_at")
})
public class Petition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "protocolo", nullable = false, unique = true, length = 32)
    private String protocol;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private UserAccount requester;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Course course;

    @Column(nullable = false, length = 80)
    private String className;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RepresentativeRole representativeRole;

    @Column(nullable = false)
    private LocalDate requestedDate;

    @Column(nullable = false)
    private Integer firstPeriod;

    @Column(nullable = false)
    private Integer lastPeriod;

    @Column(length = 500)
    private String affectedSubjects;

    @Column(nullable = false)
    private Integer expectedStudents;

    @Column(nullable = false, length = 1800)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PetitionStatus status = PetitionStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    @OneToMany(mappedBy = "petition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PetitionDepartment> departmentEntries = new ArrayList<>();

    @OneToMany(mappedBy = "petition", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("occurredAt ASC")
    private List<PetitionEvent> events = new ArrayList<>();

    protected Petition() {
    }

    public Petition(UserAccount requester, LocalDate requestedDate,
                    Integer firstPeriod, Integer lastPeriod,
                    String affectedSubjects, Integer expectedStudents,
                    String reason, RepresentativeRole representativeRole) {
        this.requester = requester;
        this.course = requester.getCourse();
        this.className = requester.getClassName();
        this.requestedDate = requestedDate;
        this.firstPeriod = firstPeriod;
        this.lastPeriod = lastPeriod;
        this.affectedSubjects = affectedSubjects;
        this.expectedStudents = expectedStudents;
        this.reason = reason;
        this.representativeRole = representativeRole;
        this.status = PetitionStatus.PENDING;
        this.createdAt = CampusTime.now();
        this.updatedAt = this.createdAt;
    }

    public void addDepartment(Department department) {
        boolean alreadyAdded = departmentEntries.stream()
                .anyMatch(entry -> entry.getDepartment() == department);
        if (!alreadyAdded) {
            departmentEntries.add(new PetitionDepartment(this, department));
        }
    }

    public void configureDepartments(Set<Department> departments) {
        departmentEntries.removeIf(entry -> !departments.contains(entry.getDepartment()));
        departments.forEach(this::addDepartment);
    }

    public void addEvent(String title, String description, UserAccount actor) {
        events.add(new PetitionEvent(this, title, description, actor));
    }

    public void updateRequest(LocalDate requestedDate, Integer firstPeriod,
                              Integer lastPeriod, String affectedSubjects,
                              Integer expectedStudents, String reason,
                              RepresentativeRole representativeRole) {
        this.requestedDate = requestedDate;
        this.firstPeriod = firstPeriod;
        this.lastPeriod = lastPeriod;
        this.affectedSubjects = affectedSubjects;
        this.expectedStudents = expectedStudents;
        this.reason = reason;
        this.representativeRole = representativeRole;
        this.updatedAt = CampusTime.now();
    }

    public void setStatus(PetitionStatus status) {
        this.status = status;
        this.updatedAt = CampusTime.now();
    }

    public void touch() {
        this.updatedAt = CampusTime.now();
    }

    public Long getId() {
        return id;
    }
    public String getProtocol() {
        return protocol;
    }
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    public UserAccount getRequester() {
        return requester;
    }
    public Course getCourse() {
        return course;
    }
    public String getClassName() {
        return className;
    }
    public RepresentativeRole getRepresentativeRole() {
        return representativeRole;
    }
    public LocalDate getRequestedDate() {
        return requestedDate;
    }
    public Integer getFirstPeriod() {
        return firstPeriod;
    }
    public Integer getLastPeriod() {
        return lastPeriod;
    }
    public String getAffectedSubjects() {
        return affectedSubjects;
    }
    public Integer getExpectedStudents() {
        return expectedStudents;
    }
    public String getReason() {
        return reason;
    }
    public PetitionStatus getStatus() {
        return status;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public List<PetitionDepartment> getDepartmentEntries() {
        return departmentEntries;
    }
    public List<PetitionEvent> getEvents() {
        return events;
    }
}
