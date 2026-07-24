package br.com.agendamento.psicologia.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record NovoHorarioRequestDTO(
        @NotNull(message = "A data e o horário são obrigatórios.")
        @Future(message = "O horário deve estar no futuro.")
        LocalDateTime dataHora
) {
}
