package br.com.agendamento.psicologia.dto;

import br.com.agendamento.psicologia.entity.HorarioDisponivel;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class HorarioDisponivelDTO {

    private Long id;

    @NotNull(message = "A data e o horário são obrigatórios.")
    @Future(message = "O horário deve estar no futuro.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dataHora;

    @NotNull(message = "O profissional é obrigatório.")
    @Positive(message = "O profissional informado é inválido.")
    private Long profissionalId;

    private boolean disponivel;

    private String profissionalNome;

    public static HorarioDisponivelDTO from(
            HorarioDisponivel horario
    ) {
        HorarioDisponivelDTO response = new HorarioDisponivelDTO();
        response.setId(horario.getId());
        response.setDataHora(horario.getDataHora());
        response.setProfissionalId(
                horario.getProfissional().getId()
        );
        response.setDisponivel(horario.isDisponivel());
        response.setProfissionalNome(
                horario.getProfissional().getNome()
        );

        return response;
    }
}