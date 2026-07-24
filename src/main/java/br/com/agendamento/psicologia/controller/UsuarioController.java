package br.com.agendamento.psicologia.controller;

import br.com.agendamento.psicologia.dto.AlterarSenhaRequestDTO;
import br.com.agendamento.psicologia.dto.UsuarioRequestDTO;
import br.com.agendamento.psicologia.dto.UsuarioResponseDTO;
import br.com.agendamento.psicologia.service.UsuarioService;
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
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDTO criar(
            @Valid @RequestBody UsuarioRequestDTO request
    ) {
        return UsuarioResponseDTO.from(
                usuarioService.criar(
                        request.email(),
                        request.senha(),
                        request.role(),
                        request.profissionalId()
                )
        );
    }

    @GetMapping
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioService.listarTodos()
                .stream()
                .map(UsuarioResponseDTO::from)
                .toList();
    }

    @GetMapping("/{usuarioId}")
    public UsuarioResponseDTO buscarPorId(
            @PathVariable
            @Positive(message = "O usuário informado é inválido.")
            Long usuarioId
    ) {
        return UsuarioResponseDTO.from(
                usuarioService.buscarPorId(usuarioId)
        );
    }

    @PatchMapping("/{usuarioId}/senha")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void alterarSenha(
            @PathVariable
            @Positive(message = "O usuário informado é inválido.")
            Long usuarioId,
            @Valid @RequestBody AlterarSenhaRequestDTO request
    ) {
        usuarioService.alterarSenha(
                usuarioId,
                request.novaSenha()
        );
    }

    @PatchMapping("/{usuarioId}/ativacao")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void ativar(
            @PathVariable
            @Positive(message = "O usuário informado é inválido.")
            Long usuarioId
    ) {
        usuarioService.ativar(usuarioId);
    }

    @PatchMapping("/{usuarioId}/desativacao")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(
            @PathVariable
            @Positive(message = "O usuário informado é inválido.")
            Long usuarioId
    ) {
        usuarioService.desativar(usuarioId);
    }
}