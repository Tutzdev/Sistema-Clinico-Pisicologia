package br.com.agendamento.psicologia.controller;

import br.com.agendamento.psicologia.dto.HorarioDisponivelDTO;
import br.com.agendamento.psicologia.service.AgendaService;
import br.com.agendamento.psicologia.service.ProfissionalService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Validated
@Controller
@RequestMapping("/admin/agenda")
public class AgendaController {

    private final AgendaService agendaService;
    private final ProfissionalService profissionalService;

    public AgendaController(
            AgendaService agendaService,
            ProfissionalService profissionalService
    ) {
        this.agendaService = agendaService;
        this.profissionalService = profissionalService;
    }

    @GetMapping
    public String agenda(Model model) {
        adicionarDadosDaAgenda(model);
        model.addAttribute(
                "horarioForm",
                new HorarioDisponivelDTO()
        );

        return "admin/agenda";
    }

    @PostMapping
    public String criarHorario(
            @Valid
            @ModelAttribute("horarioForm")
            HorarioDisponivelDTO horarioForm,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            adicionarDadosDaAgenda(model);

            return "admin/agenda";
        }

        agendaService.criarHorario(
                horarioForm.getProfissionalId(),
                horarioForm.getDataHora()
        );

        return "redirect:/admin/agenda";
    }

    @PostMapping("/{horarioId}/excluir")
    public String excluirHorario(
            @PathVariable
            @Positive(message = "O horário informado é inválido.")
            Long horarioId
    ) {
        agendaService.excluirHorario(horarioId);

        return "redirect:/admin/agenda";
    }

    private void adicionarDadosDaAgenda(Model model) {
        model.addAttribute(
                "horarios",
                agendaService.listarHorariosDisponiveis()
                        .stream()
                        .map(HorarioDisponivelDTO::from)
                        .toList()
        );

        model.addAttribute(
                "profissionais",
                profissionalService.listarTodos()
        );
    }
}