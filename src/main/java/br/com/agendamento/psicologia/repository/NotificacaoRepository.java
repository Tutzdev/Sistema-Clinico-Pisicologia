package br.com.agendamento.psicologia.repository;

import br.com.agendamento.psicologia.entity.Notificacao;
import br.com.agendamento.psicologia.enums.TipoNotificacaoEnum;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;

public interface NotificacaoRepository
        extends JpaRepository<Notificacao, Long> {

    boolean existsByAgendamentoIdAndTipo(
            Long agendamentoId,
            TipoNotificacaoEnum tipo
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Notificacao>
            findTop20ByEnviadaFalseAndTentativasLessThanOrderByDataCriacaoAsc(
                    int limiteTentativas
            );
}