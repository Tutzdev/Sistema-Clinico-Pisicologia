package br.com.agendamento.psicologia.controller;

import br.com.agendamento.psicologia.dto.AgendamentoRequestDTO;
import br.com.agendamento.psicologia.dto.AgendamentoResponseDTO;
import br.com.agendamento.psicologia.service.AgendamentoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;

    public AgendamentoController(
            AgendamentoService agendamentoService
    ) {
        this.agendamentoService = agendamentoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgendamentoResponseDTO agendar(
            @Valid @RequestBody AgendamentoRequestDTO request
    ) {
        return AgendamentoResponseDTO.from(
                agendamentoService.agendar(
                        request.pacienteId(),
                        request.horarioId()
                )
        );
    }

    @GetMapping("/{agendamentoId}")
    public AgendamentoResponseDTO buscarPorId(
            @PathVariable
            @Positive(message = "O agendamento informado é inválido.")
            Long agendamentoId
    ) {
        return AgendamentoResponseDTO.from(
                agendamentoService.buscarPorId(agendamentoId)
        );
    }

    @GetMapping("/profissional/{profissionalId}")
    public List<AgendamentoResponseDTO> listarPorProfissional(
            @PathVariable
            @Positive(message = "O profissional informado é inválido.")
            Long profissionalId
    ) {
        return agendamentoService
                .listarPorProfissional(profissionalId)
                .stream()
                .map(AgendamentoResponseDTO::from)
                .toList();
    }

    @PatchMapping("/{agendamentoId}/confirmacao")
    public AgendamentoResponseDTO confirmar(
            @PathVariable
            @Positive(message = "O agendamento informado é inválido.")
            Long agendamentoId
    ) {
        return AgendamentoResponseDTO.from(
                agendamentoService.confirmar(agendamentoId)
        );
    }

    @PatchMapping("/{agendamentoId}/cancelamento")
    public AgendamentoResponseDTO cancelar(
            @PathVariable
            @Positive(message = "O agendamento informado é inválido.")
            Long agendamentoId
    ) {
        return AgendamentoResponseDTO.from(
                agendamentoService.cancelar(agendamentoId)
        );
    }

    @PatchMapping("/{agendamentoId}/conclusao")
    public AgendamentoResponseDTO concluir(
            @PathVariable
            @Positive(message = "O agendamento informado é inválido.")
            Long agendamentoId
    ) {
        return AgendamentoResponseDTO.from(
                agendamentoService.concluir(agendamentoId)
        );
    }
}