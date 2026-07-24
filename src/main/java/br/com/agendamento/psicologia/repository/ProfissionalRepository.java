package br.com.agendamento.psicologia.repository;

import br.com.agendamento.psicologia.entity.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfissionalRepository
        extends JpaRepository<Profissional, Long> {

    List<Profissional> findAllByOrderByNomeAsc();

    Optional<Profissional> findByEmailIgnoreCase(String email);

    Optional<Profissional> findByCodigoAgenda(String codigoAgenda);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(
            String email,
            Long profissionalId
    );

    boolean existsByCodigoAgenda(String codigoAgenda);

    boolean existsByCodigoAgendaAndIdNot(
            String codigoAgenda,
            Long profissionalId
    );
}