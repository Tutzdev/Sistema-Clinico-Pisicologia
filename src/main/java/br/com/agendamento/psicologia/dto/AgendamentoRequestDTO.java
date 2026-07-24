package br.com.agendamento.psicologia.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AgendamentoRequestDTO(
        @NotNull(message = "O paciente é obrigatório.")
        @Positive(message = "O paciente informado é inválido.")
        Long pacienteId,

        @NotNull(message = "O horário é obrigatório.")
        @Positive(message = "O horário informado é inválido.")
        Long horarioId
) {
}