package br.edu.ifmt.liberacampus;

import br.edu.ifmt.liberacampus.model.Course;
import br.edu.ifmt.liberacampus.model.Department;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LiberaCampusApplicationTests {

    @Test
    void contextLoadsAndCoursesRouteToTheirDepartments() {
        assertEquals(Department.DCOM, Course.INFORMATICA.getDepartment());
        assertEquals(Department.DINFRA, Course.EDIFICACOES.getDepartment());
        assertEquals(Department.DINFRA, Course.AGRIMENSURA.getDepartment());
        assertEquals(Department.DGH, Course.EVENTOS.getDepartment());
        assertEquals(Department.DGH, Course.SECRETARIADO.getDepartment());
        assertEquals(Department.DEEA, Course.ELETRONICA.getDepartment());
        assertEquals(Department.DEEA, Course.ELETROTECNICA.getDepartment());
    }
}
