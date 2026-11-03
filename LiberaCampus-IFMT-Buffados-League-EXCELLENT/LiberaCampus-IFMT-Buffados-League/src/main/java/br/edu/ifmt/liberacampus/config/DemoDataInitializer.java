package br.edu.ifmt.liberacampus.config;

import br.edu.ifmt.liberacampus.util.CampusTime;
import br.edu.ifmt.liberacampus.model.Course;
import br.edu.ifmt.liberacampus.model.Department;
import br.edu.ifmt.liberacampus.model.Petition;
import br.edu.ifmt.liberacampus.model.RepresentativeRole;
import br.edu.ifmt.liberacampus.model.Role;
import br.edu.ifmt.liberacampus.model.UserAccount;
import br.edu.ifmt.liberacampus.repository.PetitionRepository;
import br.edu.ifmt.liberacampus.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;
import java.util.Set;

@Component
@ConditionalOnProperty(name = "app.demo.enabled", havingValue = "true")
public class DemoDataInitializer implements CommandLineRunner {

    private final UserAccountRepository userRepository;
    private final PetitionRepository petitionRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataInitializer(UserAccountRepository userRepository,
                               PetitionRepository petitionRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.petitionRepository = petitionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        UserAccount student = userRepository.findByMatriculaIgnoreCase("202600001")
                .orElseGet(() -> userRepository.save(new UserAccount(
                        "202600001",
                        "Estudante de Demonstração",
                        "estudante.demo@ifmt.edu.br",
                        passwordEncoder.encode("Aluno@2026"),
                        Role.STUDENT,
                        Course.INFORMATICA,
                        "3º C",
                        Set.of()
                )));

        createAdmin("admin", "Administrador de Demonstração",
                EnumSet.allOf(Department.class));
        for (Department department : Department.values()) {
            createAdmin("admin." + department.name().toLowerCase(),
                    "Administrador " + department.name(), EnumSet.of(department));
        }

        if (petitionRepository.count() == 0) {
            Petition sample = new Petition(
                    student,
                    CampusTime.today().plusDays(2),
                    2,
                    4,
                    "Programação Web, Matemática e Língua Portuguesa",
                    32,
                    "Realização de atividade interdisciplinar do projeto integrador, com apresentação de protótipo e participação da turma.",
                    RepresentativeRole.LEADER
            );
            sample.addDepartment(Department.DCOM);
            sample.addDepartment(Department.DABC);
            sample.setProtocol("PET-DEMO-2026-001");
            sample.addEvent("Petição protocolada",
                    "Registro fictício criado automaticamente para demonstração acadêmica.", student);
            petitionRepository.save(sample);
        }
    }

    private void createAdmin(String matricula, String name, Set<Department> departments) {
        if (!userRepository.existsByMatriculaIgnoreCase(matricula)) {
            UserAccount admin = new UserAccount(
                    matricula,
                    name,
                    null,
                    passwordEncoder.encode("Admin@2026"),
                    Role.ADMIN,
                    null,
                    null,
                    departments
            );
            userRepository.save(admin);
        }
    }
}
