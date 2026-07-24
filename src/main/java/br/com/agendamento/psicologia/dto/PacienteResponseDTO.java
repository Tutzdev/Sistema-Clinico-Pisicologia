package br.com.agendamento.psicologia.dto;

import br.com.agendamento.psicologia.entity.Paciente;

public record PacienteResponseDTO(
        Long id,
        String nome,
        String telefone,
        String email
) {

    public static PacienteResponseDTO from(Paciente paciente) {
        return new PacienteResponseDTO(
                paciente.getId(),
                paciente.getNome(),
                paciente.getTelefone(),
                paciente.getEmail()
        );
    }
}