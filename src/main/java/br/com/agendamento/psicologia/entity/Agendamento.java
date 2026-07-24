package br.com.agendamento.psicologia.entity;

import br.com.agendamento.psicologia.enums.StatusAgendamentoEnum;
import br.com.agendamento.psicologia.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "agendamentos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusAgendamentoEnum status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "horario_id", nullable = false)
    private HorarioDisponivel horario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profissional_id", nullable = false)
    private Profissional profissional;

    public static Agendamento criar(
            Paciente paciente,
            HorarioDisponivel horario,
            LocalDateTime dataCriacao
    ) {
        Agendamento agendamento = new Agendamento();
        agendamento.paciente = paciente;
        agendamento.horario = horario;
        agendamento.profissional = horario.getProfissional();
        agendamento.dataCriacao = dataCriacao;
        agendamento.status = StatusAgendamentoEnum.PENDENTE;

        return agendamento;
    }

    public void confirmar() {
        if (status == StatusAgendamentoEnum.CONFIRMADO) {
            return;
        }

        if (status != StatusAgendamentoEnum.PENDENTE) {
            throw new BusinessException(
                    "Somente agendamentos pendentes podem ser confirmados."
            );
        }

        this.status = StatusAgendamentoEnum.CONFIRMADO;
    }

    public void cancelar() {
        if (status == StatusAgendamentoEnum.CANCELADO) {
            return;
        }

        if (status == StatusAgendamentoEnum.CONCLUIDO) {
            throw new BusinessException(
                    "Um agendamento concluído não pode ser cancelado."
            );
        }

        this.status = StatusAgendamentoEnum.CANCELADO;
    }

    public void concluir(LocalDateTime dataAtual) {
        if (status == StatusAgendamentoEnum.CONCLUIDO) {
            return;
        }

        if (status != StatusAgendamentoEnum.CONFIRMADO) {
            throw new BusinessException(
                    "Somente agendamentos confirmados podem ser concluídos."
            );
        }

        if (horario.getDataHora().isAfter(dataAtual)) {
            throw new BusinessException(
                    "Não é possível concluir um atendimento futuro."
            );
        }

        this.status = StatusAgendamentoEnum.CONCLUIDO;
    }
}