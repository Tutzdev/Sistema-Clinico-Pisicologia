package br.com.agendamento.psicologia.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "horarios_disponiveis",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_horario_profissional_data",
                columnNames = {"profissional_id", "data_hora"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HorarioDisponivel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(nullable = false)
    private boolean disponivel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profissional_id", nullable = false)
    private Profissional profissional;

    public static HorarioDisponivel criar(
            LocalDateTime dataHora,
            Profissional profissional
    ) {
        HorarioDisponivel horario = new HorarioDisponivel();
        horario.dataHora = dataHora;
        horario.profissional = profissional;
        horario.disponivel = true;

        return horario;
    }

    public void reservar() {
        this.disponivel = false;
    }
}