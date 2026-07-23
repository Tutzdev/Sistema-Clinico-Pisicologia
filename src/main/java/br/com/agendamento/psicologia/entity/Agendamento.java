package br.com.agendamento.psicologia.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "agendamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataCriacao;

    private boolean confirmado;

    @ManyToOne
    private Paciente paciente;

    @ManyToOne
    private HorarioDisponivel horario;

    @ManyToOne
    private Profissional profissional;
}