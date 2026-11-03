package br.edu.ifmt.liberacampus.service;

import br.edu.ifmt.liberacampus.repository.UserAccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userRepository;

    public CustomUserDetailsService(UserAccountRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String matricula) throws UsernameNotFoundException {
        return userRepository.findByMatriculaIgnoreCase(matricula.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Conta não encontrada."));
    }
}
