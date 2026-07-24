package br.com.agendamento.psicologia.dto;

import br.com.agendamento.psicologia.entity.Usuario;
import br.com.agendamento.psicologia.enums.RoleEnum;

public record LoginResponseDTO(
        String accessToken,
        String tokenType,
        long expiresIn,
        Long usuarioId,
        String email,
        RoleEnum role,
        Long profissionalId,
        String profissionalNome
) {

    private static final String TOKEN_TYPE = "Bearer";

    public static LoginResponseDTO of(
            String accessToken,
            long expiresIn,
            Usuario usuario
    ) {
        var profissional = usuario.getProfissional();

        return new LoginResponseDTO(
                accessToken,
                TOKEN_TYPE,
                expiresIn,
                usuario.getId(),
                usuario.getEmail(),
                usuario.getRole(),
                profissional != null ? profissional.getId() : null,
                profissional != null ? profissional.getNome() : null
        );
    }
}
