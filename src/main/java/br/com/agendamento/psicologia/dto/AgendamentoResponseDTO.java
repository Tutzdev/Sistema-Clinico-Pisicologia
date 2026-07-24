package br.com.agendamento.psicologia.dto;

import br.com.agendamento.psicologia.entity.Agendamento;

import java.time.LocalDateTime;

public record AgendamentoResponseDTO(
        Long id,
        LocalDateTime dataCriacao,
        boolean confirmado,
        Long pacienteId,
        String pacienteNome,
        Long horarioId,
        LocalDateTime dataHora,
        Long profissionalId,
        String profissionalNome
) {

    public static AgendamentoResponseDTO from(Agendamento agendamento) {
        return new AgendamentoResponseDTO(
                agendamento.getId(),
                agendamento.getDataCriacao(),
                agendamento.isConfirmado(),
                agendamento.getPaciente().getId(),
                agendamento.getPaciente().getNome(),
                agendamento.getHorario().getId(),
                agendamento.getHorario().getDataHora(),
                agendamento.getProfissional().getId(),
                agendamento.getProfissional().getNome()
        );
    }
}