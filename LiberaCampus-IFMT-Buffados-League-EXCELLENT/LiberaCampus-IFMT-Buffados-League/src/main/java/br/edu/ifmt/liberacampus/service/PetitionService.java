package br.edu.ifmt.liberacampus.service;

import br.edu.ifmt.liberacampus.util.CampusTime;
import br.edu.ifmt.liberacampus.dto.PetitionForm;
import br.edu.ifmt.liberacampus.model.Department;
import br.edu.ifmt.liberacampus.model.DepartmentStatus;
import br.edu.ifmt.liberacampus.model.Petition;
import br.edu.ifmt.liberacampus.model.PetitionDepartment;
import br.edu.ifmt.liberacampus.model.PetitionStatus;
import br.edu.ifmt.liberacampus.model.Role;
import br.edu.ifmt.liberacampus.model.UserAccount;
import br.edu.ifmt.liberacampus.repository.PetitionRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class PetitionService {

    private final PetitionRepository petitionRepository;

    public PetitionService(PetitionRepository petitionRepository) {
        this.petitionRepository = petitionRepository;
    }

    @Transactional
    public Petition create(PetitionForm form, UserAccount requester) {
        requireRole(requester, Role.STUDENT);
        validatePeriodOrder(form);
        validateRequestedDate(form.getRequestedDate());

        Petition petition = new Petition(
                requester,
                form.getRequestedDate(),
                form.getFirstPeriod(),
                form.getLastPeriod(),
                clean(form.getAffectedSubjects()),
                form.getExpectedStudents(),
                form.getReason().trim(),
                form.getRepresentativeRole()
        );

        Set<Department> routing = resolveRouting(requester, form);
        petition.configureDepartments(routing);
        petition.setProtocol(createProtocol());
        petition.addEvent("Petição protocolada",
                "Solicitação registrada e encaminhada aos departamentos: "
                        + formatDepartments(routing) + ".", requester);

        return petitionRepository.save(petition);
    }

    @Transactional
    public Petition update(Long id, PetitionForm form, UserAccount requester) {
        requireRole(requester, Role.STUDENT);
        Petition petition = findForStudent(id, requester);
        if (petition.getStatus() != PetitionStatus.PENDING) {
            throw new IllegalStateException("A petição só pode ser editada enquanto estiver pendente.");
        }
        validatePeriodOrder(form);
        validateRequestedDate(form.getRequestedDate());

        petition.updateRequest(
                form.getRequestedDate(),
                form.getFirstPeriod(),
                form.getLastPeriod(),
                clean(form.getAffectedSubjects()),
                form.getExpectedStudents(),
                form.getReason().trim(),
                form.getRepresentativeRole()
        );
        Set<Department> routing = resolveRouting(requester, form);
        petition.configureDepartments(routing);
        petition.addEvent("Petição atualizada",
                "O solicitante alterou os dados e o encaminhamento. Departamentos: "
                        + formatDepartments(routing) + ".", requester);
        return petitionRepository.save(petition);
    }

    @Transactional
    public void cancel(Long id, UserAccount requester) {
        requireRole(requester, Role.STUDENT);
        Petition petition = findForStudent(id, requester);
        if (petition.getStatus() != PetitionStatus.PENDING) {
            throw new IllegalStateException("Somente petições pendentes podem ser canceladas.");
        }
        petition.setStatus(PetitionStatus.CANCELLED);
        petition.addEvent("Petição cancelada", "Cancelamento solicitado pelo estudante.", requester);
        petitionRepository.save(petition);
    }

    @Transactional(readOnly = true)
    public List<Petition> findMine(UserAccount requester) {
        requireRole(requester, Role.STUDENT);
        return petitionRepository.findByRequesterOrderByCreatedAtDesc(requester);
    }

    @Transactional(readOnly = true)
    public Petition findForStudent(Long id, UserAccount requester) {
        requireRole(requester, Role.STUDENT);
        Petition petition = petitionRepository.findDetailedById(id)
                .filter(item -> item.getRequester().getId().equals(requester.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Petição não encontrada."));
        petition.getEvents().size();
        return petition;
    }

    @Transactional(readOnly = true)
    public List<Petition> findVisibleToAdmin(UserAccount administrator) {
        requireRole(administrator, Role.ADMIN);
        if (administrator.getAssignedDepartments().isEmpty()) {
            return List.of();
        }
        return petitionRepository.findVisibleToDepartments(administrator.getAssignedDepartments());
    }

    @Transactional(readOnly = true)
    public Petition findForAdmin(Long id, UserAccount administrator) {
        requireRole(administrator, Role.ADMIN);
        Petition petition = petitionRepository.findDetailedById(id)
                .orElseThrow(() -> new IllegalArgumentException("Petição não encontrada."));
        boolean inScope = petition.getDepartmentEntries().stream()
                .anyMatch(entry -> administrator.getAssignedDepartments().contains(entry.getDepartment()));
        if (!inScope) {
            throw new AccessDeniedException("Esta petição não está vinculada aos departamentos da sua conta.");
        }
        petition.getEvents().size();
        return petition;
    }

    @Transactional
    public Petition updateDepartmentStatus(Long id, Department department,
                                           DepartmentStatus newStatus, String note,
                                           UserAccount administrator) {
        requireRole(administrator, Role.ADMIN);
        if (!administrator.getAssignedDepartments().contains(department)) {
            throw new AccessDeniedException("Sua conta não possui permissão para esse departamento.");
        }
        if (newStatus == DepartmentStatus.PENDING) {
            throw new IllegalArgumentException("Não é permitido retornar uma manifestação para o estado inicial.");
        }

        Petition petition = findForAdmin(id, administrator);
        if (petition.getStatus() == PetitionStatus.CANCELLED) {
            throw new IllegalStateException("Uma petição cancelada não pode receber novas manifestações.");
        }
        if (petition.getStatus() == PetitionStatus.APPROVED
                || petition.getStatus() == PetitionStatus.REJECTED) {
            throw new IllegalStateException("Esta petição já recebeu uma decisão final.");
        }

        PetitionDepartment entry = petition.getDepartmentEntries().stream()
                .filter(item -> item.getDepartment() == department)
                .findFirst()
                .orElseThrow(() -> new AccessDeniedException("O departamento não participa desta petição."));

        if (entry.getStatus() == DepartmentStatus.APPROVED
                || entry.getStatus() == DepartmentStatus.REJECTED) {
            throw new IllegalStateException("Este departamento já registrou sua manifestação final.");
        }
        if (entry.getStatus() == DepartmentStatus.IN_ANALYSIS
                && newStatus == DepartmentStatus.IN_ANALYSIS) {
            throw new IllegalStateException("A análise deste departamento já foi iniciada.");
        }
        if (newStatus == DepartmentStatus.REJECTED && (note == null || note.isBlank())) {
            throw new IllegalArgumentException("O indeferimento exige uma justificativa.");
        }

        String normalizedNote = clean(note);
        entry.updateDecision(newStatus, normalizedNote, administrator);
        petition.touch();
        petition.addEvent(
                titleFor(newStatus, department),
                department.name() + ": " + newStatus.getLabel()
                        + (normalizedNote == null ? "." : ". Justificativa/observação: " + normalizedNote),
                administrator
        );
        aggregateStatus(petition);
        return petitionRepository.save(petition);
    }

    @Transactional(readOnly = true)
    public List<Petition> findAllForAdmin(UserAccount administrator) {
        requireRole(administrator, Role.ADMIN);
        if (administrator.getAssignedDepartments().isEmpty()) {
            return List.of();
        }
        return petitionRepository.findVisibleToDepartments(administrator.getAssignedDepartments());
    }

    private void aggregateStatus(Petition petition) {
        List<DepartmentStatus> statuses = petition.getDepartmentEntries().stream()
                .map(PetitionDepartment::getStatus)
                .toList();
        PetitionStatus aggregate = PetitionStatus.fromDepartmentStatuses(statuses);

        if (petition.getStatus() != aggregate) {
            petition.setStatus(aggregate);
            petition.addEvent("Status geral atualizado", "Situação consolidada: "
                    + aggregate.getLabel() + ".", null);
        }
    }

    private Set<Department> resolveRouting(UserAccount requester, PetitionForm form) {
        Set<Department> routing = EnumSet.of(requester.getCourse().getDepartment());
        if (form.isIncludeDabc()) {
            routing.add(Department.DABC);
        }
        return routing;
    }

    private String formatDepartments(Set<Department> departments) {
        return departments.stream()
                .map(Department::getCode)
                .sorted()
                .reduce((left, right) -> left + ", " + right)
                .orElse("Nenhum departamento");
    }

    private void validatePeriodOrder(PetitionForm form) {
        if (form.getFirstPeriod() != null && form.getLastPeriod() != null
                && form.getFirstPeriod() > form.getLastPeriod()) {
            throw new IllegalArgumentException("O último horário/aula não pode anteceder o primeiro.");
        }
    }

    private void validateRequestedDate(LocalDate date) {
        if (date != null && date.isBefore(CampusTime.today())) {
            throw new IllegalArgumentException("A data solicitada não pode estar no passado.");
        }
    }

    private void requireRole(UserAccount account, Role expectedRole) {
        if (account == null || account.getRole() != expectedRole) {
            throw new AccessDeniedException("Perfil sem permissão para esta operação.");
        }
    }

    private String createProtocol() {
        String suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        return "PET-" + CampusTime.today().getYear() + "-" + suffix;
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String titleFor(DepartmentStatus status, Department department) {
        return switch (status) {
            case IN_ANALYSIS -> "Análise iniciada · " + department.name();
            case APPROVED -> "Manifestação favorável · " + department.name();
            case REJECTED -> "Manifestação desfavorável · " + department.name();
            case PENDING -> "Petição pendente · " + department.name();
        };
    }
}
