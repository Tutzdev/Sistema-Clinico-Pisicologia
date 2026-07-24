package br.com.agendamento.psicologia.controller;

import br.com.agendamento.psicologia.dto.PacienteResponseDTO;
import br.com.agendamento.psicologia.service.AgendaService;
import br.com.agendamento.psicologia.service.AgendamentoService;
import br.com.agendamento.psicologia.service.PacienteService;
import br.com.agendamento.psicologia.service.ProfissionalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final PacienteService pacienteService;
    private final ProfissionalService profissionalService;
    private final AgendaService agendaService;
    private final AgendamentoService agendamentoService;

    public AdminController(
            PacienteService pacienteService,
            ProfissionalService profissionalService,
            AgendaService agendaService,
            AgendamentoService agendamentoService
    ) {
        this.pacienteService = pacienteService;
        this.profissionalService = profissionalService;
        this.agendaService = agendaService;
        this.agendamentoService = agendamentoService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute(
                "totalPacientes",
                pacienteService.contarTodos()
        );

        model.addAttribute(
                "totalProfissionais",
                profissionalService.contarTodos()
        );

        model.addAttribute(
                "horariosDisponiveis",
                agendaService.contarHorariosDisponiveis()
        );

        model.addAttribute(
                "agendamentosPendentes",
                agendamentoService.contarPendentes()
        );

        return "admin/dashboard";
    }

    @GetMapping("/pacientes")
    public String pacientes(Model model) {
        model.addAttribute(
                "pacientes",
                pacienteService.listarTodos()
                        .stream()
                        .map(PacienteResponseDTO::from)
                        .toList()
        );

        return "admin/pacientes";
    }
}