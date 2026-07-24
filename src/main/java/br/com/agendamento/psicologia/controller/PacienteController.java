package br.com.agendamento.psicologia.controller;

import br.com.agendamento.psicologia.dto.PacienteRequestDTO;
import br.com.agendamento.psicologia.dto.PacienteResponseDTO;
import br.com.agendamento.psicologia.service.PacienteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PacienteResponseDTO criar(
            @Valid @RequestBody PacienteRequestDTO request
    ) {
        return PacienteResponseDTO.from(
                pacienteService.criar(
                        request.nome(),
                        request.telefone(),
                        request.email()
                )
        );
    }

    @GetMapping("/{pacienteId}")
    public PacienteResponseDTO buscarPorId(
            @PathVariable
            @Positive(message = "O paciente informado é inválido.")
            Long pacienteId
    ) {
        return PacienteResponseDTO.from(
                pacienteService.buscarPorId(pacienteId)
        );
    }

    @GetMapping
    public List<PacienteResponseDTO> listarTodos() {
        return pacienteService.listarTodos()
                .stream()
                .map(PacienteResponseDTO::from)
                .toList();
    }

    @PutMapping("/{pacienteId}")
    public PacienteResponseDTO atualizar(
            @PathVariable
            @Positive(message = "O paciente informado é inválido.")
            Long pacienteId,
            @Valid @RequestBody PacienteRequestDTO request
    ) {
        return PacienteResponseDTO.from(
                pacienteService.atualizar(
                        pacienteId,
                        request.nome(),
                        request.telefone(),
                        request.email()
                )
        );
    }

    @DeleteMapping("/{pacienteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(
            @PathVariable
            @Positive(message = "O paciente informado é inválido.")
            Long pacienteId
    ) {
        pacienteService.excluir(pacienteId);
    }
}