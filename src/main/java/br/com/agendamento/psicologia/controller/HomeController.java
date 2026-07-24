package br.com.agendamento.psicologia.controller;

import br.com.agendamento.psicologia.service.ProfissionalService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProfissionalService profissionalService;

    public HomeController(
            ProfissionalService profissionalService
    ) {
        this.profissionalService = profissionalService;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute(
                "profissionais",
                profissionalService.listarTodos()
        );

        return "public/index";
    }
}
