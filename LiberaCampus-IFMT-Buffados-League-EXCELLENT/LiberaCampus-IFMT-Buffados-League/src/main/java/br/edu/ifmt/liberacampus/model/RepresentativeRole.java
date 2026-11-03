package br.edu.ifmt.liberacampus.model;

public enum RepresentativeRole {
    LEADER("Líder de turma"),
    VICE_LEADER("Vice-líder de turma"),
    STUDENT("Estudante da turma");

    private final String label;

    RepresentativeRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
