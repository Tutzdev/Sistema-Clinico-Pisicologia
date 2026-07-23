package br.com.agendamento.psicologia.repository;

import br.com.agendamento.psicologia.entity.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfissionalRepository extends JpaRepository<Profissional, Long> {

    Optional<Profissional> findByEmail(String email);

    Optional<Profissional> findByCodigoAgenda(String codigoAgenda);
}