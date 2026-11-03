package br.edu.ifmt.liberacampus.model;

public enum Department {
    DCOM("Departamento de Computação", "Técnico em Informática"),
    DINFRA("Departamento de Infraestrutura", "Técnicos em Edificações e Agrimensura"),
    DABC("Departamento de Área de Base Comum", "Disciplinas de Base Comum"),
    DGH("Departamento de Gestão e Hospitalidade", "Técnicos em Eventos e Secretariado"),
    DEEA("Departamento de Engenharia Elétrica e Automação", "Técnicos em Eletrônica e Eletrotécnica");

    private final String name;
    private final String scope;

    Department(String name, String scope) {
        this.name = name;
        this.scope = scope;
    }

    public String getName() {
        return name;
    }

    public String getScope() {
        return scope;
    }

    public String getCode() {
        return name();
    }
}
