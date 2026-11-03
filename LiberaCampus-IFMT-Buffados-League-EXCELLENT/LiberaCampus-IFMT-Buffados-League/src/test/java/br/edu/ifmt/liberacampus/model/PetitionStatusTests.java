package br.edu.ifmt.liberacampus.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PetitionStatusTests {

    @Test
    void emptyDepartmentListRemainsPending() {
        assertEquals(PetitionStatus.PENDING, PetitionStatus.fromDepartmentStatuses(List.of()));
    }

    @Test
    void allPendingDepartmentsKeepPetitionPending() {
        assertEquals(PetitionStatus.PENDING,
                PetitionStatus.fromDepartmentStatuses(List.of(
                        DepartmentStatus.PENDING,
                        DepartmentStatus.PENDING
                )));
    }

    @Test
    void oneDepartmentInAnalysisMeansGeneralAnalysis() {
        assertEquals(PetitionStatus.IN_ANALYSIS,
                PetitionStatus.fromDepartmentStatuses(List.of(
                        DepartmentStatus.IN_ANALYSIS,
                        DepartmentStatus.PENDING
                )));
    }

    @Test
    void partialApprovalIsNotFinalApproval() {
        assertEquals(PetitionStatus.IN_ANALYSIS,
                PetitionStatus.fromDepartmentStatuses(List.of(
                        DepartmentStatus.APPROVED,
                        DepartmentStatus.PENDING
                )));
    }

    @Test
    void allDepartmentsMustApproveForGeneralApproval() {
        assertEquals(PetitionStatus.APPROVED,
                PetitionStatus.fromDepartmentStatuses(List.of(
                        DepartmentStatus.APPROVED,
                        DepartmentStatus.APPROVED
                )));
    }

    @Test
    void anyRejectionMakesGeneralStatusRejected() {
        assertEquals(PetitionStatus.REJECTED,
                PetitionStatus.fromDepartmentStatuses(List.of(
                        DepartmentStatus.APPROVED,
                        DepartmentStatus.REJECTED,
                        DepartmentStatus.PENDING
                )));
    }
}
