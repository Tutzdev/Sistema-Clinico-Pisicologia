package br.com.agendamento.psicologia.dto;

import br.com.agendamento.psicologia.entity.Usuario;
import br.com.agendamento.psicologia.enums.RoleEnum;

public record LoginResponseDTO(
        String accessToken,
        String tokenType,
        long expiresIn,
        Long usuarioId,
        String email,
        RoleEnum role
) {

    private static final String TOKEN_TYPE = "Bearer";

    public static LoginResponseDTO of(
            String accessToken,
            long expiresIn,
            Usuario usuario
    ) {
        return new LoginResponseDTO(
                accessToken,
                TOKEN_TYPE,
                expiresIn,
                usuario.getId(),
                usuario.getEmail(),
                usuario.getRole()
        );
    }
}
