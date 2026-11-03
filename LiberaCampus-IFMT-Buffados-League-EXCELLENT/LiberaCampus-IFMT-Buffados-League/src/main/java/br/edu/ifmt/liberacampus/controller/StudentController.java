package br.edu.ifmt.liberacampus.controller;

import br.edu.ifmt.liberacampus.util.CampusTime;
import br.edu.ifmt.liberacampus.dto.PetitionForm;
import br.edu.ifmt.liberacampus.model.Department;
import br.edu.ifmt.liberacampus.model.Petition;
import br.edu.ifmt.liberacampus.model.PetitionStatus;
import br.edu.ifmt.liberacampus.model.RepresentativeRole;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.time.format.DateTimeFormatter;

@Controller
public class StudentController {

    private final PetitionService petitionService;

    public StudentController(PetitionService petitionService) {
        this.petitionService = petitionService;
    }

    @GetMapping("/aluno/dashboard")
    public String dashboard(@AuthenticationPrincipal UserAccount account, Model model) {
        List<Petition> petitions = petitionService.findMine(account);
        model.addAttribute("account", account);
        model.addAttribute("petitions", petitions.stream().limit(5).toList());
        model.addAttribute("total", petitions.size());
        model.addAttribute("pending", petitions.stream()
                .filter(p -> p.getStatus() == PetitionStatus.PENDING
                        || p.getStatus() == PetitionStatus.IN_ANALYSIS).count());
        model.addAttribute("approved", petitions.stream()
                .filter(p -> p.getStatus() == PetitionStatus.APPROVED).count());
        return "student/dashboard";
    }

    @GetMapping("/peticoes")
    public String list(@AuthenticationPrincipal UserAccount account, Model model) {
        model.addAttribute("petitions", petitionService.findMine(account));
        model.addAttribute("account", account);
        return "petition/list";
    }

    @GetMapping("/peticoes/nova")
    public String newPetition(@AuthenticationPrincipal UserAccount account, Model model) {
        model.addAttribute("form", new PetitionForm());
        model.addAttribute("account", account);
        addFormOptions(model);
        return "petition/form";
    }

    @PostMapping("/peticoes")
    public String create(@Valid @ModelAttribute("form") PetitionForm form,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal UserAccount account,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("account", account);
            addFormOptions(model);
            return "petition/form";
        }

        try {
            Petition created = petitionService.create(form, account);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Petição " + created.getProtocol() + " protocolada com sucesso.");
            return "redirect:/peticoes/" + created.getId();
        } catch (IllegalArgumentException exception) {
            model.addAttribute("account", account);
            model.addAttribute("formError", exception.getMessage());
            addFormOptions(model);
            return "petition/form";
        }
    }

    @GetMapping("/peticoes/{id}")
    public String details(@PathVariable Long id,
                          @AuthenticationPrincipal UserAccount account,
                          Model model) {
        model.addAttribute("petition", petitionService.findForStudent(id, account));
        model.addAttribute("adminView", false);
        model.addAttribute("account", account);
        return "petition/detail";
    }

    @GetMapping("/peticoes/{id}/editar")
    public String edit(@PathVariable Long id,
                       @AuthenticationPrincipal UserAccount account,
                       Model model) {
        Petition petition = petitionService.findForStudent(id, account);
        if (petition.getStatus() != PetitionStatus.PENDING) {
            return "redirect:/peticoes/" + id;
        }
        model.addAttribute("form", mapToForm(petition));
        model.addAttribute("account", account);
        model.addAttribute("editing", true);
        model.addAttribute("petitionId", id);
        addFormOptions(model);
        return "petition/form";
    }

    @PostMapping("/peticoes/{id}/editar")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("form") PetitionForm form,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal UserAccount account,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("account", account);
            model.addAttribute("editing", true);
            model.addAttribute("petitionId", id);
            addFormOptions(model);
            return "petition/form";
        }

        try {
            petitionService.update(id, form, account);
            redirectAttributes.addFlashAttribute("successMessage", "Petição atualizada.");
            return "redirect:/peticoes/" + id;
        } catch (IllegalArgumentException | IllegalStateException exception) {
            model.addAttribute("account", account);
            model.addAttribute("editing", true);
            model.addAttribute("petitionId", id);
            model.addAttribute("formError", exception.getMessage());
            addFormOptions(model);
            return "petition/form";
        }
    }

    @PostMapping("/peticoes/{id}/cancelar")
    public String cancel(@PathVariable Long id,
                         @AuthenticationPrincipal UserAccount account,
                         RedirectAttributes redirectAttributes) {
        try {
            petitionService.cancel(id, account);
            redirectAttributes.addFlashAttribute("successMessage", "Petição cancelada.");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/peticoes/" + id;
    }

    private void addFormOptions(Model model) {
        model.addAttribute("representativeRoles", RepresentativeRole.values());
        model.addAttribute("departments", Department.values());
        model.addAttribute("today", CampusTime.today().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        model.addAttribute("timetableUrl", "https://ifmtcba.edupage.org/timetable/");
    }

    private PetitionForm mapToForm(Petition petition) {
        PetitionForm form = new PetitionForm();
        form.setRequestedDate(petition.getRequestedDate());
        form.setFirstPeriod(petition.getFirstPeriod());
        form.setLastPeriod(petition.getLastPeriod());
        form.setAffectedSubjects(petition.getAffectedSubjects());
        form.setExpectedStudents(petition.getExpectedStudents());
        form.setReason(petition.getReason());
        form.setRepresentativeRole(petition.getRepresentativeRole());
        form.setIncludeDabc(petition.getDepartmentEntries().stream()
                .anyMatch(entry -> entry.getDepartment() == Department.DABC));
        return form;
    }
}
