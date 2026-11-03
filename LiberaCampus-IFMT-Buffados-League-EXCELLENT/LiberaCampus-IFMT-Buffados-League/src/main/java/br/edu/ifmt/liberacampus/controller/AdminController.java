package br.edu.ifmt.liberacampus.controller;

import br.edu.ifmt.liberacampus.dto.DecisionForm;
import br.edu.ifmt.liberacampus.model.Department;
import br.edu.ifmt.liberacampus.model.DepartmentStatus;
import br.edu.ifmt.liberacampus.model.Petition;
import br.edu.ifmt.liberacampus.model.PetitionStatus;
import br.edu.ifmt.liberacampus.model.UserAccount;
import br.edu.ifmt.liberacampus.service.PetitionService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

@Controller
public class AdminController {

    private final PetitionService petitionService;

    public AdminController(PetitionService petitionService) {
        this.petitionService = petitionService;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(@AuthenticationPrincipal UserAccount account, Model model) {
        List<Petition> petitions = petitionService.findVisibleToAdmin(account);
        addDashboardMetrics(model, petitions);
        model.addAttribute("account", account);
        model.addAttribute("petitions", petitions.stream().limit(6).toList());
        return "admin/dashboard";
    }

    @GetMapping("/admin/peticoes")
    public String list(@AuthenticationPrincipal UserAccount account,
                       @RequestParam(required = false) String q,
                       @RequestParam(required = false) PetitionStatus status,
                       Model model) {
        List<Petition> petitions = petitionService.findAllForAdmin(account);
        Predicate<Petition> predicate = petition -> true;
        if (q != null && !q.isBlank()) {
            String query = q.trim().toLowerCase(Locale.ROOT);
            predicate = predicate.and(petition -> petition.getProtocol().toLowerCase(Locale.ROOT).contains(query)
                    || petition.getClassName().toLowerCase(Locale.ROOT).contains(query)
                    || petition.getRequester().getFullName().toLowerCase(Locale.ROOT).contains(query));
        }
        if (status != null) {
            predicate = predicate.and(petition -> petition.getStatus() == status);
        }
        model.addAttribute("petitions", petitions.stream().filter(predicate).toList());
        model.addAttribute("account", account);
        model.addAttribute("query", q == null ? "" : q);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statuses", PetitionStatus.values());
        return "admin/list";
    }

    @GetMapping("/admin/peticoes/{id}")
    public String details(@PathVariable Long id,
                          @AuthenticationPrincipal UserAccount account,
                          Model model) {
        Petition petition = petitionService.findForAdmin(id, account);
        model.addAttribute("petition", petition);
        model.addAttribute("decisionForm", new DecisionForm());
        model.addAttribute("account", account);
        model.addAttribute("managedDepartments", account.getAssignedDepartments());
        model.addAttribute("decisionStatuses", new DepartmentStatus[]{
                DepartmentStatus.IN_ANALYSIS,
                DepartmentStatus.APPROVED,
                DepartmentStatus.REJECTED
        });
        model.addAttribute("adminView", true);
        return "petition/detail";
    }

    @PostMapping("/admin/peticoes/{id}/departamento/{department}")
    public String updateDepartment(@PathVariable Long id,
                                   @PathVariable Department department,
                                   @RequestParam DepartmentStatus status,
                                   @Valid @ModelAttribute("decisionForm") DecisionForm form,
                                   BindingResult bindingResult,
                                   @AuthenticationPrincipal UserAccount account,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "A justificativa excede o limite permitido.");
            return "redirect:/admin/peticoes/" + id;
        }

        try {
            petitionService.updateDepartmentStatus(
                    id, department, status, form.getNote(), account);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Manifestação do departamento " + department.name() + " registrada.");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/admin/peticoes/" + id;
    }

    private void addDashboardMetrics(Model model, List<Petition> petitions) {
        model.addAttribute("total", petitions.size());
        model.addAttribute("pending", petitions.stream()
                .filter(p -> p.getStatus() == PetitionStatus.PENDING).count());
        model.addAttribute("inAnalysis", petitions.stream()
                .filter(p -> p.getStatus() == PetitionStatus.IN_ANALYSIS).count());
        model.addAttribute("approved", petitions.stream()
                .filter(p -> p.getStatus() == PetitionStatus.APPROVED).count());
        model.addAttribute("rejected", petitions.stream()
                .filter(p -> p.getStatus() == PetitionStatus.REJECTED).count());
    }
}
