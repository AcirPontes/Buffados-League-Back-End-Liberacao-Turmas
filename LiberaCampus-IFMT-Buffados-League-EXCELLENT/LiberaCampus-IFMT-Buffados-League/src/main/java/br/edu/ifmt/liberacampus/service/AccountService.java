package br.edu.ifmt.liberacampus.service;

import br.edu.ifmt.liberacampus.dto.RegistrationForm;
import br.edu.ifmt.liberacampus.model.Role;
import br.edu.ifmt.liberacampus.model.UserAccount;
import br.edu.ifmt.liberacampus.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AccountService {

    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(UserAccountRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserAccount registerStudent(RegistrationForm form) {
        String matricula = form.getMatricula().trim();
        if (userRepository.existsByMatriculaIgnoreCase(matricula)) {
            throw new IllegalArgumentException("Já existe uma conta associada a essa matrícula.");
        }

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new IllegalArgumentException("A confirmação de senha não corresponde.");
        }

        UserAccount student = new UserAccount(
                matricula,
                form.getFullName().trim(),
                normalizeEmail(form.getEmail()),
                passwordEncoder.encode(form.getPassword()),
                Role.STUDENT,
                form.getCourse(),
                form.getClassName().trim(),
                Set.of()
        );

        return userRepository.save(student);
    }

    private String normalizeEmail(String email) {
        return email == null || email.isBlank() ? null : email.trim().toLowerCase();
    }
}
