package br.edu.ifmt.liberacampus.dto;

import jakarta.validation.constraints.Size;

public class DecisionForm {

    @Size(max = 1800, message = "A justificativa deve ter até 1.800 caracteres.")
    private String note;

    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
}
