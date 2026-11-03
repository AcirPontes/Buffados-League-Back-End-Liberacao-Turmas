package br.edu.ifmt.liberacampus.config;

import br.edu.ifmt.liberacampus.model.Department;
import br.edu.ifmt.liberacampus.model.Role;
import br.edu.ifmt.liberacampus.model.UserAccount;
import br.edu.ifmt.liberacampus.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BootstrapAdminInitializer implements CommandLineRunner {

    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.matricula:}")
    private String matricula;

    @Value("${app.bootstrap.admin.nome:Administrador institucional}")
    private String fullName;

    @Value("${app.bootstrap.admin.senha:}")
    private String password;

    @Value("${app.bootstrap.admin.departamentos:DCOM,DABC,DINFRA,DGH,DEEA}")
    private String departments;

    public BootstrapAdminInitializer(UserAccountRepository userRepository,
                                     PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (matricula == null || matricula.isBlank()
                || password == null || password.isBlank()
                || userRepository.existsByMatriculaIgnoreCase(matricula)) {
            return;
        }

        Set<Department> assigned = Arrays.stream(departments.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(value -> Department.valueOf(value.toUpperCase()))
                .collect(Collectors.toSet());

        userRepository.save(new UserAccount(
                matricula.trim(),
                fullName,
                null,
                passwordEncoder.encode(password),
                Role.ADMIN,
                null,
                null,
                assigned
        ));
    }
}
