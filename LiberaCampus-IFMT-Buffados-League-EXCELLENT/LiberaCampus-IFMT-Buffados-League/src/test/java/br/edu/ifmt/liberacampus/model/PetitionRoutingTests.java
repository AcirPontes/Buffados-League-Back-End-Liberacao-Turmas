package br.edu.ifmt.liberacampus.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PetitionRoutingTests {

    @Test
    void pendingPetitionCanReplaceItsDepartmentRoutingWithoutDuplicates() {
        UserAccount requester = new UserAccount(
                "202600010",
                "Estudante de Teste",
                null,
                "hash",
                Role.STUDENT,
                Course.INFORMATICA,
                "3º C",
                Set.of()
        );
        Petition petition = new Petition(
                requester,
                LocalDate.of(2026, 11, 5),
                1,
                2,
                "Programação Web",
                30,
                "Atividade pedagógica de teste com descrição suficiente.",
                RepresentativeRole.LEADER
        );

        petition.configureDepartments(EnumSet.of(Department.DCOM, Department.DABC));
        petition.configureDepartments(EnumSet.of(Department.DCOM));
        petition.configureDepartments(EnumSet.of(Department.DCOM, Department.DABC));

        Set<Department> actual = petition.getDepartmentEntries().stream()
                .map(PetitionDepartment::getDepartment)
                .collect(Collectors.toSet());

        assertEquals(Set.of(Department.DCOM, Department.DABC), actual);
        assertEquals(2, petition.getDepartmentEntries().size());
    }
}
