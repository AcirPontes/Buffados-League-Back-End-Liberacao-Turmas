package br.edu.ifmt.liberacampus.controller;

import br.edu.ifmt.liberacampus.dto.RegistrationForm;
import br.edu.ifmt.liberacampus.model.Course;
import br.edu.ifmt.liberacampus.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Objects;

@Controller
public class AuthController {

    private final AccountService accountService;

    public AuthController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/cadastro")
    public String registration(Model model) {
        model.addAttribute("form", new RegistrationForm());
        model.addAttribute("courses", Course.values());
        return "auth/register";
    }

    @PostMapping("/cadastro")
    public String register(@Valid @ModelAttribute("form") RegistrationForm form,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (!Objects.equals(form.getPassword(), form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "password.mismatch",
                    "A confirmação não corresponde à senha informada.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("courses", Course.values());
            return "auth/register";
        }

        try {
            accountService.registerStudent(form);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Cadastro realizado. Entre com sua matrícula e a senha criada neste portal.");
            return "redirect:/login";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("courses", Course.values());
            model.addAttribute("registrationError", exception.getMessage());
            return "auth/register";
        }
    }
}
