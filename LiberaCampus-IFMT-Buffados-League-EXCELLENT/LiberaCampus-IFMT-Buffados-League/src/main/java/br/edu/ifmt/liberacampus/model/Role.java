package br.edu.ifmt.liberacampus.model;

public enum Role {
    STUDENT("Estudante"),
    ADMIN("Administrador");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
