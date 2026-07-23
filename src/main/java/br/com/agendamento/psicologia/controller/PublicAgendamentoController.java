package br.com.agendamento.psicologia.controller;

import br.com.agendamento.psicologia.entity.Profissional;
import br.com.agendamento.psicologia.service.AgendaService;
import br.com.agendamento.psicologia.service.ProfissionalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/agendar")
public class PublicAgendamentoController {

    private final ProfssionalService profissionalService;
    private final AgendaService agendaService;

    public PublicAgendamentoController(
        ProfissionalService profissionalService,
        AgendaService agendaService
    ) {
        this.profissionalService = profissionalService;
        this.agendaService = agendaService;
    }

    @GetMapping("/{codigoAgenda}")
    public String paginaAgendamento(
        @PathVariable String codigoAgenda,
        Model model
    ) {
        Profissional profissional = 
                profssionalService.buscarPorCodigoAgenda(codigoAgenda).orElseThrow();

            model.addAttribute("profissional", profissional);

            model.addAttribute(
                "horarios",
                agendaService.listarHorariosDisponiveis()
            );

        return "public/agendamento";
    }
}