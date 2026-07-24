package br.com.agendamento.psicologia.controller;

import br.com.agendamento.psicologia.dto.HorarioDisponivelDTO;
import br.com.agendamento.psicologia.service.AgendaService;
import br.com.agendamento.psicologia.service.ProfissionalService;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/public")
public class PublicAgendaApiController {

    private final ProfissionalService profissionalService;
    private final AgendaService agendaService;

    public PublicAgendaApiController(
            ProfissionalService profissionalService,
            AgendaService agendaService
    ) {
        this.profissionalService = profissionalService;
        this.agendaService = agendaService;
    }

    @GetMapping("/profissionais/{profissionalId}/horarios")
    public List<HorarioDisponivelDTO> listarHorarios(
            @PathVariable
            @Positive(message = "O profissional informado é inválido.")
            Long profissionalId
    ) {
        profissionalService.buscarPorId(profissionalId);

        return agendaService
                .listarHorariosDisponiveisPorProfissional(
                        profissionalId
                )
                .stream()
                .map(HorarioDisponivelDTO::from)
                .toList();
    }
}
