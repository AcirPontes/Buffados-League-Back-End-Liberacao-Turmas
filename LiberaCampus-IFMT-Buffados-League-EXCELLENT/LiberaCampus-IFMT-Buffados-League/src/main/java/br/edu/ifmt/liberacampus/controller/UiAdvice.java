package br.edu.ifmt.liberacampus.controller;

import br.edu.ifmt.liberacampus.model.UserAccount;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UiAdvice {

    @ExceptionHandler(AccessDeniedException.class)
    public String accessDenied(@AuthenticationPrincipal UserAccount account, Model model) {
        model.addAttribute("account", account);
        model.addAttribute("message", "Sua conta não possui permissão para acessar este recurso.");
        return "error/403";
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public String notFoundOrInvalid(RuntimeException exception,
                                    @AuthenticationPrincipal UserAccount account,
                                    Model model) {
        model.addAttribute("account", account);
        model.addAttribute("message", exception.getMessage());
        model.addAttribute("backUrl", "/dashboard");
        return "error/notice";
    }
}
