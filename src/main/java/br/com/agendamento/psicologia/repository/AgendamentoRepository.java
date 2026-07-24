package br.com.agendamento.psicologia.repository;

import br.com.agendamento.psicologia.entity.Agendamento;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AgendamentoRepository
        extends JpaRepository<Agendamento, Long> {

    @EntityGraph(attributePaths = {
            "paciente",
            "horario",
            "profissional"
    })
    @Query("""
            SELECT agendamento
            FROM Agendamento agendamento
            WHERE agendamento.id = :agendamentoId
            """)
    Optional<Agendamento> findDetailsById(
            @Param("agendamentoId") Long agendamentoId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {
            "paciente",
            "horario",
            "profissional"
    })
    @Query("""
            SELECT agendamento
            FROM Agendamento agendamento
            WHERE agendamento.id = :agendamentoId
            """)
    Optional<Agendamento> findByIdForUpdate(
            @Param("agendamentoId") Long agendamentoId
    );

    @EntityGraph(attributePaths = {
            "paciente",
            "horario",
            "profissional"
    })
    List<Agendamento> findByProfissionalIdOrderByHorarioDataHoraAsc(
            Long profissionalId
    );
}