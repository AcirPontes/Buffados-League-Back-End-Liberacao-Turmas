package br.edu.ifmt.liberacampus.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Centraliza a referência temporal do campus, evitando diferenças de data
 * quando a aplicação estiver hospedada em um servidor com fuso UTC.
 */
public final class CampusTime {

    public static final ZoneId ZONE = ZoneId.of("America/Cuiaba");

    private CampusTime() {
    }

    public static LocalDate today() {
        return LocalDate.now(ZONE);
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(ZONE);
    }
}
