package br.com.agendamento.psicologia.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "horarios_disponiveis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HorarioDisponivel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataHora;

    private boolean disponivel;

    @ManyToOne
    private Profissional profissional;
}