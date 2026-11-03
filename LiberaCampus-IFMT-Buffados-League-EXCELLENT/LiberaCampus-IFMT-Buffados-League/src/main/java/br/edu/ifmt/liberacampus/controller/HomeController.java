package br.edu.ifmt.liberacampus.controller;

import br.edu.ifmt.liberacampus.model.Role;
import br.edu.ifmt.liberacampus.model.UserAccount;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserAccount account) {
        return account.getRole() == Role.ADMIN
                ? "redirect:/admin/dashboard"
                : "redirect:/aluno/dashboard";
    }
}
