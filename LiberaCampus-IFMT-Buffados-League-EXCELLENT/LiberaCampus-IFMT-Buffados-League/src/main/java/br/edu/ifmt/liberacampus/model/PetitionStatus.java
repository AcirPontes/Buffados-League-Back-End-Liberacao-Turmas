package br.edu.ifmt.liberacampus.model;

import java.util.Collection;

public enum PetitionStatus {
    PENDING("Pendente"),
    IN_ANALYSIS("Em análise"),
    APPROVED("Aprovada"),
    REJECTED("Indeferida"),
    CANCELLED("Cancelada");

    private final String label;

    PetitionStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Consolida as manifestações departamentais sem permitir que uma aprovação
     * parcial seja apresentada como deferimento definitivo.
     */
    public static PetitionStatus fromDepartmentStatuses(Collection<DepartmentStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return PENDING;
        }
        if (statuses.contains(DepartmentStatus.REJECTED)) {
            return REJECTED;
        }
        if (statuses.stream().allMatch(status -> status == DepartmentStatus.APPROVED)) {
            return APPROVED;
        }
        if (statuses.stream().anyMatch(status -> status == DepartmentStatus.IN_ANALYSIS
                || status == DepartmentStatus.APPROVED)) {
            return IN_ANALYSIS;
        }
        return PENDING;
    }
}
