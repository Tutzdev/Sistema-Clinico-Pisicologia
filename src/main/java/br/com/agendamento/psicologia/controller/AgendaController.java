package br.com.agendamento.psicologia.controller;

import br.com.agendamento.psicologia.entity.HorarioDisponivel;
import br.com.agendamento.psicologia.service.AgendaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("admin/agenda")
public class AgendaController {

    private final AgendaService agendaService;

    public AgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    @GetMapping
    public String agenda(Model model) {

        model.addAttribute(
            "horarios",
            agendaService.listarHorariosDisponiveis()
        );

        return "admin/agenda"
    }

    @PostMapping
    public String criarHorario(
            @ModelAttribute HorarioDisponivel horario
    ) {
        agendaService.criarHorario(horario);

        return "redirect:/admin/agenda"
    }
}