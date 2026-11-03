package br.edu.ifmt.liberacampus.model;

public enum Course {
    INFORMATICA("Técnico em Informática", Department.DCOM),
    EDIFICACOES("Técnico em Edificações", Department.DINFRA),
    AGRIMENSURA("Técnico em Agrimensura", Department.DINFRA),
    EVENTOS("Técnico em Eventos", Department.DGH),
    SECRETARIADO("Técnico em Secretariado", Department.DGH),
    ELETRONICA("Técnico em Eletrônica", Department.DEEA),
    ELETROTECNICA("Técnico em Eletrotécnica", Department.DEEA);

    private final String label;
    private final Department department;

    Course(String label, Department department) {
        this.label = label;
        this.department = department;
    }

    public String getLabel() {
        return label;
    }

    public Department getDepartment() {
        return department;
    }
}
