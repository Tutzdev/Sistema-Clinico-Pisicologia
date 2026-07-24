package br.com.agendamento.psicologia.dto;

import br.com.agendamento.psicologia.entity.Profissional;
import br.com.agendamento.psicologia.entity.Usuario;
import br.com.agendamento.psicologia.enums.RoleEnum;

import java.time.LocalDateTime;

public record UsuarioResponseDTO(
        Long id,
        String email,
        RoleEnum role,
        boolean ativo,
        Long profissionalId,
        String profissionalNome,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm
) {

    public static UsuarioResponseDTO from(Usuario usuario) {
        Profissional profissional = usuario.getProfissional();

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.isAtivo(),
                profissional != null ? profissional.getId() : null,
                profissional != null ? profissional.getNome() : null,
                usuario.getCriadoEm(),
                usuario.getAtualizadoEm()
        );
    }
}