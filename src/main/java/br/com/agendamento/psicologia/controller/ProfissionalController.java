package br.com.agendamento.psicologia.controller;

import br.com.agendamento.psicologia.dto.ProfissionalRequestDTO;
import br.com.agendamento.psicologia.dto.ProfissionalResponseDTO;
import br.com.agendamento.psicologia.service.ProfissionalService;
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
@RequestMapping("/api/profissionais")
public class ProfissionaisController {

    private final ProfissionalService profissionalService;

    public ProfissionalController(
            ProfissionalService profissionalService
    ) {
        this.profissionalService = profissionalService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProfissionalResponseDTO criar(
        @Valid @RequestBody ProfissionalRequestDTO request
    ) {
        return ProfissionalResponseDTO.from(
                profissionalService.criar(
                    request.nome(),
                    request.email(),
                    request.codigoAgenda()
                )
        );
    }

    @GetMapping
    public List<ProfissionalResponseDTO> listarTodos() {
        return profissionalService.listarTodos()
                .stream()
                .map(ProfissionalResponseDTO::from)
                .toList();
    }

    @GetMapping("/{profissionalId")
    public ProfissionalResponseDTO buscarPorId(
        @PathVariable
        @Positive(message = "O profissional informado é inválido.")
        Long profissionalId
    ) {
        return ProfissionalResponseDTO.from(
                profissionalService.buscarPorId(profissionalId)
        );
    }

    @PutMapping("/{profissionalId")
    public ProfissionalResponseDTO atualizar(
            @PathVariable
            @Positive(message = "O profissional informado é inválido.")
            Long profissionalId,
            @Valid @RequestBody ProfissionalRequestDTO request
    ) {
        return ProfissionalResponseDTO.from(
                profissionalService.atualizar(
                    profissionalId,
                    request.nome(),
                    request.email(),
                    request.codigoAgenda()
                )
        );
    }

    @DeleteMapping("/{profissionalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(
            @PathVariable
            @Positive(message = "O profissional informado é inválido.")
            Long profissionalId
    ) {
        profissionalService.excluir(profissionalId);
    }
}