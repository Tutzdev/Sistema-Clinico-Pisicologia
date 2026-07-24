package br.com.agendamento.psicologia.dto;

import br.com.agendamento.psicologia.entity.Agendamento;
import br.com.agendamento.psicologia.enums.StatusAgendamentoEnum;

import java.time.LocalDateTime;

public record AgendamentoResponseDTO(
        Long id,
        LocalDateTime dataCriacao,
        StatusAgendamentoEnum status,
        Long pacienteId,
        String pacienteNome,
        String pacienteTelefone,
        String pacienteEmail,
        Long horarioId,
        LocalDateTime dataHora,
        Long profissionalId,
        String profissionalNome
) {

    public static AgendamentoResponseDTO from(
            Agendamento agendamento
    ) {
        return new AgendamentoResponseDTO(
                agendamento.getId(),
                agendamento.getDataCriacao(),
                agendamento.getStatus(),
                agendamento.getPaciente().getId(),
                agendamento.getPaciente().getNome(),
                agendamento.getPaciente().getTelefone(),
                agendamento.getPaciente().getEmail(),
                agendamento.getHorario().getId(),
                agendamento.getHorario().getDataHora(),
                agendamento.getProfissional().getId(),
                agendamento.getProfissional().getNome()
        );
    }
}
