package br.edu.ifmt.liberacampus.model;

public enum DepartmentStatus {
    PENDING("Aguardando análise"),
    IN_ANALYSIS("Em análise"),
    APPROVED("Manifestação favorável"),
    REJECTED("Manifestação desfavorável");

    private final String label;

    DepartmentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
