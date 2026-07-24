package br.com.agendamento.psicologia.dto;

import br.com.agendamento.psicologia.entity.Profissional;

public record ProfissionalResponseDTO(
        Long id,
        String nome,
        String email,
        String codigoAgenda
) {

    public static ProfissionalResponseDTO from(
            Profissional profissional
    ) {
        return new ProfissionalResponseDTO(
                profissional.getId(),
                profissional.getNome(),
                profissional.getEmail(),
                profissional.getCodigoAgenda()
        );
    }
}