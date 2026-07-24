package br.com.agendamento.psicologia.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfissionalPageController {

    @GetMapping("/profissional")
    public String dashboard() {
        return "profissional/dashboard";
    }
}
