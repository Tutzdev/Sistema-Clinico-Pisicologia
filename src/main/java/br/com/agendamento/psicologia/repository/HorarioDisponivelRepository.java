package br.com.agendamento.psicologia.repository;

import br.com.agendamento.psicologia.entity.HorarioDisponivel;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HorarioDisponivelRepository
        extends JpaRepository<HorarioDisponivel, Long> {

    @EntityGraph(attributePaths = "profissional")
    List<HorarioDisponivel>
            findByDisponivelTrueAndDataHoraAfterOrderByDataHoraAsc(
                    LocalDateTime dataAtual
            );

    @EntityGraph(attributePaths = "profissional")
    List<HorarioDisponivel>
            findByProfissionalIdAndDisponivelTrueAndDataHoraAfterOrderByDataHoraAsc(
                    Long profissionalId,
                    LocalDateTime dataAtual
            );

    boolean existsByProfissionalIdAndDataHora(
            Long profissionalId,
            LocalDateTime dataHora
    );

    long countByDisponivelTrueAndDataHoraAfter(
            LocalDateTime dataAtual
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT horario
            FROM HorarioDisponivel horario
            WHERE horario.id = :horarioId
            """)
    Optional<HorarioDisponivel> findByIdForUpdate(
            @Param("horarioId") Long horarioId
    );
}
