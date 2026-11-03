package br.edu.ifmt.liberacampus.repository;

import br.edu.ifmt.liberacampus.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByMatriculaIgnoreCase(String matricula);

    boolean existsByMatriculaIgnoreCase(String matricula);
}
